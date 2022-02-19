package io.fonimus.pokedexmvvm.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.fonimus.pokedexmvvm.BuildConfig
import java.util.concurrent.Executor


@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val favoritesDao: FavoritesDao

    companion object {
        private const val DATABASE_NAME = "app_database"

        fun create(
            context: Context
            //,ioExecutor: Executor
        ): AppDatabase {
            val builder = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
//            builder.addCallback(object : Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    ioExecutor.execute {
//                        val projectDao: ProjectDao =
//                            getInstance(application, ioExecutor)!!.projectDao
//                        projectDao.insert(
//                            ProjectEntity(
//                                application.getString(R.string.tartampion_project),
//                                ResourcesCompat.getColor(
//                                    application.resources,
//                                    R.color.project_color_tartampion,
//                                    null
//                                )
//                            )
//                        )
//                    }
//                }
//            })
            if (BuildConfig.DEBUG) {
                builder.fallbackToDestructiveMigration()
            }
            return builder.build()
        }
    }


}
