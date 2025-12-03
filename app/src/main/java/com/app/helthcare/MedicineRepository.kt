package com.app.helthcare

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MedicineRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("medicine_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getMedicines(): MutableList<Medicine> {
        val json = sharedPreferences.getString("medicines", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Medicine>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveMedicines(medicines: List<Medicine>) {
        val json = gson.toJson(medicines)
        sharedPreferences.edit().putString("medicines", json).apply()
    }

    fun markAsTaken(medicineId: String) {
        val medicines = getMedicines()
        val medicine = medicines.find { it.id == medicineId }
        if (medicine != null) {
            medicine.isTaken = true
            saveMedicines(medicines)
        }
    }
}