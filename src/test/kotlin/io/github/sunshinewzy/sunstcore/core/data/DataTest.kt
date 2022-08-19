package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IData.Companion.getWithType
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import io.github.sunshinewzy.sunstcore.api.namespace.Namespace
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.container.DataContainer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataTest {
    
    @Test
    fun container() {
        val container: IDataContainer = DataContainer()
        val data = container[NamespacedId(Namespace.get("sunstcore"), "awa_container")]
        data["awa"] = 233
        data.createData("qaq.twt")["pap"] = hashMapOf("1" to 1, "2" to 2)
        data.getData("qaq")?.set("owo", 114 to 514)
        
        println(data.getKeys(false))
        println(data.getKeys(true))
        
        println(data.getValues(false))
        println(data.getValues(true))
        
        assertEquals(data.getWithType<Int>("awa"), 233)
    }
    
}