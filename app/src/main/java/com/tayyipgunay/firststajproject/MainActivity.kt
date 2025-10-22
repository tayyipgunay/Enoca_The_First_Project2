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
                val vm: ProductListViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()

                ProductListScreen(
                    state = state,
                    onIntent = vm::onIntent,
                    onAddClick = {
                        navController.navigate(Constants.ADD_PRODUCT)
                    },
                    events = vm.event,
                    uiEvents = vm.uiEvent
                )
            }

            // ---------- Add Product ----------
            composable(Constants.ADD_PRODUCT) {
                val vm: AddProductViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()

                AddProductScreen(
                    state = state,
                    onIntent = vm::onIntent,
                    onSavedNavigateBack = {
                        // Add tamamlandıysa listeye geri dön
                        navController.popBackStack()
                        // (İstersen listeyi yenilemek için: vmList.onIntent(ProductListIntent.Load))
                    }
                    ,
                    events = vm.event,
                    uiEvents = vm.uiEvent
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {

    }
}
