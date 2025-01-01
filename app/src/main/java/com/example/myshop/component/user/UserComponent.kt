package com.example.myshop.component.user

import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.component.edit_product.readImagePermission
import com.example.myshop.ui.theme.Inactive
import com.example.myshop.ui.theme.Primary
import com.example.myshop.ui.theme.shimmerEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted


@SuppressLint("UnrememberedMutableInteractionSource")
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@ExperimentalComposeUiApi
@Composable
fun UserContent(
    sheetState: SheetState,
    state: UserState = UserState(),
    onChangeName: (String) -> Unit = {},
    setShowDialogChangeName: (Boolean) -> Unit = {},
    onChangeBio: (String) -> Unit = {},
    setShowDialogChangeBio: (Boolean) -> Unit = {},
    onChangePhoneNumber: (String) -> Unit = {},
    setShowDialogChangePhoneNumber: (Boolean) -> Unit = {},
    onChangeAddress: (String) -> Unit = {},
    setShowDialogChangeAddress: (Boolean) -> Unit = {},
    setChooseImage: (ChooseImage) -> Unit = {},
    setShowBottomSheet: (Boolean) -> Unit = {},
    setProfilePictureUri: (Uri?) -> Unit = {},
    setCoverPhotoUri: (Uri?) -> Unit = {},
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    if (state.isLoading) {
        AlertDialog(
            onDismissRequest = {},
            shape = RoundedCornerShape(15.dp),  // Thêm shape nếu muốn
            title = {
                Text(text = "Loading...", fontSize = 20.sp, color = Primary)
            },
            text = {
                CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 4.dp)
            },
            confirmButton = {
                Button(onClick = { /* Do something */ }) {
                    Text("OK")
                }
            }
        )

    }

    val readImagePermission = readImagePermission(
        onPermissionGranted = {
            setShowBottomSheet(true)
        }
    )

    //Dialog for change information
    ChangeInformationDialog(
        visible = state.changeName,
        label = "Change Name",
        originalContent = state.user.name,
        onChange = onChangeName,
        onDismiss = setShowDialogChangeName,
        maxLengthContent = 30,
    )
    ChangeInformationDialog(
        visible = state.changeBio,
        label = "Change Bio",
        originalContent = state.user.bio,
        onChange = onChangeBio,
        onDismiss = setShowDialogChangeBio,
        maxLines = 4,
        maxLengthContent = 200
    )
    ChangeInformationDialog(
        visible = state.changePhoneNumber,
        label = "Change Phone Number",
        originalContent = state.user.phoneNumber,
        onChange = onChangePhoneNumber,
        onDismiss = setShowDialogChangePhoneNumber,
        maxLengthContent = 15
    )
    ChangeInformationDialog(
        visible = state.changeAddress,
        label = "Change Address",
        originalContent = state.user.address,
        onChange = onChangeAddress,
        onDismiss = setShowDialogChangeAddress,
        maxLengthContent = 200
    )
    if(sheetState.isVisible) {
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = { setShowBottomSheet(false) },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            tonalElevation = 0.dp
        ) {
            ChooseImage(
                hideBottomSheet = { setShowBottomSheet(false) },
                onChooseImage = { uri ->
                    when (state.chooseImage) {
                        ChooseImage.PROFILE_PICTURE -> {
                            setProfilePictureUri(uri)
                        }

                        ChooseImage.COVER_PHOTO -> {
                            setCoverPhotoUri(uri)
                        }
                    }
                }
            )
        }

    }
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 16.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Icon Back",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp)
                            .align(Alignment.CenterStart)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onBack
                            ),
                        tint = Primary
                    )
                    Text(
                        text = "My Account",
                        fontSize = 24.sp,
                        color = Primary,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    TextButton(
                        onClick = onSave,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 5.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            color = Primary,
                            fontWeight = FontWeight.Bold
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        setChooseImage(ChooseImage.COVER_PHOTO)
                        if (readImagePermission.status.isGranted) {
                            setShowBottomSheet(true)
                        } else {
                            readImagePermission.launchPermissionRequest()
                        }
                    }
                ) {
                    AsyncImage(
                        model = if (state.coverPhoto != null)
                            state.coverPhoto
                        else
                            state.user.coverPhoto,
                        contentDescription = "Cover Photo",
                        modifier = if (state.user.coverPhoto.isBlank()) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier
                                .fillMaxSize()
                                .shimmerEffect()
                        },
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(5.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Icon pencil",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Edit", fontSize = 18.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(120.dp)
                        .align(Alignment.BottomStart)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .clickable {
                            setChooseImage(ChooseImage.PROFILE_PICTURE)
                            if (readImagePermission.status.isGranted) {
                                setShowBottomSheet(true)
                            } else {
                                readImagePermission.launchPermissionRequest()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = if (state.profilePicture != null)
                            state.profilePicture
                        else
                            state.user.profilePicture.ifBlank { R.drawable.user },
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(5.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Icon pencil",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Edit", fontSize = 18.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ItemUser(label = "Name",
                content = state.user.name,
                onClick = { setShowDialogChangeName(true) })
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemUser(label = "Bio",
                content = state.user.bio,
                onClick = { setShowDialogChangeBio(true) })
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemUser(
                label = "Email",
                content = state.user.email,
                showIconNext = false,
                enable = false
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemUser(label = "Phone Number",
                content = state.user.phoneNumber,
                onClick = { setShowDialogChangePhoneNumber(true) })
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemUser(label = "Address",
                content = state.user.address,
                onClick = { setShowDialogChangeAddress(true) })
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
        }
    }
    BackHandler(
        enabled = sheetState.isVisible,
        onBack = { setShowBottomSheet(false) }
    )
}

@ExperimentalComposeUiApi
@Composable
fun ChangeInformationDialog(
    visible: Boolean = false,
    label: String = "",
    originalContent: String = "",
    maxLines: Int = 1,
    maxLengthContent: Int = 30,
    onChange: (String) -> Unit = {},
    onDismiss: (Boolean) -> Unit = {},
) {
    if (visible) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        var content by remember {
            mutableStateOf(
                TextFieldValue(
                    text = originalContent,
                    selection = TextRange(originalContent.length)
                )
            )
        }
        val focusRequester = remember { FocusRequester() }
        AlertDialog(
            onDismissRequest = { onDismiss(false) },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = label,
                    fontSize = 26.sp,
                    color = Primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            if (it.text.length < maxLengthContent) {
                                content = it
                            }
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        maxLines = maxLines
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onChange(content.text)
                        onDismiss(false)
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Change")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss(false) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Cancel")
                }
            }
        )

    }
}

@Composable
fun ItemUser(
    modifier: Modifier = Modifier,
    label: String = "",
    content: String = "",
    showIconNext: Boolean = true,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick, enabled = enable),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = content.ifBlank { "Set Now" },
            color = if(content.isBlank()) Inactive else MaterialTheme.colorScheme.onBackground,
            modifier = if(showIconNext) Modifier.weight(1f) else Modifier.padding(horizontal = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
        if (showIconNext) {
            Image(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "Icon Next",
                modifier = Modifier
                    .size(45.dp)
                    .padding(horizontal = 5.dp),
            )
        }
    }
}

@Composable
fun ChooseImage(
    hideBottomSheet: () -> Unit = {},
    onChooseImage: (Uri) -> Unit = {},
) {
    var mediaList by remember {
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

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
                text = "Choose Image", fontSize = 26.sp, modifier = Modifier.align(Alignment.Center)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
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
                                onChooseImage(uri)
                                hideBottomSheet()
                            }
                        )
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        )
    }
}