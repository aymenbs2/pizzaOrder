package com.aymendev.pizzaorder.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aymendev.pizzaorder.R
import com.aymendev.pizzaorder.data.Pizza
import com.aymendev.pizzaorder.ui.theme.Orange40
import com.aymendev.pizzaorder.ui.theme.Pink40
import com.aymendev.pizzaorder.ui.theme.PizzaOrderTheme
import com.aymendev.pizzaorder.ui.theme.Yellow40
import com.aymendev.pizzaorder.ui.theme.Yellow50
import com.aymendev.pizzaorder.ui.theme.Yellow60
import com.aymendev.pizzaorder.ui.theme.Yellow70
import com.aymendev.pizzaorder.ui.utils.ScrollUtils
import com.aymendev.pizzaorder.ui.viewModels.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint


import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzaOrderTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainScreen(viewModel: MainViewModel) {


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
    val cardColor =
        animateColorAsState(
            targetValue = if (!isPizzaSelected.value) Yellow60 else Color.White,
            label = "cardColor"
        )
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
        if (isPizzaSelected.value)
            DetailTopBar()
        else
            MainTopBar()
    }) {
        MainContent(

            it,
            viewModel = viewModel,
            isPizzaSelected = isPizzaSelected,
            cardColor = cardColor,
            rootWidth = rootWidth,
            cornerRadiusBg = cornerRadiusBg,
            containerSize = containerSize,
            selectedRotation = selectedRotation,
            selectedScale = selectedScale,
            pizzaInWindow = pizzaInWindow,
            selectedRotationFinished = selectedRotationFinished,
            rotation = rotation
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
        Row(Modifier.align(Alignment.BottomStart), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "", tint = Pink40)
            Text(
                text = "Paris",
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = Pink40
            )
        }
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
            .height(100.dp)
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

    }
}

