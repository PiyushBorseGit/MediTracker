package com.app.helthcare

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MedicineViewModel : ViewModel() {
    private val _medicines = MutableLiveData<MutableList<Medicine>>(mutableListOf())
    val medicines: LiveData<MutableList<Medicine>> = _medicines

    fun addMedicine(medicine: Medicine) {
        val currentList = _medicines.value
        currentList?.add(medicine)
        _medicines.postValue(currentList)
    }
}
