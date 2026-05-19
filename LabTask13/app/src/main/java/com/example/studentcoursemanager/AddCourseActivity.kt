package com.example.studentcoursemanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcoursemanager.databinding.ActivityAddCourseBinding
import com.google.firebase.database.FirebaseDatabase

class AddCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCourseBinding
    private val database = FirebaseDatabase.getInstance().getReference("courses")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()

        binding.btnSave.setOnClickListener {
            saveCourse()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinners() {
        val credits = arrayOf(1, 2, 3, 4)
        val creditAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, credits)
        creditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCredits.adapter = creditAdapter

        val semesters = arrayOf("Spring 2025", "Summer 2025", "Fall 2025")
        val semesterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, semesters)
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSemester.adapter = semesterAdapter
    }

    private fun saveCourse() {
        val name = binding.etCourseName.text.toString().trim()
        val code = binding.etCourseCode.text.toString().trim()
        val instructor = binding.etInstructor.text.toString().trim()
        val credits = binding.spinnerCredits.selectedItem as Int
        val schedule = binding.etSchedule.text.toString().trim()
        val room = binding.etRoom.text.toString().trim()
        val semester = binding.spinnerSemester.selectedItem.toString()

        if (name.isEmpty() || code.isEmpty() || instructor.isEmpty()) {
            makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        val id = database.push().key
        val course = Course(id, name, code, instructor, credits, schedule, room, semester)

        if (id != null) {
            database.child(id).setValue(course).addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                if (task.isSuccessful) {
                    makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    makeText(this, "Failed to add course: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