@Composable
private fun MainContent(
    it: PaddingValues,
    isPizzaSelected: MutableState<Boolean>,
    rootWidth: MutableState<Dp>,
    cardColor: State<Color>,
    cornerRadiusBg: State<Dp>,
    containerSize: State<Dp>,
    selectedRotation: State<Float>,
    selectedScale: State<Float>,
    pizzaInWindow: MutableState<Any>,
    selectedRotationFinished: MutableState<Boolean>,
    rotation: MutableFloatState,
    viewModel: MainViewModel
) {
    val currentPizza = remember {
        mutableStateOf(viewModel.pizzas[ScrollUtils.currentIndex])
    }
    val currentSupplementsCount= remember {
        mutableIntStateOf(0)
    }
    Column(
        modifier = Modifier
            .background(brush = Brush.verticalGradient(listOf(Yellow60, Yellow50)))
            .padding(it)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        PizzaButton(isPizzaSelected)
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    rootWidth.value = it.size.width.toFloat().dp
                }) {
            val (container, pizza, pager, bg, infoBloc, cartButton, supplimentsImgsOnPizza, supplimentsImgsOnPizza2, additions) = createRefs()
            Box(
                modifier = Modifier
                    .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(cornerRadiusBg.value))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Yellow60, cardColor.value
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadiusBg.value)
                    )
                    .constrainAs(bg) {
                        width = Dimension.fillToConstraints
                        height = Dimension.percent(0.9f)
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

            )
            Image(modifier = Modifier
                .size(containerSize.value)
                .shadow(
                    8.dp,
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

            if (isPizzaSelected.value) {
                Image(modifier = Modifier
                    .size(containerSize.value - 10.dp)
                    .clip(CircleShape)
                    .scale(selectedScale.value)
                    .rotate(selectedRotation.value)
                    .clickable {
                        isPizzaSelected.value = !isPizzaSelected.value
                        currentSupplementsCount.intValue=0
                        viewModel.currentSupplement.clear()
                    }
                    .constrainAs(pizza) {
                        start.linkTo(container.start)
                        top.linkTo(container.top)
                        end.linkTo(container.end)
                        bottom.linkTo(container.bottom)
                    },
                    painter = painterResource(
                        id = currentPizza.value.image
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
                if (currentSupplementsCount.intValue > 0) {
                    for (i in 0 until viewModel.currentSupplement.size)
                        CircularImages(
                            modifier = Modifier
                                .constrainAs(if(i==0)supplimentsImgsOnPizza else supplimentsImgsOnPizza2)
                                {
                                    height = Dimension.fillToConstraints
                                    width = Dimension.fillToConstraints
                                    start.linkTo(container.start)
                                    top.linkTo(container.top)
                                    end.linkTo(container.end)
                                    bottom.linkTo(container.bottom)

                                },
                            viewModel.currentSupplement[i].image,
                            radius = 50+i*20
                        )
                }

                Additions(
                    modifier = Modifier
                        .constrainAs(additions) {
                            width = Dimension.fillToConstraints
                            top.linkTo(infoBloc.bottom)
                            start.linkTo(bg.start)
                            end.linkTo(bg.end)
                        },
                    pizzaInWindow = pizzaInWindow,
                    viewModel = viewModel

                ) {
                    if (viewModel.currentSupplement.size < 2){
                        viewModel.currentSupplement.add(viewModel.supplements[it])
                        currentSupplementsCount.intValue++

                    }
                }
            }


            if (!isPizzaSelected.value) {
                PizzaPager(
                    Modifier
                        .constrainAs(pager) {
                            top.linkTo(container.top)
                            bottom.linkTo(container.bottom)
                        }
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    pizzas = viewModel.pizzas,
                    pizzaSize = containerSize.value - 10.dp,
                    onPizzaClicked = {
                        if (isPizzaSelected.value) {
                            selectedRotationFinished.value = false
                            viewModel.currentOrderPizza.pizza = currentPizza.value
                        }
                        isPizzaSelected.value = !isPizzaSelected.value
                    },
                    onPageScroll = {
                        currentPizza.value = viewModel.pizzas[it]
                    },
                    onScroll = {
                        rotation.floatValue = 100 * -it
                    },
                    rotation = selectedRotation.value
                )

            }

            PizzaDetails(
                modifier = Modifier
                    .constrainAs(infoBloc) {
                        height = Dimension.fillToConstraints
                        top.linkTo(container.bottom)
                        start.linkTo(bg.start)
                        end.linkTo(bg.end)
                    },
                isPizzaSelected = isPizzaSelected,
                pizza = currentPizza
            )


            CartButton(modifier = Modifier
                .size(80.dp)
                .padding(top = 10.dp, bottom = 10.dp)
                .shadow(2.dp, shape = RoundedCornerShape(20.dp), ambientColor = Orange40)
                .background(color = Orange40, shape = RoundedCornerShape(20.dp))
                .constrainAs(cartButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(bg.start)
                    end.linkTo(
                        bg.end
                    )
                })
        }

    }

}

@Composable
fun CircularImages(
    modifier: Modifier,
    champinion: Int,
    center: Offset = Offset(0f, 0f),
    numberOfImages: Int = 6,
    radius: Int = 50
) {
    val isPositioned = remember {
        mutableStateOf(false)
    }

    val animatedRadius = animateFloatAsState(
        targetValue = if (isPositioned.value) radius.toFloat() else 90f,
        label = "",
        animationSpec = spring(Spring.DampingRatioHighBouncy)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        val angleStep = 2 * Math.PI / numberOfImages
        for (i in 0 until numberOfImages) {
            val angle = i * angleStep
            val x = center.x + animatedRadius.value * cos(angle).toFloat()
            val y = center.y + animatedRadius.value * sin(angle).toFloat()

            // Draw each image at calculated positions
            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size(30.dp) // Adjust the size as needed
                    .graphicsLayer {
                        translationX = x - center.x
                        translationY = y - center.y
                    }
                    .onGloballyPositioned {
                        isPositioned.value = true
                    }
            ) {
                Image(
                    painter = painterResource(id = champinion),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Composable
private fun CartButton(modifier: Modifier) {
    Box(modifier) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = "",
            tint = Color.White
        )
    }
}

@Composable
private fun Additions(
    modifier: Modifier,
    pizzaInWindow: MutableState<Any>,
    viewModel: MainViewModel,
    onDragged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (i in 0 until viewModel.supplements.size) {
            DraggableBox(
                modifier = Modifier
                    .background(color = Yellow70, shape = CircleShape)
                    .wrapContentSize(),
                targetLayoutCoordinates = pizzaInWindow,
                onDragged = {
                    onDragged(i)
                }) {
                Box(
                    Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = viewModel.supplements[i].image),
                        contentDescription = ""
                    )
                }
            }
        }
    }


}

