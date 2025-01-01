package com.example.myshop.component.review

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.component.edit_product.ChooseImages
import com.example.myshop.component.edit_product.CustomTextField
import com.example.myshop.component.edit_product.readImagePermission
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.ProductConstant
import com.example.myshop.util.decimalFormat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableInteractionSource")
@ExperimentalPermissionsApi
@Composable
fun ReviewContent(
    sheetState: SheetState,
    state: ReviewState = ReviewState(),
    onRateChange: (Int) -> Unit = {},
    onCommentChange: (String) -> Unit = {},
    onListImageCommentChange: (List<Uri>) -> Unit = {},
    onSendReview: () -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    clearFocus: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val readImagePermission = readImagePermission(
        onPermissionGranted = {
            setShowBottomSheet(true)
        }
    )
    if(sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {setShowBottomSheet(false)},
            containerColor =  MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            ChooseImages(
                hideBottomSheet = { setShowBottomSheet(false) },
                maxImages = ProductConstant.MAX_IMAGES_REVIEW,
                currentSize = state.listImagesComment.size,
                currentImages = state.listImagesComment,
                onChangeImages = onListImageCommentChange
            )
        }
    }
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = clearFocus
            ),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Review Product",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Icon Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .size(35.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onBack
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = if (state.order.product.images.isNotEmpty()) state.order.product.images[0] else "",
                    contentDescription = "First Product's image",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(120.dp)
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = state.order.product.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Category: " + state.order.product.getCategory(),
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = "Description:\n${state.order.product.description}",
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Price: ${decimalFormat.format(state.order.product.price)}đ",
                        color = Color.Red,
                        modifier = Modifier.padding(5.dp),
                        fontSize = 18.sp
                    )
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth())
            Text(
                text = "Rate: ",
                modifier = Modifier
                    .padding(5.dp)
                    .padding(start = 10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..5).forEach { index ->
                    Icon(
                        painter = painterResource(
                            id = if (index <= state.rate) R.drawable.rating_star_filled else R.drawable.rating_star_outline
                        ),
                        contentDescription = "Icon star",
                        tint = Primary,
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .size(30.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = { onRateChange(index) }
                            )
                    )
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth())
            Text(
                text = "Image: ", modifier = Modifier
                    .padding(5.dp)
                    .padding(start = 10.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentPadding = PaddingValues(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                itemsIndexed(state.listImagesComment) { index, photoUrl ->
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Image comment",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, Color.Red, CircleShape)
                                .clickable(interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        val tempList = state.listImagesComment.toMutableList()
                                        tempList.removeAt(index)
                                        onListImageCommentChange(tempList)
                                    }
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Icon close",
                                tint = Color.Red,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(15.dp)
                            )
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(5.dp))
                            .border(
                                width = 1.dp, color = Primary, shape = RoundedCornerShape(5.dp)
                            )
                            .clickable(interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    if (readImagePermission.status.isGranted) {
                                        setShowBottomSheet(true)
                                    } else {
                                        readImagePermission.launchPermissionRequest()
                                    }
                                }), contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+ Add", color = Primary)
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth())
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Comment: ",
                hint = "Please leave a message",
                value = state.comment,
                onValueChange = onCommentChange,
                maxLength = ProductConstant.MAX_COMMENT
            )
            HorizontalDivider(Modifier.fillMaxWidth())
            Button(
                onClick = onSendReview,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                enabled = state.rate > 0 && state.listImagesComment.isNotEmpty() && state.comment.length >= 10
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                } else {
                    Text(text = "Send review")
                }
            }
        }
    }
}
