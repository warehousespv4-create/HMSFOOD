package com.example.foodku.data

import java.text.NumberFormat
import java.util.Locale

data class MenuItem(val id: Int, val name: String, val desc: String, val price: Int, val emoji: String)

data class Restaurant(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val distanceKm: Double,
    val etaMin: Int,
    val emoji: String,
    val promo: String?,
    val menu: List<MenuItem>
)

data class Category(val name: String, val emoji: String)

data class CartLine(val item: MenuItem, val qty: Int)

data class Order(
    val id: Int,
    val restaurantName: String,
    val summary: String,
    val total: Int,
    val status: String
)

fun rupiah(v: Int): String =
    "Rp" + NumberFormat.getNumberInstance(Locale("id", "ID")).format(v)
