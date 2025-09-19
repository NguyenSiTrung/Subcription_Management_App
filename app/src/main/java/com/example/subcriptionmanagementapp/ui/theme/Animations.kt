package com.example.subcriptionmanagementapp.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

// Duration constants for animations
object AnimationDurations {
    const val FAST: Int = 200
    const val MEDIUM: Int = 300
    const val SLOW: Int = 500
    const val EXTRA_SLOW: Int = 800
}

// Easing functions
object AppEasing {
    val EaseInOut: Easing = androidx.compose.animation.core.EaseInOut
    val EaseOut: Easing = androidx.compose.animation.core.EaseOut
    val EaseIn: Easing = androidx.compose.animation.core.EaseIn
    val Linear: Easing = androidx.compose.animation.core.LinearEasing
    val Bounce: Easing = androidx.compose.animation.core.FastOutSlowInEasing
}

// Button press animation
@Composable
fun buttonPressAnimation(): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale: Float by
            animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec =
                            spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                            ),
                    label = "buttonPress"
            )

    return Modifier.scale(scale)
}

// Card hover animation
@Composable
fun cardHoverAnimation(): Modifier {
    var isHovered by remember { mutableStateOf(false) }

    val elevation: androidx.compose.ui.unit.Dp by
            animateDpAsState(
                    targetValue = if (isHovered) 8.dp else 2.dp,
                    animationSpec =
                            tween(
                                    durationMillis = AnimationDurations.FAST,
                                    easing = AppEasing.EaseOut
                            ),
                    label = "cardElevation"
            )

    val scale: Float by
            animateFloatAsState(
                    targetValue = if (isHovered) 1.02f else 1f,
                    animationSpec =
                            tween(
                                    durationMillis = AnimationDurations.FAST,
                                    easing = AppEasing.EaseOut
                            ),
                    label = "cardScale"
            )

    return Modifier.scale(scale)
}

// Pulse animation for important elements
@Composable
fun pulseAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition()

    val scale: Float by
            infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation =
                                            tween(
                                                    durationMillis = 1000,
                                                    easing = AppEasing.EaseInOut
                                            ),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "pulseScale"
            )

    val alpha: Float by
            infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.7f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation =
                                            tween(
                                                    durationMillis = 1000,
                                                    easing = AppEasing.EaseInOut
                                            ),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "pulseAlpha"
            )

    return Modifier.scale(scale).graphicsLayer { this.alpha = alpha }
}

// Shimmer animation for loading states
@Composable
fun shimmerAnimation(): Modifier {
    val shimmerColors =
            listOf(
                    Color.LightGray.copy(alpha = 0.6f),
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 0.6f),
            )

    val transition = rememberInfiniteTransition()
    val translateAnim: Float by
            transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1000f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(durationMillis = 1000, easing = LinearEasing),
                                    RepeatMode.Restart
                            ),
                    label = "shimmer"
            )

    return Modifier.drawWithContent {
        val brush =
                Brush.linearGradient(
                        colors = shimmerColors,
                        start = androidx.compose.ui.geometry.Offset(translateAnim - 1000f, 0f),
                        end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
                )

        drawContent()
        drawRect(brush = brush)
    }
}

// Floating action button animation
@Composable
fun fabAnimation(isVisible: Boolean, onClick: () -> Unit): @Composable () -> Unit {
    val scale: Float by
            animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec =
                            spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                            ),
                    label = "fabScale"
            )

    val rotation: Float by
            animateFloatAsState(
                    targetValue = if (isVisible) 0f else -45f,
                    animationSpec =
                            tween(
                                    durationMillis = AnimationDurations.MEDIUM,
                                    easing = AppEasing.EaseOut
                            ),
                    label = "fabRotation"
            )

    return {
        FloatingActionButton(
                onClick = onClick,
                modifier = Modifier.scale(scale),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.rotate(rotation)
            )
        }
    }
}

// Custom animation specs
object AppAnimationSpec {
    val buttonPress: SpringSpec<Float> =
            spring<Float>(
                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
            )

    val cardHover: TweenSpec<Float> =
            tween<Float>(durationMillis = AnimationDurations.FAST, easing = AppEasing.EaseOut)

    val fadeInOut: TweenSpec<Float> =
            tween<Float>(durationMillis = AnimationDurations.MEDIUM, easing = AppEasing.EaseInOut)
}
