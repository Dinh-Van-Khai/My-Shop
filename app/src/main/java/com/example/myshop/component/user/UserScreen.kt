package com.example.myshop.component.user

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@ExperimentalComposeUiApi
@Composable
fun UserScreen(
    navController: NavHostController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is UserEvent.BackToProfile -> {
                    navController.popBackStack()
                }

                is UserEvent.SetShowBottomSheet -> {
                    if (event.status) sheetState.show() else sheetState.hide()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    UserContent(
        sheetState = sheetState,
        state = state,
        onChangeName = viewModel::onChangeName,
        setShowDialogChangeName = viewModel::setShowDialogChangeChangeName,
        onChangeBio = viewModel::onChangeBio,
        setShowDialogChangeBio = viewModel::setShowDialogChangeBio,
        onChangePhoneNumber = viewModel::onChangePhoneNumber,
        setShowDialogChangePhoneNumber = viewModel::setShowDialogChangePhoneNumber,
        onChangeAddress = viewModel::onChangeAddress,
        setShowDialogChangeAddress = viewModel::setShowDialogChangeAddress,
        setChooseImage = viewModel::setChooseImageFor,
        setShowBottomSheet = viewModel::setShowBottomSheet,
        setProfilePictureUri = viewModel::setProfilePicture,
        setCoverPhotoUri = viewModel::setCoverPhoto,
        onBack = { viewModel.onEvent(UserEvent.BackToProfile) },
        onSave = viewModel::updateInformation,
    )
}
