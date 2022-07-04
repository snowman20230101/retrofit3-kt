package com.windy.retrofit3.sample.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity
data class Title(@PrimaryKey val id: Int, val title: String, val contents: String)

@Dao
interface TitleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitle(title: Title)

    @Query("select * from Title where id =:id")
    suspend fun get(@NotNull id: Int): Title

    @Query("select * from Title")
    fun getAllTitle(): List<Title>

    @Query("select * from Title")
    fun getAllTitleToLiveData(): LiveData<List<Title>>
}

abstract class AppDataBase : RoomDatabase() {
    abstract fun getTitle(): TitleDao
}

private lateinit var INSTANCE: AppDataBase

fun getAppDataBase(context: Context): AppDataBase {
    if (!::INSTANCE.isInitialized) {
        INSTANCE =
            Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "app_database.db"
            )
                .allowMainThreadQueries()
                .build()
    }

    return INSTANCE
}