package io.github.sunshinewzy.sunstcore.core.task

import io.github.sunshinewzy.sunstcore.utils.castMapBoolean
import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * 任务进度
 */
class TaskProgress() : ConfigurationSerializable {
    private val progress = HashMap<String, MutableMap<String, Boolean>>()

    
    init {
        
    }
    
    constructor(map: Map<String, Any>) : this() {
        map.forEach { (key, value) ->
            if(key == "==") return@forEach
            
            val mapCast = value.castMapBoolean()
            progress[key] = mapCast
        }
    }
    
    
    override fun serialize(): Map<String, Any> {
        val map = HashMap<String, Any>()
        
        progress.forEach { (key, value) -> 
            map[key] = value
        }
        
        return map
    }
    
    
    fun completeTask(task: TaskBase, isCompleted: Boolean = true) {
        val stageId = task.taskStage.id
        val taskId = task.id

        if(progress.containsKey(stageId)){
            val stagePro = progress[stageId] ?: kotlin.run { 
                val map = HashMap<String, Boolean>()
                progress[stageId] = map
                map
            }
            stagePro[taskId] = isCompleted
        }
        else progress[stageId] = hashMapOf(taskId to isCompleted)
    }
    
    fun hasCompleteTask(task: TaskBase): Boolean {
        val taskStage = task.taskStage
        
        if(progress.containsKey(taskStage.id)){
            val stagePro = progress[taskStage.id]
            if(stagePro?.containsKey(task.id) == true){
                val taskPro = stagePro[task.id]
                if(taskPro == true){
                    return true
                }
            }
        }
        
        return false
    }
    
    fun hasCompleteStage(taskStage: TaskStage): Boolean {
        val finalTask = taskStage.finalTask ?: return true

        if(progress.containsKey(taskStage.id)){
            val stagePro = progress[taskStage.id]
            if(stagePro?.containsKey(finalTask.id) == true){
                val taskPro = stagePro[finalTask.id]
                if(taskPro == true){
                    return true
                }
            }
        }
        
        return false
    }
}