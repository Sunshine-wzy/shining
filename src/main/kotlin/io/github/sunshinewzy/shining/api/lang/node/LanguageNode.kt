package io.github.sunshinewzy.shining.api.lang.node

interface LanguageNode {

    companion object {
        val argRegex = Regex("\\{[0-9]+}")
    }

}