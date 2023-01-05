package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.executePlayerDataContainer
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.getDataContainer
import io.github.sunshinewzy.shining.core.menu.MenuBuilder.onBack
import io.github.sunshinewzy.shining.core.menu.MenuBuilder.openMultiPageMenu
import io.github.sunshinewzy.shining.core.menu.MenuBuilder.openSearchMenu
import io.github.sunshinewzy.shining.core.menu.Search
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.SItem.Companion.setLore
import io.github.sunshinewzy.shining.objects.SItem.Companion.setName
import io.github.sunshinewzy.shining.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.submit
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import java.util.*

class GuideTeam(id: EntityID<Int>) : IntEntity(id) {
    var name: String by GuideTeams.name
    var owner: UUID by GuideTeams.owner
    var symbol: ItemStack by GuideTeams.symbol
    
    private var members: HashSet<UUID> by GuideTeams.members
    private var applicants: HashSet<UUID> by GuideTeams.applicants


    fun join(player: Player) {
        join(player.uniqueId)
        player.getDataContainer()["guide_team"] = id
    }

    private fun join(uuid: UUID) {
        members.let { 
            it += uuid
            members = it
        }
    }

    fun leave(player: Player) {
        leave(player.uniqueId)
        player.getDataContainer()
    }

    private fun leave(uuid: UUID) {
        members.let { 
            it -= uuid
            members = it
        }
    }

    fun apply(player: Player) {
        apply(player.uniqueId)
        player.getDataContainer()["guide_team_apply"] = id
    }

    private fun apply(uuid: UUID) {
        applicants.let { 
            it += uuid
            applicants = it
        }
    }
    
    fun accept(uuid: UUID): Boolean {
        if(!applicants.contains(uuid))
            return false
        
        join(uuid)
        uuid.executePlayerDataContainer { 
            it["guide_team"] = id
        }
        applicants.let { 
            it -= uuid
            applicants = it
        }
        return true
    }
    

    @SkipTo(LifeCycle.ACTIVE)
    companion object : IntEntityClass<GuideTeam>(GuideTeams) {
        const val GUIDE_TEAM = "guide_team"

        private val createTeamItem = SItem(Material.SLIME_BALL, "&f创建队伍")
        private val joinTeamItem = SItem(Material.ENDER_PEARL, "&f加入队伍")
        private val editTeamNameItem = SItem(Material.NAME_TAG, "&f编辑队伍名称")


        private suspend fun create(owner: Player, name: String, symbol: ItemStack): Boolean {
            val container = owner.getDataContainer()
            if(!container[GUIDE_TEAM].isNullOrEmpty()) {
                return false
            }
            
            return newSuspendedTransaction transaction@{
                find { GuideTeams.owner eq owner.uniqueId }.let {
                    if(!it.empty()) {
                        return@transaction false
                    }
                }

                val guideTeam = new {
                    this.owner = owner.uniqueId
                    this.name = name
                    this.symbol = symbol
                    this.members = hashSetOf()
                    this.applicants = hashSetOf()
                }
                container[GUIDE_TEAM] = guideTeam.id
                true
            }
        }


        suspend fun Player.hasGuideTeam(): Boolean {
            return newSuspendedTransaction transaction@{
                getDataContainer()[GUIDE_TEAM]?.toInt()?.let { id ->
                    findById(id)?.let {
                        return@transaction true
                    }
                }
                false
            }
        }

        suspend fun Player.getGuideTeam(): GuideTeam? {
            return newSuspendedTransaction transaction@{
                getDataContainer()[GUIDE_TEAM]?.toInt()?.let { id ->
                    findById(id)?.let {
                        return@transaction it
                    }
                }

                null
            }
        }

        fun Player.setupGuideTeam() {
            openMenu<Basic>("SGuide - 创建或加入队伍") {
                rows(3)

                map(
                    "",
                    "ooaoooboo"
                )

                set('a', createTeamItem)
                set('b', joinTeamItem)

                onClick('a') {
                    createGuideTeam()
                }

                onClick('b') {
                    joinGuideTeam()
                }

                onClick(lock = true)
            }
        }


        private fun Player.createGuideTeam(name: String = "", symbol: ItemStack = ItemStack(Material.GRASS_BLOCK)) {
            openMenu<Basic>("SGuide - 创建队伍") {
                rows(3)

                map(
                    "",
                    "obocoodoo"
                )

                set('b', editTeamNameItem.clone().setLore("", "&a> 当前队伍名称", "", "&e$name"))
                set('c', symbol.clone().setName("&f编辑队伍图标"))
                set('d', createTeamItem.clone().setLore("", "&a> 当前队伍信息", "", "&f$name"))

                
                onClick('b') {
                    sendMsg(Shining.prefixName, "请输入队伍名称")

                    PlayerChatSubscriber(this@createGuideTeam, "队伍ID编辑") {
                        submit {
                            createGuideTeam(message.replace(" ", ""), symbol)
                        }
                        true
                    }.register()

                    closeInventory()
                }

                onClick('c') {
                    openSearchMenu<ItemStack>("SGuide - 选择队伍图标") {
                        searchMap { Search.allItemMap }

                        onClick { event, item ->
                            createGuideTeam(name, item)
                        }

                        onGenerate { _, element, _, _ ->
                            element
                        }

                        onBack {
                            createGuideTeam(name, symbol)
                        }
                    }
                }

                onClick('d') {
                    if(name.isNotBlank()) {
                        Shining.scope.launch(Dispatchers.IO) {
                            if(create(this@createGuideTeam, name, symbol)) {
                                sendMsg(Shining.prefixName, "&a队伍 &f$name &a创建成功，SGuide功能已开启！")
                                submit {
                                    closeInventory()
                                    playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                                }
                            } else {
                                sendMsg(Shining.prefixName, "&c队伍ID已存在！")
                                playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                            }
                        }
                    } else {
                        sendMsg(Shining.prefixName, "&c队伍ID和名称都不能为空！")
                        playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                    }
                }

                onClick(lock = true)
            }
        }

        private fun Player.joinGuideTeam() {
            Shining.scope.launch(Dispatchers.IO) {
                val list = newSuspendedTransaction {
                    all().toList()
                }
                val s = getDataContainer()["guide_team_apply"]
                
                submit {
                    openMultiPageMenu<GuideTeam>("SGuide - 加入队伍") {
                        elements { list }

                        onGenerate(async = true) { _, element, index, slot ->
                            buildItem(element.symbol) {
                                this.name = "§f${element.name}"
                                lore += listOf("", "§e> 队长", "§f${element.owner.offlinePlayer.name}", "", "§a> 队员")
                                element.members.forEach {
                                    lore += "§f${it.offlinePlayer.name}"
                                }
                            }
                        }

                        onBack {
                            setupGuideTeam()
                        }

                        onClick { event, element ->
                            element.apply(this@joinGuideTeam)
                            sendMsg(Shining.prefixName, "&a申请加入队伍 &f${element.name} &a成功，等待队长同意")
                            closeInventory()
                        }

                    }
                }
            }
        }
    }
    
}