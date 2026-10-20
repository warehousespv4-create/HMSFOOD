package com.example.foodku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.data.Restaurant
import com.example.foodku.data.SampleData
import com.example.foodku.ui.theme.Orange
import com.example.foodku.ui.theme.OrangeLight
import com.example.foodku.ui.theme.OrangeSoft
import com.example.foodku.ui.theme.StarYellow
import com.example.foodku.ui.theme.Surface
import com.example.foodku.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    restaurants: List<Restaurant>,
    cartCount: Int,
    onRestaurant: (Int) -> Unit,
    onCart: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<String?>(null) }

    val list = remember(query, selected, restaurants) {
        restaurants.filter {
            (selected == null || it.category == selected) &&
                (it.name.contains(query, true) || it.category.contains(query, true))
        }
    }

    LazyColumn(Modifier.fillMaxSize().background(Surface)) {
        item {
            Column(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Dikirim ke", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        Text("Jakarta Selatan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(containerColor = Color.White, contentColor = Orange) {
                                    Text("$cartCount")
                                }
                            }
                        },
                        modifier = Modifier.clickable { onCart() }
                    ) { Icon(Icons.Default.ShoppingCart, null, tint = Color.White) }
                }
                Spacer(Modifier.height(14.dp))
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Mau makan apa hari ini?") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Orange) },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        item {
            LazyRow(
                Modifier.background(Color.White).padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(SampleData.categories) { c ->
                    val active = selected == c.name
                    Column(
                        Modifier.clip(RoundedCornerShape(12.dp))
                            .clickable { selected = if (active) null else c.name }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.size(56.dp).clip(RoundedCornerShape(16.dp))
                                .background(if (active) Orange else OrangeSoft),
                            contentAlignment = Alignment.Center
                        ) { Text(c.emoji, fontSize = 26.sp) }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            c.name,
                            fontSize = 12.sp,
                            color = if (active) Orange else Color.Black,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        item {
            Box(
                Modifier.padding(16.dp).fillMaxWidth().height(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFF512F), Color(0xFFFF9A3C))))
                    .padding(20.dp)
            ) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text("GRATIS ONGKIR", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text("Min. belanja Rp50.000", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
                Text("🛵", fontSize = 56.sp, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }

        item {
            Text(
                if (selected == null) "Restoran Terdekat" else "Kategori: $selected",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (list.isEmpty()) {
            item { EmptyState("🍽️", "Belum ada restoran yang cocok") }
        }

        items(list, key = { it.id }) { r -> RestaurantCard(r) { onRestaurant(r.id) } }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun RestaurantCard(r: Restaurant, onClick: () -> Unit) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 6.dp).fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)).background(Color.White)
            .clickable { onClick() }.padding(12.dp)
    ) {
        EmojiBox(r.emoji, 84)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(r.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = StarYellow, modifier = Modifier.size(15.dp))
                Text(" ${r.rating} · ${r.category}", fontSize = 12.sp, color = TextGray)
            }
            Text("${r.distanceKm} km · ${r.etaMin} mnt", fontSize = 12.sp, color = TextGray)
            r.promo?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    it, fontSize = 11.sp, color = Orange, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(OrangeSoft)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
