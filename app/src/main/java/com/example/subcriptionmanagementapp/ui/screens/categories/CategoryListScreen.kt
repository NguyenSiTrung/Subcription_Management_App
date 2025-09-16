package com.example.subcriptionmanagementapp.ui.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.EmptyState
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.components.NoCategoriesEmptyState
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.ui.viewmodel.CategoryViewModel

@Composable
fun CategoryListScreen(
    navController: NavController,
    viewModel: CategoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.categories),
                navController = navController,
                currentRoute = Screen.CategoryList.route,
                onAddClick = {
                    // Navigate to add category screen
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(message = error!!) {
                    viewModel.loadCategories()
                }
                categories.isEmpty() -> NoCategoriesEmptyState {
                    // Navigate to add category screen
                }
                else -> CategoryListContent(
                    categories = categories,
                    onCategoryClick = { categoryId ->
                        // Navigate to category detail or filter subscriptions by category
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryListContent(
    categories: List<Category>,
    onCategoryClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryListItem(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
fun CategoryListItem(
    category: Category,
    onClick: () -> Unit
) {
    val categoryColor = when (category.name.lowercase()) {
        "streaming" -> StreamingColor
        "music" -> MusicColor
        "software" -> SoftwareColor
        "gaming" -> GamingColor
        "news" -> NewsColor
        "education" -> EducationColor
        "health" -> HealthColor
        "finance" -> FinanceColor
        else -> OtherColor
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 12.dp)
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = categoryColor)
                    }
                }
                
                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (category.description != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            if (category.isPredefined) {
                Text(
                    text = stringResource(R.string.predefined),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}