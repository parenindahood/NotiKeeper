package by.iapsit.notificationkeeperandhelper.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteApplicationEntity (
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "notification_id") @PrimaryKey(autoGenerate = true) val notificationID: Long = 0
)