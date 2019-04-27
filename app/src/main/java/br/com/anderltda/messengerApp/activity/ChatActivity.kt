package br.com.anderltda.messengerApp.activity

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import br.com.anderltda.messengerApp.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import br.com.anderltda.messengerApp.activity.BaseActivity
import br.com.anderltda.messengerApp.adapter.ChatAdapter
import br.com.anderltda.messengerApp.data.AppDatabase
import br.com.anderltda.messengerApp.data.dao.UserDao
import br.com.anderltda.messengerApp.model.Chat
import br.com.anderltda.messengerApp.util.Constants
import kotlinx.android.synthetic.main.content_chat.*


class ChatActivity : BaseActivity() {

    companion object {
        const val SORT_TIMER = "time"
    }

    private var sort = SORT_TIMER

    private lateinit var root: ViewGroup

    private lateinit var adapter: ChatAdapter

    private lateinit var auth: FirebaseAuth

    private lateinit var appDatabase: AppDatabase

    private lateinit var userDao: UserDao

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val refStates by lazy {

        val it = intent

        val room = it.getStringExtra("ROOM")

        firestore.collection(room)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance();

        root = findViewById(R.id.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        title = toolbar.findViewById(R.id.tv_title) as TextView
        title.text = resources.getString(R.string.title_chat)

        val back = toolbar.findViewById(R.id.tv_back) as TextView
        back.visibility = View.VISIBLE
        back.setOnClickListener{
            finish()
        }

        toolbar.inflateMenu(R.menu.menu_userphoto)

        adapter = ChatAdapter({

            refStates.orderBy(sort, Query.Direction.ASCENDING)

        })


        adapter.onDeleteListener = { position ->

            val chatData = adapter.get(position)

            val snapshot = adapter.getSnapshot(position)

            delete(chatData, snapshot.reference)

        }

        adapter.onUpListener = { position ->

            val chatData = adapter.get(position)

            val snapshot = adapter.getSnapshot(position)

            incrementDocument(chatData, snapshot.reference)

        }
        adapter.onClickListener = { position ->
            Snackbar.make(root, "$position clicked", Snackbar.LENGTH_SHORT)
                .show()
        }

        val list = findViewById<RecyclerView>(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this)

        list.adapter = adapter

        list.layoutManager = layoutManager

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                super.onScrolled(recyclerView, dx, dy)

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }
        })

        adapter.setupOnScrollListener(list, layoutManager)

        adapter.onLoadingMore = {
            log("onLoadingMore")
        }
        adapter.onLoadingMoreComplete = {
            log("onLoadingMoreComplete")
        }
        adapter.onHasLoadedAll = {
            log("onHasLoadedAll")
        }

        bt_send.setOnClickListener {

            if(et_message.text.toString().isNotBlank())  {

                send(et_message.text.toString())

                //list.smoothScrollToPosition(list.getAdapter()!!.getItemCount() - 1)

                et_message.setText("")
            }

        }
    }

    override fun onStart() {
        super.onStart()
        adapter.clear()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun log(string: String) {
        Log.d("TEST", string)
    }

    fun snackbar(string: String) {
        Snackbar.make(root, string, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun incrementDocument(chat: Chat, docRef: DocumentReference) {

        firestore.runTransaction { transaction ->

            val snapshot = transaction.get(docRef)

            val newPopulation = snapshot.getDouble("population")!! + 1

            transaction.update(docRef, "population", newPopulation)

            // Success
            null

        }.addOnSuccessListener {

            log("Transaction success!")

        }.addOnFailureListener { e ->

            e.printStackTrace()

            snackbar("Failed to increment ${chat.name}")
        }
    }

    fun delete(chat: Chat, docRef: DocumentReference) {

        docRef.delete().addOnSuccessListener {

                log("Transaction success!")

        }.addOnFailureListener { e ->
                e.printStackTrace()
                snackbar("Failed to delete ${chat.name}")
        }
    }


    fun send(message: String) {

        appDatabase = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            Constants.STORE_DATABASE)
            .allowMainThreadQueries()
            .build()

        userDao = appDatabase.userDao()
        var user = userDao.findId(auth.currentUser!!.uid.toString())

        val chat = Chat()
        chat.name = user.name
        chat.uid =  auth.currentUser!!.uid.toString()
        chat.time = Calendar.getInstance().getTime()
        chat.message = message
        refStates.document().set(chat);

    }

}
