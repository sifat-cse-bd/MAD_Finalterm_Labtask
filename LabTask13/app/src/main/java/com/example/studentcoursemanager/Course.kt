package com.example.studentcoursemanager

import java.io.Serializable

data class Course(
    var id: String? = null,
    val name: String? = null,
    val code: String? = null,
    val instructor: String? = null,
    val credits: Int? = null,
    val schedule: String? = null,
    val room: String? = null,
    val semester: String? = null
) : Serializable
