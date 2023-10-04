package io.github.sunshinewzy.shining.api.lang

import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import java.io.File

class LanguageFile(val file: File, val nodeMap: MutableMap<String, LanguageNode>)