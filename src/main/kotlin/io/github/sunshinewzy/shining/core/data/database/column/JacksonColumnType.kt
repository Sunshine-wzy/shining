package io.github.sunshinewzy.shining.core.data.database.column

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.TextColumnType

class JacksonColumnType(
    private val objectMapper: ObjectMapper,
    typeConstructor: ObjectMapper.() -> JavaType
) : TextColumnType() {
    val type: JavaType = typeConstructor(objectMapper)
    

    override fun valueFromDB(value: Any): Any {
        super.valueFromDB(value).let { 
            if(it is String) {
                return objectMapper.readValue(it, type)
            }
        }
        return value
    }

    override fun notNullValueToDB(value: Any): Any {
        return super.notNullValueToDB(serialize(value))
    }

    override fun nonNullValueToString(value: Any): String {
        return super.nonNullValueToString(serialize(value))
    }
    
    
    private fun serialize(value: Any): Any {
        if(type.rawClass.isInstance(value)) {
            return objectMapper.writeValueAsString(value)
        }
        return value
    }
}


fun <T> Table.jackson(name: String, objectMapper: ObjectMapper, typeConstructor: ObjectMapper.() -> JavaType): Column<T> =
    registerColumn(name, JacksonColumnType(objectMapper, typeConstructor))

fun <T> Table.jackson(name: String, objectMapper: ObjectMapper, type: Class<T>): Column<T> =
    jackson(name, objectMapper) {
        constructType(type)
    }

fun <T> Table.jackson(name: String, objectMapper: ObjectMapper, type: TypeReference<T>): Column<T> =
    jackson(name, objectMapper) {
        constructType(type)
    }