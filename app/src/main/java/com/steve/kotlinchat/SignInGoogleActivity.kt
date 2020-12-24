package com.steve.kotlinchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.steve.kotlinchat.messages.LatestMessagesActivity
import java.util.*

class SignInGoogleActivity : AppCompatActivity() {

    lateinit var providers : List<AuthUI.IdpConfig>
    private val RC_SIGN_IN = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_google)



        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),   //Email Login
            AuthUI.IdpConfig.GoogleBuilder().build())  //Google Login


        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser //get current user
                startActivity(Intent(this@SignInGoogleActivity,LatestMessagesActivity::class.java))
                finish()
                return
//                Toast.makeText(this, "Welcome" + user!!.email, Toast.LENGTH_SHORT).show()
            } else {
                // Sign in failed, check response for error code
                Toast.makeText(
                    this,
                    "Oops!! Error signing in" + response!!.error!!.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
