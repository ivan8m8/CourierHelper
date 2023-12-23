package io.github.ivan8m8.courierhelper.data.db.daos

import androidx.room.*
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.DeliveryStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface DeliveriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Delivery): Single<Long>

    @Update
    fun update(item: Delivery): Completable

    @Delete
    fun delete(item: Delivery): Completable

    @Query("SELECT * FROM Delivery WHERE id = :id")
    fun get(id: Long): Flowable<Delivery>

    @Query("SELECT * FROM Delivery WHERE status = :status")
    fun getAll(status: DeliveryStatus): Flowable<List<Delivery>>
}