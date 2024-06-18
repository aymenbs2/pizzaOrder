package com.aymendev.pizzaorder.ui.core

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun DraggableBox(
    modifier: Modifier,
    targetLayoutCoordinates: MutableState<Any>,
    onDragged: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = modifier
            .zIndex(1000f)
            .absoluteOffset {
                IntOffset(
                    offsetX.roundToInt(),
                    offsetY.roundToInt()
                )
            }
            .onGloballyPositioned {
                if ((targetLayoutCoordinates.value as LayoutCoordinates)
                        .boundsInWindow()
                        .contains(it.positionInWindow())
                ) {
                    onDragged()
                    offsetX = 0f
                    offsetY = 0f
                }


            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                    }, onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y

                }
            },
    ) {
        content()
    }
}