package com.example.foodku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.data.*
import com.example.foodku.ui.theme.*

@Composable
fun OrdersScreen(orders: List<Order>) {
    Column(Modifier.fillMaxSize().background(Surface)) {
        Text(
            "Pesanan Saya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth().background(Orange).statusBarsPadding().padding(16.dp)
        )
        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧾", fontSize = 64.sp)
                    Text("Belum ada pesanan", color = TextGray)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(orders, key = { it.id }) { o ->
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(14.dp)
                    ) {
                        Row {
                            Text(o.restaurantName, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text(o.status, color = Orange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(o.summary, color = TextGray, fontSize = 13.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Total ${rupiah(o.total)}", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(Modifier.fillMaxSize().background(Surface)) {
        Column(
            Modifier.fillMaxWidth().background(Orange).statusBarsPadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(80.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) { Text("😋", fontSize = 40.sp) }
            Spacer(Modifier.height(8.dp))
            Text("Pengguna FoodKu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("user@foodku.id", color = Color.White.copy(0.85f), fontSize = 13.sp)
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("📍  Alamat Tersimpan", "🎟️  Voucher Saya", "💳  Metode Pembayaran", "❓  Bantuan").forEach {
                Text(
                    it, Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp)
                )
            }
        }
    }
}
