package com.aymendev.pizzaorder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aymendev.pizzaorder.ui.theme.Orange40
import com.aymendev.pizzaorder.ui.theme.Pink40
import com.aymendev.pizzaorder.ui.theme.PizzaOrderTheme
import com.aymendev.pizzaorder.ui.theme.Yellow40
import com.aymendev.pizzaorder.ui.theme.Yellow50
import com.aymendev.pizzaorder.ui.theme.Yellow60
import com.aymendev.pizzaorder.ui.utils.ScrollUtils
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainScreen() {

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val isPizzaSelected = remember {
        mutableStateOf(false)
    }

    val rootWidth = remember {
        mutableStateOf(0.dp)
    }
    val containerSize = animateDpAsState(
        targetValue = if (isPizzaSelected.value) 250.dp else 230.dp,
        label = "cornerRadiusBg",
        animationSpec = tween(500)

    )
    val selectedRotationFinished = remember {
        mutableStateOf(false)
    }
    val cornerRadiusBg = animateDpAsState(
        targetValue = if (!isPizzaSelected.value) 100.dp else 20.dp,
        label = "cornerRadiusBg",
        animationSpec = tween(500)
    )
    val selectedRotation =
        animateFloatAsState(targetValue = if (!isPizzaSelected.value) 0f else 35f,
            label = "selectedRotation",
            animationSpec = tween(500),
            finishedListener = {
                selectedRotationFinished.value = true
            }
        )
    val selectedScale = animateFloatAsState(
        targetValue = if (isPizzaSelected.value) 1.001f else 1f,
        label = "selectedRotation",
        animationSpec = tween(500)
    )
    val pizzaInWindow = remember {
        mutableStateOf(Any())
    }
    val rotation = remember {
        mutableFloatStateOf(0f)
    }

    Scaffold(topBar = {
        MainTopBar()
    }) {
        MainContent(
            it,
            isPizzaSelected,
            rootWidth,
            cornerRadiusBg,
            containerSize,
            selectedRotation,
            selectedScale,
            pizzaInWindow,
            selectedRotationFinished,
            rotation
        )
    }

}

@Composable
fun MainTopBar() {
    Box(
        Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(Yellow60)
            .padding(horizontal = 20.dp)
    ) {

        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = stringResource(id = R.string.order_manually),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Pink40
        )
        Icon(
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Outlined.ShoppingCart,
            tint = Pink40,
        )

    }
}

@Composable
fun DetailTopBar() {
    Box(
        Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(Yellow60)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = stringResource(id = R.string.order_manually),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Pink40
        )
        Icon(
            contentDescription = "",
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Outlined.ShoppingCart,
            tint = Pink40,
        )

    }
}

@Composable
private fun MainContent(
    it: PaddingValues,
    isPizzaSelected: MutableState<Boolean>,
    rootWidth: MutableState<Dp>,
    cornerRadiusBg: State<Dp>,
    containerSize: State<Dp>,
    selectedRotation: State<Float>,
    selectedScale: State<Float>,
    pizzaInWindow: MutableState<Any>,
    selectedRotationFinished: MutableState<Boolean>,
    rotation: MutableFloatState
) {
    Column(
        modifier = Modifier
            .padding(it)
            .background(Yellow60)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        AnimatedVisibility(visible = !isPizzaSelected.value) {

            Column(
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(120.dp, 50.dp)
                        .background(shape = RoundedCornerShape(20.dp), color = Yellow40),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = stringResource(id = R.string.pizza),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Pink40,
                        fontSize = 22.sp
                    )
                }
            }


        }
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    rootWidth.value = it.size.width.toFloat().dp
                }) {
            val (container, pizza, pager, bg, infoBloc) = createRefs()
            Card(
                shape = RoundedCornerShape(cornerRadiusBg.value),
                colors = CardDefaults.cardColors(Yellow50),
                modifier = Modifier.constrainAs(bg) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    if (!isPizzaSelected.value) {
                        top.linkTo(container.top)
                        start.linkTo(container.start)
                        end.linkTo(container.end)
                    } else {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    bottom.linkTo(parent.bottom)
                }

            ) {

            }

            Image(modifier = Modifier
                .size(containerSize.value)
                .shadow(
                    20.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .rotate(selectedRotation.value)
                .scale(selectedScale.value)
                .constrainAs(container) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .onGloballyPositioned {
                    pizzaInWindow.value = it
                },
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.plate),
                contentDescription = ""
            )

            if (isPizzaSelected.value)
                Image(modifier = Modifier
                    .size(containerSize.value - 10.dp)
                    .clip(CircleShape)
                    .scale(selectedScale.value)
                    .rotate(selectedRotation.value)
                    .clickable {
                        isPizzaSelected.value = !isPizzaSelected.value
                    }
                    .constrainAs(pizza) {
                        start.linkTo(container.start)
                        top.linkTo(container.top)
                        end.linkTo(container.end)
                        bottom.linkTo(container.bottom)
                    },
                    painter = painterResource(id = R.drawable.pizza),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
            if (!isPizzaSelected.value) {
                PizzaPager(
                    Modifier
                        .constrainAs(pager) {
                            top.linkTo(container.top)
                            bottom.linkTo(container.bottom)
                        }
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    pizzaSize = containerSize.value - 10.dp,
                    onPizzaClicked = {
                        if (isPizzaSelected.value)
                            selectedRotationFinished.value = false
                        isPizzaSelected.value = !isPizzaSelected.value

                    },
                    onScroll = {
                        rotation.floatValue = 100 * -it
                    },
                    rotation = selectedRotation.value
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(infoBloc) {
                        top.linkTo(container.bottom)
                        bottom.linkTo(bg.bottom)
                    },

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedVisibility(
                    enter = slideInVertically(animationSpec = tween(100)) { -it * 3 },
                    exit = slideOutVertically(tween(100)) { -it * 3 },
                    visible = !isPizzaSelected.value,
                    content = {
                        Column (modifier = Modifier.fillMaxWidth(),   horizontalAlignment = Alignment.CenterHorizontally,){
                            Text(
                                text = "New Orleans Pizza",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Pink40,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Stars()
                        }

                    })

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "$15", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Pink40)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape),
                    ) {

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "S",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            color = Pink40
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(3.dp, shape = CircleShape)
                            .background(Color.White, shape = CircleShape)
                            .clip(CircleShape),
                    ) {

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "M",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            color = Pink40
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape),
                    ) {

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "L",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            color = Pink40
                        )
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(visible = isPizzaSelected.value) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(

                            textAlign = TextAlign.Center,
                            text = "${stringResource(id = R.string.tapping)} ${
                                String.format(
                                    stringResource(id = R.string.must_be),
                                    2
                                )
                            }", fontWeight = FontWeight.W400, fontSize = 12.sp
                        )

                        LazyRow {
                            items(6) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(40.dp)
                                        .background(Color.White, shape = CircleShape),
                                ) {

                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center),
                                        text = it.toString(),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.W400,
                                        fontSize = 16.sp,
                                        color = Pink40
                                    )
                                }
                            }
                        }
                    }

                }
            }

        }


    }
}

