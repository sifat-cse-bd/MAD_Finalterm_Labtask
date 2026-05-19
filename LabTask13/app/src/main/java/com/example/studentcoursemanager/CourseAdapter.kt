package com.example.studentcoursemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(
    private var courses: List<Course>,
    private val onEditClick: (Course) -> Unit,
    private val onDeleteClick: (Course) -> Unit,
    private val onItemClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    fun updateList(newList: List<Course>) {
        courses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, onEditClick, onDeleteClick, onItemClick)
    }

    override fun getItemCount(): Int = courses.size

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCourseName)
        private val tvCode: TextView = itemView.findViewById(R.id.tvCourseCode)
        private val tvInstructor: TextView = itemView.findViewById(R.id.tvInstructor)
        private val tvCredits: TextView = itemView.findViewById(R.id.tvCredits)
        private val tvSchedule: TextView = itemView.findViewById(R.id.tvSchedule)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(
            course: Course,
            onEditClick: (Course) -> Unit,
            onDeleteClick: (Course) -> Unit,
            onItemClick: (Course) -> Unit
        ) {
            tvName.text = course.name
            tvCode.text = course.code
            tvInstructor.text = course.instructor
            tvCredits.text = "${course.credits} Credits"
            tvSchedule.text = course.schedule

            btnEdit.setOnClickListener { onEditClick(course) }
            btnDelete.setOnClickListener { onDeleteClick(course) }
            itemView.setOnClickListener { onItemClick(course) }
        }
    }
}
