package io.github.sunshinewzy.shining.core.data.serializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import io.github.sunshinewzy.shining.core.addon.ShiningAddonJarDescription
import io.github.sunshinewzy.shining.core.addon.loader.ShiningAddonLoadingException
import taboolib.common.env.Dependency

object ShiningAddonJarDescriptionDeserializer : StdDeserializer<ShiningAddonJarDescription>(ShiningAddonJarDescription::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ShiningAddonJarDescription {
        val node = p.readValueAsTree<JsonNode>()
        
        val name = node["name"]?.asText() ?: throw ShiningAddonLoadingException("null", "Missing required field 'name'")
        val version = node["version"]?.asText() ?: throw ShiningAddonLoadingException(name, "Missing required field 'version'")
        val main = node["main"]?.asText() ?: throw ShiningAddonLoadingException(name, "Missing required field 'main'")
        val shiningVersion = node["shining_version"]?.asText() ?: throw ShiningAddonLoadingException(name, "Missing required field 'shining_version'")
        val authors = node["authors"]?.let { 
            ctxt.readTreeAsValue<ArrayList<String>>(it, ctxt.typeFactory.constructCollectionType(ArrayList::class.java, String::class.java))
        } ?: node["author"]?.asText()?.let { listOf(it) } ?: throw ShiningAddonLoadingException(name, "Missing required field 'author' or 'authors'")
        if (authors.isEmpty()) throw ShiningAddonLoadingException(name, "List of authors cannot be empty")
        
        return ShiningAddonJarDescription(
            name, version, main, shiningVersion, authors,
            node["description"]?.asText() ?: "",
            node["depend"]?.let { ctxt.readTreeAsValue<HashSet<String>>(it, ctxt.typeFactory.constructCollectionType(HashSet::class.java, String::class.java)) } ?: emptySet(),
            node["softdepend"]?.let { ctxt.readTreeAsValue<HashSet<String>>(it, ctxt.typeFactory.constructCollectionType(HashSet::class.java, String::class.java)) } ?: emptySet(),
            node["repositories"]?.let { ctxt.readTreeAsValue<ArrayList<String>>(it, ctxt.typeFactory.constructCollectionType(ArrayList::class.java, String::class.java)) } ?: emptyList(),
            node["libraries"]?.let { ctxt.readTreeAsValue<ArrayList<Dependency>>(it, ctxt.typeFactory.constructCollectionType(ArrayList::class.java, Dependency::class.java)) } ?: emptyList()
        )
    }
    
}