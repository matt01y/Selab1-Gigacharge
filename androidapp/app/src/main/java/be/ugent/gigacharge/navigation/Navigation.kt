package be.ugent.gigacharge.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.ugent.gigacharge.features.location.LocationRoute
import be.ugent.gigacharge.features.main.MainRoute
import be.ugent.gigacharge.features.register.RegisterRoute
import be.ugent.gigacharge.features.splash.SplashScreen

@Composable
fun Navigation(finishApp : () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.SPLASH_SCREEN) {
        composable(Destinations.SPLASH_SCREEN) {
            SplashScreen(openAndPopUp = { s: String -> navController.navigate(s) })
        }

        composable(Destinations.REGISTER_SCREEN) {
            RegisterRoute(
                openAndPopUp = { navController.navigate(Destinations.MAIN) },
                finishApp = finishApp,
                hiltViewModel()
            )
        }

        composable(Destinations.MAIN) {
            MainRoute(
                onRegisterSelectClick = { navController.navigate(Destinations.REGISTER_SCREEN) },
                onLocationSelectClick = { navController.navigate(Destinations.LOCATION_SELECTION) },
                finishApp = finishApp,
                hiltViewModel()
            )
        }
        composable(Destinations.LOCATION_SELECTION) {
            LocationRoute(onBackArrowClick = { navController.navigateUp() }, hiltViewModel())
        }
    }
}