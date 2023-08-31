package io.github.sunshinewzy.shining.api.guide.settings

data class RepeatableSettings(
    var repeatable: Boolean,
    var period: Long
) {
    
    constructor() : this(false, 0)
    
    fun hasRepeatablePeriod(): Boolean = repeatable && period > 0
    
}