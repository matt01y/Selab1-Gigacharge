package be.ugent.gigacharge.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.ugent.gigacharge.features.location.LocationRoute
import be.ugent.gigacharge.features.main.MainRoute
import be.ugent.gigacharge.features.register.RegisterScreen
import be.ugent.gigacharge.features.splash.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.SPLASH_SCREEN) {
        composable(Destinations.SPLASH_SCREEN) {
            SplashScreen(openAndPopUp = { s : String -> navController.navigate(s) })
        }

        composable(Destinations.REGISTER_SCREEN){
            RegisterScreen(openAndPopUp = { navController.navigate(Destinations.MAIN) })
        }

        composable(Destinations.MAIN) {
            MainRoute(onLocationSelectClick = { navController.navigate(Destinations.LOCATION_SELECTION) }, hiltViewModel(), hiltViewModel(), hiltViewModel())
        }
        composable(Destinations.LOCATION_SELECTION) {
            LocationRoute(onBackArrowClick = { navController.navigateUp() }, hiltViewModel())
        }
    }
}