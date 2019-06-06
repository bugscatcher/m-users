package com.github.bugscatcher.service

import com.github.bugscatcher.model.ChangeType
import com.github.bugscatcher.model.Notification
import com.github.bugscatcher.model.User
import com.github.bugscatcher.model.Users
import com.github.bugscatcher.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

class UsersService {
    private val listeners = mutableMapOf<Int, suspend (Notification<User?>) -> Unit>()

    private suspend fun onChange(type: ChangeType, id: UUID, entity: User? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }

    suspend fun addUser(user: User): User {
        var key = UUID.randomUUID()
        dbQuery {
            key = (Users.insert {
                it[id] = user.id
                it[name] = user.name
            } get Users.id)
        }
        return getUser(key)!!.also {
            onChange(ChangeType.CREATE, key, it)
        }
    }

    suspend fun getUser(id: UUID): User? = dbQuery {
        Users.select {
            (Users.id eq id)
        }.mapNotNull { toUser(it) }
            .singleOrNull()
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            name = row[Users.name]
        )

    suspend fun deleteUser(fromString: UUID): Boolean {
        return dbQuery {
            Users.deleteWhere { Users.id eq fromString } > 0
        }.also {
            if (it) onChange(type = ChangeType.DELETE, id = fromString)
        }
    }
}