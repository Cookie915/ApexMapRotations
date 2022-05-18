package com.example.apexmaprotations.viewmodels

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.retrofit.CurrentMap
import com.example.apexmaprotations.models.retrofit.MapData
import com.example.apexmaprotations.repo.ApexRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class ApexViewModel : ViewModel() {
    private val tag = "ApexViewModel"
    private val apexRepo = ApexRepo()
    val test = "Test"

    private var mCurrentMap = MutableStateFlow<Resource<CurrentMap>>(Resource.Failure("Fail"))
    val currentMap: StateFlow<Resource<CurrentMap>>
        get() = mCurrentMap.asStateFlow()

    private suspend fun initializeMapData(){
        apexRepo.getMapData().collect {
            when (it) {
                is Resource.Loading -> {
                    mCurrentMap.value = Resource.Loading()
                }
                is Resource.Failure -> {
                    mCurrentMap.value = Resource.Failure("Failed")
                }
                is Resource.Success -> {
                    mCurrentMap.value = Resource.Success(it.data?.currentMap)
                }
            }
        }
    }
    init {
        viewModelScope.launch {
            initializeMapData()
        }
    }
}