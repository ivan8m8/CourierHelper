package io.github.ivan8m8.courierhelper.data.db.daos

import androidx.room.Dao
import androidx.room.Query
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.Models
import io.reactivex.rxjava3.core.Observable

@Dao
interface DeliveriesDao {

    @Query("SELECT * FROM Delivery WHERE status = :status")
    fun getAll(status: Models.DeliveryStatus): Observable<List<Delivery>>
}