package com.steve.kotlinchat.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.steve.kotlinchat.NewMessageActivity
import com.steve.kotlinchat.R
import com.steve.kotlinchat.models.ChatMessage
import com.steve.kotlinchat.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG="ChatLog"
    }
    lateinit var recyclerView_chat: RecyclerView
    lateinit var btnSendMessage:Button
    lateinit var textMsg:EditText

    val adapter=GroupAdapter<ViewHolder>()
    var toUser:User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser=  intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title=toUser?.username

        btnSendMessage=findViewById(R.id.buttonSend)
        textMsg=findViewById(R.id.chatEdit)
        recyclerView_chat=findViewById(R.id.recycler_chat_log)
        recyclerView_chat.adapter=adapter

        listenForMessages()
        btnSendMessage.setOnClickListener {
            Log.d(TAG,"Attempt to send message..")
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val fromId=FirebaseAuth.getInstance().uid
        val toId=toUser?.uid
        val ref=FirebaseDatabase.getInstance().getReference("user-messages/$fromId/$toId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG,chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser=LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text,currentUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                }
                recyclerView_chat.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun performSendMessage() {
        val text=textMsg.text.toString()
        val fromId=FirebaseAuth.getInstance().uid
        val user=  intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user?.uid
        if (fromId==null) return
        if (toId==null) return

        val reference=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference= FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage=ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"saved succesfully: ${reference.key}")
                chatEdit.text.clear()
                recyclerView_chat.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)
        val latestMessageRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)


    }

}

class ChatFromItem(val text: String,val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chat_from_message.text=text
        val uri=user.profileImageUrl
        Picasso.get().load(uri).into(viewHolder.itemView.findViewById<ImageView>(R.id.chat_from_image))


    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row


    }

}

class ChatToItem(val text:String,val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chat_to_message.text=text
        val uri=user.profileImageUrl
        Picasso.get().load(uri).into(viewHolder.itemView.findViewById<ImageView>(R.id.chat_to_image))

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row

    }

}
