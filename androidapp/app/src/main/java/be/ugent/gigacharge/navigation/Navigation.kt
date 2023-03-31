package be.ugent.gigacharge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.ugent.gigacharge.features.LocationRoute
import be.ugent.gigacharge.features.MainRoute

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.MAIN) {
        composable(Destinations.MAIN) {
            MainRoute(onLocationSelectClick = { navController.navigate(Destinations.LOCATION_SELECTION) })
        }
        composable(Destinations.LOCATION_SELECTION) {
            LocationRoute(onBackArrowClick = { navController.navigateUp() })
        }
    }
}