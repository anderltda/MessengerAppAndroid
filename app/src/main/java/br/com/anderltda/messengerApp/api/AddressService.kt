package br.com.anderltda.messengerApp.api

import br.com.anderltda.messengerApp.data.entity.Address
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AddressService {

        @GET("/ws/{cep}/json/")
        fun buscar(@Path("cep") cep: String): Call<Address>
}



