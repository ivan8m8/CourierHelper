package io.github.ivan8m8.courierhelper.data.db.daos

import androidx.room.*
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.Models
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface DeliveriesDao {

    @Insert
    fun insert(item: Delivery): Completable

    @Update
    fun update(item: Delivery): Completable

    @Delete
    fun delete(item: Delivery): Completable

    @Query("SELECT * FROM Delivery WHERE status = :status")
    fun getAll(status: Models.DeliveryStatus): Observable<List<Delivery>>
}