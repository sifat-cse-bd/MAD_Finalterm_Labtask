package com.example.studentcoursemanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcoursemanager.databinding.ActivityCourseDetailBinding

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDetailBinding
    private var course: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        course = intent.getSerializableExtra("COURSE_DATA") as? Course

        setupToolbar()
        displayCourseDetails()

        binding.fabEditCourse.setOnClickListener {
            val intent = Intent(this, EditCourseActivity::class.java)
            intent.putExtra("COURSE_DATA", course)
            startActivity(intent)
            finish() // Optional: close detail view when going to edit
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun displayCourseDetails() {
        course?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailCode.text = it.code
            binding.tvDetailInstructor.text = it.instructor
            binding.tvDetailCredits.text = "${it.credits} Credits"
            binding.tvDetailSchedule.text = it.schedule
            binding.tvDetailRoom.text = it.room
            binding.tvDetailSemester.text = it.semester
        }
    }
}
