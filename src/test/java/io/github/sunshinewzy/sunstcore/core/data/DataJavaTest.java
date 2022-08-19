package io.github.sunshinewzy.sunstcore.core.data;

import io.github.sunshinewzy.sunstcore.api.data.IData;
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer;
import io.github.sunshinewzy.sunstcore.api.namespace.Namespace;
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId;
import io.github.sunshinewzy.sunstcore.core.data.container.DataContainer;
import kotlin.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataJavaTest {
	
	@Test
	public void container() {
		IDataContainer container = new DataContainer();
		IData data = container.get(new NamespacedId(Namespace.get("sunstcore"), "awa_container"));
		data.set("awa", 233);
		
		HashMap<String, Integer> map = new HashMap<>();
		map.put("1", 1);
		map.put("2", 2);
		data.createData("qaq.twt").set("pap", map);
		Optional.ofNullable(data.getData("qaq"))
				.ifPresent(qaqData -> qaqData.set("owo", new Pair<>(114, 514)));

		System.out.println(data.getKeys(false));
		System.out.println(data.getKeys(true));
		
		System.out.println(data.getValues(false));
		System.out.println(data.getValues(true));
		
		int value = data.getWithType("awa", Integer.class, 0);
		assertEquals(value, 233);
	}
	
}
