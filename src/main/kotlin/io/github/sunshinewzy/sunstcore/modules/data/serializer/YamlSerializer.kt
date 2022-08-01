package io.github.sunshinewzy.sunstcore.modules.data.serializer

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.yaml.snakeyaml.Yaml

open class YamlSerializer<T: ConfigurationSerializable>(serialName: String) : StringSerializer<T>(serialName) {
    private val yaml = Yaml()
    
    
    override fun toString(value: T): String {
        return yaml.dump(value.serialize())
    }

    override fun fromString(source: String): T {
        return yaml.load(source)
    }
    
}