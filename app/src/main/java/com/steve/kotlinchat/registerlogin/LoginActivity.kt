package com.steve.kotlinchat.registerlogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.steve.kotlinchat.R
import com.steve.kotlinchat.messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {

    lateinit var loginEmail: EditText
    lateinit var loginPassword: EditText
    lateinit var loginBtn: Button
    lateinit var progressBar:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginEmail=findViewById(R.id.loginEmail)
        loginPassword=findViewById(R.id.loginPassword)
        loginBtn=findViewById(R.id.loginBtn)
        progressBar= ProgressDialog(this)

        loginBtn.setOnClickListener {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            if (email.isEmpty()) {
                loginEmail.error="Enter Email"
                return@setOnClickListener
            }

            if (password.isEmpty()){
                loginPassword.error="Enter Password"
                return@setOnClickListener

            }
            loginUser(email,password)
        }


    }

    private fun loginUser(email:String,password:String) {

        progressBar.setMessage("Please Wait...")
//        Log.d("RegisterActivity","email is :" +email)
//        Log.d("RegisterActivity","Password: $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressBar.setMessage("Please Wait...")
                progressBar.show()
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Main","Succesfully created user: ${it.result?.user?.uid}")
                progressBar.hide()

                val intent=Intent(this,LatestMessagesActivity::class.java)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("Main","Failed created user: ${it.message}")
            }
    }

    }
