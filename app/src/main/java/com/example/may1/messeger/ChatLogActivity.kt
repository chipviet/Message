package com.example.may1.messeger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.show_from_message_row.view.*
import kotlinx.android.synthetic.main.show_to_message_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var toUser : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        // warning: if you coding and changing the title of Activity appear the R.layout error,

        //val username =intent.getStringExtra(NewMessageActivity.USER_KEY)

        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        ListernforMessage()
        //SetupDummyData()
        send_message_btn_chatlog.setOnClickListener {
            Log.d(TAG,"attempt to send message.....")
            performSendMessage()
        }


    }
    class ChatMessage( val id: String, val fromID: String, val toID: String, val text: String, val timestamp: Long) {
        constructor() : this("", "","","",-1)
    }


    private fun ListernforMessage() {

        //read the value of user-messages
        val fromID = FirebaseAuth.getInstance().uid
        //val toID = toUser?.uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toID = user.uid

        //val ref = FirebaseDatabase.getInstance().getReference("/message")
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if(chatMessage.fromID == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }
                    else {
                        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {
        // we will send message to database
        val text = enter_message_textview.text.toString()
        val fromID = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toID = user.uid

        if (fromID ==null) return

       //val reference = FirebaseDatabase.getInstance().getReference("/message").push()
        // create a new database "user-messages" content messages
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        val chatMessage = ChatMessage(reference.key!!,fromID,toID,text, System.currentTimeMillis()/1000)
        // note : if you want the real time, taking the System.currentTimeMiliss() into 1000
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, " Successfully to send message to database : ${reference.key}")
                enter_message_textview.text.clear()
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        latestMessageToRef.setValue(chatMessage)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.chat_video -> {
                val intent = Intent(this,VideoChatViewActivity::class.java)
                startActivity(intent)
            }
            R.id.cap_picture -> {
                val intent = Intent(this, CapPictureActivity1::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class  ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // enter the message to textview
        viewHolder.itemView.textview_from_row.text = text

        val uri = user.profileImagerURL
        val targetImageView = viewHolder.itemView.image_from_row
        Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.show_from_message_row
    }
}
class  ChatToItem (val text:String, val user: User): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textview_to_row.text = text

        val uri = user.profileImagerURL
        val targetImageView = viewHolder.itemView.image_to_row
        Picasso.get().load(uri).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.show_to_message_row
    }
}
