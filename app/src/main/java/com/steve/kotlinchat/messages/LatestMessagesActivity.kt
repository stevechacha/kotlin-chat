package com.steve.kotlinchat.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.steve.kotlinchat.NewMessageActivity
import com.steve.kotlinchat.R
import com.steve.kotlinchat.models.ChatMessage
import com.steve.kotlinchat.models.User
import com.steve.kotlinchat.registerlogin.RegisterActivity
import com.steve.kotlinchat.views.LatestMessageRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessagesActivity : AppCompatActivity() {
    companion object{
        var currentUser: User?=null
        val TAG="LatestMessageActivity"
    }
    lateinit var LatestMessagerecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        LatestMessagerecyclerView=findViewById(R.id.recycler_latest_message)
        LatestMessagerecyclerView.adapter=adapter
        LatestMessagerecyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        veryUserLoggedIn()
        fetcurrentUser()
        listenForLatestMessages()
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG,"123")
            val intent=Intent(this,ChatLogActivity::class.java)
            startActivity(intent)
            val row=item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser)
        }
    }
    val latestMessageMap=HashMap<String,ChatMessage>()
   private fun refreshRecyclerView(){
       adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)?:return
                latestMessageMap[snapshot.key!!]=chatMessage
                refreshRecyclerView()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)?:return
                latestMessageMap[snapshot.key!!]=chatMessage
                refreshRecyclerView()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    val adapter=GroupAdapter<ViewHolder>()
//    private fun setupDummpRow() {
//
////        LatestMessagerecyclerView.adapter=adapter
////        adapter.add(LatestMessageRow())
////        adapter.add(LatestMessageRow())
////        adapter.add(LatestMessageRow())
//
//    }

    private fun fetcurrentUser() {
        val uid=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser=snapshot.getValue(User::class.java)
                Log.d("Listens Message","${currentUser?.username}")
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun veryUserLoggedIn() {
        val uid= FirebaseAuth.getInstance().uid
        if (uid==null){
            val intent=Intent(this, RegisterActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.New_Message ->{
               val intent=Intent(this, NewMessageActivity::class.java)
               intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }
           R.id.signOut ->{
               FirebaseAuth.getInstance().signOut()
               val intent=Intent(this, RegisterActivity::class.java)
               startActivity(intent)
           }
           R.id.nnewessaages ->{
               val intent=Intent(this, NewMessageActivity::class.java)
               intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }
       }
        return true

    }
}