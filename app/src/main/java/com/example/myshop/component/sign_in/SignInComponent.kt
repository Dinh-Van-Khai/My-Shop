package com.example.myshop.component.sign_in

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.R
import com.example.myshop.ui.theme.Primary


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun SignInContent(
    state: SignInState = SignInState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onToggleVisiblePassword: () -> Unit = {},
    onHideKeyBoard: () -> Unit = {},
    onBack: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onSignInWithGoogle: () -> Unit = {},
    onSignInWithFacebook: () -> Unit = {},
    onCreateNewAccount: () -> Unit = {},
) {
    if (state.loadingScreen) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onHideKeyBoard
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Icon Back",
                modifier = Modifier
                    .padding(top = 5.dp, start = 5.dp)
                    .size(40.dp)
                    .align(Alignment.Start)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onBack
                    ),
                tint = Primary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_round),
                    contentDescription = "Logo Shopping Circle",
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .size(70.dp)
                        .clip(CircleShape)
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text(text = "Email", fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    maxLines = 1
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(text = "Password", fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = {
                        IconButton(onClick = onToggleVisiblePassword) {
                            Icon(
                                painter = painterResource(id = if (state.visiblePassword) R.drawable.ic_pw_visible else R.drawable.ic_pw_invisible),
                                contentDescription = "Visible password",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    visualTransformation = if (state.visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    maxLines = 1
                )
                Button(
                    onClick = onSignIn,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = state.email.isNotBlank() && state.password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Primary,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (state.loadingButton) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Text(text = "Log in", fontSize = 16.sp)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Logo google",
                        modifier = Modifier
                            .size(45.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onSignInWithGoogle,
                            )
                    )
                    Text(text = "or", modifier = Modifier.padding(horizontal = 25.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Logo google",
                        modifier = Modifier
                            .size(45.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onSignInWithFacebook
                            )
                    )
                }
            }
            Text(
                text = "Create new account",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onCreateNewAccount
                    )
            )
        }
    }
}
