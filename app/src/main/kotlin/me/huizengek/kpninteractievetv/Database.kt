package me.huizengek.kpninteractievetv

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import me.huizengek.kpninteractievetv.models.Session

@Database(
    entities = [Session::class],
    version = 1
)
abstract class DatabaseCreator : RoomDatabase() {
    abstract val dao: DatabaseAccessor
}

@Dao
interface DatabaseAccessor {
    companion object {
        lateinit var instance: DatabaseAccessor

        context(Context)
        fun init() {
            instance = Room
                .databaseBuilder(
                    context = applicationContext,
                    klass = DatabaseCreator::class.java,
                    name = "database.db"
                )
                .build()
                .dao
        }
    }

    @Query("SELECT * FROM Session")
    fun sessions(): Flow<List<Session>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(session: Session)

    @Delete
    fun delete(session: Session)
}

object Database : DatabaseAccessor by DatabaseAccessor.instance
