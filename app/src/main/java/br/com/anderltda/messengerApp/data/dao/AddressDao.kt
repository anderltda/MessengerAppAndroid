package br.com.anderltda.messengerApp.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import br.com.anderltda.messengerApp.data.entity.Address

@Dao
interface AddressDao {

    @Insert(onConflict = REPLACE)
    fun save(address: Address)

    @Query("SELECT * FROM Address WHERE id = :uid")
    fun findId(uid: String): Address

}