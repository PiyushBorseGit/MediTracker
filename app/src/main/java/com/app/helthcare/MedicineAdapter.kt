package com.app.helthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapter(private val medicines: List<Medicine>) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textMedicineName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textMedicineDescription)
        val timeTextView: TextView = itemView.findViewById(R.id.textMedicineTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medicine_card, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.nameTextView.text = medicine.name
        holder.descriptionTextView.text = medicine.description
        holder.timeTextView.text = medicine.time
    }

    override fun getItemCount() = medicines.size
}