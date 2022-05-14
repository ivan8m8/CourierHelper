package io.github.ivan8m8.courierhelper.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.ivan8m8.courierhelper.data.db.daos.DeliveriesDao
import io.github.ivan8m8.courierhelper.data.models.Delivery

@Database(
    entities = [
        Delivery::class
    ],
    version = 1
)
abstract class DeliveriesDb: RoomDatabase() {
    abstract fun deliveriesDao(): DeliveriesDao
}