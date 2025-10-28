package com.app.helthcare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels

class SettingsFragment : Fragment() {

    private val medicineViewModel: MedicineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val updateNameEditText = view.findViewById<EditText>(R.id.et_update_name)
        val updateNameButton = view.findViewById<Button>(R.id.btn_update_name)
        val resetDataButton = view.findViewById<Button>(R.id.btn_reset_data)

        updateNameButton.setOnClickListener {
            val newName = updateNameEditText.text.toString()
            if (newName.isNotEmpty()) {
                updateName(newName)
            }
        }

        resetDataButton.setOnClickListener {
            showResetDataConfirmationDialog()
        }

        return view
    }

    private fun updateName(newName: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("username", newName)
            apply()
        }
        (activity as? MainActivity)?.updateToolbarTitle()
    }

    private fun showResetDataConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset App Data")
            .setMessage("Are you sure you want to reset all app data? This action cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                resetAppData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetAppData() {
        // Clear SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Clear ViewModel data
        medicineViewModel.medicines.value?.clear()

        // Restart the app
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}