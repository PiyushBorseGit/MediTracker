package com.app.helthcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardFragment : Fragment() {

    private lateinit var rvMedicineList: RecyclerView
    private lateinit var rvDueMedicines: RecyclerView
    private lateinit var tvAttentionHeader: TextView
    private lateinit var tvEmptyState: TextView
    
    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var dueMedicineAdapter: MedicineAdapter
    
    private val medicineViewModel: MedicineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        rvMedicineList = view.findViewById(R.id.rv_medicine_list)
        rvDueMedicines = view.findViewById(R.id.rv_due_medicines)
        tvAttentionHeader = view.findViewById(R.id.tv_attention_header)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        
        rvMedicineList.layoutManager = LinearLayoutManager(context)
        rvDueMedicines.layoutManager = LinearLayoutManager(context)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        medicineViewModel.medicines.observe(viewLifecycleOwner) { medicines ->
            if (medicines.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvMedicineList.visibility = View.GONE
                rvDueMedicines.visibility = View.GONE
                tvAttentionHeader.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvMedicineList.visibility = View.VISIBLE
                
                val currentTime = System.currentTimeMillis()
                
                // Filter due medicines (Due time passed and not taken)
                val dueMedicines = medicines.filter { 
                    !it.isTaken && it.dueTimeInMillis > 0 && currentTime > it.dueTimeInMillis 
                }
                
                // All other medicines (or just all of them if you prefer "My Schedule" to show everything)
                // showing all in "My Schedule" for overview
                val allMedicines = medicines
                
                setupDueList(dueMedicines)
                setupMainList(allMedicines)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        medicineViewModel.refresh()
    }

    private fun setupDueList(medicines: List<Medicine>) {
        if (medicines.isNotEmpty()) {
            tvAttentionHeader.visibility = View.VISIBLE
            rvDueMedicines.visibility = View.VISIBLE
            
            dueMedicineAdapter = MedicineAdapter(
                medicines,
                onEditClick = { medicine ->
                    navigateToEdit(medicine)
                },
                onDeleteClick = { medicine ->
                    medicineViewModel.deleteMedicine(medicine, requireContext())
                }
            )
            rvDueMedicines.adapter = dueMedicineAdapter
        } else {
            tvAttentionHeader.visibility = View.GONE
            rvDueMedicines.visibility = View.GONE
        }
    }

    private fun setupMainList(medicines: List<Medicine>) {
        medicineAdapter = MedicineAdapter(
            medicines,
            onEditClick = { medicine ->
                navigateToEdit(medicine)
            },
            onDeleteClick = { medicine ->
                medicineViewModel.deleteMedicine(medicine, requireContext())
            }
        )
        rvMedicineList.adapter = medicineAdapter
    }
    
    private fun navigateToEdit(medicine: Medicine) {
        medicineViewModel.setMedicineToEdit(medicine)
        (activity as? MainActivity)?.let {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddMedicineFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}