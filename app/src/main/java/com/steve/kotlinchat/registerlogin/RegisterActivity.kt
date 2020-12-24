package com.steve.kotlinchat.registerlogin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.steve.kotlinchat.R
import com.steve.kotlinchat.messages.LatestMessagesActivity
import com.steve.kotlinchat.models.User
import java.util.*

class RegisterActivity : AppCompatActivity() {
    lateinit var registerUsername: EditText
    lateinit var registerEmail: EditText
    lateinit var registerPassword: EditText
    lateinit var AlreadyhaveAccount: TextView
    lateinit var registerBtn: Button
    lateinit var selectPhoto:ImageView
    lateinit var selectPhotobtn:Button
    lateinit var progressBar: ProgressDialog
    lateinit var forgotpassword:TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerUsername = findViewById(R.id.userName)
        registerEmail = findViewById(R.id.registerEmail)
        registerPassword = findViewById(R.id.registerPassword)
        registerBtn = findViewById(R.id.btnRegister)
        AlreadyhaveAccount = findViewById(R.id.alreadyHaveAccount)
        selectPhoto=findViewById(R.id.imageView)
        selectPhotobtn=findViewById(R.id.button)
        progressBar=ProgressDialog(this)
        forgotpassword=findViewById(R.id.ForgotPassword)


        forgotpassword.setOnClickListener {
            val intent=Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        AlreadyhaveAccount.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
        registerBtn.setOnClickListener {
            val username = registerUsername.text.toString()
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()

            if (username.isEmpty()){
                registerUsername.error="Enter Username"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                registerEmail.error="Enter Email"
                return@setOnClickListener
            }

            if (password.isEmpty()){
                registerPassword.error="Enter Password"
                return@setOnClickListener

            }

            registerUser(username,email,password)
        }

        selectPhotobtn.setOnClickListener {
            val intent=Intent(Intent.ACTION_GET_CONTENT)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

    }


    var selectPhotoUri: Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode==Activity.RESULT_OK && requestCode==0 && data !=null){
//
           selectPhotoUri=data.data
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectPhotoUri)
            selectPhoto.setImageBitmap(bitmap)
            selectPhotobtn.alpha=0f
//
//             val uri=data.data
////           selectPhoto.setImageURI(uri)
////           val bitmapDrawable=BitmapDrawable(bitmap)
////            selectPhoto.setBackgroundDrawable(bitmapDrawable)
        }


    }




    private fun registerUser(username:String,email:String,password:String) {
        progressBar.setMessage("Registering User...")
        progressBar.show()
//        Log.d("RegisterActivity", "email is :$email")
//        Log.d("RegisterActivity","Password: $password")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        Log.d("Main", "Succesfully created user: ${it.result!!.user!!.uid}")

                        progressBar.hide()


                        uploadImageToFirebase()
                    }
                    .addOnFailureListener {
                        Log.d("Main", "Failed created user: ${it.message}")
                        Toast.makeText(this, "Failed to create User: ${it.message}", Toast.LENGTH_LONG).show()

                        Toast.makeText(this@RegisterActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()

                        progressBar.hide()
                    }
    }

    private fun uploadImageToFirebase() {
        if (selectPhotoUri==null) return
        val filename=UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Succesfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnCompleteListener {
//                    it.toString()
                    Log.d("RegisterActivity","File Location: $it")

                    saveUserToFirebaseData(it.toString())

                    val intent=Intent(applicationContext, LatestMessagesActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseData(profileImageUrl: String) {
      val uid=FirebaseAuth.getInstance().uid
      val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user= User(uid!!,registerUsername.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Succesfully saved to database")
            }
    }

}
