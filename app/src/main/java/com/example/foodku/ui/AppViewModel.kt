package com.example.foodku.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.foodku.data.AppRepository
import com.example.foodku.data.CartLine
import com.example.foodku.data.MenuItem
import com.example.foodku.data.Order
import com.example.foodku.data.OrderStatus
import com.example.foodku.data.Restaurant
import com.example.foodku.data.Role
import com.example.foodku.data.SampleData
import com.example.foodku.data.User

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var restaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var orders by mutableStateOf<List<Order>>(emptyList())
        private set

    var session by mutableStateOf<User?>(null)
        private set

    var cartRestaurantId by mutableStateOf<Int?>(null)
        private set

    var cart by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    init {
        users = repo.seedAdminIfNeeded()
        val stored = repo.loadRestaurants()
        restaurants = if (stored.isEmpty()) {
            SampleData.restaurants.also { repo.saveRestaurants(it) }
        } else {
            stored
        }
        orders = repo.loadOrders()
        val sid = repo.loadSessionId()
        session = users.firstOrNull { it.id == sid && it.active }
    }

    // ================================================================ session

    fun login(username: String, password: String): String? {
        val uname = username.trim()
        if (uname.isEmpty() || password.isEmpty()) return "Username dan password wajib diisi"
        val user = users.firstOrNull { it.username.equals(uname, true) }
            ?: return "Username tidak ditemukan"
        if (user.passwordHash != repo.hash(password, user.salt)) return "Password salah"
        if (!user.active) {
            return if (user.role == Role.SELLER) {
                "Akun penjual belum disetujui admin"
            } else {
                "Akun diblokir. Hubungi administrator"
            }
        }
        session = user
        repo.saveSessionId(user.id)
        return null
    }

    fun register(
        fullName: String,
        username: String,
        password: String,
        confirm: String,
        role: Role,
        storeName: String = ""
    ): String? {
        if (role == Role.ADMIN) return "Akun administrator tidak bisa didaftarkan sendiri"

        val name = fullName.trim()
        val uname = username.trim()
        if (name.length < 3) return "Nama minimal 3 karakter"
        if (uname.length < 3) return "Username minimal 3 karakter"
        if (!uname.matches(Regex("^[a-zA-Z0-9._]+$"))) {
            return "Username hanya boleh huruf, angka, titik, dan underscore"
        }
        if (users.any { it.username.equals(uname, true) }) return "Username sudah dipakai"
        if (password.length < 6) return "Password minimal 6 karakter"
        if (password != confirm) return "Konfirmasi password tidak sama"
        if (role == Role.SELLER && storeName.trim().length < 3) return "Nama toko minimal 3 karakter"

        val salt = repo.newSalt()
        val user = User(
            id = (users.maxOfOrNull { it.id } ?: 0) + 1,
            username = uname,
            passwordHash = repo.hash(password, salt),
            salt = salt,
            fullName = name,
            role = role,
            active = role != Role.SELLER,
            storeName = storeName.trim(),
            createdAt = System.currentTimeMillis()
        )
        commitUsers(users + user)

        if (role == Role.SELLER) {
            val resto = Restaurant(
                id = (restaurants.maxOfOrNull { it.id } ?: 0) + 1,
                name = storeName.trim(),
                category = "Lainnya",
                rating = 0.0,
                distanceKm = 1.0,
                etaMin = 20,
                emoji = "🍽️",
                promo = null,
                menu = emptyList(),
                ownerId = user.id,
                open = false
            )
            commitRestaurants(restaurants + resto)
        }
        return null
    }

    fun logout() {
        session = null
        repo.saveSessionId(0)
        cart = emptyMap()
        cartRestaurantId = null
    }

    fun changePassword(old: String, new: String, confirm: String): String? {
        val me = session ?: return "Sesi berakhir, silakan masuk ulang"
        if (repo.hash(old, me.salt) != me.passwordHash) return "Password lama salah"
        if (new.length < 6) return "Password baru minimal 6 karakter"
        if (new != confirm) return "Konfirmasi password tidak sama"

        val salt = repo.newSalt()
        val updated = me.copy(passwordHash = repo.hash(new, salt), salt = salt)
        commitUsers(users.map { if (it.id == me.id) updated else it })
        session = updated
        return null
    }

    // ================================================================== buyer

    fun restaurantById(id: Int): Restaurant? = restaurants.firstOrNull { it.id == id }

    /** Toko yang tampil di beranda pembeli. */
    fun visibleRestaurants(): List<Restaurant> =
        restaurants.filter { it.open && it.menu.isNotEmpty() }

    val count: Int get() = cart.values.sum()

    fun qty(itemId: Int) = cart[itemId] ?: 0

    fun add(restaurant: Restaurant, item: MenuItem) {
        if (cartRestaurantId != restaurant.id) {
            cart = emptyMap()
            cartRestaurantId = restaurant.id
        }
        cart = cart + (item.id to qty(item.id) + 1)
    }

    fun remove(item: MenuItem) {
        val q = qty(item.id)
        cart = if (q <= 1) cart - item.id else cart + (item.id to q - 1)
        if (cart.isEmpty()) cartRestaurantId = null
    }

    fun lines(): List<CartLine> {
        val rid = cartRestaurantId ?: return emptyList()
        val resto = restaurantById(rid) ?: return emptyList()
        return resto.menu
            .filter { qty(it.id) > 0 }
            .map { CartLine(it, qty(it.id)) }
    }

    fun subtotal() = lines().sumOf { it.item.price * it.qty }

    fun total() = if (cart.isEmpty()) 0 else subtotal() + DELIVERY_FEE + SERVICE_FEE

    fun checkout(): String? {
        val me = session ?: return "Sesi berakhir, silakan masuk ulang"
        if (me.role != Role.BUYER) return "Hanya akun pembeli yang bisa memesan"
        val rid = cartRestaurantId ?: return "Keranjang masih kosong"
        val resto = restaurantById(rid) ?: return "Toko tidak ditemukan"
        if (lines().isEmpty()) return "Keranjang masih kosong"

        val order = Order(
            id = (orders.maxOfOrNull { it.id } ?: 0) + 1,
            buyerId = me.id,
            buyerName = me.fullName,
            restaurantId = resto.id,
            restaurantName = resto.name,
            summary = lines().joinToString { "${it.qty}x ${it.item.name}" },
            total = total(),
            status = OrderStatus.WAITING,
            createdAt = System.currentTimeMillis()
        )
        commitOrders(listOf(order) + orders)
        cart = emptyMap()
        cartRestaurantId = null
        return null
    }

    fun myOrders(): List<Order> = orders.filter { it.buyerId == session?.id }

    fun cancelOrder(orderId: Int): String? {
        val me = session ?: return "Sesi berakhir"
        val order = orders.firstOrNull { it.id == orderId } ?: return "Pesanan tidak ditemukan"
        if (order.buyerId != me.id) return "Ini bukan pesanan Anda"
        if (order.status != OrderStatus.WAITING) return "Pesanan sudah diproses penjual"
        commitOrders(orders.map {
            if (it.id == orderId) it.copy(status = OrderStatus.CANCELLED) else it
        })
        return null
    }

    // ================================================================= seller

    fun myRestaurant(): Restaurant? =
        session?.let { s -> restaurants.firstOrNull { it.ownerId == s.id } }

    fun sellerOrders(): List<Order> {
        val resto = myRestaurant() ?: return emptyList()
        return orders.filter { it.restaurantId == resto.id }
    }

    fun saveMenuItem(
        itemId: Int?,
        name: String,
        desc: String,
        price: Int,
        emoji: String
    ): String? {
        val resto = myRestaurant() ?: return "Toko tidak ditemukan"
        if (name.trim().length < 3) return "Nama menu minimal 3 karakter"
        if (price <= 0) return "Harga harus lebih dari 0"

        val newItem = MenuItem(
            id = itemId ?: (restaurants.flatMap { it.menu }.maxOfOrNull { it.id } ?: 100) + 1,
            name = name.trim(),
            desc = desc.trim(),
            price = price,
            emoji = emoji.ifBlank { "🍽️" }
        )
        val menu = if (itemId == null) {
            resto.menu + newItem
        } else {
            resto.menu.map { if (it.id == itemId) newItem else it }
        }
        commitRestaurants(restaurants.map { if (it.id == resto.id) it.copy(menu = menu) else it })
        return null
    }

    fun deleteMenuItem(itemId: Int) {
        val resto = myRestaurant() ?: return
        commitRestaurants(
            restaurants.map {
                if (it.id == resto.id) it.copy(menu = it.menu.filterNot { m -> m.id == itemId }) else it
            }
        )
    }

    fun setStoreOpen(open: Boolean) {
        val resto = myRestaurant() ?: return
        commitRestaurants(restaurants.map { if (it.id == resto.id) it.copy(open = open) else it })
    }

    fun advanceOrder(orderId: Int): String? {
        val resto = myRestaurant() ?: return "Toko tidak ditemukan"
        val order = orders.firstOrNull { it.id == orderId } ?: return "Pesanan tidak ditemukan"
        if (order.restaurantId != resto.id) return "Ini bukan pesanan toko Anda"
        val next = when (order.status) {
            OrderStatus.WAITING -> OrderStatus.PROCESSING
            OrderStatus.PROCESSING -> OrderStatus.DELIVERING
            OrderStatus.DELIVERING -> OrderStatus.DONE
            else -> return "Pesanan sudah final"
        }
        commitOrders(orders.map { if (it.id == orderId) it.copy(status = next) else it })
        return null
    }

    fun rejectOrder(orderId: Int): String? {
        val resto = myRestaurant() ?: return "Toko tidak ditemukan"
        val order = orders.firstOrNull { it.id == orderId } ?: return "Pesanan tidak ditemukan"
        if (order.restaurantId != resto.id) return "Ini bukan pesanan toko Anda"
        if (order.status == OrderStatus.DONE) return "Pesanan sudah selesai"
        commitOrders(orders.map {
            if (it.id == orderId) it.copy(status = OrderStatus.CANCELLED) else it
        })
        return null
    }

    // ================================================================== admin

    fun pendingSellers(): List<User> =
        users.filter { it.role == Role.SELLER && !it.active }

    fun approveSeller(userId: Int) {
        commitUsers(users.map { if (it.id == userId) it.copy(active = true) else it })
        commitRestaurants(restaurants.map { if (it.ownerId == userId) it.copy(open = true) else it })
    }

    fun setUserActive(userId: Int, active: Boolean): String? {
        if (userId == session?.id) return "Anda tidak bisa mengubah status akun sendiri"
        commitUsers(users.map { if (it.id == userId) it.copy(active = active) else it })
        return null
    }

    fun deleteUser(userId: Int): String? {
        val target = users.firstOrNull { it.id == userId } ?: return "Pengguna tidak ditemukan"
        if (target.id == session?.id) return "Anda tidak bisa menghapus akun sendiri"
        if (target.role == Role.ADMIN && users.count { it.role == Role.ADMIN } <= 1) {
            return "Minimal harus ada satu administrator"
        }
        commitUsers(users.filterNot { it.id == userId })
        commitRestaurants(restaurants.filterNot { it.ownerId == userId })
        return null
    }

    fun adminOrders(): List<Order> = orders

    fun forceCancelOrder(orderId: Int) {
        commitOrders(orders.map {
            if (it.id == orderId) it.copy(status = OrderStatus.CANCELLED) else it
        })
    }

    fun totalRevenue(): Int =
        orders.filter { it.status == OrderStatus.DONE }.sumOf { it.total }

    // ================================================================ helpers

    private fun commitUsers(list: List<User>) {
        users = list
        repo.saveUsers(list)
    }

    private fun commitRestaurants(list: List<Restaurant>) {
        restaurants = list
        repo.saveRestaurants(list)
    }

    private fun commitOrders(list: List<Order>) {
        orders = list
        repo.saveOrders(list)
    }

    companion object {
        const val DELIVERY_FEE = 8000
        const val SERVICE_FEE = 2000
    }
}
