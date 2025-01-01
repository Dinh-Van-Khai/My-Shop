package com.example.myshop.component.my_shop_purchase

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Order
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.OrderStatus.CANCELLED
import com.example.myshop.util.OrderStatus.ORDERED
import com.example.myshop.util.OrderStatus.SHIPPED
import com.example.myshop.util.OrderStatus.SHIPPING
import com.example.myshop.util.decimalFormat
import java.text.SimpleDateFormat

@SuppressLint("UnrememberedMutableInteractionSource", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyShopPurchaseContent(
    state: MyShopPurchaseState = MyShopPurchaseState(),
    setTab: (Int) -> Unit = {},
    onConfirmOrder: (Order) -> Unit = {},
    onCancelOrder: (Order) -> Unit = {},
    onConfirmShipped: (Order) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 16.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = onBack
                                )
                        )
                        Text(
                            text = "My Shop's Purchase",
                            fontSize = 20.sp,
                            color = Primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    val tabs = listOf("All", ORDERED, SHIPPING, SHIPPED, CANCELLED)
                    ScrollableTabRow(
                        selectedTabIndex = state.currentTab,
                        containerColor = Color.White,
                        contentColor = Primary,
                        edgePadding = 0.dp
                    ) {
                        tabs.forEachIndexed { index, tabName ->
                            val numberOfOrders = when (index) {
                                0 -> state.allOrders.size
                                1 -> state.orderedOrders.size
                                2 -> state.shippingOrders.size
                                3 -> state.shippedOrders.size
                                4 -> state.cancelledOrders.size
                                else -> 0
                            }
                            Tab(
                                selected = index == state.currentTab,
                                onClick = { setTab(index) },
                                text = {
                                    Text(
                                        text = "$tabName${if (numberOfOrders > 0) " ($numberOfOrders)" else ""}",
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                },
                                selectedContentColor = Primary,
                                unselectedContentColor = Color.Black
                            )
                        }
                    }
                }
            }
        }
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = state.currentTab,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = {
                        if (state.oldTab > state.currentTab) -it else it
                    }
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = {
                        if (state.oldTab > state.currentTab) it else -it
                    }
                )
            },
            content = { tabIndex ->
                when (tabIndex) {
                    //All
                    0 -> MyShopPurchaseTag(
                        orders = state.allOrders,
                        onConfirmOrder = onConfirmOrder,
                        onCancelOrder = onCancelOrder,
                        onConfirmShipped = onConfirmShipped
                    )

                    //Ordered
                    1 -> MyShopPurchaseTag(
                        orders = state.orderedOrders,
                        onConfirmOrder = onConfirmOrder,
                        onCancelOrder = onCancelOrder,
                    )

                    //Shipping
                    2 -> MyShopPurchaseTag(
                        orders = state.shippingOrders,
                        onCancelOrder = onCancelOrder,
                        onConfirmShipped = onConfirmShipped
                    )

                    //Shipped, Cancelled
                    3 -> MyShopPurchaseTag(state.shippedOrders)
                    4 -> MyShopPurchaseTag(state.cancelledOrders)
                }
            }, label = ""
        )
    }
}

@Composable
fun MyShopPurchaseTag(
    orders: List<Order> = emptyList(),
    onConfirmOrder: (Order) -> Unit = {},
    onCancelOrder: (Order) -> Unit = {},
    onConfirmShipped: (Order) -> Unit = {}
) {
    LazyColumn {
        if (orders.isEmpty()) {
            item {
                Text(
                    text = "No Order yet",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        itemsIndexed(orders) { index, order ->
            MyShopPurchaseItem(
                modifier = Modifier.fillMaxWidth(),
                order = order,
                onConfirmOrder = onConfirmOrder,
                onCancelOrder = onCancelOrder,
                onConfirmShipped = onConfirmShipped
            )
            if (index != orders.size - 1) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun MyShopPurchaseItem(
    modifier: Modifier = Modifier,
    order: Order = Order(),
    onConfirmOrder: (Order) -> Unit = {},
    onCancelOrder: (Order) -> Unit = {},
    onConfirmShipped: (Order) -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
    Column(modifier = modifier.background(Color.White)) {
        Row {
            AsyncImage(
                model = order.product.images[0],
                contentDescription = "Product's First Image",
                modifier = Modifier
                    .padding(10.dp)
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = order.product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Variations: ${order.getVariationsString()}")
                Text(text = "Price: ${decimalFormat.format(order.product.price)}đ")
                Text(text = "Quantity: ${order.quantity}")
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Message: ",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = order.message.ifBlank { "No message" },
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
                    .padding(start = 10.dp),
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ordered Date: ",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = order.orderedDate?.let { dateFormat.format(it) } ?: "-",
                modifier = Modifier.padding(10.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        if (order.status == SHIPPED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Shipped Date: ",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = order.shippedDate?.let { dateFormat.format(it) } ?: "-",
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        if (order.status == CANCELLED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cancelled Date: ",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = order.cancelledDate?.let { dateFormat.format(it) } ?: "-",
                    modifier = Modifier.padding(10.dp),
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Status: ",
                modifier = Modifier.padding(10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100))
                    .background(
                        when (order.status) {
                            ORDERED -> Primary
                            SHIPPING -> Color.Blue
                            SHIPPED -> Color.Green
                            CANCELLED -> Color.Red
                            else -> Color.Black
                        }
                    )
            ) {
                Text(
                    text = order.status,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Color.White
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Order Total (${order.quantity} item): ",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "${decimalFormat.format(order.getTotalCost())}đ",
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.padding(10.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        if(order.status == ORDERED || order.status == SHIPPING) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        when (order.status) {
                            ORDERED -> onConfirmOrder(order)
                            SHIPPING -> onConfirmShipped(order)
                        }
                    }
            ) {
                Text(
                    text = when (order.status) {
                        ORDERED -> "Confirm Order"
                        SHIPPING -> "Confirm Shipped"
                        else -> return@Box
                    },
                    textAlign = TextAlign.Center,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{ onCancelOrder(order) }
        ) {
            Text(
                text = when (order.status) {
                    ORDERED, SHIPPING -> "Cancel"
                    else -> return@Box
                },
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}