package io.github.sunshinewzy.sunstcore.utils

import org.bukkit.Location
import org.bukkit.block.Block

class BlockOperator(val block: Block) {
    
    constructor(loc: Location) : this(loc.block)
    
    
    inline fun x(offset: Int, operator: BlockOperator.() -> Unit) {
        val loc = block.location
        loc.x += offset
        operator(BlockOperator(loc))
    }

    inline fun y(offset: Int, operator: BlockOperator.() -> Unit) {
        val loc = block.location
        loc.y += offset
        operator(BlockOperator(loc))
    }

    inline fun z(offset: Int, operator: BlockOperator.() -> Unit) {
        val loc = block.location
        loc.z += offset
        operator(BlockOperator(loc))
    }

    fun block(operator: Block.() -> Unit) {
        operator(block)
    }

    /**
     * 空间四周六个面
     */
    fun surroundings(operation: Block.() -> Boolean): Boolean {
        var flag = false
        
        x(1) {
            flag = operation(block)
        }
        if(flag) return true
        
        x(-1) {
            flag = operation(block)
        }
        if(flag) return true

        y(1) {
            flag = operation(block)
        }
        if(flag) return true

        y(-1) {
            flag = operation(block)
        }
        if(flag) return true

        z(1) {
            flag = operation(block)
        }
        if(flag) return true

        z(-1) {
            flag = operation(block)
        }
        if(flag) return true
        
        return false
    }

    /**
     * 水平面四周
     * 
     * @param around 是否包含四个角
     */
    fun horizontal(around: Boolean = false, operation: Block.() -> Boolean) {
        var flag = false

        x(1) {
            flag = operation(block)

            if(around && !flag) {
                z(1) {
                    flag = operation(block)
                }
                if(flag) return
                z(-1) {
                    flag = operation(block)
                }
            }
        }
        if(flag) return

        x(-1) {
            flag = operation(block)

            if(around && !flag) {
                z(1) {
                    flag = operation(block)
                }
                if(flag) return
                z(-1) {
                    flag = operation(block)
                }
            }
        }
        if(flag) return

        z(1) {
            flag = operation(block)
        }
        if(flag) return

        z(-1) {
            flag = operation(block)
        }
    }
    
    
    companion object {
        fun Block.operate(operator: BlockOperator.() -> Unit) {
            operator(BlockOperator(this))
        }
        
        fun Location.operate(operator: BlockOperator.() -> Unit) {
            operator(BlockOperator(this))
        }
    }
    
}