package com.github.bugscatcher.service

import com.github.bugscatcher.model.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object DatabaseFactory {
    fun init() {
        Database.connect(url = "jdbc:h2:tcp://localhost:1521/default", driver = "org.h2.Driver")
        transaction {
            create(Users)
            Users.insert {
                it[id] = UUID.randomUUID()
                it[name] = "test user"
            }
        }
    }

    suspend fun <T> dbQuery(
        block: () -> T
    ): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}