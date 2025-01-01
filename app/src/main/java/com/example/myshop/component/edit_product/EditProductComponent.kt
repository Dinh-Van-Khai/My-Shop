package com.example.myshop.component.edit_product

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Category
import com.example.myshop.model.Stock
import com.example.myshop.model.Variation
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.ProductConstant
import com.example.myshop.util.decimalFormat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun EditProductContent(
    label: String,
    button: String,
    sheetState: SheetState,
    state: EditProductState = EditProductState(),
    onChangeExistImages: (List<String>) -> Unit = {},
    onChangeImages: (List<Uri>) -> Unit = {},
    onChangeName: (String) -> Unit = {},
    onChangeDescription: (String) -> Unit = {},
    onChangePrice: (Long) -> Unit = {},
    onChangeCategory: (List<String>) -> Unit = {},
    onChangeVariation: (List<Variation>) -> Unit = {},
    setStocks: (List<Stock>) -> Unit = {},
    onAdd: () -> Unit = {},
    setSheetContent: (SheetContent) -> Unit = {},
    onBack: () -> Unit = {},
    onClearFocus: () -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = { setShowBottomSheet(false) },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            tonalElevation = 0.dp
        ) {
            when (state.sheetContent) {
                SheetContent.DEFAULT -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                SheetContent.IMAGES -> {
                    ChooseImages(
                        currentImages = state.images,
                        currentSize = state.product.images.size + state.images.size,
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onChangeImages = onChangeImages,
                    )
                }

                SheetContent.CATEGORY -> {
                    Category(
                        categories = state.categories,
                        currentCategory = state.currentCategory,
                        category = state.product.category,
                        setCategory = onChangeCategory,
                        hideBottomSheet = { setShowBottomSheet(false) }
                    )
                }

                SheetContent.VARIATIONS -> {
                    Variation(
                        variations = state.product.variations,
                        hideBottomSheet = { setShowBottomSheet(false) },
                        onChangeVariation = onChangeVariation,
                    )
                }

                SheetContent.STOCK -> {
                    Stock(
                        variations = state.product.variations,
                        hideBottomSheet = { setShowBottomSheet(false) },
                        stocks = state.product.stocks,
                        setStocks = setStocks
                    )
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClearFocus
            ),
        topBar = {
            Surface(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
                shadowElevation = 15.dp,
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Icon back",
                            tint = Primary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 5.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        onClearFocus()
                                        onBack()
                                    })
                        )
                        Text(
                            text = label,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_image),
                        contentDescription = "Icon image",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(25.dp)
                    )
                    Text(text = "Image(${state.images.size + state.product.images.size}/9): ")
                }
                ProductImages(
                    modifier = Modifier.fillMaxWidth(),
                    existImages = state.product.images,
                    onChangeExistImages = onChangeExistImages,
                    images = state.images,
                    onChangeImages = onChangeImages,
                    setSheetContent = setSheetContent,
                    setShowBottomSheet = setShowBottomSheet,
                )
                Spacer(modifier = Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Product Name",
                    hint = "Enter Product Name",
                    value = state.product.name,
                    onValueChange = onChangeName,
                    maxLength = ProductConstant.MAX_NAME,
                    imeAction = ImeAction.Next
                )
                Spacer(modifier = Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Product Description",
                    hint = "Enter Product Description",
                    value = state.product.description,
                    onValueChange = onChangeDescription,
                    maxLength = ProductConstant.MAX_DESCRIPTION,
                    imeAction = ImeAction.Default
                )
                Spacer(modifier = Modifier.height(10.dp))
                ItemEditProduct(
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.ic_categories,
                    label = "Category: ",
                    value = state.product.getCategory(),
                    onClick = {
                        onClearFocus()
                        setSheetContent(SheetContent.CATEGORY)
                        setShowBottomSheet(true)
                    }
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                ItemEditProduct(
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.ic_variations,
                    label = "Variation",
                    value = "",
                    onClick = {
                        onClearFocus()
                        setSheetContent(SheetContent.VARIATIONS)
                        setShowBottomSheet(true)
                    }
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                ItemEditProduct(
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.ic_stock,
                    label = "Stock",
                    value = "",
                    onClick = {
                        onClearFocus()
                        setSheetContent(SheetContent.STOCK)
                        setShowBottomSheet(true)
                    }
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                ItemProductPrice(
                    price = state.product.price,
                    onChangePrice = onChangePrice
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 15.dp
            ) {
                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = Color.White,
                            modifier = Modifier.size(25.dp)
                        )
                    } else {
                        Text(text = button)
                    }
                }
            }
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChooseImages(
    maxImages: Int = ProductConstant.MAX_IMAGES,
    currentSize: Int = 0,
    currentImages: List<Uri> = emptyList(),
    hideBottomSheet: () -> Unit = {},
    onChangeImages: (List<Uri>) -> Unit = {},
) {
    var mediaList by remember {
        mutableStateOf(emptyList<Uri>())
    }

    var selectedImages by remember {
        mutableStateOf(emptyList<Uri>())
    }

    val queryUri: Uri = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.DATE_ADDED
    )
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
    )
    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        context.contentResolver.query(
            queryUri, projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(queryUri, id)
                mediaList = mediaList + contentUri
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = hideBottomSheet
                    ),
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Icon close"
            )
            Text(
                text = "Choose Images", fontSize = 26.sp, modifier = Modifier.weight(1f)
            )
            Text(
                text = "${selectedImages.size + currentSize}/$maxImages",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(text = "Add",
                fontSize = 20.sp,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            onChangeImages(currentImages + selectedImages)
                            selectedImages = emptyList()
                            hideBottomSheet()
                        }
                    )
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyVerticalGrid(columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            content = {
                items(mediaList) { uri ->
                    Box(modifier = Modifier
                        .aspectRatio(1f)
                        .clickable(interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (!selectedImages.contains(uri)) {
                                    if (selectedImages.size + currentSize < maxImages) {
                                        selectedImages = selectedImages + uri
                                    }
                                } else {
                                    val tempImageList = selectedImages.toMutableList()
                                    tempImageList.remove(uri)
                                    selectedImages = tempImageList
                                }
                            })) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (selectedImages.contains(uri)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.onBackground.copy(0.2f))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(5.dp)
                                    .size(25.dp)
                                    .border(2.dp, Color.White, CircleShape)
                                    .clip(CircleShape)
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check),
                                    contentDescription = "Icon check",
                                    modifier = Modifier.size(15.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource", "AutoboxingStateCreation")
@ExperimentalAnimationApi
@Composable
fun Category(
    categories: List<Category> = emptyList(),
    currentCategory: Category = Category(),
    category: List<String> = emptyList(),
    setCategory: (List<String>) -> Unit = {},
    hideBottomSheet: () -> Unit = {}
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = hideBottomSheet
                    ),
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Icon close"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Choose Category",
                fontSize = 26.sp,
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            selectedTabIndex = selectedIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = Primary,
        ) {
            Tab(
                selected = selectedIndex == 0,
                onClick = { selectedIndex = 0 },
                selectedContentColor = Primary,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = if (category.isEmpty()) "Please Choose" else category[0]
                )
            }
            if (category.isNotEmpty()) {
                Tab(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    selectedContentColor = Primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    Text(
                        text = if (category.size <= 1) "Please Choose" else category[1]
                    )
                }
            } else {
                Spacer(modifier = Modifier.fillMaxWidth(0.5f))
            }
        }
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                slideInHorizontally(
                                animationSpec = tween(200),
                                initialOffsetX = { if(selectedIndex == 0) -it else it }
                            ).togetherWith(slideOutHorizontally(
                    animationSpec = tween(200),
                    targetOffsetX = { if (selectedIndex == 0) it else -it }
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            content = { index ->
                if (index == 0) {
                    LazyColumn {
                        items(count = categories.size, key = {it}) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        setCategory(listOf(categories[it].name))
                                        selectedIndex = 1
                                    }
                            ) {
                                Text(
                                    text = categories[it].name,
                                    modifier = Modifier.padding(15.dp),
                                    color = if (category.contains(categories[it].name)) Primary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }

                    }
                } else {
                    LazyColumn {
                        items(count = currentCategory.child.size, key = {it}) {
                            val childName = currentCategory.child[it]
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        setCategory(category + childName)
                                        hideBottomSheet()
                                    }
                            ) {
                                Text(
                                    text = childName,
                                    modifier = Modifier.padding(15.dp),
                                    color = if (category.contains(childName)) Primary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }, label = ""
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@ExperimentalFoundationApi
@Composable
fun Variation(
    variations: List<Variation> = emptyList(),
    hideBottomSheet: () -> Unit = {},
    onChangeVariation: (List<Variation>) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            focusManager.clearFocus()
                            hideBottomSheet()
                        }
                    ),
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Icon close"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Variations",
                fontSize = 26.sp,
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        variations.forEachIndexed { index, variation ->
            VariationItem(
                variation = variation,
                onVariationChange = {
                    val tempList = variations.toMutableList()
                    tempList[index] = it
                    onChangeVariation(tempList)
                },
                onDeleteVariation = {
                    val tempList = variations.toMutableList()
                    tempList.removeAt(index)
                    onChangeVariation(tempList)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (variations.size < 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable {
                        onChangeVariation(variations + Variation())
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Icon add",
                    modifier = Modifier
                        .padding(15.dp)
                        .size(25.dp)
                )
                Text(text = "Add Variation")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@ExperimentalFoundationApi
@Composable
fun VariationItem(
    variation: Variation = Variation(),
    onVariationChange: (Variation) -> Unit = {},
    onDeleteVariation: () -> Unit = {}
) {
    var editing by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = variation.name,
                    onValueChange = {
                        if (it.length <= 30) {
                            onVariationChange(variation.copy(name = it))
                        }
                    },
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    enabled = editing
                )
                if (variation.name.isEmpty()) {
                    Text(
                        text = "Variation Name",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }
            if (editing) {
                Text(
                    text = "Delete", color = Primary, modifier = Modifier
                        .padding(15.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onDeleteVariation
                        )
                )
            }
            Text(
                text = if (editing) "Done" else "Edit",
                color = Primary,
                modifier = Modifier
                    .padding(15.dp)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { editing = !editing }
                    )
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            content = {
                itemsIndexed(variation.child) { index, value ->
                    Box(
                        modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = value,
                            onValueChange = {
                                if (it.length <= 20) {
                                    val tempList = variation.child.toMutableList()
                                    tempList[index] = it
                                    onVariationChange(variation.copy(child = tempList))
                                }
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp),
                            enabled = editing,
                            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 14.sp),
                            singleLine = true
                        )
                        if (editing) {
                            Box(modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp, color = Primary, shape = CircleShape
                                )
                                .background(MaterialTheme.colorScheme.background)
                                .clickable {
                                    val tempChild = variation.child.toMutableList()
                                    tempChild.removeAt(index)
                                    onVariationChange(variation.copy(child = tempChild))
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Icon delete",
                                    tint = Primary,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(10.dp),
                                )
                            }
                        }
                    }
                }
                if (editing) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp, color = Primary, shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    onVariationChange(variation.copy(child = variation.child + ""))
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+ Add", modifier = Modifier.padding(5.dp), color = Primary)
                        }
                    }
                }
            }
        )
    }
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun Stock(
    variations: List<Variation> = emptyList(),
    stocks: List<Stock> = emptyList(),
    hideBottomSheet: () -> Unit = {},
    setStocks: (List<Stock>) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        if (stocks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "You have to complete variations before",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                focusManager.clearFocus()
                                hideBottomSheet()
                            }
                        ),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Icon close"
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Stock",
                    fontSize = 26.sp,
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        variations.forEach {
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .padding(15.dp)
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                color = Primary
                            )
                        }
                        Text(
                            text = "Stock",
                            modifier = Modifier
                                .padding(15.dp)
                                .weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = Primary
                        )
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
                itemsIndexed(stocks) { index, stock ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stock.variations.forEach { variation ->
                            Text(
                                text = variation,
                                modifier = Modifier
                                    .padding(15.dp)
                                    .weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = if (stock.quantity > 0) stock.quantity.toString() else "",
                            onValueChange = {
                                try {
                                    if (it.isBlank()) {
                                        val tempList = stocks.toMutableList()
                                        tempList[index] = tempList[index].copy(quantity = 0)
                                        setStocks(tempList)
                                    } else if (it.length <= 7 && it.toLong() > 0) {
                                        val tempList = stocks.toMutableList()
                                        tempList[index] =
                                            tempList[index].copy(quantity = it.toLong())
                                        setStocks(tempList)
                                    }
                                } catch (_: Exception) {
                                }
                            },
                            modifier = Modifier
                                .padding(15.dp)
                                .weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalFoundationApi
@Composable
fun ProductImages(
    modifier: Modifier = Modifier,
    existImages: List<String> = emptyList(),
    onChangeExistImages: (List<String>) -> Unit = {},
    images: List<Uri> = emptyList(),
    onChangeImages: (List<Uri>) -> Unit = {},
    setSheetContent: (SheetContent) -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {}
) {
    val readImagePermission = readImagePermission(
        onPermissionGranted = {
            setSheetContent(SheetContent.IMAGES)
            setShowBottomSheet(true)
        }
    )
    val height = LocalConfiguration.current.screenWidthDp.dp / 4
    LazyVerticalStaggeredGrid(modifier = modifier
        .height(height * (1 + images.size / 4))
        .background(MaterialTheme.colorScheme.background),
        columns = StaggeredGridCells.Fixed(4),
        content = {
            itemsIndexed(existImages) { index, url ->
                ItemProductImage(
                    model = url,
                    onDelete = {
                        val tempList = existImages.toMutableList()
                        tempList.removeAt(index)
                        onChangeExistImages(tempList)
                    }
                )
            }
            itemsIndexed(images) { index, uri ->
                ItemProductImage(
                    model = uri,
                    onDelete = {
                        val tempList = images.toMutableList()
                        tempList.removeAt(index)
                        onChangeImages(tempList)
                    }
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(5.dp))
                        .padding(10.dp)
                        .border(
                            width = 1.dp, color = Primary, shape = RoundedCornerShape(5.dp)
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (readImagePermission.status.isGranted) {
                                    setSheetContent(SheetContent.IMAGES)
                                    setShowBottomSheet(true)
                                } else {
                                    setShowBottomSheet(false)
                                    readImagePermission.launchPermissionRequest()
                                }
                            }), contentAlignment = Alignment.Center
                ) {
                    Text(text = "+ Add", color = Primary)
                }
            }
        }
    )
}

@Composable
private fun ItemProductImage(
    model: Any?,
    onDelete: () -> Unit = {}
) {
    Box {
        AsyncImage(
            model = model,
            contentDescription = "Image",
            modifier = Modifier
                .padding(10.dp)
                .aspectRatio(1f)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .border(
                    width = 1.dp, color = Primary, shape = CircleShape
                )
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onDelete)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Icon delete",
                tint = Primary,
                modifier = Modifier
                    .padding(5.dp)
                    .size(10.dp),
            )
        }
    }
}


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String = "",
    hint: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    maxLength: Int = 150,
    imeAction: ImeAction = ImeAction.Done,
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(15.dp),
            text = label
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(15.dp),
            text = "${value.length}/$maxLength",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .padding(top = 30.dp),
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            cursorBrush = SolidColor(Primary),
        )
        if (value.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .padding(top = 30.dp),
                text = hint,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ItemEditProduct(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Icon",
            modifier = Modifier
                .padding(15.dp)
                .size(25.dp)
        )
        Text(text = label, modifier = Modifier.padding(end = 10.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            maxLines = 1
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_next),
            contentDescription = "Icon next",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(25.dp)
        )
    }
}