@Composable
private fun AdditionsText(modifier: Modifier, isPizzaSelected: MutableState<Boolean>) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isPizzaSelected.value
    ) {
        Text(

            textAlign = TextAlign.Center,
            text = "${stringResource(id = R.string.tapping)} ${
                String.format(
                    stringResource(id = R.string.must_be),
                    2
                )
            }",
            fontWeight = FontWeight.W400,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun PizzaDetails(
    modifier: Modifier,
    pizza: MutableState<Pizza>,
    isPizzaSelected: MutableState<Boolean>

) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            enter = slideInVertically(animationSpec = tween(100)) { -it * 3 },
            exit = slideOutVertically(tween(100)) { -it * 3 },
            visible = !isPizzaSelected.value,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = pizza.value.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Pink40,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Stars(pizza.value.rate)
                }

            })

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier.zIndex(1f),
            text = "$${pizza.value.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Pink40
        )
        Spacer(modifier = Modifier.height(20.dp))
        PizzaSizes()
        Spacer(modifier = Modifier.height(20.dp))
        AdditionsText(
            modifier = Modifier, isPizzaSelected
        )
    }
}

@Composable
private fun DraggableBox(
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


@Composable
private fun PizzaSizes() {
    Row(
        modifier = Modifier
            .zIndex(1f)
            .fillMaxWidth(0.6f),
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
}

@Composable
private fun PizzaButton(isPizzaSelected: MutableState<Boolean>) {
    AnimatedVisibility(visible = !isPizzaSelected.value) {

        Column(
            Modifier
                .padding(5.dp)
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
}

@Composable
private fun Stars(rate: Int) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                tint = if (i <= rate) Orange40 else Color.LightGray,
                contentDescription = ""
            )
        }

    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun PizzaPager(
    modifier: Modifier,
    pizzaSize: Dp,
    onPizzaClicked: (Int) -> Unit,
    rotation: Float? = null,
    onScroll: (Float) -> Unit,
    onPageScroll: (Int) -> Unit,
    pizzas: List<Pizza>
) {
    val pagerState = rememberPagerState()
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(ScrollUtils.currentIndex)
    }

    if (pagerState.isScrollInProgress) {
        onScroll(pagerState.currentPageOffset)
    }
    HorizontalPager(
        count = pizzas.size,
        state = pagerState,
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(75.dp),
        modifier = modifier,

        ) { page ->

        val pageOffset = calculatePageOffset(pagerState, page)
        val offset = 100.dp * pageOffset
        onPageScroll(pagerState.currentPage)
        val scale =
            animateFloatAsState(targetValue = if (pageOffset < 0.5) 1.0F else 0.7f, label = "")
        PizzaPage(
            offset,
            pageOffset,
            scale,
            pizza = pizzas[page],
            size = pizzaSize,
            rotation = if (pagerState.isScrollInProgress && (pageOffset < 0.5)) (100 * -pageOffset) else if ((pageOffset < 0.5)) rotation
                ?: 0f else 0f
        ) {
            ScrollUtils.currentIndex = pagerState.currentPage
            if (page == pagerState.currentPage)
                onPizzaClicked(pagerState.currentPage)
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
    pizza: Pizza,
    onClickAction: () -> Unit,

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
        painter = painterResource(id = pizza.image),
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
