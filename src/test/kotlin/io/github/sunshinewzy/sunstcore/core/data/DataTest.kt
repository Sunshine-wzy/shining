package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IData.Companion.getWithType
import io.github.sunshinewzy.sunstcore.api.data.IDataRoot
import io.github.sunshinewzy.sunstcore.api.data.ISerialData.Companion.setSerializable
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.api.namespace.Namespace
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.container.DataContainer
import io.github.sunshinewzy.sunstcore.core.data.container.SerialDataContainer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataTest {
    private val container: IDataContainer = DataContainer()
    private val serialContainer: ISerialDataContainer = SerialDataContainer()
    
    
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
        
        val project1 = Project1("Steve", 123456L)
        root.setSerializable("a.b.c", project1)
        root.set("a.b.d", project1, Project1.serializer())

        println(root.getValues(true))
    }
    
    
    private fun getDataRoot(id: String): IDataRoot {
        return container[NamespacedId(Namespace.get("sunstcore"), id)]
    }

    private fun getSerialDataRoot(id: String): ISerialDataRoot {
        return serialContainer[NamespacedId(Namespace.get("sunstcore"), id)]
    }
    
}