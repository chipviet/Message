package com.example.may1.messeger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity () {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)



        login_button.setOnClickListener {
            val email = email_editted_login.text.toString()
            val password = password_editted_login.text.toString()

            Log.d("Login","Attempt login with email and password: $email")

//            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
//                .addOnSuccessListener (this) {
//                    //if (!it.isSuccessful) return@addOnSuccessListener
//
//                    if(it.i)
//
//                }
            if(!email.isEmpty() && !password.isEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            val intent = Intent(this,LatestMessagesActivity::class.java)
                            startActivity(intent)
                            Log.d("LoginActivity","Successfully to Login Account")
                        }
                        else {
                            Log.d("LoginActivity","Failed to Login Account")
                        }
                    }

            }
        }

        back_to_registration_textview.setOnClickListener {
            finish()
        }

    }

}