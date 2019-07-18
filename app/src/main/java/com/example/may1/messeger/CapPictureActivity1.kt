package com.example.may1.messeger

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cap_picture.*
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.show_from_message_row.view.*
import java.util.*

class CapPictureActivity1 : AppCompatActivity() {

    private val PERMISSION_CODE = 1000;
    var photoUri: Uri? = null
    private val IMAGE_CAPTURE_CODE = 1001;

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cap_picture)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if(checkSelfPermission(android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED ) {
                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission,PERMISSION_CODE)
                }
                else {
                    openCamera()
                }
            else {
                openCamera()
            }
        btn_send_image_to_firebase.setOnClickListener {
            uploadImageToFirebaseStorage()
            //performSendPicture()
        }
    }



    private fun openCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, " New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, " From the Camera ")
        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT , photoUri)
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE)
    }

    //var photoUri: Uri? = null
    // choosing an image from library
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode ==0 && resultCode == Activity.RESULT_OK && data!= null) {
//            //proceed and check what the slected image was ...
//            Log.d("RegisterActivity","photo was selected")
//
//            selectedPhotoUri = data.data
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
//
//            selected_image_register_circle.setImageBitmap(bitmap)
//            selectphoto_button_register.alpha= 0f
////            val bitmapDrawable = BitmapDrawable(bitmap)
////
////            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
//        }
//    }

    //Up load image to Firebase
    private fun uploadImageToFirebaseStorage() {
        if (photoUri == null) return

        //val fromID = FirebaseAuth.getInstance().uid
        //val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
       // val toID = user.uid
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/cap_picture/$filename")

        ref.putFile(photoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity","Succesfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        // it.toString()
                        Log.d("RegisterActivity","File Location: $it")
                        saveImagetoFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {

                }
    }

    private fun saveImagetoFirebaseDatabase(profilePictureURL: String) {
        //val uid = FirebaseAuth.getInstance().uid ?: ""
        //val ref =FirebaseDatabase.getInstance().getReference("/pictures/$uid")
        //Log.d("Picture","i want you action :$ref")

        val fromID = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toID = user.uid

        // Creating user-messages to contains the message
        val putPicture =FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        Log.d("Capture Picture","Successfully to save image to database")
       // val reference = User(uid, register_username_text_view.text.toString(),profilePictureURL )
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        val chatMessage = ChatLogActivity.ChatMessage(putPicture.key!!, fromID!!, toID, profilePictureURL, System.currentTimeMillis() / 1000)

        putPicture.setValue(chatMessage)

                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Success save data to Firebase")

                    //open LatestMessegesActivity when you success save data to database
                    val intent = Intent(this, ChatLogActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to set value to database :${it.message}")
                }
        toReference.setValue(chatMessage)
    }

//    private fun performSendPicture() {
//
//
//        //val text = enter_message_textview.text.toString()
//        val fromID = FirebaseAuth.getInstance().uid
//        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
//        val toID = user.uid
//        val uri = user.profileImagerURL
//        //val targetImageView = viewHolder.itemView.image_from_row
//        //Picasso.get().load(uri).into(targetImageView)
//        //checking the id of from user
//        if (fromID ==null) return
//
//        val reference = FirebaseStorage.getInstance().getReference("/cap picture//$fromID/$toID").push()
//
//        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
//        val chatMessage = ChatLogActivity.ChatMessage(reference.key!!, fromID, toID, uri, System.currentTimeMillis() / 1000)
//
//        reference.putFile(chatMessage)
//                .addOnSuccessListener {
//                    Log.d("CapPicture", " Successfully to send image to database : ${reference.key}")
//                    //enter_message_textview.text.clear()
//                }
//        toReference.setValue(chatMessage)
//
//        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
//        latestMessageRef.setValue(chatMessage)
//
//        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$fromID")
//        latestMessageToRef.setValue(chatMessage)
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                }
                else {
                    Toast.makeText(this,"Permission dined", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            user_image_cap_picture.setImageURI(photoUri)
        }
    }
    //Open CapPictureAcitivity
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.cap_picture -> {
                val intent = Intent(this, ChatLogActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
