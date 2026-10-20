package com.example.foodku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodku.data.Role
import com.example.foodku.ui.AppViewModel
import com.example.foodku.ui.BottomTab
import com.example.foodku.ui.CartScreen
import com.example.foodku.ui.ChangePasswordDialog
import com.example.foodku.ui.EmptyState
import com.example.foodku.ui.HomeScreen
import com.example.foodku.ui.OrdersScreen
import com.example.foodku.ui.ProfileScreen
import com.example.foodku.ui.RestaurantScreen
import com.example.foodku.ui.admin.AdminApp
import com.example.foodku.ui.auth.AuthFlow
import com.example.foodku.ui.seller.SellerApp
import com.example.foodku.ui.theme.FoodKuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodKuTheme {
                val vm: AppViewModel = viewModel()
                RootScreen(vm)
            }
        }
    }
}

@Composable
private fun RootScreen(vm: AppViewModel) {
    val session = vm.session
    if (session == null) {
        AuthFlow(vm)
    } else {
        when (session.role) {
            Role.BUYER -> BuyerApp(vm)
            Role.SELLER -> SellerApp(vm)
            Role.ADMIN -> AdminApp(vm)
        }
    }
}

private data class BuyerTab(val route: String, val label: String, val icon: ImageVector)

@Composable
private fun BuyerApp(vm: AppViewModel) {
    val user = vm.session ?: return
    var showPassword by remember { mutableStateOf(false) }

    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val tabs = listOf(
        BuyerTab("home", "Beranda", Icons.Default.Home),
        BuyerTab("orders", "Pesanan", Icons.Default.Receipt),
        BuyerTab("profile", "Akun", Icons.Default.Person)
    )
    val currentIndex = tabs.indexOfFirst { it.route == route }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentIndex >= 0) {
                NavigationBar(containerColor = Color.White) {
                    tabs.forEachIndexed { index, tab ->
                        BottomTab(index, currentIndex, tab.label, tab.icon) {
                            nav.navigate(tab.route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(pad)) {
            composable("home") {
                HomeScreen(
                    restaurants = vm.visibleRestaurants(),
                    cartCount = vm.count,
                    onRestaurant = { nav.navigate("restaurant/$it") },
                    onCart = { nav.navigate("cart") }
                )
            }

            composable("orders") {
                OrdersScreen(orders = vm.myOrders(), onCancel = { vm.cancelOrder(it) })
            }

            composable("profile") {
                ProfileScreen(
                    user = user,
                    onLogout = { vm.logout() },
                    onChangePassword = { showPassword = true }
                )
            }

            composable(
                "restaurant/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: 1
                val resto = vm.restaurantById(id)
                if (resto == null) {
                    EmptyState("🍽️", "Restoran tidak ditemukan")
                } else {
                    RestaurantScreen(
                        restaurant = resto,
                        vm = vm,
                        onBack = { nav.popBackStack() },
                        onCart = { nav.navigate("cart") }
                    )
                }
            }

            composable("cart") {
                CartScreen(
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onOrdered = { nav.navigate("orders") { popUpTo("home") } }
                )
            }
        }
    }

    if (showPassword) ChangePasswordDialog(vm) { showPassword = false }
}
