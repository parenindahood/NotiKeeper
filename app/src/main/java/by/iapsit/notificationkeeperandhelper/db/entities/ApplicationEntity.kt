package by.iapsit.notificationkeeperandhelper.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ApplicationEntity(
    @ColumnInfo(name = "package_name") @PrimaryKey val packageName: String,
    @ColumnInfo(name = "is_favourite") val isFavourite: Boolean,
    @ColumnInfo(name = "is_system") val isSystem: Boolean
)