package io.github.sunshinewzy.sunstcore.core.data.persistence;

import io.github.sunshinewzy.sunstcore.core.data.Data;
import io.github.sunshinewzy.sunstcore.core.data.IData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistentDataJavaTest {
	
	@Test
	public void setAndGet() {
		IData data = new Data();
		data.set("awa", 123);
		Object value = data.get("awa");
		
		assertEquals(value, 123);
	}
	
}
