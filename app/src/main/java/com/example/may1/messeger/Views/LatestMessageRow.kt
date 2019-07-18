package com.example.may1.messeger.Views

import com.example.may1.messeger.Models.ChatMessage
import com.example.may1.messeger.R
import com.example.may1.messeger.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {

    var chatPartnerUser : User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest.text = chatMessage.text

        val chatPartnerId: String
        if( chatMessage.fromID == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toID
        }
        else {
            chatPartnerId = chatMessage.fromID
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                // put the user name into the latest message row
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_latest_message.text = chatPartnerUser?.username

                // put the user image into the latest message row
                val targetImageView = viewHolder.itemView.image_latest_message_row
                Picasso.get().load(chatPartnerUser?.profileImagerURL).into(targetImageView)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}