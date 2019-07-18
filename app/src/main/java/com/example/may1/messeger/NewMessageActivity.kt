package com.example.may1.messeger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Adapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        // This will replace the name of Activity to Select User
        supportActionBar?.title = "Select User"

        //val adapter = GroupAdapter<ViewHolder>()
        // Each adapter.add() is an user
        //adapter.add(UserItem())
        //adapter.add(UserItem())
       // adapter.add(UserItem())

        // In here, i have an error but i don't now how to fix it, i will come back soon :)))
        //fix: replace implementation 'com.xwray:groupie:2.3.0' by implementation 'com.xwray:groupie:2.1.0'
        // don't use latest version if you are following someone, do as they are
        //recycleview_newmessage.adapter = adapter
        //recycleview_newmessage.layoutManager = LinearLayoutManager(this)

        fetchUser()

    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUser() {
        // read the value in the Firebase
     val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessageActivity", it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null) {
                        adapter.add(UserItem(user))
                    }
                    // this represents the number users inside of the Firebase
                }
                // when you click a user the ChatLog will appear
                adapter.setOnItemClickListener{ item, view ->

                    val userItem = item as UserItem
                    val intent = Intent (view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,item.user)
                    startActivity(intent)
                    finish()

                }
                recycleview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}
class UserItem (val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // will be called in our list for each user object
        // it's present the name of users
      viewHolder.itemView.username_textview_new_message.text =  user.username
        // add the image of user
        Picasso.get().load(user.profileImagerURL).into(viewHolder.itemView.image_view_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
        
    }
}
