package com.aymendev.pizzaorder.ui.core

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aymendev.pizzaorder.R


@Composable
fun PizzaBox(
    modifier: Modifier,
    pizzaImage: Int,
    isVisible: MutableState<Boolean>,
    isClosed: MutableState<Boolean>,
    onAnimationFinish: () -> Unit
) {
    val duration = 2000

    val spacer = animateDpAsState(
        targetValue = if (isClosed.value) 0.dp else 250.dp,
        label = "",
        animationSpec = tween(duration),
    )
    val size = animateDpAsState(
        targetValue = if (isClosed.value) 0.dp else 230.dp,
        label = "",
        animationSpec = tween(duration)
    )
    val offset = animateDpAsState(
        targetValue = if (isClosed.value) 150.dp else 0.dp,
        label = "",
        animationSpec = tween(duration, delayMillis = 90),

        )
    val scale = animateFloatAsState(
        targetValue = if (isClosed.value && isVisible.value) 0f else 1f,
        label = "",
        animationSpec = tween(duration, delayMillis = 90),
        finishedListener = {
            if (it==0f) {
                onAnimationFinish()
            }

            isClosed.value = false
            isVisible.value = false

        }
    )


    val rotation = animateFloatAsState(
        targetValue = if (isClosed.value) 0f else 26F,
        label = "",
        animationSpec = tween(duration),
    )

    AnimatedVisibility(modifier = modifier.onGloballyPositioned {
        isClosed.value = true
    }, enter = fadeIn(), exit = fadeOut(), visible = isVisible.value) {
        Box(
            modifier = Modifier
                .alpha(scale.value)
                .offset(x = offset.value, y = -offset.value)
                .fillMaxSize()
        ) {
            // Pizza box base
            Image(
                painter = painterResource(id = R.drawable.base3),
                contentDescription = "Pizza Box Base",
                modifier = Modifier
                    .padding(top = spacer.value / 2)
                    .size(300.dp)
                    .graphicsLayer {
                        rotationX = rotation.value
                    }
                    .scale(scale.value)
                    .align(Alignment.Center)
            )
            Image(
                painter = painterResource(id = pizzaImage),
                contentDescription = "Pizza ",
                modifier = Modifier
                    .padding(top = spacer.value / 2)
                    .scale(scale.value)
                    .size(size.value)
                    .align(Alignment.Center)
            )
            Image(
                painter = painterResource(id = R.drawable.lid3),
                contentDescription = "Pizza Box Lid",
                modifier = Modifier
                    .graphicsLayer {
                        rotationX = rotation.value
                    }
                    .scale(scale.value)
                    .padding(bottom = spacer.value / 2)
                    .size(300.dp)
                    .align(Alignment.Center)

            )
        }
    }
}