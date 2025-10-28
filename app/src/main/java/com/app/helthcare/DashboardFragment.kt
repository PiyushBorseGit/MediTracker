package com.app.helthcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicineViewModel: MedicineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerView = view.findViewById(R.id.rv_medicine_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medicineViewModel.medicines.observe(viewLifecycleOwner) { medicines ->
            medicineAdapter = MedicineAdapter(medicines)
            recyclerView.adapter = medicineAdapter
        }
    }
}