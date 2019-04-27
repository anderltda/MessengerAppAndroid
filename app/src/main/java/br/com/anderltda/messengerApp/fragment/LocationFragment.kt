package br.com.anderltda.messengerApp.fragment


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.anderltda.messengerApp.R
import br.com.anderltda.messengerApp.activity.LocationActivity

import br.com.anderltda.messengerApp.adapter.LocationAdapter
import br.com.anderltda.messengerApp.data.entity.Address
import br.com.anderltda.messengerApp.util.Constants
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LocationFragment : Fragment() {

    private lateinit var root: ViewGroup

    private lateinit var adapter: LocationAdapter

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val refStates by lazy {
        firestore.collection(Constants.CONTACTS_LOCATION_APP_FIREBASE)
    }

    private var sort = SORT_NAME

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        root = view.findViewById(R.id.root)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        val title = toolbar.findViewById(R.id.tv_title) as TextView
        title.text = resources.getString(R.string.title_location)

        adapter = LocationAdapter({
            refStates.orderBy(sort, Query.Direction.ASCENDING)
        })


        adapter.onDeleteListener = { position ->

            val address = adapter.get(position)

            val snapshot = adapter.getSnapshot(position)

            delete(address, snapshot.reference)

        }
        adapter.onUpListener = { position ->

            val contactLocation = adapter.get(position)

            val snapshot = adapter.getSnapshot(position)

            incrementPopulation(contactLocation, snapshot.reference)

        }
        adapter.onClickListener = { position ->

            val address = adapter.get(position)
            //val ui = FirebaseAuth.getInstance().currentUser!!.uid

            val next = Intent(activity, LocationActivity::class.java)
            next.putExtra("lat", address.lat)
            next.putExtra("long", address.long)
            next.putExtra("name", address.name)

            startActivity(next)
        }

        val list = view.findViewById<RecyclerView>(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(context)

        list.adapter = adapter

        list.layoutManager = layoutManager

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


        return view
    }

    fun log(string: String) {
        Log.d("TEST", string)
    }

    fun snackbar(string: String) {
        Snackbar.make(root, string, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun incrementPopulation(address: Address, docRef: DocumentReference) {

        firestore.runTransaction { transaction ->

            val snapshot = transaction.get(docRef)

            val newPopulation = snapshot.getDouble("number")!! + 1

            transaction.update(docRef, "number", newPopulation)

            // Success
            null

        }.addOnSuccessListener {

            log("Transaction success!")

        }.addOnFailureListener { e ->

            e.printStackTrace()

            snackbar("Failed to increment ${address.name}")
        }
    }

    fun delete(address: Address, docRef: DocumentReference) {

        docRef.delete()
            .addOnSuccessListener {
                log("Transaction success!")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                snackbar("Failed to delete ${address.name}")
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

    companion object {

        const val SORT_NAME = "name"

        fun newInstance(): LocationFragment {
            val fragment = LocationFragment()
            return fragment
        }
    }

}
