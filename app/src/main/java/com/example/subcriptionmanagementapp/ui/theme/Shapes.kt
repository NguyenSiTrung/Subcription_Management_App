package com.example.subcriptionmanagementapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes =
        Shapes(
                extraSmall = RoundedCornerShape(4.dp),
                small = RoundedCornerShape(8.dp),
                medium = RoundedCornerShape(12.dp),
                large = RoundedCornerShape(16.dp),
                extraLarge = RoundedCornerShape(24.dp)
        )

// Additional custom shapes for specific components
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(20.dp)
val DialogShape = RoundedCornerShape(24.dp)
val BottomSheetShape =
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp)

// Modern shapes for enhanced UI components
val ModernCardShape = RoundedCornerShape(20.dp)
val ModernFabShape = RoundedCornerShape(16.dp)
val ModernBadgeShape = RoundedCornerShape(8.dp)
val ModernSearchBarShape = RoundedCornerShape(24.dp)
val ModernFilterChipShape = RoundedCornerShape(20.dp)
val ModernProgressShape = RoundedCornerShape(12.dp)
val ModernSliderShape = RoundedCornerShape(8.dp)
val ModernSwitchShape = RoundedCornerShape(16.dp)
val ModernCheckboxShape = RoundedCornerShape(4.dp)
val ModernRadioShape = RoundedCornerShape(12.dp)

// Premium shapes for special components
val PremiumCardShape = RoundedCornerShape(24.dp)
val PremiumDialogShape = RoundedCornerShape(28.dp)
val PremiumBottomSheetShape = 
        RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
val PremiumFabShape = RoundedCornerShape(20.dp)
val PremiumBadgeShape = RoundedCornerShape(12.dp)

// Shapes for modern list items
val ListItemShape = RoundedCornerShape(16.dp)
val ListHeaderShape = RoundedCornerShape(12.dp)
val ListFooterShape = RoundedCornerShape(12.dp)

// Shapes for status indicators
val StatusIndicatorShape = RoundedCornerShape(8.dp)
val StatusBadgeShape = RoundedCornerShape(12.dp)
val ProgressIndicatorShape = RoundedCornerShape(4.dp)

// Shapes for navigation components
val NavItemShape = RoundedCornerShape(12.dp)
val NavDrawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
val BottomNavShape = RoundedCornerShape(20.dp)

// Shapes for input components
val InputFieldShape = RoundedCornerShape(12.dp)
val SearchFieldShape = RoundedCornerShape(24.dp)
val TextAreaShape = RoundedCornerShape(12.dp)
val SelectFieldShape = RoundedCornerShape(12.dp)

// Shapes for buttons
val PrimaryButtonShape = RoundedCornerShape(12.dp)
val SecondaryButtonShape = RoundedCornerShape(12.dp)
val TertiaryButtonShape = RoundedCornerShape(8.dp)
val IconButtonShape = RoundedCornerShape(12.dp)
val TextButtonShape = RoundedCornerShape(8.dp)

// Shapes for chips and tags
val FilterChipShape = RoundedCornerShape(20.dp)
val InputChipShape = RoundedCornerShape(16.dp)
val SuggestionChipShape = RoundedCornerShape(16.dp)
val AssistChipShape = RoundedCornerShape(8.dp)
val CategoryTagShape = RoundedCornerShape(12.dp)
val StatusTagShape = RoundedCornerShape(16.dp)

// Shapes for containers
val ContainerShape = RoundedCornerShape(16.dp)
val SurfaceContainerShape = RoundedCornerShape(12.dp)
val CardContainerShape = RoundedCornerShape(20.dp)
val ModalContainerShape = RoundedCornerShape(24.dp)
val PopupContainerShape = RoundedCornerShape(16.dp)

// Shapes for special effects
val GlowShape = RoundedCornerShape(50) // For glow effects
val PulseShape = RoundedCornerShape(50) // For pulse animations
val RippleShape = RoundedCornerShape(50) // For ripple effects

// Asymmetric shapes for modern design
val AsymmetricCardShape1 = 
        RoundedCornerShape(topStart = 20.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 20.dp)
val AsymmetricCardShape2 = 
        RoundedCornerShape(topStart = 8.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 8.dp)
val AsymmetricDialogShape = 
        RoundedCornerShape(topStart = 28.dp, topEnd = 12.dp, bottomStart = 20.dp, bottomEnd = 12.dp)

// Helper function to create adaptive shapes based on screen size
fun createAdaptiveShape(
    baseSize: Float,
    isSmallScreen: Boolean = false,
    isLargeScreen: Boolean = false
): RoundedCornerShape {
    val multiplier = when {
        isSmallScreen -> 0.8f
        isLargeScreen -> 1.2f
        else -> 1.0f
    }
    return RoundedCornerShape((baseSize * multiplier).dp)
}
