package com.app.helthcare

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkManager
import java.util.UUID

class MedicineViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicineRepository(application)
    private val _medicines = MutableLiveData<MutableList<Medicine>>()
    val medicines: LiveData<MutableList<Medicine>> = _medicines

    // LiveData to hold the medicine being edited
    private val _medicineToEdit = MutableLiveData<Medicine?>()
    val medicineToEdit: LiveData<Medicine?> = _medicineToEdit

    init {
        loadMedicines()
    }

    private fun loadMedicines() {
        _medicines.value = repository.getMedicines()
    }

    fun addMedicine(medicine: Medicine) {
        val currentList = _medicines.value ?: mutableListOf()
        currentList.add(medicine)
        _medicines.postValue(currentList)
        repository.saveMedicines(currentList)
    }

    fun updateMedicine(oldMedicine: Medicine, newMedicine: Medicine) {
        val currentList = _medicines.value ?: return
        val index = currentList.indexOfFirst { it.id == oldMedicine.id }
        if (index != -1) {
            currentList[index] = newMedicine
            _medicines.postValue(currentList)
            repository.saveMedicines(currentList)
        }
    }

    fun deleteMedicine(medicine: Medicine, context: Context) {
        medicine.workId?.let { workId ->
            WorkManager.getInstance(context).cancelWorkById(workId)
        }
        
        val currentList = _medicines.value
        currentList?.remove(medicine)
        _medicines.postValue(currentList)
        repository.saveMedicines(currentList ?: mutableListOf())
    }

    fun setMedicineToEdit(medicine: Medicine?) {
        _medicineToEdit.value = medicine
    }
    
    fun refresh() {
        loadMedicines()
    }
}