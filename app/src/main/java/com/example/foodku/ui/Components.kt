package com.example.foodku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.ui.theme.Orange

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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(Color.White)
                    .clickable { onRemove() },
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
