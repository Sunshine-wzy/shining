package io.github.sunshinewzy.shining.core.addon

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.github.sunshinewzy.shining.core.data.serializer.ShiningAddonJarDescriptionDeserializer
import taboolib.common.env.Dependency

@JsonDeserialize(using = ShiningAddonJarDescriptionDeserializer::class)
data class ShiningAddonJarDescription(
    val name: String,
    val version: String,
    val main: String,
    val shiningVersion: String,
    val authors: List<String>,
    val description: String,
    val depend: Set<String>,
    val softdepend: Set<String>,
    val repositories: List<String>,
    val libraries: List<Dependency>
) {
    
    fun getNameAndVersion(): String = "$name v$version"
    
}