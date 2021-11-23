package by.iapsit.notikeeper.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FilteredApplicationEntity(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0
)
