package com.tayyipgunay.firststajproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tayyipgunay.firststajproject.presentation.add.AddProductScreen
import com.tayyipgunay.firststajproject.presentation.add.AddProductViewModel
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListScreen
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tayyipgunay.firststajproject.core.util.Constants
import com.tayyipgunay.firststajproject.presentation.add.AddProductContract
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListContract

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AppNavHost()   // navigation yönlendirmeleri burada
                }
            }
        }
    }

    @Composable
    fun AppNavHost(
        startDestination: String = Constants.PRODUCTS
    ) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // ---------- Product List ----------
            composable(Constants.PRODUCTS) {
                val pr: ProductListViewModel = hiltViewModel()
                val state by pr.state.collectAsStateWithLifecycle()

                ProductListScreen(
                    state = state,
                    onIntent = pr::onIntent,
                    onAddClick = {
                        navController.navigate(Constants.ADD_PRODUCT)
                    },
                    effect = pr.effect,
                    onNavigateAdd = {

                    },
                    onNavigateDetail = {

                    }

                    //  uiEvents = vm.uiEvent
                )
            }

            // ---------- Add Product ----------
            composable(Constants.ADD_PRODUCT) {
                val Advm: AddProductViewModel = hiltViewModel()
                val state by Advm.state.collectAsStateWithLifecycle()
                AddProductScreen(
                    state = state,
                    onIntent = {
                        Advm.onIntent(it)
                    },
                    onSavedNavigateBack = { navController.popBackStack() },
                    effect = Advm.effect
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {

    }
}
