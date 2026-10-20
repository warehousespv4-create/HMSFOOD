package com.example.foodku.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

/**
 * Penyimpanan lokal (SharedPreferences + JSON) untuk akun, toko, dan pesanan.
 * Password disimpan sebagai SHA-256(salt + ":" + password).
 */
class AppRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("foodku_db", Context.MODE_PRIVATE)

    // ------------------------------------------------------------------ crypto

    fun newSalt(): String = UUID.randomUUID().toString().replace("-", "").take(12)

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest((salt + ":" + password).toByteArray(Charsets.UTF_8))
        val sb = StringBuilder(digest.size * 2)
        for (b in digest) sb.append("%02x".format(b.toInt() and 0xff))
        return sb.toString()
    }

    // ------------------------------------------------------------------- users

    fun loadUsers(): List<User> = runCatching {
        val arr = JSONArray(prefs.getString(KEY_USERS, "[]"))
        (0 until arr.length()).map { parseUser(arr.getJSONObject(it)) }
    }.getOrDefault(emptyList())

    fun saveUsers(users: List<User>) {
        val arr = JSONArray()
        users.forEach { arr.put(userToJson(it)) }
        prefs.edit().putString(KEY_USERS, arr.toString()).apply()
    }

    /** Membuat akun administrator bawaan kalau belum ada admin sama sekali. */
    fun seedAdminIfNeeded(): List<User> {
        val existing = loadUsers()
        if (existing.any { it.role == Role.ADMIN }) return existing

        val salt = newSalt()
        val admin = User(
            id = nextId(existing.map { it.id }),
            username = DEFAULT_ADMIN_USER,
            passwordHash = hash(DEFAULT_ADMIN_PASS, salt),
            salt = salt,
            fullName = "Administrator",
            role = Role.ADMIN,
            active = true,
            createdAt = System.currentTimeMillis()
        )
        val all = existing + admin
        saveUsers(all)
        return all
    }

    // ------------------------------------------------------------- restaurants

    fun loadRestaurants(): List<Restaurant> = runCatching {
        val raw = prefs.getString(KEY_RESTAURANTS, null) ?: return@runCatching emptyList()
        val arr = JSONArray(raw)
        (0 until arr.length()).map { parseRestaurant(arr.getJSONObject(it)) }
    }.getOrDefault(emptyList())

    fun saveRestaurants(list: List<Restaurant>) {
        val arr = JSONArray()
        list.forEach { arr.put(restaurantToJson(it)) }
        prefs.edit().putString(KEY_RESTAURANTS, arr.toString()).apply()
    }

    // ------------------------------------------------------------------ orders

    fun loadOrders(): List<Order> = runCatching {
        val arr = JSONArray(prefs.getString(KEY_ORDERS, "[]"))
        (0 until arr.length()).map { parseOrder(arr.getJSONObject(it)) }
    }.getOrDefault(emptyList())

    fun saveOrders(orders: List<Order>) {
        val arr = JSONArray()
        orders.forEach { arr.put(orderToJson(it)) }
        prefs.edit().putString(KEY_ORDERS, arr.toString()).apply()
    }

    // ----------------------------------------------------------------- session

    fun loadSessionId(): Int = prefs.getInt(KEY_SESSION, 0)

    fun saveSessionId(id: Int) {
        prefs.edit().putInt(KEY_SESSION, id).apply()
    }

    // ------------------------------------------------------------------ mapper

    private fun nextId(ids: List<Int>): Int = (ids.maxOrNull() ?: 0) + 1

    private fun userToJson(u: User) = JSONObject().apply {
        put("id", u.id)
        put("username", u.username)
        put("passwordHash", u.passwordHash)
        put("salt", u.salt)
        put("fullName", u.fullName)
        put("role", u.role.name)
        put("active", u.active)
        put("storeName", u.storeName)
        put("createdAt", u.createdAt)
    }

    private fun parseUser(o: JSONObject) = User(
        id = o.getInt("id"),
        username = o.getString("username"),
        passwordHash = o.getString("passwordHash"),
        salt = o.getString("salt"),
        fullName = o.optString("fullName", o.getString("username")),
        role = runCatching { Role.valueOf(o.getString("role")) }.getOrDefault(Role.BUYER),
        active = o.optBoolean("active", true),
        storeName = o.optString("storeName", ""),
        createdAt = o.optLong("createdAt", 0L)
    )

    private fun restaurantToJson(r: Restaurant) = JSONObject().apply {
        put("id", r.id)
        put("name", r.name)
        put("category", r.category)
        put("rating", r.rating)
        put("distanceKm", r.distanceKm)
        put("etaMin", r.etaMin)
        put("emoji", r.emoji)
        put("promo", r.promo ?: JSONObject.NULL)
        put("ownerId", r.ownerId)
        put("open", r.open)
        put("menu", JSONArray().apply {
            r.menu.forEach { m ->
                put(JSONObject().apply {
                    put("id", m.id)
                    put("name", m.name)
                    put("desc", m.desc)
                    put("price", m.price)
                    put("emoji", m.emoji)
                })
            }
        })
    }

    private fun parseRestaurant(o: JSONObject): Restaurant {
        val menuArr = o.optJSONArray("menu") ?: JSONArray()
        val menu = (0 until menuArr.length()).map { i ->
            val m = menuArr.getJSONObject(i)
            MenuItem(
                id = m.getInt("id"),
                name = m.getString("name"),
                desc = m.optString("desc", ""),
                price = m.optInt("price", 0),
                emoji = m.optString("emoji", "🍽️")
            )
        }
        return Restaurant(
            id = o.getInt("id"),
            name = o.getString("name"),
            category = o.optString("category", "Lainnya"),
            rating = o.optDouble("rating", 0.0),
            distanceKm = o.optDouble("distanceKm", 1.0),
            etaMin = o.optInt("etaMin", 20),
            emoji = o.optString("emoji", "🍽️"),
            promo = if (o.isNull("promo")) null else o.optString("promo", null),
            menu = menu,
            ownerId = o.optInt("ownerId", 0),
            open = o.optBoolean("open", true)
        )
    }

    private fun orderToJson(o: Order) = JSONObject().apply {
        put("id", o.id)
        put("buyerId", o.buyerId)
        put("buyerName", o.buyerName)
        put("restaurantId", o.restaurantId)
        put("restaurantName", o.restaurantName)
        put("summary", o.summary)
        put("total", o.total)
        put("status", o.status)
        put("createdAt", o.createdAt)
    }

    private fun parseOrder(o: JSONObject) = Order(
        id = o.getInt("id"),
        buyerId = o.optInt("buyerId", 0),
        buyerName = o.optString("buyerName", "-"),
        restaurantId = o.optInt("restaurantId", 0),
        restaurantName = o.optString("restaurantName", "-"),
        summary = o.optString("summary", ""),
        total = o.optInt("total", 0),
        status = o.optString("status", OrderStatus.WAITING),
        createdAt = o.optLong("createdAt", 0L)
    )

    companion object {
        const val DEFAULT_ADMIN_USER = "admin"
        const val DEFAULT_ADMIN_PASS = "Admin123!"

        private const val KEY_USERS = "users"
        private const val KEY_RESTAURANTS = "restaurants"
        private const val KEY_ORDERS = "orders"
        private const val KEY_SESSION = "session_user_id"
    }
}
