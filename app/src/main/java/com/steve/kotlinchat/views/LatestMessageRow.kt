package com.steve.kotlinchat.views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.steve.kotlinchat.R
import com.steve.kotlinchat.models.ChatMessage
import com.steve.kotlinchat.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_messages_row.view.*


class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser:User?=null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textMessage.text=chatMessage.text

        val chatPartnerId:String
        if (chatMessage.fromId== FirebaseAuth.getInstance().uid){
            chatPartnerId=chatMessage.toId

        }else{
            chatPartnerId=chatMessage.fromId
        }

        val ref= FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser=snapshot.getValue(User::class.java)
                chatPartnerUser?.username
                viewHolder.itemView.userNameLatest.text= chatPartnerUser?.username
                val target=viewHolder.itemView.new_image_latest
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(target)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

}