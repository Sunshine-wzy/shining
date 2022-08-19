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
        
        assertEquals(data.getWithType<Int>("awa"), 233)
    }
    
}