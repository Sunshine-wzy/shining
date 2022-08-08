package io.github.sunshinewzy.sunstcore.core.task

import io.github.sunshinewzy.sunstcore.objects.inventoryholder.SProtectInventoryHolder

class TaskInventoryHolder(task: TaskBase) : SProtectInventoryHolder<Triple<String, String, String>>(
    Triple(
        task.taskStage.taskProject.id,
        task.taskStage.id,
        task.id
    )
) {
    var isEditMode = false
}