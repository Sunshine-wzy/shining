package io.github.sunshinewzy.sunstcore.core.guide

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.menu.MenuBuilder.onBack
import io.github.sunshinewzy.sunstcore.core.menu.MenuBuilder.openMultiPageMenu
import io.github.sunshinewzy.sunstcore.core.menu.MenuBuilder.openSearchMenu
import io.github.sunshinewzy.sunstcore.core.menu.Search
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setLore
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setName
import io.github.sunshinewzy.sunstcore.utils.PlayerChatSubscriber
import io.github.sunshinewzy.sunstcore.utils.player
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.util.sync
import taboolib.expansion.getDataContainer
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import java.util.*

class GuideGroup(id: EntityID<Int>) : IntEntity(id) {
    var name: String by GuideGroups.name
    var owner: UUID by GuideGroups.owner
    var symbol: ItemStack by GuideGroups.symbol
    
    private val members: HashSet<UUID> by GuideGroups.members
    private val applicants: HashSet<UUID> by GuideGroups.applicants


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

    @SkipTo(LifeCycle.ACTIVE)
    companion object : IntEntityClass<GuideGroup>(GuideGroups) {
        const val GUIDE_GROUP = "guide_group"

        private val createGroupItem = SItem(Material.SLIME_BALL, "&f创建队伍")
        private val joinGroupItem = SItem(Material.ENDER_PEARL, "&f加入队伍")
        private val editGroupNameItem = SItem(Material.NAME_TAG, "&f编辑队伍名称")


        private fun create(owner: Player, name: String, symbol: ItemStack): Boolean {
            val container = owner.getDataContainer()
            if(!container[GUIDE_GROUP].isNullOrEmpty()) {
                return false
            }
            
            return transaction {
                find { GuideGroups.owner eq owner.uniqueId }.let {
                    if(!it.empty()) {
                        return@transaction false
                    }
                }

                val guideGroup = new {
                    this.owner = owner.uniqueId
                    this.name = name
                    this.symbol = symbol
                }
                container[GUIDE_GROUP] = guideGroup.id
                true
            }
        }


        fun Player.hasGuideGroup(): Boolean {
            return transaction {
                getDataContainer()[GUIDE_GROUP]?.toInt()?.let { id ->
                    findById(id)?.let {
                        return@transaction true
                    }
                }
                false
            }
        }

        fun Player.getGuideGroup(): GuideGroup? {
            return transaction {
                getDataContainer()[GUIDE_GROUP]?.toInt()?.let { id ->
                    findById(id)?.let {
                        return@transaction it
                    }
                }

                null
            }
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


        private fun Player.createGuideGroup(name: String = "", symbol: ItemStack = ItemStack(Material.GRASS_BLOCK)) {
            openMenu<Basic>("SGuide - 创建队伍") {
                rows(3)

                map(
                    "",
                    "obocoodoo"
                )

                set('b', editGroupNameItem.clone().setLore("", "&a> 当前队伍名称", "", "&e$name"))
                set('c', symbol.clone().setName("&f编辑队伍图标"))
                set('d', createGroupItem.clone().setLore("", "&a> 当前队伍信息", "", "&f$name"))

                
                onClick('b') {
                    sendMsg(SunSTCore.prefixName, "请输入队伍名称")

                    PlayerChatSubscriber(this@createGuideGroup, "队伍ID编辑") {
                        sync {
                            createGuideGroup(message.replace(" ", ""), symbol)
                        }
                        true
                    }.register()

                    closeInventory()
                }

                onClick('c') {
                    openSearchMenu<ItemStack>("SGuide - 选择队伍图标") {
                        searchMap { Search.allItemMap }

                        onClick { event, item ->
                            createGuideGroup(name, item)
                        }

                        onGenerate { _, element, _, _ ->
                            element
                        }

                        onBack {
                            createGuideGroup(name, symbol)
                        }
                    }
                }

                onClick('d') {
                    if(name.isNotBlank()) {
                        if(create(this@createGuideGroup, name, symbol)) {
                            sendMsg(SunSTCore.prefixName, "&a队伍 &f$name &a创建成功，SGuide功能已开启！")
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
                elements { 
                    transaction {
                        all().toList()
                    }
                }

                onGenerate { _, element, index, slot ->
                    buildItem(element.symbol) {
                        this.name = "§f${element.name}"
                        lore += listOf("", "§e> 队长", "§f${element.owner.player}", "", "§a> 队员")
                        element.members.forEach {
                            lore += "§f${it.player}"
                        }
                    }
                }

                onBack {
                    setupGuideGroup()
                }

                onClick { event, element ->
                    element.apply(this@joinGuideGroup)
                    sendMsg(SunSTCore.prefixName, "&a申请加入队伍 &f${element.name} &a成功，等待队长同意")
                    closeInventory()
                }

            }
        }
    }
    
}