package com.example.foodku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.data.*
import com.example.foodku.ui.theme.*

@Composable
fun RestaurantScreen(restaurant: Restaurant, vm: AppViewModel, onBack: () -> Unit, onCart: () -> Unit) {
    val showCartBar = vm.cartRestaurantId == restaurant.id && vm.count > 0

    Box(Modifier.fillMaxSize().background(Surface)) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
            item {
                Box(
                    Modifier.fillMaxWidth().height(190.dp)
                        .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
                ) {
                    Text(restaurant.emoji, fontSize = 90.sp, modifier = Modifier.align(Alignment.Center))
                    Box(
                        Modifier.statusBarsPadding().padding(12.dp).size(38.dp)
                            .clip(CircleShape).background(Color.White).clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Orange) }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Text(restaurant.name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = StarYellow, modifier = Modifier.size(16.dp))
                        Text(
                            " ${restaurant.rating} • ${restaurant.category} • ${restaurant.distanceKm} km • ${restaurant.etaMin} mnt",
                            color = TextGray, fontSize = 13.sp
                        )
                    }
                    restaurant.promo?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "🏷️ $it", color = Orange, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(OrangeSoft)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
            item {
                Text("Menu", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp))
            }
            items(restaurant.menu, key = { it.id }) { m ->
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)).background(Color.White).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiBox(m.emoji, 70)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.name, fontWeight = FontWeight.SemiBold)
                        Text(m.desc, fontSize = 12.sp, color = TextGray, maxLines = 2)
                        Spacer(Modifier.height(4.dp))
                        Text(rupiah(m.price), color = Orange, fontWeight = FontWeight.Bold)
                    }
                    QtyStepper(vm.qty(m.id), { vm.add(restaurant, m) }, { vm.remove(m) })
                }
            }
        }

        if (showCartBar) {
            Row(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(Color.White).navigationBarsPadding().padding(12.dp)
                    .clip(RoundedCornerShape(28.dp)).background(Orange)
                    .clickable { onCart() }.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛒 ${vm.count} item", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("${rupiah(vm.subtotal())}  ›", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
