package com.example.myshop.component.product

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.component.home.ItemProduct
import com.example.myshop.component.home.RatingBar
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.ui.theme.shimmerEffect
import com.example.myshop.util.decimalFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableInteractionSource", "DefaultLocale")
@Composable
fun ProductContent(
    sheetState: SheetState,
    state: ProductState = ProductState(),
    setFirstItemOffset: (Offset) -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChatIcon: () -> Unit = {},
    onClickChatWithShop: (String) -> Unit = {},
    onClickAddToCart: () -> Unit = {},
    onClickBuyNow: () -> Unit = {},
    onChangeOrder: (Order) -> Unit = {},
    addToCart: () -> Unit = {},
    checkOut: (List<Order>) -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    viewShop: () -> Unit = {},
    viewProduct: (Product) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState{state.product.images.size}
    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {setShowBottomSheet(false)},
            containerColor =  MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            when (state.sheetContent) {
                SheetContent.DEFAULT -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                SheetContent.ADD_TO_CART -> {
                    AddToCartOrBuy(
                        order = state.order,
                        label = "Add to Cart",
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onChangeOrder = onChangeOrder,
                        onClickButton = addToCart
                    )
                }

                SheetContent.BUY_NOW -> {
                    AddToCartOrBuy(
                        order = state.order,
                        label = "Buy Now",
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onChangeOrder = onChangeOrder,
                        onClickButton = {
                            scope.launch {
                                setShowBottomSheet(false)
                                delay(500)
                                checkOut(listOf(state.order))
                            }
                        }
                    )
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .then(if (state.user.uid != state.product.user.uid) Modifier.navigationBarsPadding() else Modifier)
                .padding(bottom = if (state.user.uid != state.product.user.uid) 55.dp else 0.dp),
            columns = GridCells.Fixed(2),
            content = {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .onGloballyPositioned { coordinates ->
                                val offset = coordinates.positionInWindow()
                                setFirstItemOffset(offset)
                            }
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            key = { state.product.images[it] },
                            pageSize = PageSize.Fill,
                        ) { index ->
                            AsyncImage(
                                model = state.product.images[index],
                                contentDescription = "Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmerEffect()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(100))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1}/${state.product.images.size}",
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                            )
                        }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    val listState = rememberLazyListState()
                    LaunchedEffect(pagerState.currentPage) {
                        listState.animateScrollToItem(pagerState.currentPage, -350)
                    }
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentPadding = PaddingValues(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        state = listState
                    ) {
                        itemsIndexed(state.product.images) { index, photoUrl ->
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Image product",
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .border(
                                        1.dp,
                                        if (pagerState.currentPage == index) Color.Red else Color.Transparent
                                    )
                                    .clickable(
                                        interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                    )
                            )
                        }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = state.product.name,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    )
                }
                item(span = { GridItemSpan(2) }) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBar(
                            modifier = Modifier.padding(start = 10.dp),
                            rating = state.product.getAvgRate(),
                            spaceBetween = 2.dp
                        )
                        Text(
                            text = String.format("%.1f", state.product.getAvgRate()),
                            modifier = Modifier.padding(start = 15.dp, end = 5.dp)
                        )
                        Text(
                            text = "|",
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            text = "${state.product.getSold()} sold",
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Price: ${decimalFormat.format(state.product.price)}đ",
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (state.user.uid != state.product.user.uid) Color.Red else Color.Gray)
                                .clickable(
                                    onClick = onClickAddToCart,
                                    enabled = state.user.uid != state.product.user.uid
                                )
                        ) {
                            Text(
                                text = "Add to Cart",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = state.product.user.profilePicture,
                                contentDescription = "Shop's Profile picture",
                                modifier = Modifier
                                    .padding(horizontal = 15.dp, vertical = 5.dp)
                                    .size(55.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = state.product.user.name,
                                    fontSize = 18.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_location),
                                        contentDescription = "Icon Location",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = state.product.user.address,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 15.dp),
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp, vertical = 10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .border(1.dp, Color.Red, RoundedCornerShape(5.dp))
                                    .clickable(onClick = viewShop)
                            ) {
                                Text(
                                    text = "View Shop",
                                    color = Color.Red,
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 5.dp
                                    )
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Shop's Product",
                                modifier = Modifier.padding(
                                    horizontal = 15.dp,
                                    vertical = 10.dp
                                )
                            )
                            Text(
                                text = "See All >",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 15.dp, vertical = 10.dp)
                                    .clickable(
                                        interactionSource = MutableInteractionSource(),
                                        indication = null,
                                        onClick = viewShop
                                    )
                            )
                        }
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentPadding = PaddingValues(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            val showList = if (state.productOfShop.size <= 6) {
                                state.productOfShop
                            } else {
                                state.productOfShop.subList(0, 6)
                            }
                            items(showList) { product ->
                                ItemProduct(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(3 / 5f),
                                    product = product,
                                    onClick = {
                                        viewProduct(product)
                                    }
                                )
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(3 / 5f)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = viewShop
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier.border(
                                                2.dp,
                                                Color.Red,
                                                CircleShape
                                            )
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_next),
                                                contentDescription = "Icon Next",
                                                tint = Color.Red,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .size(25.dp)
                                            )
                                        }
                                        Text(
                                            text = "See More",
                                            color = Color.Red,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(top = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Text(
                            text = "Category: ${state.product.getCategory()}",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Text(
                            text = "Description:\n${state.product.description}",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        )
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Text(
                            text = "Review: ${if (state.product.reviews.isEmpty()) "No Review" else state.product.reviews.size.toString()}",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        )
                        state.product.reviews.forEach { review ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = review.user.profilePicture,
                                    contentDescription = "User's profile picture",
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .padding(start = 5.dp)
                                        .clip(CircleShape)
                                        .size(45.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = review.user.name,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            RatingBar(
                                modifier = Modifier.padding(start = 65.dp),
                                rating = review.rate.toFloat(),
                                spaceBetween = 2.dp
                            )
                            Text(
                                text = review.comment,
                                modifier = Modifier.padding(start = 65.dp)
                            )
                            LazyRow(
                                modifier = Modifier
                                    .padding(start = 55.dp)
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentPadding = PaddingValues(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(review.images) { photoUrl ->
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Image review product",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Similar Products",
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )
                }
                items(state.similarProducts) { product ->
                    ItemProduct(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .aspectRatio(3 / 5f),
                        product = product,
                        onClick = {
                            viewProduct(product)
                        }
                    )
                }
                if (state.user.uid == state.product.user.uid) {
                    item(span = { GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        )
        ProductTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            firstItemOffset = state.firstItemOffset,
            numberOfCart = state.numberOfCart,
            numberOfChats = state.numberOfChats,
            onBack = onBack,
            onClickCart = onClickCart,
            onClickChats = onClickChatIcon
        )
        if (state.user.uid != state.product.user.uid) {
            ProductBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onClickChats = { onClickChatWithShop(state.product.user.uid) },
                onClickAddToCart = onClickAddToCart,
                onClickBuyNow = onClickBuyNow
            )
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}

@Composable
fun AddToCartOrBuy(
    order: Order,
    label: String,
    hideBottomSheet: () -> Unit,
    onClickButton: () -> Unit = {},
    onChangeOrder: (Order) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val showStock = order.product.stocks.filter {
        it.variations.containsAll(
            order.variations.values
        )
    }.sumOf {
        it.quantity
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            AsyncImage(
                model = if (order.product.images.isNotEmpty()) order.product.images[0] else "",
                contentDescription = "Image",
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Icon close",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                            .size(25.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    focusManager.clearFocus()
                                    hideBottomSheet()
                                }
                            )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Price: ${decimalFormat.format(order.product.price)}đ",
                    fontSize = 18.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                )
                Text(
                    text = "Stock: $showStock",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
            items(order.product.variations) { variation ->
                Text(text = variation.name, modifier = Modifier.padding(5.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    itemsIndexed(variation.child) { childIndex, childVariation ->
                        val value = order.variations[variation.name] ?: ""
                        val chooseVariation = variation.child.indexOf(value)
                        val color = if (chooseVariation == childIndex)
                            Color.Red
                        else
                            MaterialTheme.colorScheme.onBackground
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp,
                                    color = color,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    val variations = order.variations.toMutableMap()
                                    variations[variation.name] = childVariation
                                    onChangeOrder(order.copy(variations = variations))
                                }
                        ) {
                            Text(
                                text = childVariation,
                                modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp),
                                color = color,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Quantity", modifier = Modifier.padding(start = 10.dp))
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Gray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (order.quantity - 1 >= 1) {
                                            onChangeOrder(order.copy(quantity = order.quantity - 1))
                                        }
                                    },
                                    enabled = order.variations.size == order.product.variations.size
                                )
                        ) {
                            Text(
                                text = "-",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Box(modifier = Modifier.border(1.dp, Color.Gray)) {
                            BasicTextField(
                                value = if (order.quantity > 0) order.quantity.toString() else "",
                                onValueChange = {
                                    if (it.isEmpty()) {
                                        onChangeOrder(order.copy(quantity = 0))
                                    }
                                    try {
                                        val newQuantity = it.toLong()
                                        if (newQuantity in 1..showStock) {
                                            onChangeOrder(order.copy(quantity = newQuantity))
                                        }
                                    } catch (_: Exception) {
                                    }
                                },
                                modifier = Modifier.padding(vertical = 5.dp),
                                textStyle = TextStyle(
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                cursorBrush = SolidColor(Color.Red),
                                enabled = order.variations.size == order.product.variations.size
                            )
                        }
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Gray)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (order.quantity + 1 <= showStock) {
                                            onChangeOrder(order.copy(quantity = order.quantity + 1))
                                        }
                                    },
                                    enabled = order.variations.size == order.product.variations.size
                                )
                        ) {
                            Text(
                                text = "+",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
            item {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price:",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = "${decimalFormat.format(order.product.price * order.quantity)}đ",
                        fontSize = 18.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onClickButton,
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(10.dp),
                enabled = order.quantity in 1..showStock && order.variations.size == order.product.variations.size
            ) {
                Text(text = label)
            }
        }
    }
}

@Composable
fun ProductTopBar(
    modifier: Modifier = Modifier,
    firstItemOffset: Offset = Offset.Zero,
    numberOfCart: Int = 0,
    numberOfChats: Int = 0,
    onBack: () -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
) {
    var backgroundColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var secondBackgroundColor by remember {
        mutableStateOf(Color.Black.copy(alpha = 0.2f))
    }
    var contentColor by remember {
        mutableStateOf(Color.White)
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
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
        elevation = if(-firstItemOffset.y >= 400f) CardDefaults.cardElevation(16.dp) else CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 3.dp)
                    .clip(CircleShape)
                    .background(secondBackgroundColor)
                    .clickable(onClick = onBack)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Icon back",
                    tint = contentColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .background(secondBackgroundColor, CircleShape)
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
                    tint = contentColor
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
                    .background(secondBackgroundColor, CircleShape)
                    .clickable(
                        interactionSource = remember{ MutableInteractionSource() },
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
                    tint = contentColor
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
}

@Composable
fun ProductBottomBar(
    modifier: Modifier = Modifier,
    onClickChats: () -> Unit = {},
    onClickAddToCart: () -> Unit = {},
    onClickBuyNow: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(55.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(onClick = onClickChats),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_message),
                    contentDescription = "Icon Message",
                    modifier = Modifier
                        .padding(2.dp)
                        .size(22.dp),
                    tint = Color.Red
                )
                Text(text = "Chat now", fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.7f)
                    .background(Color.Gray)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(onClick = onClickAddToCart),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Icon Cart",
                    modifier = Modifier
                        .padding(2.dp)
                        .size(22.dp),
                    tint = Color.Red
                )
                Text(text = "Add to Cart", fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .weight(1.7f)
                    .fillMaxHeight()
                    .background(Color.Red)
                    .clickable(onClick = onClickBuyNow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Buy Now",
                    color = Color.White
                )
            }
        }
    }
}