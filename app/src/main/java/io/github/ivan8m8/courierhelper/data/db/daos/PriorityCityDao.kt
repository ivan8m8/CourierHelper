package io.github.ivan8m8.courierhelper.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.ivan8m8.courierhelper.data.models.PriorityCity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface PriorityCityDao {

    @Query("SELECT * FROM PriorityCity")
    fun get(): Maybe<PriorityCity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(priorityCity: PriorityCity): Completable

    @Query("DELETE FROM PriorityCity")
    fun clearTable(): Completable

    @Transaction
    fun clearAndInsert(priorityCity: PriorityCity) {
        clearTable().blockingAwait()
        insert(priorityCity).blockingAwait()
    }
}