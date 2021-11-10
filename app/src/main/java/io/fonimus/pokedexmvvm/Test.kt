package io.fonimus.pokedexmvvm

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class AllInOneRepository : ViewModel() {

    private val distancesFlow = MutableStateFlow(mutableMapOf<String, Int>())

    fun getList(): LiveData<List<RestaurantViewState>> {
        return liveData {

            combine(
                getLocationFlow(),
                getLocationFlow().flatMapLatest { location ->
                    getNearbyRestaurants(
                        location.latitude,
                        location.longitude
                    )
                },
                distancesFlow
            ) { location: Location, restaurants: List<Restaurant>, distances: MutableMap<String, Int> ->
                emit(restaurants.map { r ->
                    if (!distances.containsKey(r.placeId)) {
                        viewModelScope.launch {
                            val distance = getGeocoderInformation(
                                r.restaurantLatitude,
                                r.restaurantLongitude,
                                location.latitude,
                                location.longitude
                            )
                            val map = distancesFlow.value
                            map[r.placeId] = distance
                            distancesFlow.value = map
                        }
                    }
                    RestaurantViewState(
                        r.placeId,
                        r.name,
                        distances[r.placeId]?.toString() ?: "unknown",
                        (r.rating * 3 / 5).roundToInt()
                    )
                })
            }
        }
    }


    fun getLocationFlow(): Flow<Location> {
        return flowOf()
    }

    fun getNearbyRestaurants(latitude: Double, longitude: Double): Flow<List<Restaurant>> {
        return flowOf()
    }

    data class Restaurant(
        val placeId: String,
        val name: String,
        val restaurantLatitude: Double,
        val restaurantLongitude: Double,
        val rating: Float // [1, 5]
    )

    suspend fun getGeocoderInformation(
        latitude1: Double, longitude1: Double,
        latitude2: Double, longitude2: Double,
    ): Int {
        delay(200)
        return Random.nextInt(500)
    }

    //    val liveData : LiveData<List<RestaurantViewState>>
    data class RestaurantViewState(
        val placeId: String,
        val name: String,
        val distance: String,
        val starRating: Int
    )
}
