package com.example.foodku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.data.OrderStatus
import com.example.foodku.data.Role
import com.example.foodku.ui.theme.Orange
import com.example.foodku.ui.theme.OrangeSoft
import com.example.foodku.ui.theme.Surface
import com.example.foodku.ui.theme.TextGray

@Composable
fun EmojiBox(emoji: String, size: Int, modifier: Modifier = Modifier) {
    Box(
        modifier.size(size.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFE3D9)),
        contentAlignment = Alignment.Center
    ) { Text(emoji, fontSize = (size * 0.5).sp) }
}

@Composable
fun QtyStepper(qty: Int, onAdd: () -> Unit, onRemove: () -> Unit) {
    if (qty == 0) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(Orange).clickable { onAdd() },
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(Surface).clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Remove, null, tint = Orange, modifier = Modifier.size(18.dp)) }
            Text("$qty", fontWeight = FontWeight.Bold)
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(Orange).clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        }
    }
}

@Composable
fun RoleBadge(role: Role, modifier: Modifier = Modifier) {
    val (bg, fg) = when (role) {
        Role.ADMIN -> Color(0xFFFFE0E0) to Color(0xFFC62828)
        Role.SELLER -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        Role.BUYER -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    }
    Text(
        role.label,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.clip(RoundedCornerShape(6.dp)).background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        OrderStatus.WAITING -> Color(0xFFFFF4E5) to Color(0xFFB26A00)
        OrderStatus.PROCESSING -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        OrderStatus.DELIVERING -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)
        OrderStatus.DONE -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        else -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }
    Text(
        status,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.clip(RoundedCornerShape(6.dp)).background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun StatCard(label: String, value: String, accent: Color = Orange, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Color.White).padding(14.dp)) {
        Text(label, color = TextGray, fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
fun EmptyState(emoji: String, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 54.sp)
        Spacer(Modifier.height(10.dp))
        Text(text, color = TextGray, fontSize = 14.sp)
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(text, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
}

@Composable
fun DividerLine(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEEEEEE)))
}

@Composable
fun BottomTab(
    index: Int,
    current: Int,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = current == index,
        onClick = onClick,
        icon = { Icon(icon, null) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Orange,
            selectedTextColor = Orange,
            indicatorColor = OrangeSoft
        )
    )
}
