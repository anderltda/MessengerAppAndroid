package br.com.anderltda.messengerApp.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity
data class User (

    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var create: Date? = null,
    var update: Date? = null

)