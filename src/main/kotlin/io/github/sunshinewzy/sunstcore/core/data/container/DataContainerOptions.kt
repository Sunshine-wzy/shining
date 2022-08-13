package io.github.sunshinewzy.sunstcore.core.data.container

import io.github.sunshinewzy.sunstcore.core.data.IData

/**
 * Various settings for controlling the input and output of [IDataContainer]
 * 
 * 
 * @param pathSeparator The char will be used to separate [IData]
 * 
 * This value does not affect how the [IDataContainer] is stored,
 * only in how you access the data. The default value is '.'.
 * 
 * 
 * 该字符被用于分离 [IData] 的路径
 * 
 * 这个字符并不会影响数据的储存, 它只是路径的分隔符，只会影响你在程序中怎样读取数据。默认为 '.'。
 * 
 * @param ignorePathSeparator The char will be used to ignore [pathSeparator]
 * in the path argument
 * 
 * Place the char at the beginning of path, and it will work.
 * The default value is '!'.
 * 
 * For example, to get the value of key "sunstcore:a.b.c",
 * use "!sunstcore:a.b.c" instead of "sunstcore:a.b.c".
 * 
 * 
 * 该字符用于忽略 path 参数中的 [分隔符][pathSeparator]
 * 
 * 将该字符放在 path 的首位就能起作用。默认为 '!'。
 * 
 * 例如，使用 "!sunstcore:a.b.c" 而非 "sunstcore:a.b.c" 来获得键
 * "sunstcore:a.b.c" 的值。
 * 
 */
class DataContainerOptions(
    var pathSeparator: Char = '.',
    var ignorePathSeparator: Char = '!'
)