package io.fonimus.pokedexmvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pokemonId: String
)

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT * FROM favorite WHERE id = :id")
    suspend fun getById(id: String): FavoriteEntity?

    @Insert
    suspend fun insert(favorite: FavoriteEntity)
}
