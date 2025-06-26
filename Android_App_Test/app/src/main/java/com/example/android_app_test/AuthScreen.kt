package com.example.android_app_test
import com.example.android_app_test.R
import androidx.compose.runtime.Composable
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    val googleSignInLauncher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(context, "Google Sign-In Success", Toast.LENGTH_SHORT).show()
                        onAuthSuccess()
                    } else {
                        Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Sign-In Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, options)
        val signInIntent = client.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isSignUp) "Create Account" else "Welcome Back", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (isSignUp) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isSignUp) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                            } else {
                                Toast.makeText(context, "Sign up failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                            } else {
                                Toast.makeText(context, "Login failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSignUp) "Sign Up" else "Sign In")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            if (email.isNotBlank()) {
                auth.sendPasswordResetEmail(email)
                Toast.makeText(context, "Reset link sent to $email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Enter your email first", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Forgot Password?")
        }

        TextButton(onClick = { isSignUp = !isSignUp }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { launchGoogleSignIn() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In with Google")
        }
    }
}

