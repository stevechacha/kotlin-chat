package com.steve.kotlinchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.steve.kotlinchat.messages.ChatLogActivity
import com.steve.kotlinchat.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {
    companion object{
        val USER_KEY="USER_KEY"
    }

    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title="Select User"
        recyclerView=findViewById(R.id.recycler_new_message)
//        val adapter=GroupAdapter<ViewHolder>()
////        adapter.add(UserItem(use))
////        adapter.add(UserItem())
////        adapter.add(UserItem())
        fetchUsers()
    }

    private fun fetchUsers() {
       val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter=GroupAdapter<ViewHolder>()
                p0.children.forEach {
                   Log.d("New Message",it.toString())
                    val user=it.getValue(User::class.java)

                    if (user!=null){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem=item as UserItem
                    val intent= Intent(view.context,ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY,userItem.user.username)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recyclerView.adapter=adapter

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
class UserItem(val user: User):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //will be called in our list of each user object
        viewHolder.itemView.findViewById<TextView>(R.id.userName).text=user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.new_image_message))
    }
    override fun getLayout(): Int {
      return R.layout.user_row_new_message
    }

}
