package br.com.anderltda.messengerApp.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Address(

        @PrimaryKey
        var id: String = "",
        var name: String = "",
        var cep: String = "",
        var logradouro: String = "",
        var complemento: String = "",
        var bairro: String = "",
        var localidade: String = "",
        @SerializedName("uf")
        var estado: String = "",
        var unidade: String = "",
        var ibge: String = "",
        var gia: String = "",
        var number: String = "",
        var lat: Double? = null,
        var long: Double? = null,
        var create: Date? = null,
        var update: Date? = null

){
        override fun toString(): String {
                return "$logradouro - $bairro, $localidade - $estado"
        }
}