package io.github.ivan8m8.courierhelper.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.ivan8m8.courierhelper.data.db.daos.DeliveriesDao
import io.github.ivan8m8.courierhelper.data.models.Delivery

@Database(
    entities = [
        Delivery::class
    ],
    version = 2
)
abstract class AppDb: RoomDatabase() {
    abstract fun deliveriesDao(): DeliveriesDao
    companion object {
        const val NAME = "CourierHelper_DB"
    }
}