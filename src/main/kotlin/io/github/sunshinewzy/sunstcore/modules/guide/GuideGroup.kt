package io.github.sunshinewzy.sunstcore.modules.guide

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.modules.data.container.LazySerialDataContainer
import io.github.sunshinewzy.sunstcore.modules.data.serializer.ItemStackSerializer
import io.github.sunshinewzy.sunstcore.modules.data.serializer.UUIDSerializer
import io.github.sunshinewzy.sunstcore.modules.menu.MenuBuilder.onBack
import io.github.sunshinewzy.sunstcore.modules.menu.MenuBuilder.openMultiPageMenu
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setLore
import io.github.sunshinewzy.sunstcore.utils.PlayerChatSubscriber
import io.github.sunshinewzy.sunstcore.utils.findPlayer
import io.github.sunshinewzy.sunstcore.utils.isLetterOrDigitOrUnderline
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.util.sync
import taboolib.expansion.getDataContainer
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import java.util.*

@Serializable
class GuideGroup(
    val id: String,
    var name: String,
    @Serializable(UUIDSerializer::class)
    var owner: UUID,
    @Serializable(ItemStackSerializer::class)
    var symbol: ItemStack
) {
    private val members: MutableList<@Serializable(UUIDSerializer::class)UUID> = arrayListOf()

    @Transient
    private val applicants = hashSetOf<UUID>()
    
    
    fun join(player: Player) {
        join(player.uniqueId)
        player.getDataContainer()["guide_group"] = id
    }
    
    private fun join(uuid: UUID) {
        members += uuid
    }
    
    fun leave(player: Player) {
        leave(player.uniqueId)
        player.getDataContainer()["guide_group"] = ""
    }
    
    private fun leave(uuid: UUID) {
        members -= uuid
    }
    
    fun apply(player: Player) {
        apply(player.uniqueId)
        player.getDataContainer()["guide_group_apply"] = id
    }
    
    private fun apply(uuid: UUID) {
        applicants += uuid
    }
    
    
    @SkipTo(LifeCycle.ENABLE)
    companion object GuideGroupManager {
        const val GUIDE_GROUP = "guide_group"
        
        
        private val groupData = LazySerialDataContainer(serializer(), GUIDE_GROUP)
        
        private val createGroupItem = SItem(Material.SLIME_BALL, "&f创建队伍")
        private val joinGroupItem = SItem(Material.ENDER_PEARL, "&f加入队伍")
        private val editGroupIdItem = SItem(Material.COMPASS, "&f编辑队伍ID")
        private val editGroupNameItem = SItem(Material.NAME_TAG, "&f编辑队伍名称")
        
        
        private fun create(id: String, name: String, owner: Player, symbol: ItemStack): Boolean {
            if(groupData.containsKey(id)) return false
            
            groupData[id] = GuideGroup(id, name, owner.uniqueId, symbol)
            owner.getDataContainer()[GUIDE_GROUP] = id
            return true
        }
        
        
        fun Player.hasGuideGroup(): Boolean {
            getDataContainer()[GUIDE_GROUP]?.let { 
                return groupData.containsKey(it)
            }
            return false
        }
        
        fun Player.getGuideGroup(): GuideGroup? {
            getDataContainer()[GUIDE_GROUP]?.let { key ->
                groupData[key]?.let {
                    return it
                }
            }
            
            return null
        }
        
        fun Player.setupGuideGroup() {
            openMenu<Basic>("SGuide - 创建或加入队伍") { 
                rows(3)
                
                map(
                    "",
                    "ooaoooboo"
                )
                
                set('a', createGroupItem)
                set('b', joinGroupItem)
                
                onClick('a') { 
                    createGuideGroup()
                }
                
                onClick('b') {
                    joinGuideGroup()
                }
                
                onClick(lock = true)
            }
        }
        
        
        private fun Player.createGuideGroup(id: String = "", name: String = "", symbol: ItemStack = ItemStack(Material.GRASS_BLOCK)) {
            openMenu<Basic>("SGuide - 创建队伍") {
                rows(3)

                map(
                    "",
                    "oabcoodoo"
                )
                
                set('a', editGroupIdItem.clone().setLore("&c队伍一经创建ID即不可修改！", "", "&a> 当前队伍ID", "", "&e$id"))
                set('b', editGroupNameItem.clone().setLore("", "&a> 当前队伍名称", "", "&e$name"))
                set('c', createGroupItem.clone().setLore("", "&a> 当前队伍信息", "", "&f$name($id)"))
                set('d', createGroupItem.clone().setLore("", "&a> 当前队伍信息", "", "&f$name($id)"))

                onClick('a') {
                    sendMsg(SunSTCore.prefixName, "请输入队伍ID (只允许英文/下划线/数字)")
                    
                    PlayerChatSubscriber(this@createGuideGroup, "队伍ID编辑") {
                        val groupId = message.replace(" ", "")
                        if(groupId.isLetterOrDigitOrUnderline()) {
                            sync {
                                createGuideGroup(
                                    groupId,
                                    if(name == "") groupId else name,
                                    symbol
                                )
                            }
                            return@PlayerChatSubscriber true
                        }
                        
                        sendMsg(SunSTCore.prefixName, "格式错误，请重新输入 (输入 . 以取消)")
                        false
                    }.register()
                    
                    closeInventory()
                }

                onClick('b') {
                    sendMsg(SunSTCore.prefixName, "请输入队伍名称")

                    PlayerChatSubscriber(this@createGuideGroup, "队伍ID编辑") {
                        sync {
                            createGuideGroup(id, message.replace(" ", ""), symbol)
                        }
                        true
                    }.register()
    
                    closeInventory()
                }
                
                onClick('c') {
                    openMultiPageMenu<GuideGroup>("SGuide - 选择队伍图标") {
                        
                    }
                }
                
                onClick('d') {
                    if(id != "" && name != "") {
                        if(create(id, name, this@createGuideGroup, symbol)) {
                            sendMsg(SunSTCore.prefixName, "&a队伍 &f$name($id) &a创建成功，SGuide功能已开启！")
                            closeInventory()
                        } else {
                            sendMsg(SunSTCore.prefixName, "&c队伍ID已存在！")
                            playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                        }
                    } else {
                        sendMsg(SunSTCore.prefixName, "&c队伍ID和名称都不能为空！")
                        playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                    }
                }

                onClick(lock = true)
            }
        }
        
        private fun Player.joinGuideGroup() {
            openMultiPageMenu<GuideGroup>("SGuide - 加入队伍") {
                elements { groupData.getValueList() }

                onGenerate { _, element, index, slot ->
                    buildItem(element.symbol) {
                        this.name = "§f${element.name}"
                        lore += listOf("§7ID: ${element.id}", "", "§e> 队长", "§f${element.owner.findPlayer()?.name ?: element.owner.toString()}", "", "§a> 队员")
                        element.members.forEach { 
                            lore += "§f${it.findPlayer()?.name ?: it.toString()}"
                        }
                    }
                }
            
                onBack {
                    setupGuideGroup()
                }
                
                onClick { event, element ->  
                    element.apply(this@joinGuideGroup)
                }
                
            }
        }
    }
    
}