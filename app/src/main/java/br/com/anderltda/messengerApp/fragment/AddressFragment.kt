package br.com.anderltda.messengerApp.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.com.anderltda.messengerApp.R
import br.com.anderltda.messengerApp.data.AppDatabase
import br.com.anderltda.messengerApp.data.dao.AddressDao
import br.com.anderltda.messengerApp.data.entity.Address
import br.com.anderltda.messengerApp.util.Constants
import br.com.anderltda.messengerApp.viewmodel.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_address.*
import kotlinx.android.synthetic.main.fragment_address.*
import java.util.*
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot


class AddressFragment : Fragment() {

    private lateinit var root: ViewGroup

    private lateinit var auth: FirebaseAuth

    private lateinit var appDatabase: AppDatabase

    private lateinit var addressDao: AddressDao

    private lateinit var searchViewModel: SearchViewModel

    private var address = Address()

    private var name = ""

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val reference by lazy {
        firestore.collection(Constants.CONTACTS_LOCATION_APP_FIREBASE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_address, container, false)

        root = view.findViewById(R.id.root)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        val title = toolbar.findViewById(R.id.tv_title) as TextView
        title.text = resources.getString(R.string.title_address)

        val back = toolbar.findViewById(R.id.tv_back) as TextView
        back.visibility = View.VISIBLE

        back.setOnClickListener{
            val fragmentManager = activity!!.supportFragmentManager
            if(fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            }
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        appDatabase = Room.databaseBuilder(
            activity!!.applicationContext,
            AppDatabase::class.java,
            Constants.STORE_DATABASE)
            .allowMainThreadQueries()
            .build()

        addressDao = appDatabase.addressDao()
        var address = addressDao.findId(uid)

        val et_zip_code: EditText = view.findViewById(R.id.et_zip_code) as EditText
        val tv_address: TextView = view.findViewById(R.id.tv_address) as TextView
        val et_number: EditText = view.findViewById(R.id.et_number) as EditText

        if(address != null) {

            et_zip_code.setText(address.cep)
            tv_address.setText(address.toString())
            et_number.setText(address.number)

        } else {

            reference.document(uid).get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val address = documentSnapshot.toObject(Address::class.java!!)

                    if(address != null) {
                        et_zip_code.setText(address!!.cep)
                        tv_address.setText(address.toString())
                        et_number.setText(address!!.number)
                        addressDao.save(address)
                    }

            })
        }

        et_zip_code.setOnClickListener{
            searchAddress()
        }

        val button = view.findViewById(br.com.anderltda.messengerApp.R.id.bt_continue) as Button

        button.setOnClickListener {

            if(et_zip_code.text.isNotBlank() && tv_address.text.isNotBlank() && et_number.text.isNotBlank()) {
                saveFirestoneDatabase(uid, address)
            } else {
                Toast.makeText(activity, resources.getString(R.string.erro_message_fields_required),
                    Toast.LENGTH_LONG).show()
            }

        }

        name = arguments!!.getString("name")

        return view
    }


    private fun hideSoftKeyboard() {
        val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.hideSoftInputFromWindow(view!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun searchAddress() {

        hideSoftKeyboard();

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        searchViewModel.buscar(et_zip_code.text.toString())

        searchViewModel.address.observe(this, Observer {
            tv_address.setText(it?.toString())
            this.address = it!!
        })

        searchViewModel.mensagemErro.observe(this, Observer {
            if (!it.equals("")) {
                Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
            }
        })

        searchViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading == true) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }

        })
    }

    private fun saveFirestoneDatabase(uid: String, address: Address) {

        if(address == null){
            this.address.create = Calendar.getInstance().getTime()
        }

        this.address.id = uid
        this.address.cep = et_zip_code.text.toString()
        this.address.lat = -23.4834015
        this.address.long = -46.661051
        this.address.name = this.name
        this.address.update = Calendar.getInstance().getTime()
        this.address.number = et_number.text.toString()
        reference.document(uid).set(this.address);
        addressDao.save(this.address)

        Toast.makeText(activity,
            resources.getString(R.string.success_message_default),
            Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(name: String): AddressFragment {
            val fragment = AddressFragment()
            val bundle = Bundle()
            bundle.putString("name", name)
            fragment.setArguments(bundle)
            return fragment
        }
    }

}
