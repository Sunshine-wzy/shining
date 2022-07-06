package io.github.sunshinewzy.sunstcore.modules.data.sunst

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.modules.data.SAutoSaveData
import io.github.sunshinewzy.sunstcore.modules.task.TaskProject
import org.bukkit.configuration.file.YamlConfiguration

/**
 * 自定义任务数据保存
 */
class SCustomTaskData(val taskProject: TaskProject) : SAutoSaveData(SunSTCore.plugin, taskProject.id, "TaskData") {

    override fun YamlConfiguration.createConfig() {
        
    }

    override fun YamlConfiguration.modifyConfig() {
        
    }

    override fun YamlConfiguration.loadConfig() {
        
    }
    
}