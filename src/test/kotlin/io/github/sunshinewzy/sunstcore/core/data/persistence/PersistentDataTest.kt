package io.github.sunshinewzy.sunstcore.core.data.persistence

import io.github.sunshinewzy.sunstcore.core.data.Data
import io.github.sunshinewzy.sunstcore.core.data.IData
import io.github.sunshinewzy.sunstcore.core.data.IData.Companion.getByType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PersistentDataTest {
    
    @Test
    fun setAndGet() {
        val data: IData = Data()
        data["awa"] = 123
        
        assertEquals(data.getByType<Int>("awa"), 123)
    }
    
}