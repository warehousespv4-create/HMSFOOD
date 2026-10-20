package com.example.foodku.ui.seller

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.example.foodku.data.MenuItem
import com.example.foodku.data.Order
import com.example.foodku.data.OrderStatus
import com.example.foodku.data.Restaurant
import com.example.foodku.data.User
import com.example.foodku.data.rupiah
import com.example.foodku.ui.AppViewModel
import com.example.foodku.ui.BottomTab
import com.example.foodku.ui.ChangePasswordDialog
import com.example.foodku.ui.EmojiBox
import com.example.foodku.ui.EmptyState
import com.example.foodku.ui.StatCard
import com.example.foodku.ui.StatusChip
import com.example.foodku.ui.theme.Orange
import com.example.foodku.ui.theme.OrangeLight
import com.example.foodku.ui.theme.Surface
import com.example.foodku.ui.theme.TextGray

@Composable
fun SellerApp(vm: AppViewModel) {
    val user = vm.session ?: return
    val resto = vm.myRestaurant()
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Surface,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                BottomTab(0, tab, "Menu", Icons.Default.Home) { tab = 0 }
                BottomTab(1, tab, "Pesanan", Icons.Default.Receipt) { tab = 1 }
                BottomTab(2, tab, "Toko", Icons.Default.Settings) { tab = 2 }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize().background(Surface)) {
            if (resto == null) {
                EmptyState("🏪", "Toko tidak ditemukan. Hubungi administrator.")
            } else {
                when (tab) {
                    0 -> SellerMenu(vm, resto)
                    1 -> SellerOrders(vm)
                    else -> SellerStore(vm, user, resto)
                }
            }
        }
    }
}

@Composable
private fun SellerHeader(title: String, subtitle: String, onLogout: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
        }
        TextButton(onClick = onLogout) {
            Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SellerMenu(vm: AppViewModel, resto: Restaurant) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<MenuItem?>(null) }

    Column(Modifier.fillMaxSize()) {
        SellerHeader(
            title = resto.name,
            subtitle = "Kelola menu · ${resto.menu.size} item",
            onLogout = { vm.logout() }
        )

        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (resto.open) "Toko sedang buka" else "Toko sedang tutup",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    if (resto.open) "Pembeli bisa melihat dan memesan menu Anda"
                    else "Pembeli tidak bisa melihat menu Anda",
                    color = TextGray,
                    fontSize = 11.sp
                )
            }
            Switch(checked = resto.open, onCheckedChange = { vm.setStoreOpen(it) })
        }

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (resto.menu.isEmpty()) {
                item { EmptyState("🍽️", "Belum ada menu.\nTambahkan menu pertama Anda.") }
            }
            items(resto.menu, key = { it.id }) { m ->
                Row(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiBox(m.emoji, 56)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(m.desc, color = TextGray, fontSize = 11.sp, maxLines = 1)
                        Text(rupiah(m.price), color = Orange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    TextButton(onClick = { editing = m; showForm = true }) {
                        Text("Ubah", fontSize = 12.sp)
                    }
                    TextButton(onClick = { vm.deleteMenuItem(m.id) }) {
                        Text("Hapus", fontSize = 12.sp, color = Color(0xFFC62828))
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth().background(Color.White).padding(12.dp)) {
            Button(
                onClick = { editing = null; showForm = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) { Text("+ Tambah Menu", fontWeight = FontWeight.Bold) }
        }
    }

    if (showForm) {
        MenuEditorDialog(
            initial = editing,
            onDismiss = { showForm = false; editing = null },
            onSave = { name, desc, price, emoji ->
                vm.saveMenuItem(editing?.id, name, desc, price, emoji)
            }
        )
    }
}

@Composable
private fun MenuEditorDialog(
    initial: MenuItem?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String) -> String?
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var desc by remember { mutableStateOf(initial?.desc ?: "") }
    var price by remember { mutableStateOf(initial?.price?.toString() ?: "") }
    var emoji by remember { mutableStateOf(initial?.emoji ?: "🍽️") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Tambah Menu" else "Ubah Menu", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Nama menu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it; error = null },
                    label = { Text("Deskripsi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { ch -> ch.isDigit() }; error = null },
                    label = { Text("Harga (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it; error = null },
                    label = { Text("Emoji menu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = Color(0xFFC62828), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = price.toIntOrNull() ?: 0
                val err = onSave(name, desc, p, emoji)
                if (err == null) onDismiss() else error = err
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
private fun SellerOrders(vm: AppViewModel) {
    val list = vm.sellerOrders()

    Column(Modifier.fillMaxSize()) {
        SellerHeader(
            title = "Pesanan Masuk",
            subtitle = "${list.size} pesanan",
            onLogout = { vm.logout() }
        )

        if (list.isEmpty()) {
            EmptyState("🧾", "Belum ada pesanan masuk")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list, key = { it.id }) { o -> SellerOrderCard(o, vm) }
            }
        }
    }
}

@Composable
private fun SellerOrderCard(order: Order, vm: AppViewModel) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#${order.id} · ${order.buyerName}", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            StatusChip(order.status)
        }
        Spacer(Modifier.height(4.dp))
        Text(order.summary, color = TextGray, fontSize = 12.sp)
        Text(rupiah(order.total), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            when (order.status) {
                OrderStatus.WAITING, OrderStatus.PROCESSING, OrderStatus.DELIVERING -> {
                    val label = when (order.status) {
                        OrderStatus.WAITING -> "Terima & Proses"
                        OrderStatus.PROCESSING -> "Kirim"
                        else -> "Selesai"
                    }
                    Button(
                        onClick = { vm.advanceOrder(order.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) { Text(label, fontSize = 12.sp) }

                    if (order.status != OrderStatus.DELIVERING) {
                        OutlinedButton(
                            onClick = { vm.rejectOrder(order.id) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) { Text("Tolak", fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerStore(vm: AppViewModel, user: User, resto: Restaurant) {
    var showPassword by remember { mutableStateOf(false) }
    val myOrders = vm.sellerOrders()
    val revenue = myOrders.filter { it.status == OrderStatus.DONE }.sumOf { it.total }

    Column(Modifier.fillMaxSize()) {
        SellerHeader(
            title = "Toko Saya",
            subtitle = "@${user.username}",
            onLogout = { vm.logout() }
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(16.dp)
                ) {
                    Text(resto.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Pemilik: ${user.fullName}", color = TextGray, fontSize = 12.sp)
                    Text("Kategori: ${resto.category}", color = TextGray, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (resto.open) "Status: Buka" else "Status: Tutup",
                        color = if (resto.open) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Menu", "${resto.menu.size}", modifier = Modifier.weight(1f))
                    StatCard("Pesanan", "${myOrders.size}", accent = Color(0xFF1565C0), modifier = Modifier.weight(1f))
                }
            }
            item {
                StatCard("Pendapatan selesai", rupiah(revenue), accent = Color(0xFF2E7D32), modifier = Modifier.fillMaxWidth())
            }
            item {
                OutlinedButton(
                    onClick = { showPassword = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Ganti Password") }
            }
            item {
                OutlinedButton(
                    onClick = { vm.logout() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Keluar", color = Color(0xFFC62828)) }
            }
        }
    }

    if (showPassword) ChangePasswordDialog(vm) { showPassword = false }
}
