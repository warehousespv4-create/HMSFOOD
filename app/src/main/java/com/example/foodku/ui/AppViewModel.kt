package com.example.foodku.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.foodku.data.*

class AppViewModel : ViewModel() {
    companion object {
        const val DELIVERY_FEE = 8000
        const val SERVICE_FEE = 2000
    }

    var cartRestaurantId by mutableStateOf<Int?>(null)
        private set
    var cart by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set
    var orders by mutableStateOf<List<Order>>(emptyList())
        private set

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
        return SampleData.restaurantById(rid).menu
            .filter { qty(it.id) > 0 }
            .map { CartLine(it, qty(it.id)) }
    }

    fun subtotal() = lines().sumOf { it.item.price * it.qty }
    fun total() = if (cart.isEmpty()) 0 else subtotal() + DELIVERY_FEE + SERVICE_FEE

    fun checkout() {
        val rid = cartRestaurantId ?: return
        val r = SampleData.restaurantById(rid)
        val summary = lines().joinToString { "${it.qty}x ${it.item.name}" }
        orders = listOf(Order(orders.size + 1, r.name, summary, total(), "Sedang disiapkan")) + orders
        cart = emptyMap()
        cartRestaurantId = null
    }
}
