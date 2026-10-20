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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.data.Order
import com.example.foodku.data.OrderStatus
import com.example.foodku.data.User
import com.example.foodku.data.rupiah
import com.example.foodku.ui.theme.Orange
import com.example.foodku.ui.theme.Surface
import com.example.foodku.ui.theme.TextGray

@Composable
fun OrdersScreen(orders: List<Order>, onCancel: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().background(Surface)) {
        Text(
            "Pesanan Saya",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth().background(Orange).statusBarsPadding().padding(16.dp)
        )

        if (orders.isEmpty()) {
            EmptyState("🧾", "Belum ada pesanan")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.id }) { o ->
                    Column(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                o.restaurantName,
                                Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            StatusChip(o.status)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(o.summary, color = TextGray, fontSize = 13.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total ${rupiah(o.total)}", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.weight(1f))
                            if (o.status == OrderStatus.WAITING) {
                                TextButton(onClick = { onCancel(o.id) }) {
                                    Text("Batalkan", color = Color(0xFFC62828), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(user: User, onLogout: () -> Unit, onChangePassword: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Surface)) {
        Column(
            Modifier.fillMaxWidth()
                .background(Orange)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(80.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.fullName.take(1).uppercase(),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Orange
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("@${user.username}", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            RoleBadge(user.role)
        }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("📍  Alamat Tersimpan", "🎟️  Voucher Saya", "💳  Metode Pembayaran", "❓  Bantuan")
                .forEach {
                    Text(
                        it,
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    )
                }

            Text(
                "🔒  Ganti Password",
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onChangePassword() }
                    .padding(16.dp)
            )
            Text(
                "🚪  Keluar",
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onLogout() }
                    .padding(16.dp),
                color = Color(0xFFC62828)
            )
        }
    }
}
