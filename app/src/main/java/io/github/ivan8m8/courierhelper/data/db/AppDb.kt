package io.github.ivan8m8.courierhelper.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.ivan8m8.courierhelper.data.db.converters.LatitudeLongitudeConverter
import io.github.ivan8m8.courierhelper.data.db.daos.DeliveriesDao
import io.github.ivan8m8.courierhelper.data.models.Delivery

@Database(
    entities = [
        Delivery::class
    ],
    version = 1
)
@TypeConverters(LatitudeLongitudeConverter::class)
abstract class AppDb: RoomDatabase() {
    abstract fun deliveriesDao(): DeliveriesDao
    companion object {
        const val NAME = "CourierHelper_DB"
    }
}