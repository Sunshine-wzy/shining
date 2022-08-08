package io.github.sunshinewzy.sunstcore.core.data.serializer

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag

@Deprecated("Do not use YAML")
open class YamlSerializer<T: ConfigurationSerializable>(
    serialName: String,
    private val clazz: Class<T>
) : StringSerializer<T>(serialName) {
    private val yaml = Yaml()
    
    
    override fun toString(value: T): String {
        return yaml.dumpAs(value.serialize(), Tag.MAP, DumperOptions.FlowStyle.BLOCK)
    }

    override fun fromString(source: String): T {
        return yaml.loadAs(source, clazz)
    }
    
}