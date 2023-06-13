package com.example.jivaproject.jiva.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jivaproject.jiva.data.model.SellerTable
import com.example.jivaproject.jiva.data.model.VillageTable
import com.example.jivaproject.jiva.domain.usecase.SellerDetailUseCase
import com.example.jivaproject.jiva.domain.usecase.VillageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val sellerDetailUseCase: SellerDetailUseCase,
    private val villageUseCase: VillageUseCase
) : ViewModel() {
    var sellerName = ""
    var grossPrice = ""
    var grossValue = ""
    private val _villages by lazy { MutableLiveData<MutableList<VillageTable>>() }
    val villages: LiveData<MutableList<VillageTable>>
        get() = _villages

    fun getVillages() {
        viewModelScope.launch {
            villageUseCase.getVillages().collect {
                Log.d("hbdc", it.toString())
                _villages.postValue(it)
            }
        }
    }

    fun insertVillage() {
        val list = ArrayList<VillageTable>()
        var villageTable = VillageTable("Ramnagar", 120.08)
        list.add(villageTable)
        villageTable = VillageTable("Jamunanagar", 80.08)
        list.add(villageTable)
        villageTable = VillageTable("Saharanpur", 180.02)
        list.add(villageTable)
        villageTable = VillageTable("Ghoshwari", 100.03)
        list.add(villageTable)
        villageTable = VillageTable("Jasodhanagar", 190.01)
        list.add(villageTable)
        viewModelScope.launch {
            villageUseCase.insertVillages(list)
        }
    }

    fun addSeller(sellerTable: SellerTable) {
        viewModelScope.launch(CoroutineExceptionHandler { _, th ->
            Timber.d("Exception in adding Seller $th")
        }) {
            sellerDetailUseCase.addSeller(sellerTable)
            sellerDetailUseCase.addSeller(sellerTable)
        }
    }

    suspend fun getSellerByName(name: String): SellerTable? {
        return withContext(viewModelScope.coroutineContext) {
            sellerDetailUseCase.getSellerDetailByName(name)
        }
    }

    suspend fun getSellerByLoyalty(loyaltyCardNo: String): SellerTable? {
        return withContext(viewModelScope.coroutineContext) {
            sellerDetailUseCase.getSellerDetailByLoyalty(loyaltyCardNo)
        }
    }

    fun checkUserExist(name: String): Boolean {
        var count = -1
        viewModelScope.async {
            count = sellerDetailUseCase.checkUserExist(name)
        }.onAwait
        return count > 0
    }
}