package io.github.sunshinewzy.shining.api.data

/**
 * Various settings for controlling the input and output of [IDataRoot]
 *
 *
 * @param pathSeparator The char will be used to separate [IData]
 *
 * This value does not affect how the [IData] is stored,
 * only in how you access the data. The default value is '.'.
 *
 *
 * 该字符被用于分离 [IData] 的路径
 *
 * 该字符并不会影响 [IData] 数据的储存, 它只是路径的分隔符，只会影响你在程序中怎样读取数据。默认为 '.'。
 */
data class DataRootOptions(
    var pathSeparator: Char = '.',
)