package io.github.sunshinewzy.shining.core.data

import com.fasterxml.jackson.annotation.JsonValue

class JacksonWrapper<T>(@JsonValue val value: T)