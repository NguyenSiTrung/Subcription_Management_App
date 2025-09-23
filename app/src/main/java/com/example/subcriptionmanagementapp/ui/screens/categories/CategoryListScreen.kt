package com.example.subcriptionmanagementapp.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.ui.components.CompactTopBar
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.components.NoCategoriesEmptyState
import com.example.subcriptionmanagementapp.ui.theme.EducationColor
import com.example.subcriptionmanagementapp.ui.theme.FinanceColor
import com.example.subcriptionmanagementapp.ui.theme.GamingColor
import com.example.subcriptionmanagementapp.ui.theme.HealthColor
import com.example.subcriptionmanagementapp.ui.theme.MusicColor
import com.example.subcriptionmanagementapp.ui.theme.NewsColor
import com.example.subcriptionmanagementapp.ui.theme.OtherColor
import com.example.subcriptionmanagementapp.ui.theme.SoftwareColor
import com.example.subcriptionmanagementapp.ui.theme.StreamingColor
import com.example.subcriptionmanagementapp.ui.viewmodel.CategoryViewModel

@Composable
fun CategoryListScreen(
        navController: NavController,
        viewModel: CategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(Unit) { viewModel.loadCategories() }

    Scaffold(
            topBar = {
                CompactTopBar(
                        title = stringResource(R.string.categories),
                        navController = navController
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                        onClick = { showCreateDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_category),
                            modifier = Modifier.size(24.dp)
                    )
                }
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(message = error!!) { viewModel.loadCategories() }
                categories.isEmpty() -> NoCategoriesEmptyState { showCreateDialog = true }
                else ->
                        CategoryGridContent(
                                categories = categories,
                                onCategoryClick = { categoryId ->
                                    // Navigate to category detail or filter subscriptions by
                                    // category
                                },
                                onCategoryEdit = { category -> showEditDialog = category },
                                onCategoryDelete = { category ->
                                    viewModel.deleteCategory(category)
                                }
                        )
            }
        }

        // Create Category Dialog
        if (showCreateDialog) {
            CreateCategoryDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreate = { name, keywords ->
                        viewModel.createCategory(name, keywords)
                        showCreateDialog = false
                    }
            )
        }

        // Edit Category Dialog
        showEditDialog?.let { category ->
            EditCategoryDialog(
                    category = category,
                    onDismiss = { showEditDialog = null },
                    onUpdate = { name, keywords ->
                        viewModel.updateCategory(category.id, name, keywords)
                        showEditDialog = null
                    }
            )
        }
    }
}

@Composable
fun CategoryGridContent(
        categories: List<Category>,
        onCategoryClick: (Long) -> Unit,
        onCategoryEdit: (Category) -> Unit,
        onCategoryDelete: (Category) -> Unit
) {
    LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 260.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category.id) },
                    onEdit = { onCategoryEdit(category) },
                    onDelete = { onCategoryDelete(category) }
            )
        }
    }
}

@Composable
fun CategoryCard(
        category: Category,
        onClick: () -> Unit,
        onEdit: () -> Unit,
        onDelete: () -> Unit
) {
    val categoryColor =
            when (category.name.lowercase()) {
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

    val categoryIcon =
            when (category.name.lowercase()) {
                "streaming" -> Icons.Outlined.Category
                "music" -> Icons.Outlined.Category
                "software" -> Icons.Outlined.Category
                "gaming" -> Icons.Outlined.Category
                "news" -> Icons.Outlined.Category
                "education" -> Icons.Outlined.Category
                "health" -> Icons.Outlined.Category
                "finance" -> Icons.Outlined.Category
                else -> Icons.Outlined.Category
            }

    var showMenu by remember { mutableStateOf(false) }

    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            onClick = onClick,
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                    ),
            elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
            )
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier =
                                Modifier.size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                                brush =
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                categoryColor.copy(alpha = 0.9f),
                                                                                categoryColor.copy(alpha = 0.4f)
                                                                        )
                                                        )
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                    Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                    )
                    if (!category.keywords.isNullOrBlank()) {
                        Text(
                                text = category.keywords.trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                        )
                    }
                }

                if (!category.isPredefined) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.more_options),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                    text = {
                                        Text(
                                                text = stringResource(R.string.edit),
                                                style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        onEdit()
                                    }
                            )
                            DropdownMenuItem(
                                    text = {
                                        Text(
                                                text = stringResource(R.string.delete),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    }
                            )
                        }
                    }
                }
            }

            if (category.isPredefined) {
                Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                            text = stringResource(R.string.predefined),
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCategoryDialog(onDismiss: () -> Unit, onCreate: (String, String?) -> Unit) {
    var categoryName by remember { mutableStateOf("") }
    var categoryKeywords by remember { mutableStateOf("") }
    val isCreateEnabled = categoryName.trim().isNotEmpty()

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.create_category_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                            value = categoryName,
                            onValueChange = { categoryName = it },
                            label = { Text(stringResource(R.string.category_name_label)) },
                            singleLine = true
                    )
                    OutlinedTextField(
                            value = categoryKeywords,
                            onValueChange = { categoryKeywords = it },
                            label = { Text(stringResource(R.string.category_keywords_label)) },
                            placeholder = {
                                Text(stringResource(R.string.category_keywords_placeholder))
                            },
                            minLines = 1,
                            maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            val trimmedName = categoryName.trim()
                            val trimmedKeywords = categoryKeywords.trim().takeIf { it.isNotEmpty() }
                            onCreate(trimmedName, trimmedKeywords)
                        },
                        enabled = isCreateEnabled
                ) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            }
    )
}

@Composable
fun EditCategoryDialog(
        category: Category,
        onDismiss: () -> Unit,
        onUpdate: (String, String?) -> Unit
) {
    var categoryName by remember { mutableStateOf(category.name) }
    var categoryKeywords by remember { mutableStateOf(category.keywords ?: "") }
    val isUpdateEnabled = categoryName.trim().isNotEmpty() && categoryName.trim() != category.name

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.edit_category_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                            value = categoryName,
                            onValueChange = { categoryName = it },
                            label = { Text(stringResource(R.string.category_name_label)) },
                            singleLine = true
                    )
                    OutlinedTextField(
                            value = categoryKeywords,
                            onValueChange = { categoryKeywords = it },
                            label = { Text(stringResource(R.string.category_keywords_label)) },
                            placeholder = {
                                Text(stringResource(R.string.category_keywords_placeholder))
                            },
                            minLines = 1,
                            maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            val trimmedName = categoryName.trim()
                            val trimmedKeywords = categoryKeywords.trim().takeIf { it.isNotEmpty() }
                            onUpdate(trimmedName, trimmedKeywords)
                        },
                        enabled = isUpdateEnabled
                ) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            }
    )
}
