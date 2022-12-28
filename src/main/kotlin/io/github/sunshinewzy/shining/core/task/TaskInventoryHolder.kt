package io.github.sunshinewzy.shining.core.task

import io.github.sunshinewzy.shining.objects.inventoryholder.SProtectInventoryHolder

class TaskInventoryHolder(task: TaskBase) : SProtectInventoryHolder<Triple<String, String, String>>(
    Triple(
        task.taskStage.taskProject.id,
        task.taskStage.id,
        task.id
    )
) {
    var isEditMode = false
}