@Composable
fun ItemProductPrice(
    price: Long = 0,
    onChangePrice: (Long) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_price),
            contentDescription = "Icon price",
            modifier = Modifier
                .padding(15.dp)
                .size(25.dp)
        )
        Text(text = "Price: ", modifier = Modifier.padding(vertical = 15.dp))
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = if (price > 0) {
                    TextFieldValue(
                        text = price.toString(),
                        selection = TextRange(price.toString().length)
                    )
                } else {
                    TextFieldValue()
                },
                onValueChange = {
                    try {
                        if (it.text.isBlank()) {
                            onChangePrice(0)
                        } else if (it.text.length <= 12 && it.text.toLong() > 0) {
                            onChangePrice(it.text.toLong())
                        }
                    } catch (_: Exception) {}
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.End,
                    color = Color.Transparent,
                    fontSize = 18.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                cursorBrush = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = try {
                    if (price > 0) {
                        decimalFormat.format(price)
                    } else {
                        "Set"
                    }
                } catch (_: Exception) {
                    "Set"
                },
                fontSize = 18.sp,
                textAlign = TextAlign.End,
                color = if (price > 0)
                    MaterialTheme.colorScheme.onBackground
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
        }
        Text(
            text = "đ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 15.dp, bottom = 15.dp, end = 15.dp),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun readImagePermission(
    onPermissionGranted: () -> Unit,
) : PermissionState {
    var showDialogGoToSetting by remember { mutableStateOf(false) }
    val readImagePermission = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_EXTERNAL_STORAGE
        else
            Manifest.permission.READ_MEDIA_IMAGES,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                showDialogGoToSetting = true
            }
        }
    )
    val context = LocalContext.current
    if (showDialogGoToSetting) {
        AlertDialog(
            onDismissRequest = { showDialogGoToSetting = false },
            shape = RoundedCornerShape(15.dp),
            title = {
                Text(
                    text = "Permission Denied",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Text(
                    text = "Permission denied, please go to settings to allow.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                    showDialogGoToSetting = false
                }) {
                    Text(text = "Go to Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogGoToSetting = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
    return readImagePermission
}