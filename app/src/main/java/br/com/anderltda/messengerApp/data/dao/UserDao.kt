package br.com.anderltda.messengerApp.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import br.com.anderltda.messengerApp.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Query("SELECT * FROM User WHERE id = :uid")
    fun findId(uid: String): User

}