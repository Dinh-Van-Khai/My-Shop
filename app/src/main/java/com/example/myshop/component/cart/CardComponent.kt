package com.example.myshop.component.cart

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.decimalFormat
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    sheetState: SheetState,
    state: CartState = CartState(),
    onCartChange: (List<CartOrder>) -> Unit = {},
    onEditOrderChange: (Order) -> Unit = {},
    editOrder: () -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    onBack: () -> Unit = {},
    checkOut: (List<Order>) -> Unit = {},
    viewProduct: (Product) -> Unit = {},
    deleteOrderInCart: (Order) -> Unit = {}
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { setShowBottomSheet(false) },
            containerColor = Color.White
        ) {
            EditOrderInCart(
                order = state.editOrder,
                onEditOrderChange = onEditOrderChange,
                editOrder = editOrder,
                hideBottomSheet = { setShowBottomSheet(false) }
            )
        }
    }
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation =  8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Icon back",
                        tint = Primary,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                            .clip(CircleShape)
                            .size(30.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onBack
                            )
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("Shopping Cart")
                            }
                            withStyle(
                                SpanStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            ) {
                                append(" (${state.orders.size})")
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        bottomBar = {
            if (state.orders.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    shadowElevation = 8.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .height(55.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val checked = state.orders.all { it.checked }
                            Box(
                                modifier = Modifier
                                    .padding(start = 15.dp, end = 10.dp)
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (checked) Primary else Color.White)
                                    .border(
                                        1.dp,
                                        if (checked) Primary else Color.Black,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            onCartChange(state.orders.map { it.copy(checked = !checked) })
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (checked) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = "Icon Tick",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "All",
                                modifier = Modifier.padding(end = 15.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight(0.7f)
                                .background(Color.Gray)
                        )
                        Text(
                            text = "Total: ${decimalFormat.format(state.getTotalCost())}đ",
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .weight(1f),
                            color = Color.Red
                        )
                        Box(
                            modifier = Modifier
                                .width(130.dp)
                                .fillMaxHeight()
                                .background(Primary)
                                .clickable(
                                    enabled = state.orders.any { it.checked },
                                    onClick = {
                                        val orders = state.orders
                                            .filter { it.checked }
                                            .map { it.order }
                                        checkOut(orders)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Check out (${state.orders.count { it.checked }})",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            if (state.orders.isEmpty()) {
                item {
                    Text(
                        text = "You have not added any products to your cart yet",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp),
                    )
                }
            }
            itemsIndexed(state.orders) { index, cartOrder ->
                CartItem(
                    modifier = Modifier.fillMaxWidth(),
                    cartOrder = cartOrder,
                    onToggleChecked = {
                        val tempList = state.orders.toMutableList()
                        tempList[index] =
                            tempList[index].copy(checked = !tempList[index].checked)
                        onCartChange(tempList)
                    },
                    onClickImage = { viewProduct(cartOrder.order.product) },
                    onEdit = {
                        onEditOrderChange(cartOrder.order)
                        setShowBottomSheet(true)
                    },
                    onDelete = { deleteOrderInCart(cartOrder.order) }
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}


@Composable
fun EditOrderInCart(
    order: Order = Order(),
    onEditOrderChange: (Order) -> Unit = {},
    editOrder: () -> Unit = {},
    hideBottomSheet: () -> Unit = {},
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
                        val scope = rememberCoroutineScope()
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
                                    onEditOrderChange(order.copy(variations = variations))
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
                                            onEditOrderChange(order.copy(quantity = order.quantity - 1))
                                        }
                                    }
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
                                        onEditOrderChange(order.copy(quantity = 0))
                                    }
                                    try {
                                        val newQuantity = it.toLong()
                                        if (newQuantity in 1..showStock) {
                                            onEditOrderChange(order.copy(quantity = newQuantity))
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
                                cursorBrush = SolidColor(Color.Red)
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
                                            onEditOrderChange(order.copy(quantity = order.quantity + 1))
                                        }
                                    }
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
                onClick = editOrder,
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(10.dp),
                enabled = order.quantity in 1..showStock && order.variations.size == order.product.variations.size
            ) {
                Text(text = "Confirm")
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun CartItem(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder = CartOrder(),
    onToggleChecked: () -> Unit = {},
    onClickImage: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val pxValue = with(LocalDensity.current) { 150.dp.toPx() }
    val swipeState = rememberSwipeableState(initialValue = 0)
    Box(
        modifier = modifier.height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(160.dp)
                .background(Color(0xFFF39407)),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(75.dp)
                    .clickable {
                        scope.launch {
                            swipeState.animateTo(0)
                            onEdit()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Edit", color = Color.White)
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(75.dp)
                    .background(Color.Red)
                    .clickable {
                        scope.launch {
                            swipeState.animateTo(0)
                            onDelete()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Delete", color = Color.White)
            }
        }
        Row(
            modifier = Modifier
                .offset {
                    IntOffset(swipeState.offset.value.roundToInt(), 0)
                }
                .fillMaxSize()
                .swipeable(
                    state = swipeState,
                    anchors = mapOf(
                        0f to 0,
                        -pxValue to 1
                    ),
                    orientation = Orientation.Horizontal,
                    thresholds = { _, _ ->
                        FractionalThreshold(0.3f)
                    }
                )
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (cartOrder.checked) Primary else Color.White)
                        .border(
                            1.dp,
                            if (cartOrder.checked) Primary else Color.Black,
                            RoundedCornerShape(5.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleChecked
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (cartOrder.checked) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Icon Tick",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            AsyncImage(
                model = cartOrder.order.product.images[0],
                contentDescription = "image",
                modifier = Modifier
                    .padding(10.dp)
                    .size(100.dp)
                    .clickable(onClick = onClickImage),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(10.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEdit
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cartOrder.order.product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Variations: ${cartOrder.order.getVariationsString()}")
                Text(text = "Quantity: ${cartOrder.order.quantity}")
                Text(
                    text = "Total Cost: ${decimalFormat.format(cartOrder.order.getTotalCost())}đ",
                    color = Color.Red
                )
            }
        }
    }
}