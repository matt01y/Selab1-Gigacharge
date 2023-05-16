package be.ugent.gigacharge

import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.ugent.gigacharge.features.main.MainScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterialApi::class)
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(GigaChargeActivity::class.java)

    @OptIn(ExperimentalMaterialApi::class)
    @Test
    fun testWhenBackButtonPressedFinishAppShouldBeCalled() {
        val finishApp = mock<() -> Unit>()
        activityScenarioRule.scenario.onActivity { activity ->
            activity.setContent {
                MainScreen(
                    onRegisterSelectClick = { /*TODO*/ },
                    onLocationSelectClick = { /*TODO*/ },
                    finishApp = finishApp,
                    viewModel = hiltViewModel()
                )
            }
        }
        pressBack()
        verify(finishApp)
    }
}