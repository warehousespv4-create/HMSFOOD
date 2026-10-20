package com.example.foodku.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodku.data.Role
import com.example.foodku.ui.AppViewModel
import com.example.foodku.ui.theme.Orange
import com.example.foodku.ui.theme.OrangeLight
import com.example.foodku.ui.theme.Surface
import com.example.foodku.ui.theme.TextGray

@Composable
fun AuthFlow(vm: AppViewModel) {
    var registerRole by remember { mutableStateOf<Role?>(null) }
    val role = registerRole
    if (role == null) {
        LoginScreen(vm, onRegister = { registerRole = it })
    } else {
        RegisterScreen(vm, role = role, onBack = { registerRole = null })
    }
}

@Composable
fun LoginScreen(vm: AppViewModel, onRegister: (Role) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AuthHeader(
            title = "Masuk",
            subtitle = "Pembeli, penjual, dan administrator masuk lewat sini."
        )
        Column(Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; error = null },
                label = { Text("Username") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                MessageBox(it, isError = true)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { error = vm.login(username, password) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(26.dp))
            Text(
                "Belum punya akun?",
                fontSize = 13.sp,
                color = TextGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onRegister(Role.BUYER) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Daftar sebagai Pembeli") }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { onRegister(Role.SELLER) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Daftar sebagai Penjual") }

            Spacer(Modifier.height(24.dp))
            MessageBox(
                "Akun administrator bawaan:\nusername admin · password Admin123!\nSegera ganti password setelah masuk.",
                isError = false
            )
        }
    }
}

@Composable
fun RegisterScreen(vm: AppViewModel, role: Role, onBack: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    val isSeller = role == Role.SELLER

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AuthHeader(
            title = "Daftar ${role.label}",
            subtitle = if (isSeller) {
                "Buat akun toko, lalu tunggu persetujuan administrator."
            } else {
                "Buat akun untuk mulai memesan makanan."
            }
        )

        Column(Modifier.padding(20.dp)) {
            if (success != null) {
                MessageBox(success!!, isError = false)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Kembali ke Halaman Masuk", fontWeight = FontWeight.Bold) }
                return@Column
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; error = null },
                label = { Text("Nama lengkap") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it; error = null },
                label = { Text("Username") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            if (isSeller) {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it; error = null },
                    label = { Text("Nama toko") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text("Password (min. 6 karakter)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it; error = null },
                label = { Text("Konfirmasi password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                MessageBox(it, isError = true)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val err = vm.register(
                        fullName = fullName,
                        username = username,
                        password = password,
                        confirm = confirm,
                        role = role,
                        storeName = storeName
                    )
                    if (err == null) {
                        success = if (isSeller) {
                            "Pendaftaran berhasil. Akun penjual Anda sedang menunggu persetujuan administrator."
                        } else {
                            "Akun pembeli berhasil dibuat. Silakan masuk dengan username dan password Anda."
                        }
                    } else {
                        error = err
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    if (isSeller) "Daftar Sebagai Penjual" else "Daftar Sebagai Pembeli",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Sudah punya akun? Masuk") }

            Spacer(Modifier.height(20.dp))
            if (isSeller) {
                MessageBox(
                    "Catatan: akun penjual harus disetujui administrator sebelum bisa masuk dan mengelola toko.",
                    isError = false
                )
            }
        }
    }
}

@Composable
private fun AuthHeader(title: String, subtitle: String) {
    Column(
        Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Orange, OrangeLight)))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text("🍔 FoodKu", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
    }
}

@Composable
private fun MessageBox(text: String, isError: Boolean) {
    Text(
        text,
        color = if (isError) Color(0xFFC62828) else Color(0xFF2E7D32),
        fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
            .padding(12.dp)
    )
}
