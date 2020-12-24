package com.steve.kotlinchat.registerlogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.steve.kotlinchat.R

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var mResetEmail:EditText
    lateinit var mResetCode:Button

    private lateinit var mAuth:FirebaseAuth

    lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mResetEmail=findViewById(R.id.emailReset)
        mResetCode=findViewById(R.id.resetPasswordBtn)

        mAuth= FirebaseAuth.getInstance()
        mProgressDialog= ProgressDialog(this)

        mResetCode.setOnClickListener {

            sendResetCode()
        }


    }

    private fun sendResetCode() {
        mProgressDialog.setMessage("Please Wait...")
        mProgressDialog.show()

        val email=mResetEmail.text.toString().trim()

        if (TextUtils.isEmpty(email)){
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){

                        mProgressDialog.dismiss()


                        Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show()

                        updateUI()


                    }   else{

                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                        mProgressDialog.dismiss()
                    }
                }

        }
    }

    private fun updateUI() {
        val mainIntent = Intent(applicationContext,LoginActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()

    }
}