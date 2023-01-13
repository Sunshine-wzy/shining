package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.lang.LanguageNode
import java.io.File

class LanguageFile(val file: File, val nodeMap: MutableMap<String, LanguageNode>)