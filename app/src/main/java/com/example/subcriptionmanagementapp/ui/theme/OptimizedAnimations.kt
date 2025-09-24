package com.example.subcriptionmanagementapp.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Optimized animation configurations for high-performance UI components
 * Focus on 60fps smooth animations with minimal computational overhead
 */
object OptimizedAnimationSpecs {

    // Ultra-fast card expansion/collapse animations
    val cardExpandCollapse: TweenSpec<Float> =
            tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
            )

    // Fast icon rotation for expand/collapse
    val iconRotation: SpringSpec<Float> =
            spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
            )

    // Quick elevation changes
    val cardElevation: TweenSpec<Dp> =
            tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
            )

    // Fast fade animations for content visibility
    val contentFadeIn: TweenSpec<Float> =
            tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
            )

    val contentFadeOut: TweenSpec<Float> =
            tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
            )

    // Quick expand/shrink animations
    val contentExpand: TweenSpec<Float> =
            tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
            )

    val contentShrink: TweenSpec<Float> =
            tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
            )

    // Minimal bounce for button interactions
    val buttonPress: SpringSpec<Float> =
            spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
            )

    // Smooth but fast hover effects
    val hoverEffect: TweenSpec<Float> =
            tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
            )
}

/**
 * Optimized animation duration constants for consistent timing
 */
object OptimizedAnimationDurations {
    const val INSTANT = 50
    const val QUICK = 150
    const val FAST = 200
    const val MEDIUM = 250
    const val SMOOTH = 300
}

/**
 * Performance-optimized spring configurations
 */
object OptimizedSprings {

    @Composable
    fun rememberQuickSpring(
            targetValue: Float,
            label: String = "quickSpring"
    ): Float {
        return animateFloatAsState(
                targetValue = targetValue,
                animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                ),
                label = label
        ).value
    }

    @Composable
    fun rememberFastTween(
            targetValue: Float,
            duration: Int = OptimizedAnimationDurations.FAST,
            label: String = "fastTween"
    ): Float {
        return animateFloatAsState(
                targetValue = targetValue,
                animationSpec = tween(
                        durationMillis = duration,
                        easing = FastOutSlowInEasing
                ),
                label = label
        ).value
    }

    @Composable
    fun rememberOptimizedElevation(
            targetValue: Dp,
            duration: Int = OptimizedAnimationDurations.QUICK
    ): Dp {
        return animateDpAsState(
                targetValue = targetValue,
                animationSpec = tween(
                        durationMillis = duration,
                        easing = FastOutSlowInEasing
                ),
                label = "optimizedElevation"
        ).value
    }
}

/**
 * Animation utilities for performance-critical components
 */
object AnimationUtils {

    /**
     * Creates a transition that minimizes recompositions during animations
     */
    @Composable
    fun <T> rememberOptimizedTransition(
            targetState: T,
            label: String = "optimizedTransition"
    ): T {
        return remember(targetState) { targetState }
    }

    /**
     * Checks if currently in an animation transition
     */
    fun isInTransition(transitionState: MutableTransitionState<Boolean>): Boolean {
        return !transitionState.isIdle
    }

    /**
     * Optimized visibility check that avoids unnecessary calculations
     */
    fun shouldShowContent(
            isExpanded: Boolean,
            isTransitioning: Boolean,
            threshold: Float = 0.1f
    ): Boolean {
        return isExpanded || (isTransitioning && threshold > 0.05f)
    }
}