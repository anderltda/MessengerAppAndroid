package br.com.anderltda.messenger.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import br.com.anderltda.messenger.data.converter.DateConverter
import br.com.anderltda.messenger.data.dao.AddressDao
import br.com.anderltda.messenger.data.dao.UserDao
import br.com.anderltda.messenger.data.entity.Address
import br.com.anderltda.messenger.data.entity.User


@Database(entities = [Address::class, User::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun addressDao(): AddressDao
    abstract fun userDao(): UserDao

    companion object {
        private val INSTANCE : AppDatabase? = null
    }
}