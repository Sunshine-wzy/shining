package io.github.sunshinewzy.sunstcore.utils

import kotlin.annotation.AnnotationTarget.*

/**
 * 标记这个类, 类型, 函数, 属性, 字段, 或构造器为测试使用的 API.
 * 
 * 这些 API 仅供测试使用, 不具有稳定性, 且可能会在任意时刻更改.
 * 不建议在发行版本中使用这些 API.
 */
@Retention(AnnotationRetention.BINARY)
@Target(CLASS, TYPEALIAS, FUNCTION, PROPERTY, FIELD, CONSTRUCTOR)
annotation class SunSTTestApi(val message: String = "")