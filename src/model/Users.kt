package com.github.bugscatcher.model

import org.jetbrains.exposed.sql.Table
import java.util.*

object Users : Table() {
    val id = uuid("id").primaryKey()
    val name = varchar("name", 255)
}

data class User(
    val id: UUID,
    val name: String
)