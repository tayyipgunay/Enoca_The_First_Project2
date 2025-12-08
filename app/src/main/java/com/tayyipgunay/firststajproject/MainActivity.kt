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
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.tayyipgunay.firststajproject.core.util.Constants
import com.tayyipgunay.firststajproject.presentation.add.AddProductContract
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListScreen2
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListViewModel2


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AppNavHost()
                }
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
            val viewModel: ProductListViewModel2 = hiltViewModel()

            ProductListScreen2(
                state = viewModel.state.collectAsState().value,
                lazyPagingItems = viewModel.products.collectAsLazyPagingItems(),
                onIntent = viewModel::onIntent,
                effectFlow = viewModel.effect,
                navController = navController
            )
        }

        // ---------- Add Product ----------
        composable(Constants.ADD_PRODUCT) {
            val addVm: AddProductViewModel = hiltViewModel()
            val state by addVm.state.collectAsStateWithLifecycle()

            AddProductScreen(
                state = state,
                onIntent = addVm::onIntent,
                onSavedNavigateBack = {
                    navController.popBackStack()
                },
                effect = addVm.effect
            )
        }
    }
}
