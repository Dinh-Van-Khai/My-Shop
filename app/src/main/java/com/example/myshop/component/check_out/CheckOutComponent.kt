package com.example.myshop.component.check_out

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Order
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.decimalFormat

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutContent(
    state: CheckOutState = CheckOutState(),
    onOrdersChange: (List<Order>) -> Unit = {},
    placeOrder: () -> Unit = {},
    onClearFocus: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    if (state.loading) {
        BasicAlertDialog(
            onDismissRequest = {},
            modifier = Modifier.background(color = Color.Transparent)
        ) {
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                modifier = Modifier.size(50.dp)
            )
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClearFocus
            ),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 16.dp,
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
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onBack
                            )
                    )
                    Text(
                        text = "Check Out (${state.orders.size})",
                        fontSize = 20.sp,
                        color = Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        bottomBar = {
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
                    val total = state.orders.sumOf { it.getTotalCost() }
                    Column(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .weight(1f),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Total:")
                        Text(
                            text = "${decimalFormat.format(total)}đ",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .fillMaxHeight()
                            .background(Primary)
                            .clickable (onClick = placeOrder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Place Order",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "Icon Location",
                            modifier = Modifier
                                .padding(5.dp)
                                .size(25.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp)
                        ) {
                            Text(text = "Receiver's Information: ")
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Name: ${state.user.name}")
                            Text(text = "Phone Number: ${state.user.phoneNumber}")
                            Text(text = "Address: ${state.user.address}")
                        }
                    }
                }
                itemsIndexed(state.orders) { index, order ->
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                    CheckOutItem(
                        modifier = Modifier.fillMaxWidth(),
                        order = order,
                        onOrderChange = { newOrder ->
                            val tempList = state.orders.toMutableList()
                            tempList[index] = newOrder
                            onOrdersChange(tempList)
                        }
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(55.dp)
            )
        }
    }
}


@Composable
private fun CheckOutItem(
    modifier: Modifier = Modifier,
    order: Order = Order(),
    onOrderChange: (Order) -> Unit = {}
) {
    Column(modifier = modifier.background(Color.White)) {
        Row {
            AsyncImage(
                model = order.product.images[0],
                contentDescription = "image",
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
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                BasicTextField(
                    value = order.message,
                    onValueChange = {
                        onOrderChange(order.copy(message = it))
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                if (order.message.isEmpty()) {
                    Text(
                        text = "Please leave a message",
                        color = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp),
                    )
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
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
    }
}