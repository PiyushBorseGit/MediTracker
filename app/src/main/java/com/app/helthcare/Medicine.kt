package com.app.helthcare

import java.util.UUID

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val time: String,
    val workId: UUID? = null,
    var isTaken: Boolean = false,
    var dueTimeInMillis: Long = 0
)