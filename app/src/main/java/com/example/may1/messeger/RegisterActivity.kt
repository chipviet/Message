package com.example.may1.messeger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button.setOnClickListener {
            val email = register_email_text_view.text.toString()
            val password = register_password_text_view.text.toString()
            val username = register_username_text_view.text.toString()

           // database = FirebaseDatabase.getInstance().reference
            if(email.isEmpty()||password.isEmpty()||username.isEmpty()) {
                Toast.makeText(this,"Please enter your email,username or password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("RegisterActivity", "Username:" + username)
            Log.d("RegisterActivity", "Email is:" + email)
            Log.d("RegisterActivity", "Password: $password")

            //writeNewUser()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("RegisterActivity", "signInWithEmail:success : ${it.result.user.uid}")
                        //writeNewUser(task.result?.user!!)
                        uploadImageToFirebaseStorage()
                       //loadDatabase()
                      //  saveUserToFirebaseDatabase()

                    }else {
                        // If sign in fails, display a message to the user.
                        Log.w("RegisterActivity", "signInWithEmail:failure", it.exception)
                        Toast.makeText(this,"Please enter your email,username or password: ${it.exception}",Toast.LENGTH_SHORT).show()
                    }


                    // ...
                }
        }

        login_button_text.setOnClickListener {
            Log.d("RegisterActivity","Go to Login form")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type ="image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)




        if(requestCode ==0 && resultCode == Activity.RESULT_OK && data!= null) {
            //proceed and check what the slected image was ...
            Log.d("RegisterActivity","photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            selected_image_register_circle.setImageBitmap(bitmap)
            selectphoto_button_register.alpha= 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//
//            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Succesfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                       // it.toString()
                        Log.d("RegisterActivity","File Location: $it")

                        saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }
    //Start basic write
    private fun saveUserToFirebaseDatabase(profileImagerURL: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        Log.d("RegisterActivity","fuck u :$uid")

       // database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val ref =FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("RegisterActivity","i want you action :$ref")
        val user = User(uid, register_username_text_view.text.toString(),profileImagerURL )

         ref.setValue(user)

            .addOnSuccessListener {
                Log.d("RegisterActivity", "Success save data to Firebase")

                //open LatestMessegesActivity when you success save data to database
                val intent = Intent(this,LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to set value to database :${it.message}")
            }

    }

}

@Parcelize
class User(val uid: String,val username: String, val profileImagerURL: String) :Parcelable{
    constructor() : this("","","")
}
//data class User(
  //  var username: String? = "",
    //var email: String? = ""
    //val password: String? =""

