package me.huizengek.kpninteractievetv.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.huizengek.kpnclient.KpnClient
import me.huizengek.kpnclient.requests.login
import me.huizengek.kpninteractievetv.Database
import me.huizengek.kpninteractievetv.LocalNavigator
import me.huizengek.kpninteractievetv.R
import me.huizengek.kpninteractievetv.models.Session
import me.huizengek.kpninteractievetv.preferences.KpnPreferences
import me.huizengek.kpninteractievetv.util.bold

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination
@Composable
fun AddAccountScreen() {
    val focusManager = LocalFocusManager.current
    val navigator = LocalNavigator.current

    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }

    val isLoggedIn by KpnClient.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) navigator.navigateUp()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.confirm_add),
                style = MaterialTheme.typography.headlineLarge.bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            var username by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var loading by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.focusRequester(focusRequester),
                maxLines = 1,
                singleLine = true,
                placeholder = { Text(text = stringResource(R.string.email)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.person),
                        contentDescription = null
                    )
                },
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                maxLines = 1,
                singleLine = true,
                placeholder = { Text(text = stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null
                    )
                },
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        loading = true
                        if (
                            !KpnClient.login(
                                username = username,
                                password = password,
                                deviceId = KpnPreferences.deviceId
                            )
                        ) return@launch
                        Database.insert(
                            Session(
                                username = username,
                                password = password
                            )
                        )
                        loading = false
                    }
                },
                enabled = !loading
            ) {
                Text(text = stringResource(R.string.add))
            }
        }
    }
}
