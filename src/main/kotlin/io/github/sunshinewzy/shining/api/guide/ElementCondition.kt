package io.github.sunshinewzy.shining.api.guide

enum class ElementCondition {
    COMPLETE,                   // The element has been completed
    UNLOCKED,                   // The element has been unlocked
    LOCKED_DEPENDENCY,          // The dependencies of the element are not completed, so the element is locked
    LOCKED_LOCK,                // The locks of the element are not achieved, so the element is locked
    REPEATABLE                  // The element has been completed, but it can be completed repeatably
}