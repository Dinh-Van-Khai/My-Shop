package com.example.myshop.component.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.ui.theme.shimmerEffect
import com.example.myshop.model.Product
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.decimalFormat
import kotlinx.coroutines.delay

@Composable
fun HomeContent(
    state: HomeState = HomeState(),
    setCurrentCategory: (String) -> Unit = {},
    onSearchTextChange: (String) -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickProduct: (Product) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState {state.eventImages.size}
    var firstItemOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    val lazyGridState = rememberLazyGridState()
    LaunchedEffect(lazyGridState.isScrollInProgress) {
        if (lazyGridState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(state.eventImages) {
        while (true) {
            delay(4000)
            if (state.eventImages.size >= 2) {
                var nextImage = pagerState.currentPage + 1
                if (nextImage >= state.eventImages.size)
                    nextImage = 0
                pagerState.animateScrollToPage(nextImage, animationSpec = tween(400))
            }
        }
    }
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .clickable(
                interactionSource = remember{MutableInteractionSource()},
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = lazyGridState
            ) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .onGloballyPositioned { coordinates ->
                                val offset = coordinates.positionInWindow()
                                firstItemOffset = offset
                            }
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            key = { state.eventImages[it] },
                            pageSize = PageSize.Fill,
                        ) { index ->
                            AsyncImage(
                                model = state.eventImages[index],
                                contentDescription = "Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmerEffect()
                            )
                        }
                        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                            state.eventImages.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == pagerState.currentPage) {
                                                Color.White
                                            } else {
                                                Color.White.copy(alpha = 0.3f)
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    ChooseCategory(
                        categories = state.categories,
                        setCurrentCategory = setCurrentCategory
                    )
                }
                items(count = state.showingProduct.size, key = {state.showingProduct[it].pid }) {
                    val product = state.showingProduct[it]

                    ItemProduct(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .aspectRatio(3 / 5f)
                            .animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = tween(500)
                            ),
                        product = product,
                        onClick = {
                            focusManager.clearFocus()
                            onClickProduct(product)
                        }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
        HomeTopBar(
            modifier = Modifier.fillMaxSize(),
            firstItemOffset = firstItemOffset,
            searchText = state.searchText,
            searchResult = state.searchResult,
            numberOfCart = state.numberOfCart,
            numberOfChats = state.numberOfChats,
            clearFocus = { focusManager.clearFocus() },
            onSearchTextChange = onSearchTextChange,
            onClickCart = onClickCart,
            onClickChats = onClickChats,
            onClickSearchItem = onClickProduct,
        )
    }
}

