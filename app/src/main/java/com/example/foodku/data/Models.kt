package com.example.foodku.data

import java.text.NumberFormat
import java.util.Locale

/** Peran pengguna di aplikasi. */
enum class Role(val label: String) {
    BUYER("Pembeli"),
    SELLER("Penjual"),
    ADMIN("Administrator")
}

/** Akun pengguna. Password tidak pernah disimpan mentah, hanya hash + salt. */
data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val fullName: String,
    val role: Role,
    val active: Boolean = true,
    val storeName: String = "",
    val createdAt: Long = 0L
)

data class MenuItem(
    val id: Int,
    val name: String,
    val desc: String,
    val price: Int,
    val emoji: String
)

data class Restaurant(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val distanceKm: Double,
    val etaMin: Int,
    val emoji: String,
    val promo: String?,
    val menu: List<MenuItem>,
    /** id pemilik (user dengan role SELLER). 0 = data bawaan sistem. */
    val ownerId: Int = 0,
    /** toko buka / tutup. */
    val open: Boolean = true
)

data class Category(val name: String, val emoji: String)

data class CartLine(val item: MenuItem, val qty: Int)

object OrderStatus {
    const val WAITING = "Menunggu konfirmasi"
    const val PROCESSING = "Diproses"
    const val DELIVERING = "Diantar"
    const val DONE = "Selesai"
    const val CANCELLED = "Dibatalkan"
}

data class Order(
    val id: Int,
    val buyerId: Int,
    val buyerName: String,
    val restaurantId: Int,
    val restaurantName: String,
    val summary: String,
    val total: Int,
    val status: String,
    val createdAt: Long = 0L
)

fun rupiah(v: Int): String =
    "Rp" + NumberFormat.getNumberInstance(Locale("id", "ID")).format(v)
