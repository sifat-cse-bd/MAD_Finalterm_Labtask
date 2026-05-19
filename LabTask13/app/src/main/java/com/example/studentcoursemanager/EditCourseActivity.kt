package com.example.studentcoursemanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcoursemanager.databinding.ActivityEditCourseBinding
import com.google.firebase.database.FirebaseDatabase

class EditCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCourseBinding
    private val database = FirebaseDatabase.getInstance().getReference("courses")
    private var course: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        course = intent.getSerializableExtra("COURSE_DATA") as? Course

        setupSpinners()
        preFillData()

        binding.btnUpdate.setOnClickListener {
            updateCourse()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
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

    private fun preFillData() {
        course?.let {
            binding.etCourseName.setText(it.name)
            binding.etCourseCode.setText(it.code)
            binding.etInstructor.setText(it.instructor)
            
            val credits = arrayOf(1, 2, 3, 4)
            binding.spinnerCredits.setSelection(credits.indexOf(it.credits))
            
            val semesters = arrayOf("Spring 2025", "Summer 2025", "Fall 2025")
            binding.spinnerSemester.setSelection(semesters.indexOf(it.semester))
            
            binding.etSchedule.setText(it.schedule)
            binding.etRoom.setText(it.room)
        }
    }

    private fun updateCourse() {
        val name = binding.etCourseName.text.toString().trim()
        val code = binding.etCourseCode.text.toString().trim()
        val instructor = binding.etInstructor.text.toString().trim()
        val credits = binding.spinnerCredits.selectedItem as Int
        val schedule = binding.etSchedule.text.toString().trim()
        val room = binding.etRoom.text.toString().trim()
        val semester = binding.spinnerSemester.selectedItem.toString()

        if (name.isEmpty() || code.isEmpty() || instructor.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpdate.isEnabled = false

        val updatedCourse = Course(course?.id, name, code, instructor, credits, schedule, room, semester)

        course?.id?.let { id ->
            database.child(id).setValue(updatedCourse).addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.btnUpdate.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCourse()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCourse() {
        course?.id?.let { id ->
            binding.progressBar.visibility = View.VISIBLE
            database.child(id).removeValue().addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Course deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Deletion failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
