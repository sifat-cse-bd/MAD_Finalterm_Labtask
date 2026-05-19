package com.example.studentcoursemanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentcoursemanager.databinding.ActivityMainBinding
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CourseAdapter
    private val database = FirebaseDatabase.getInstance().getReference("courses")
    private val courseList = mutableListOf<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        fetchCourses()

        binding.fabAddCourse.setOnClickListener {
            startActivity(Intent(this, AddCourseActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter(
            courses = courseList,
            onEditClick = { course ->
                val intent = Intent(this, EditCourseActivity::class.java)
                intent.putExtra("COURSE_DATA", course)
                startActivity(intent)
            },
            onDeleteClick = { course ->
                showDeleteConfirmation(course)
            },
            onItemClick = { course ->
                val intent = Intent(this, CourseDetailActivity::class.java)
                intent.putExtra("COURSE_DATA", course)
                startActivity(intent)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filtered = if (query.isNullOrBlank()) {
            courseList
        } else {
            courseList.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                it.code?.contains(query, ignoreCase = true) == true
            }
        }
        adapter.updateList(filtered)
    }

    private fun fetchCourses() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courseList.clear()
                for (postSnapshot in snapshot.children) {
                    val course = postSnapshot.getValue(Course::class.java)
                    if (course != null) {
                        courseList.add(course)
                    }
                }
                adapter.updateList(courseList)
                binding.emptyStateView.visibility = if (courseList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmation(course: Course) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Delete") { _, _ ->
                course.id?.let {
                    database.child(it).removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
