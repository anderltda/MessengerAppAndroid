package br.com.anderltda.messenger.fragment

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import br.com.anderltda.messenger.R
import br.com.anderltda.messenger.data.AppDatabase
import br.com.anderltda.messenger.data.dao.UserDao
import br.com.anderltda.messenger.data.entity.User
import br.com.anderltda.messenger.util.Constants
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_person.*
import java.util.*


class PersonFragment : Fragment() {

    private lateinit var root: ViewGroup

    private lateinit var auth: FirebaseAuth

    private lateinit var appDatabase: AppDatabase

    private lateinit var userDao: UserDao

    private var user = User()

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val reference by lazy {
        firestore.collection(Constants.USER_DEFAULT_APP_FIREBASE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_person, container, false)

        root = view.findViewById(R.id.root)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val title = toolbar.findViewById(R.id.tv_title) as TextView
        title.text = resources.getString(R.string.title_person)
        toolbar.inflateMenu(R.menu.menu_perfil)

        var uid = FirebaseAuth.getInstance().currentUser!!.uid

        buscarUserDatabase(uid, view)

        toolbar.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.nav_address -> {

                    newFragment(AddressFragment.newInstance(user.name))
                    return@setOnMenuItemClickListener true
                }

            }

            false
        }

        val button = view.findViewById(R.id.bt_continue) as Button

        button.setOnClickListener {
            updateFirestoneDatabase()
        }

        return view
    }

    private fun buscarUserDatabase(uid: String, view: View) {

        appDatabase = Room.databaseBuilder(
            activity!!.applicationContext,
            AppDatabase::class.java,
            Constants.STORE_DATABASE)
            .allowMainThreadQueries()
            .build()

        userDao = appDatabase.userDao()
        user = userDao.findId(uid)

        val et_fullname: EditText = view.findViewById(R.id.et_fullname) as EditText
        val tv_email: TextView = view.findViewById(R.id.tv_email) as TextView
        val et_phone: EditText = view.findViewById(R.id.et_phone) as EditText

        if(user != null) {

            et_fullname.setText(user!!.name)
            tv_email.setText(user!!.email)
            et_phone.setText(user!!.phone)

        } else {

            reference.document(uid).get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java!!)
                this.user = user!!
                et_fullname.setText(user!!.name)
                tv_email.setText(user!!.email)
                et_phone.setText(user!!.phone)
                userDao.save(user)
            })

        }
    }

    private fun updateFirestoneDatabase() {

        user.id = FirebaseAuth.getInstance().currentUser!!.uid
        user.name = et_fullname.text.toString()
        user.phone = et_phone.text.toString()
        user.update = Calendar.getInstance().getTime()
        reference.document(user.id).set(user);
        userDao.save(user)

        Toast.makeText(activity,
            resources.getString(R.string.success_message_default),
            Toast.LENGTH_LONG).show()
    }


    companion object {
        fun newInstance(): PersonFragment {
            val fragment = PersonFragment()
            return fragment
        }
    }


    private fun newFragment(fragment: Fragment) {
        val ft = activity!!.supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.addToBackStack(null)
        ft.commit()
    }

}