@Composable
private fun Stars() {
    Row {
        Icon(imageVector = Icons.Default.Star, tint = Orange40, contentDescription = "")
        Icon(imageVector = Icons.Default.Star, tint = Orange40, contentDescription = "")
        Icon(imageVector = Icons.Default.Star, tint = Orange40, contentDescription = "")
        Icon(imageVector = Icons.Default.Star, tint = Orange40, contentDescription = "")
        Icon(imageVector = Icons.Default.Star, tint = Orange40, contentDescription = "")
    }
}

@Composable
fun PizzaName(modifier: Modifier, isPizzaSelected: Boolean, name: String) {

    AnimatedVisibility(modifier = modifier,
        enter = slideInVertically(animationSpec = tween(100)) { -it * 3 },
        exit = slideOutVertically(tween(100)) { -it * 3 },
        visible = !isPizzaSelected,
        content = {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Pink40,
                textAlign = TextAlign.Center
            )
        })

}


//ToDO
//            Box(
//                modifier = Modifier
//                    .size(100.dp)
//                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
//                    .background(Color.Red)
//                    .onGloballyPositioned {
//                        val re = Rect(
//                            it.positionInRoot(),
//                            Size(it.size.width.toFloat(), it.size.width.toFloat())
//                        )
//                        val pizPos = (pizzaInWindow.value as LayoutCoordinates)
//                        Log.d(
//                            "TAG",
//                            "nnn:${
//                                pizPos
//                                    .boundsInWindow()
//                                    .contains(it.positionInWindow())
//                            }:: "
//                        )
//
//                    }
//                    .pointerInput(Unit) {
//                        detectDragGestures { change, dragAmount ->
//                            change.consume()
//                            offsetX += dragAmount.x
//                            offsetY += dragAmount.y
//
//                        }
//                    }
//            )
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PizzaPager(
    modifier: Modifier,
    pizzaSize: Dp,
    onPizzaClicked: (Int) -> Unit,
    rotation: Float? = null,
    onScroll: (Float) -> Unit
) {
    val pagerState = rememberPagerState()
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(ScrollUtils.currentIndex)
    }

    if (pagerState.isScrollInProgress) {
        onScroll(pagerState.currentPageOffset)
    }
    HorizontalPager(
        count = 10,
        state = pagerState,
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(75.dp),
        modifier = modifier,

        ) { page ->

        val pageOffset = calculatePageOffset(pagerState, page)
        val offset = 100.dp * pageOffset
        val scale =
            animateFloatAsState(targetValue = if (pageOffset < 0.5) 1.0F else 0.7f, label = "")
        PizzaPage(
            offset,
            pageOffset,
            scale,
            size = pizzaSize,
            rotation = if (pagerState.isScrollInProgress && (pageOffset < 0.5)) (100 * -pageOffset) else if ((pageOffset < 0.5)) rotation
                ?: 0f else 0f
        ) {
            ScrollUtils.currentIndex = pagerState.currentPage
            if (page == pagerState.currentPage) onPizzaClicked(pagerState.currentPage)
        }

    }
}


@Composable
fun PizzaPage(
    offset: Dp,
    pageOffset: Float,
    scale: State<Float>,
    size: Dp = 240.dp,
    rotation: Float = 100 * -pageOffset,
    onClickAction: () -> Unit
) {
    Image(contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(y = offset)
            .size(size)
            .clip(CircleShape)
            .background(Color.Transparent, shape = CircleShape)
            .scale(scale.value)
            .clickable {
                onClickAction()
            }
            .rotate(
                rotation
            ),
        painter = painterResource(id = R.drawable.pizza),
        contentDescription = "")
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
