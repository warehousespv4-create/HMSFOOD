package com.example.foodku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun CartScreen(vm: AppViewModel, onBack: () -> Unit, onOrdered: () -> Unit) {
    val lines = vm.lines()
    val restaurant = vm.cartRestaurantId?.let { SampleData.restaurantById(it) }

    Column(Modifier.fillMaxSize().background(Surface)) {
        Row(
            Modifier.fillMaxWidth().background(Orange).statusBarsPadding().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White,
                modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Text("Keranjang", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        if (lines.isEmpty() || restaurant == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 64.sp)
                    Text("Keranjangmu masih kosong", color = TextGray)
                }
            }
            return@Column
        }

        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("🍽️ ${restaurant.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            items(lines, key = { it.item.id }) { l ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiBox(l.item.emoji, 52)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(l.item.name, fontWeight = FontWeight.SemiBold)
                        Text(rupiah(l.item.price * l.qty), color = Orange, fontWeight = FontWeight.Bold)
                    }
                    QtyStepper(l.qty, { vm.add(restaurant, l.item) }, { vm.remove(l.item) })
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Rincian Pembayaran", fontWeight = FontWeight.Bold)
                    PriceRow("Subtotal", vm.subtotal())
                    PriceRow("Ongkos kirim", AppViewModel.DELIVERY_FEE)
                    PriceRow("Biaya layanan", AppViewModel.SERVICE_FEE)
                    HorizontalDivider()
                    PriceRow("Total", vm.total(), bold = true)
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Total", color = TextGray, fontSize = 12.sp)
                Text(rupiah(vm.total()), color = Orange, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Button(
                onClick = { vm.checkout(); onOrdered() },
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
            ) { Text("Pesan Sekarang", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: Int, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, Modifier.weight(1f), fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(rupiah(value), fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}
