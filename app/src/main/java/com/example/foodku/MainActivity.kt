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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.foodku.data.SampleData
import com.example.foodku.ui.*
import com.example.foodku.ui.theme.FoodKuTheme
import com.example.foodku.ui.theme.Orange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FoodKuTheme { FoodKuApp() } }
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun FoodKuApp(vm: AppViewModel = viewModel()) {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val tabs = listOf(
        Tab("home", "Beranda", Icons.Default.Home),
        Tab("orders", "Pesanan", Icons.Default.Receipt),
        Tab("profile", "Akun", Icons.Default.Person)
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (route in tabs.map { it.route }) {
                NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
                    tabs.forEach { t ->
                        NavigationBarItem(
                            selected = route == t.route,
                            onClick = {
                                nav.navigate(t.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(t.icon, null) },
                            label = { Text(t.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Orange, selectedTextColor = Orange,
                                indicatorColor = com.example.foodku.ui.theme.OrangeSoft
                            )
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(pad)) {
            composable("home") {
                HomeScreen(vm.count, { nav.navigate("restaurant/$it") }, { nav.navigate("cart") })
            }
            composable("orders") { OrdersScreen(vm.orders) }
            composable("profile") { ProfileScreen() }
            composable(
                "restaurant/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: 1
                RestaurantScreen(
                    SampleData.restaurantById(id), vm,
                    onBack = { nav.popBackStack() }, onCart = { nav.navigate("cart") }
                )
            }
            composable("cart") {
                CartScreen(vm, onBack = { nav.popBackStack() }, onOrdered = {
                    nav.navigate("orders") { popUpTo("home") }
                })
            }
        }
    }
}