@Composable
fun ChooseCategory(
    categories: List<String> = emptyList(),
    setCurrentCategory: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(
            index = selectedIndex + 1,
            scrollOffset = -300
        )
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Spacer(modifier = Modifier.width(10.dp))
        }
        items(categories.size) { index ->
            val isSelected = selectedIndex == index
            val backgroundColor = animateColorAsState(
                targetValue = if (isSelected)
                    MaterialTheme.colorScheme.onBackground
                else
                    MaterialTheme.colorScheme.surface,
                animationSpec = tween(200)
            )
            Card(
                modifier = Modifier
                    .clickable {
                        selectedIndex = index
                        setCurrentCategory(categories[index])
                    },
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor.value,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = categories[index],
                    modifier = Modifier.padding(
                        horizontal = 10.dp, vertical = 5.dp
                    )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun ItemProduct(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    product: Product = Product(),
    enableClick: Boolean = true,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shadowElevation = elevation,
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        enabled = enableClick,
        onClick = onClick
    ) {
        Column {
            AsyncImage(
                model = if (product.images.isNotEmpty()) product.images[0] else "",
                contentDescription = "First item image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shimmerEffect(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .align(Alignment.CenterStart),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = try {
                        if (product.price >= 0) {
                            decimalFormat.format(product.price) + "đ"
                        } else {
                            "-"
                        }
                    } catch (_: Exception) {
                        "-"
                    },
                    color = Color.Red,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "${product.getSold()} sold",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    firstItemOffset: Offset = Offset.Zero,
    searchText: String = "",
    searchResult: List<Product> = emptyList(),
    numberOfCart: Int = 0,
    numberOfChats: Int = 0,
    onSearchTextChange: (String) -> Unit = {},
    clearFocus: () -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickSearchItem: (Product) -> Unit = {}
) {
    var isFocusing by remember {
        mutableStateOf(false)
    }
    var backgroundColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var secondBackgroundColor by remember {
        mutableStateOf(Color.Black.copy(alpha = 0.2f))
    }
    var contentColor by remember {
        mutableStateOf(Color.White)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState.isScrollInProgress) {
        keyboardController?.hide()
    }

    LaunchedEffect(firstItemOffset) {
        val alpha = if (-firstItemOffset.y >= 400) 1f else -firstItemOffset.y / 400f

        backgroundColor = Color.White.copy(alpha = alpha)
        secondBackgroundColor = Color.Black.copy(alpha = if (alpha < 0.2f) 0.2f - alpha else 0f)
        contentColor = Color.White.copy(
            red = 1f - alpha,
            green = 1f - alpha,
            blue = 1f - alpha
        )
    }
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isFocusing) Color.White else backgroundColor
            ),
            elevation = if (-firstItemOffset.y >= 400f) CardDefaults.cardElevation(16.dp) else CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = isFocusing) {
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = clearFocus
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back icon",
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(30.dp),
                            tint = Color.Black
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(100))
                        .background(if (isFocusing) Color.White else secondBackgroundColor)
                        .border(
                            1.dp,
                            if (isFocusing) Color.Black else contentColor.copy(alpha = 0.4f),
                            RoundedCornerShape(100)
                        )
                ) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 15.dp)
                            .onFocusChanged {
                                isFocusing = it.isFocused
                            },
                        textStyle = TextStyle(
                            color = if (isFocusing) Color.Black else contentColor,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(Primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                    )
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search",
                            color = if (isFocusing) Color.Black.copy(alpha = 0.4f) else contentColor.copy(
                                alpha = 0.4f
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 15.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(
                            if (isFocusing) Color.White else secondBackgroundColor,
                            CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClickCart
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cart),
                        contentDescription = "Cart icon",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp),
                        tint = if (isFocusing) Color.Black else contentColor
                    )
                    if (numberOfCart > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numberOfCart.toString(),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(
                            if (isFocusing) Color.White else secondBackgroundColor,
                            CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClickChats
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = "Message icon",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp),
                        tint = if (isFocusing) Color.Black else contentColor
                    )
                    if (numberOfChats > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numberOfChats.toString(),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = isFocusing) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { keyboardController?.hide() }
                    )
            ) {
                items(count = searchResult.size, key = { index -> searchResult[index].pid }) { index ->
                    val product = searchResult[index]

                    SearchItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = tween(500)
                            ),
                        product = product,
                        onClick = { onClickSearchItem(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchItem(
    modifier: Modifier = Modifier,
    product: Product = Product(),
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(120.dp)
            .background(Color.White)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = product.images[0],
            contentDescription = "image",
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Cost: ${decimalFormat.format(product.price)}đ",
                color = Color.Red,
                fontSize = 18.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBar(
                    rating = product.getAvgRate(),
                    spaceBetween = 4.dp,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Text(
                    text = "Sold: ${product.getSold()}",
                )
            }
            Text(text = "${product.reviews.size} reviews", Modifier.padding(horizontal = 5.dp))
        }
    }
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Float = 0f,
    count: Int = 5,
    spaceBetween: Dp = 0.dp
) {

    val image = ImageBitmap.imageResource(id = R.drawable.ratingstar_outline)
    val imageFull = ImageBitmap.imageResource(id = R.drawable.ratingstar_filled)

    val height = LocalDensity.current.run { 32.dp }
    val width = LocalDensity.current.run { 32.dp }
    val space = LocalDensity.current.run { spaceBetween.toPx() }
    val totalWidth = width * count + spaceBetween * (count - 1)

    Box(
        modifier
            .width(totalWidth)
            .height(height)
            .drawBehind {
                drawRating(rating, count, image, imageFull, space)
            }
    )
}

private fun DrawScope.drawRating(
    rating: Float,
    count: Int,
    image: ImageBitmap,
    imageFull: ImageBitmap,
    space: Float
) {
    val imageWidth = image.width.toFloat()
    val imageHeight = size.height

    val reminder = rating - rating.toInt()
    val ratingInt = (rating - reminder).toInt()

    for (i in 0 until count) {

        val start = imageWidth * i + space * i

        drawImage(
            image = image,
            topLeft = Offset(start, 0f),
            colorFilter = ColorFilter.tint(Color.Red)
        )
    }

    drawWithLayer {
        for (i in 0 until count) {
            val start = imageWidth * i + space * i
            // Destination
            drawImage(
                image = imageFull,
                topLeft = Offset(start, 0f),
                colorFilter = ColorFilter.tint(Color.Red)
            )
        }

        val end = imageWidth * count + space * (count - 1)
        val start = rating * imageWidth + ratingInt * space
        val size = end - start

        // Source
        drawRect(
            Color.Transparent,
            topLeft = Offset(start, 0f),
            size = Size(size, height = imageHeight),
            blendMode = BlendMode.SrcIn
        )
    }
}

private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}