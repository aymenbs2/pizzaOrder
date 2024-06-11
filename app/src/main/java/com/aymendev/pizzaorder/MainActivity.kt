package com.aymendev.pizzaorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aymendev.pizzaorder.ui.theme.PizzaOrderTheme
import com.aymendev.pizzaorder.ui.theme.Yellow60
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzaOrderTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .background(Yellow60)
            .fillMaxSize()

    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            CenteredImage(
                Modifier
                    .padding(top = 50.dp)

                    .size(300.dp)

            )
            PizzaPager(
                Modifier
                    .fillMaxWidth()
                    .height(400.dp)

            )
        }
    }
}


@Composable
fun CenteredImage(modifier: Modifier) {
    Image(
        modifier = modifier,
        contentScale = ContentScale.Crop,
        painter = painterResource(id = R.drawable.platepng),
        contentDescription = ""
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PizzaPager(modifier: Modifier) {
    val pagerState = rememberPagerState()
    HorizontalPager(
        count = 10,
        state = pagerState,
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 64.dp),
        modifier = modifier,
    ) { page ->
        val pageOffset = calculatePageOffset(pagerState, page)
        val offset = 100.dp * pageOffset
        val scale =
            animateFloatAsState(targetValue = if (pageOffset < 0.5) 1F else 0.8f, label = "")
        PizzaPage(offset, pageOffset, scale)
    }
}

@Composable
fun PizzaPage(offset: Dp, pageOffset: Float, scale: State<Float>) {
    Box(
        modifier = Modifier
            .offset(y = offset)
            .size(270.dp)
            .scale(scale.value)
            .rotate(100 * (1 - pageOffset))
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            painter = painterResource(id = R.drawable.pizza),
            contentDescription = ""
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
fun calculatePageOffset(pagerState: PagerState, page: Int): Float {
    return when (page) {
        pagerState.currentPage -> {
            pagerState.currentPageOffset.absoluteValue
        }

        pagerState.currentPage - 1 -> {
            1 + pagerState.currentPageOffset.coerceAtMost(0f)
        }

        pagerState.currentPage + 1 -> {
            1 - pagerState.currentPageOffset.coerceAtLeast(0f)
        }

        else -> {
            1f
        }
    }
}
