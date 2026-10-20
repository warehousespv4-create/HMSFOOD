package com.example.foodku.data

/** Data awal (seed) yang dipakai saat aplikasi pertama kali dijalankan. */
object SampleData {

    val categories = listOf(
        Category("Ayam", "🍗"), Category("Nasi", "🍚"), Category("Mie", "🍜"),
        Category("Kopi", "☕"), Category("Boba", "🧋"), Category("Burger", "🍔"),
        Category("Pizza", "🍕"), Category("Dessert", "🍰")
    )

    val restaurants = listOf(
        Restaurant(
            1, "Ayam Geprek Bu Rini", "Ayam", 4.8, 0.9, 15, "🍗", "Diskon 30%", listOf(
                MenuItem(101, "Ayam Geprek Original", "Sambal bawang level 1-5", 22000, "🍗"),
                MenuItem(102, "Ayam Geprek Keju", "Dengan topping mozzarella", 28000, "🧀"),
                MenuItem(103, "Nasi Putih", "Porsi regular", 5000, "🍚"),
                MenuItem(104, "Es Teh Manis", "Segar dan dingin", 6000, "🥤")
            )
        ),
        Restaurant(
            2, "Warung Nasi Padang Sederhana", "Nasi", 4.7, 1.4, 20, "🍛", "Gratis Ongkir", listOf(
                MenuItem(201, "Nasi Rendang", "Rendang sapi empuk", 35000, "🍛"),
                MenuItem(202, "Nasi Ayam Pop", "Dengan sambal hijau", 30000, "🍗"),
                MenuItem(203, "Nasi Telur Balado", "Telur balado pedas", 25000, "🍳"),
                MenuItem(204, "Es Jeruk", "Jeruk peras asli", 8000, "🍊")
            )
        ),
        Restaurant(
            3, "Mie Gacoan Express", "Mie", 4.6, 2.1, 25, "🍜", "Diskon 20%", listOf(
                MenuItem(301, "Mie Gacoan Level 3", "Mie pedas khas", 16000, "🍜"),
                MenuItem(302, "Udang Rambutan", "Isi 3 pcs", 12000, "🍤"),
                MenuItem(303, "Siomay Goreng", "Isi 4 pcs", 11000, "🥟"),
                MenuItem(304, "Es Gobak Sodor", "Minuman segar", 10000, "🥤")
            )
        ),
        Restaurant(
            4, "Kopi Senja", "Kopi", 4.9, 0.6, 10, "☕", "Beli 1 Gratis 1", listOf(
                MenuItem(401, "Kopi Susu Gula Aren", "Best seller", 18000, "☕"),
                MenuItem(402, "Americano", "Double shot", 17000, "☕"),
                MenuItem(403, "Matcha Latte", "Matcha premium", 24000, "🍵"),
                MenuItem(404, "Croissant", "Butter croissant", 20000, "🥐")
            )
        ),
        Restaurant(
            5, "Boba Time", "Boba", 4.5, 1.8, 18, "🧋", "Diskon 15%", listOf(
                MenuItem(501, "Brown Sugar Boba", "Manis legit", 22000, "🧋"),
                MenuItem(502, "Taro Milk Tea", "Creamy taro", 21000, "🧋"),
                MenuItem(503, "Thai Tea", "Thai tea asli", 19000, "🧋")
            )
        ),
        Restaurant(
            6, "Burger Bangor", "Burger", 4.4, 2.6, 28, "🍔", null, listOf(
                MenuItem(601, "Cheese Burger", "Daging sapi 100gr", 27000, "🍔"),
                MenuItem(602, "Double Beef Burger", "2 patty daging", 38000, "🍔"),
                MenuItem(603, "Kentang Goreng", "Large", 15000, "🍟")
            )
        ),
        Restaurant(
            7, "Pizza Kita", "Pizza", 4.6, 3.0, 30, "🍕", "Gratis Ongkir", listOf(
                MenuItem(701, "Pizza Pepperoni M", "Medium 8 slice", 65000, "🍕"),
                MenuItem(702, "Pizza Margherita M", "Mozzarella & basil", 58000, "🍕")
            )
        ),
        Restaurant(
            8, "Dessert Box Manis", "Dessert", 4.7, 1.2, 15, "🍰", "Diskon 10%", listOf(
                MenuItem(801, "Choco Lava Cake", "Lumer di dalam", 25000, "🍰"),
                MenuItem(802, "Red Velvet Slice", "Cream cheese frosting", 28000, "🍰"),
                MenuItem(803, "Es Krim Vanila", "Cup 120ml", 12000, "🍨")
            )
        )
    )
}
