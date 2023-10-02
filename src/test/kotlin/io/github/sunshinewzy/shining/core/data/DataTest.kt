package io.github.sunshinewzy.shining.core.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.IData.Companion.getWithType
import io.github.sunshinewzy.shining.core.data.container.DataContainer
import io.github.sunshinewzy.shining.core.data.container.IDataContainer
import io.github.sunshinewzy.shining.core.data.container.ISerialDataContainer
import io.github.sunshinewzy.shining.core.data.container.SerialDataContainer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataTest {
    private val container: IDataContainer = DataContainer()
    private val serialContainer: ISerialDataContainer = SerialDataContainer(jacksonObjectMapper())
    private val mapper = serialContainer.objectMapper


    @Test
    fun container() {
        val root = getDataRoot("a")
        root["awa"] = 233
        root.createData("qaq.twt")["pap"] = hashMapOf("1" to 1, "2" to 2)
        root.getData("qaq")?.set("owo", 114 to 514)

        println(root.getKeys(false))
        println(root.getKeys(true))

        println(root.getValues(false))
        println(root.getValues(true))

        assertEquals(root.getWithType<Int>("awa"), 233)
    }

    @Test
    fun getByType() {
        val root = getDataRoot("b")
        root["int"] = 114514
        root["q.w.e.double"] = 114.514
        root["q.w.int_list"] = listOf(1, 1, 4, 5, 1, 4)

        assertEquals(root.getInt("int"), 114514)
        assertEquals(root.getDouble("q.w.e.double"), 114.514)

        println(root.getIntList("q.w.int_list"))
    }


    @Test
    fun serialContainer() {
        val root = getSerialDataRoot("a")

        root["a.b.c"] = Project1("Steve", 12345L)
        root["a.b.d"] = Project1("Mike", 67890L)
        root["a.e"] = 123
        root["awa"] = 114.514

        val string = serialContainer.serializeToString()
        println(string)

        val container = ISerialDataContainer.deserialize(string, mapper)
        println(container)
    }


    private fun getDataRoot(id: String): IDataRoot {
        return container[NamespacedId(Namespace.get("shining"), id)]
    }

    private fun getSerialDataRoot(id: String): ISerialDataRoot {
        return serialContainer[NamespacedId(Namespace.get("shining"), id)]
    }

}