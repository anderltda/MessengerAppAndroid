package br.com.anderltda.messengerApp.activity

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import br.com.anderltda.messengerApp.R
import br.com.anderltda.messengerApp.data.AppDatabase
import br.com.anderltda.messengerApp.data.dao.UserDao
import br.com.anderltda.messengerApp.data.entity.User
import br.com.anderltda.messengerApp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.loading
import kotlinx.android.synthetic.main.content_sign_up.*
import java.util.*

class SignUpActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var appDatabase: AppDatabase

    private lateinit var userDao: UserDao

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val reference by lazy {
        firestore.collection(Constants.USER_DEFAULT_APP_FIREBASE)
    }

    override fun onCreate(bundle: Bundle?) {

        super.onCreate(bundle)

        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        title = toolbar.findViewById(R.id.tv_title) as TextView
        title.text = resources.getString(R.string.title_activity_sign_in)

        val back = toolbar.findViewById(R.id.tv_back) as TextView
        back.visibility = View.VISIBLE
        back.setOnClickListener {
            finish()
        }

        bt_continue.setOnClickListener {

            loading.visibility = View.VISIBLE

            if (et_fullname.text.toString().isNotBlank()
                && et_phone.text.toString().isNotBlank()
                && et_email.text.toString().isNotBlank()
                && et_password.text.toString().isNotBlank()
            ) {

                auth.createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())

                    .addOnCompleteListener(this) { task ->

                        loading.visibility = View.GONE

                        if (task.isSuccessful) {

                            Log.d("TAG", "create user with email:success")

                            createUserFirestoneDatabase()

                        } else {

                            Log.w("TAG", "create user with email:failure", task.exception)

                            Toast.makeText(this@SignUpActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                        }

                    }

            } else {

                loading.visibility = View.GONE
                Toast.makeText(this@SignUpActivity, resources.getString(R.string.erro_message_fields_required),
                    Toast.LENGTH_LONG).show()

            }

        }
    }

    private fun createUserFirestoneDatabase() {

        appDatabase = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            Constants.STORE_DATABASE)
            .allowMainThreadQueries()
            .build()

        val user = User()
        user.id = FirebaseAuth.getInstance().currentUser!!.uid
        user.email = et_email.text.toString()
        user.name = et_fullname.text.toString()
        user.phone = et_phone.text.toString()
        user.create = Calendar.getInstance().getTime()
        reference.document(user.id).set(user);

        userDao = appDatabase.userDao()
        userDao.save(user)

        val intent = Intent()
        intent.putExtra("email", et_email.text.toString())
        intent.putExtra("password", et_password.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()

        Toast.makeText(
            this@SignUpActivity,
            resources.getString(R.string.success_message_default),
            Toast.LENGTH_LONG
        ).show()

    }
}