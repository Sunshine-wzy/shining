package io.github.sunshinewzy.sunstcore.core.data;

import io.github.sunshinewzy.sunstcore.api.data.IData;
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer;
import io.github.sunshinewzy.sunstcore.api.namespace.Namespace;
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId;
import io.github.sunshinewzy.sunstcore.core.data.container.DataContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataJavaTest {
	
	@Test
	public void setAndGet() {
		IData data = new Data("qwq", new DataContainer());
		data.set("awa", 123);
		Object value = data.get("awa");
		
		assertEquals(value, 123);
	}
	
	@Test
	public void container() {
		IDataContainer container = new DataContainer();
		IData data = container.get(new NamespacedId(Namespace.get("sunstcore"), "awa_container"));
		data.set("awa", 233);
		
		int value = data.getWithType("awa", Integer.class, 0);
		assertEquals(value, 233);
	}
	
}
