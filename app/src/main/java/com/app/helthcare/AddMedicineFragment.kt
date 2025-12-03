package com.app.helthcare

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

class AddMedicineFragment : Fragment() {

    private val medicineViewModel: MedicineViewModel by activityViewModels()
    private lateinit var medicineNameEditText: EditText
    private lateinit var medicineDescriptionEditText: EditText
    private lateinit var medicineTimePicker: TimePicker
    private var medicineToEdit: Medicine? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                saveMedicine()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_medicine, container, false)

        medicineNameEditText = view.findViewById(R.id.et_medicine_name)
        medicineDescriptionEditText = view.findViewById(R.id.et_medicine_description)
        medicineTimePicker = view.findViewById(R.id.tp_medicine_time)
        val saveButton = view.findViewById<Button>(R.id.btn_save_medicine)

        medicineViewModel.medicineToEdit.observe(viewLifecycleOwner) { medicine ->
            medicineToEdit = medicine
            if (medicine != null) {
                medicineNameEditText.setText(medicine.name)
                medicineDescriptionEditText.setText(medicine.description)
                val timeParts = medicine.time.split(":")
                if (timeParts.size == 2) {
                    medicineTimePicker.hour = timeParts[0].toInt()
                    medicineTimePicker.minute = timeParts[1].toInt()
                }
                saveButton.text = "Update"
            } else {
                saveButton.text = "Save"
            }
        }

        saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        saveMedicine()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                saveMedicine()
            }
        }

        return view
    }

    private fun saveMedicine() {
        val name = medicineNameEditText.text.toString()
        val description = medicineDescriptionEditText.text.toString()
        val hour = medicineTimePicker.hour
        val minute = medicineTimePicker.minute
        val time = String.format("%02d:%02d", hour, minute)

        if (name.isNotEmpty() && description.isNotEmpty()) {
            // Cancel old work if editing
            medicineToEdit?.workId?.let { workId ->
                WorkManager.getInstance(requireContext()).cancelWorkById(workId)
            }

            val medicineId = medicineToEdit?.id ?: UUID.randomUUID().toString()
            val (workId, dueTime) = scheduleNotification(name, description, hour, minute, medicineId)
            
            val newMedicine = Medicine(
                id = medicineId,
                name = name, 
                description = description, 
                time = time, 
                workId = workId,
                dueTimeInMillis = dueTime
            )

            if (medicineToEdit != null) {
                medicineViewModel.updateMedicine(medicineToEdit!!, newMedicine)
                medicineViewModel.setMedicineToEdit(null) // Reset edit state
            } else {
                medicineViewModel.addMedicine(newMedicine)
            }

            (activity as? MainActivity)?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DashboardFragment())
                    .commit()
            }
        }
    }

    private fun scheduleNotification(name: String, description: String, hour: Int, minute: Int, medicineId: String): Pair<UUID, Long> {
        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = dueTime.timeInMillis - currentTime.timeInMillis

        val data = Data.Builder()
            .putString("medicine_name", name)
            .putString("medicine_description", description)
            .putString("medicine_id", medicineId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
        
        return Pair(workRequest.id, dueTime.timeInMillis)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        if (isRemoving) {
            medicineViewModel.setMedicineToEdit(null)
        }
    }
}