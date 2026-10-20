package com.example.foodku.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.foodku.data.Order
import com.example.foodku.data.OrderStatus
import com.example.foodku.data.Role
import com.example.foodku.data.User
import com.example.foodku.data.rupiah
import com.example.foodku.ui.AppViewModel
import com.example.foodku.ui.BottomTab
import com.example.foodku.ui.EmptyState
import com.example.foodku.ui.RoleBadge
import com.example.foodku.ui.SectionTitle
import com.example.foodku.ui.StatCard
import com.example.foodku.ui.StatusChip
import com.example.foodku.ui.theme.Orange
import com.example.foodku.ui.theme.OrangeLight
import com.example.foodku.ui.theme.Surface
import com.example.foodku.ui.theme.TextGray

@Composable
fun AdminApp(vm: AppViewModel) {
    val user = vm.session ?: return
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Surface,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                BottomTab(0, tab, "Dasbor", Icons.Default.Home) { tab = 0 }
                BottomTab(1, tab, "Pengguna", Icons.Default.Person) { tab = 1 }
                BottomTab(2, tab, "Pesanan", Icons.Default.Receipt) { tab = 2 }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize().background(Surface)) {
            when (tab) {
                0 -> AdminDashboard(vm, user)
                1 -> AdminUsers(vm)
                else -> AdminOrders(vm)
            }
        }
    }
}

@Composable
private fun AdminDashboard(vm: AppViewModel, user: User) {
    val pending = vm.pendingSellers()
    val buyers = vm.users.count { it.role == Role.BUYER }
    val sellers = vm.users.count { it.role == Role.SELLER }
    val admins = vm.users.count { it.role == Role.ADMIN }
    val activeSellers = vm.users.count { it.role == Role.SELLER && it.active }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Column(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Dasbor Administrator",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "${user.fullName} · @${user.username}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                    TextButton(onClick = { vm.logout() }) {
                        Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Total pengguna", "${vm.users.size}", modifier = Modifier.weight(1f))
                    StatCard("Pembeli", "$buyers", accent = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Penjual aktif", "$activeSellers / $sellers", accent = Color(0xFF1565C0), modifier = Modifier.weight(1f))
                    StatCard("Administrator", "$admins", accent = Color(0xFFC62828), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Total pesanan", "${vm.orders.size}", modifier = Modifier.weight(1f))
                    StatCard("Pendapatan selesai", rupiah(vm.totalRevenue()), modifier = Modifier.weight(1f))
                }
            }
        }

        if (pending.isNotEmpty()) {
            item { SectionTitle("Menunggu persetujuan (${pending.size})") }
            items(pending, key = { it.id }) { seller ->
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(seller.storeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("@${seller.username} · ${seller.fullName}", color = TextGray, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { vm.approveSeller(seller.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) { Text("Setujui", fontSize = 12.sp) }
                }
            }
        }

        item {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Kelola akun pembeli, penjual, dan administrator lewat tab Pengguna. " +
                        "Setiap penjual yang mendaftar harus disetujui sebelum tokonya tayang.",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AdminUsers(vm: AppViewModel) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<Role?>(null) }
    var deleteTarget by remember { mutableStateOf<User?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    val list = vm.users.filter { u ->
        (filter == null || u.role == filter) &&
            (query.isBlank() ||
                u.fullName.contains(query, true) ||
                u.username.contains(query, true) ||
                u.storeName.contains(query, true))
    }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Column(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text("Manajemen Pengguna", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${vm.users.size} akun terdaftar", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
        }

        item {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Cari nama, username, atau toko") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filter == null,
                            onClick = { filter = null },
                            label = { Text("Semua") }
                        )
                    }
                    items(Role.values().toList()) { role ->
                        FilterChip(
                            selected = filter == role,
                            onClick = { filter = role },
                            label = { Text(role.label) }
                        )
                    }
                }
                message?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = Color(0xFFC62828), fontSize = 12.sp)
                }
            }
        }

        if (list.isEmpty()) {
            item { EmptyState("🔍", "Tidak ada pengguna yang cocok") }
        }

        items(list, key = { it.id }) { u ->
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFFFE3D9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        u.fullName.take(1).uppercase(),
                        color = Orange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        RoleBadge(u.role)
                    }
                    Text("@${u.username}", color = TextGray, fontSize = 12.sp)
                    if (u.storeName.isNotBlank()) {
                        Text(u.storeName, color = TextGray, fontSize = 12.sp)
                    }
                    Text(
                        if (u.active) "Aktif" else "Menunggu persetujuan / diblokir",
                        color = if (u.active) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (u.role == Role.SELLER && !u.active) {
                        TextButton(onClick = { vm.approveSeller(u.id) }) {
                            Text("Setujui", fontSize = 12.sp)
                        }
                    }
                    if (u.id != vm.session?.id) {
                        TextButton(onClick = {
                            val err = vm.setUserActive(u.id, !u.active)
                            message = err
                        }) {
                            Text(if (u.active) "Blokir" else "Aktifkan", fontSize = 12.sp)
                        }
                        TextButton(onClick = { deleteTarget = u }) {
                            Text("Hapus", fontSize = 12.sp, color = Color(0xFFC62828))
                        }
                    } else {
                        Text("Akun Anda", color = TextGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    val target = deleteTarget
    if (target != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Hapus pengguna?") },
            text = {
                Text(
                    "Akun ${target.fullName} (@${target.username}) akan dihapus" +
                        if (target.role == Role.SELLER) " beserta tokonya." else "."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    message = vm.deleteUser(target.id)
                    deleteTarget = null
                }) { Text("Hapus", color = Color(0xFFC62828)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun AdminOrders(vm: AppViewModel) {
    val list = vm.adminOrders()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Column(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text("Semua Pesanan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${list.size} pesanan tercatat", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
        }

        if (list.isEmpty()) {
            item { EmptyState("🧾", "Belum ada pesanan") }
        }

        items(list, key = { it.id }) { o -> AdminOrderCard(o, onCancel = { vm.forceCancelOrder(o.id) }) }
    }
}

@Composable
private fun AdminOrderCard(order: Order, onCancel: () -> Unit) {
    Column(
        Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#${order.id} · ${order.restaurantName}", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            StatusChip(order.status)
        }
        Spacer(Modifier.height(4.dp))
        Text("Pembeli: ${order.buyerName}", color = TextGray, fontSize = 12.sp)
        Text(order.summary, color = TextGray, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(rupiah(order.total), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            if (order.status != OrderStatus.DONE && order.status != OrderStatus.CANCELLED) {
                TextButton(onClick = onCancel) {
                    Text("Batalkan", color = Color(0xFFC62828), fontSize = 12.sp)
                }
            }
        }
    }
}
