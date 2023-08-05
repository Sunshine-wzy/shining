package io.github.sunshinewzy.shining.core.guide.team

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.event.ShiningGuideTeamSetupEvent
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.executePlayerDataContainer
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.getDataContainer
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.lang.*
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.Search
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.core.menu.openSearchMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.submit
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import java.util.*

open class GuideTeam(id: EntityID<Int>) : IntEntity(id) {
    var name: String by GuideTeams.name
    var captain: UUID by GuideTeams.captain
    var symbol: ItemStack by GuideTeams.symbol

    private var members: JacksonWrapper<HashSet<UUID>> by GuideTeams.members
    private var applicants: JacksonWrapper<HashSet<UUID>> by GuideTeams.applicants
    private var data: JacksonWrapper<GuideTeamData> by GuideTeams.data
    

    suspend fun join(player: Player) {
        join(player.uniqueId)
        player.getDataContainer()[GUIDE_TEAM_ID] = id
    }

    private suspend fun join(uuid: UUID) {
        newSuspendedTransaction {
            members.value.let {
                it += uuid
                members = JacksonWrapper(it)
            }
        }
    }

    suspend fun leave(player: Player) {
        leave(player.uniqueId)
        player.getDataContainer().delete(player.uniqueId.toString())
    }

    private suspend fun leave(uuid: UUID) {
        newSuspendedTransaction {
            members.value.let {
                it -= uuid
                members = JacksonWrapper(it)
            }
        }
    }

    suspend fun apply(player: Player) {
        apply(player.uniqueId)
        player.getDataContainer()[GUIDE_TEAM_APPLY] = id
        notifyCaptainApplication()
    }

    private suspend fun apply(uuid: UUID) {
        newSuspendedTransaction {
            applicants.value.let {
                it += uuid
                applicants = JacksonWrapper(it)
            }
        }
    }
    
    suspend fun getTeamData(): GuideTeamData =
        newSuspendedTransaction { data.value }
    
    suspend fun updateTeamData() {
        newSuspendedTransaction { 
            data.value.let { 
                data = JacksonWrapper(it)
            }
        }
    }

    suspend fun approveApplication(uuid: UUID): Boolean {
        if (!applicants.value.contains(uuid))
            return false

        join(uuid)
        uuid.executePlayerDataContainer {
            it[GUIDE_TEAM_ID] = id
            it.delete(GUIDE_TEAM_APPLY)
        }

        newSuspendedTransaction {
            applicants.value.let {
                it -= uuid
                applicants = JacksonWrapper(it)
            }
        }
        return true
    }

    suspend fun refuseApplication(uuid: UUID): Boolean {
        if (!applicants.value.contains(uuid))
            return false

        uuid.executePlayerDataContainer {
            it.delete(GUIDE_TEAM_APPLY)
        }

        newSuspendedTransaction {
            applicants.value.let {
                it -= uuid
                applicants = JacksonWrapper(it)
            }
        }
        return true
    }

    fun notifyCaptainApplication() {
        if (applicants.value.isEmpty()) return

        captain.player?.let { player ->
            val names = applicants.value.joinToString { it.playerName }
            player.getLangSectionNode("menu-shining_guide-team-notify_captain_application")?.let {
                it.sendPrefixedJson(player, Shining.prefix, names)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f)
            }
        }
    }

    fun getOnlinePlayers(): List<Player> {
        val list = ArrayList<Player>()
        captain.player?.let { list += it }
        members.value.mapNotNullTo(list) { it.player }
        return list
    }

    fun welcome(uuid: UUID) {
        uuid.player?.let { player ->
            ShiningGuide.fireworkCongratulate(player)
        }

        val playerName = uuid.playerName
        getOnlinePlayers().forEach { player ->
            player.sendPrefixedLangText("menu-shining_guide-team-welcome", Shining.prefix, playerName, name)
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f)
        }
    }

    fun openInfoMenu(player: Player) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-team-info-title")) {
            rows(5)

            map(
                "-B-------",
                "-   a  m-",
                "- b     -",
                "-       -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player), ShiningGuide.onClickBack)

            set('a', symbol.clone().localize(player.getLanguageNode("menu-shining_guide-team-info-symbol"), name))
            set(
                'b',
                buildItem(Material.PLAYER_HEAD) {
                    skullOwner = captain.playerName
                    if (player.uniqueId == captain) shiny()
                }.localize(player.getLanguageNode("menu-shining_guide-team-info-captain"), captain.playerName)
            )

            if (player.uniqueId == captain) {
                set('m', teamManageItem.toLocalizedItem(player)) {
                    openManageMenu(player)
                }
            }


            onClick(lock = true)
        }
    }

    fun openManageMenu(player: Player) {
        if (player.uniqueId != captain) {
            player.sendPrefixedLangText("menu-shining_guide-team-manage-no_permission")
            return
        }

        player.openMenu<Basic>(player.getLangText("menu-shining_guide-team-manage-title")) {
            rows(6)

            map(
                "-B-------",
                "-a      -",
                "-       -",
                "-       -",
                "-       -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player), ShiningGuide.onClickBack)

            set('a', if (applicants.value.isEmpty()) applicationManageItem else applicationManageItem.shinyItem) {
                openManageApplicationMenu(player)
            }


            onClick(lock = true)
        }
    }

    fun openManageApplicationMenu(player: Player) {
        player.openMultiPageMenu<UUID>(player.getLangText("menu-shining_guide-team-manage-application-title")) {
            elements { applicants.value.toList() }

            onGenerate(true) { player, element, index, slot ->
                buildItem(Material.PLAYER_HEAD) {
                    val applicant = element.playerName
                    skullOwner = applicant
                    name = "§f$applicant"
                    player.getLangListNode("menu-shining_guide-team-manage-application-applicant")
                        ?.getColoredStringList()
                        ?.let { lore += it }
                }
            }

            onBack(player) {
                openManageMenu(player)
            }

            onClick { event, element ->
                when (event.clickEvent().click) {
                    ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                        ShiningDispatchers.launchDB {
                            if (approveApplication(element)) {
                                player.sendPrefixedLangText(
                                    "menu-shining_guide-team-manage-application-approve",
                                    Shining.prefix,
                                    element.playerName
                                )
                                welcome(element)
                            } else {
                                player.sendPrefixedLangText(
                                    "menu-shining_guide-team-manage-application-not_found",
                                    Shining.prefix,
                                    element.playerName
                                )
                            }

                            openManageApplicationMenu(player)
                        }
                    }

                    ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                        ShiningDispatchers.launchDB {
                            if (refuseApplication(element)) {
                                player.sendPrefixedLangText(
                                    "menu-shining_guide-team-manage-application-refuse",
                                    Shining.prefix,
                                    element.playerName
                                )
                                element.player?.let {
                                    it.sendPrefixedLangText(
                                        "menu-shining_guide-team-manage-application-was_refused",
                                        Shining.prefix,
                                        name,
                                        player.name
                                    )
                                    it.playSound(it.location, Sound.ENTITY_VILLAGER_NO, 1f, 0.5f)
                                }
                            } else {
                                player.sendPrefixedLangText(
                                    "menu-shining_guide-team-manage-application-not_found",
                                    Shining.prefix,
                                    element.playerName
                                )
                            }

                            openManageApplicationMenu(player)
                        }
                    }

                    else -> {}
                }
            }
        }
    }


    @SkipTo(LifeCycle.ACTIVE)
    companion object : IntEntityClass<GuideTeam>(GuideTeams) {
        const val GUIDE_TEAM_ID = "shining_guide-team-id"
        const val GUIDE_TEAM_APPLY = "shining_guide-team-apply"

        private val createTeamItem =
            NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "shining_guide-team-create"))
        private val joinTeamItem =
            NamespacedIdItem(Material.ENDER_PEARL, NamespacedId(Shining, "shining_guide-team-join"))
        private val editTeamNameItem =
            NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-team-edit_name"))
        private val applicationManageItem =
            NamespacedIdItem(Material.BREAD, NamespacedId(Shining, "shining_guide-team-manage-application"))
        private val teamManageItem =
            NamespacedIdItem(Material.GOLDEN_APPLE, NamespacedId(Shining, "shining_guide-team-manage"))


        private suspend fun create(captain: Player, name: String, symbol: ItemStack): Boolean {
            val container = captain.getDataContainer()
            if (!container[GUIDE_TEAM_ID].isNullOrEmpty()) {
                return false
            }

            return newSuspendedTransaction transaction@{
                find { GuideTeams.captain eq captain.uniqueId }.let {
                    if (!it.empty()) {
                        return@transaction false
                    }
                }

                val guideTeam = new {
                    this.captain = captain.uniqueId
                    this.name = name
                    this.symbol = symbol
                    this.members = JacksonWrapper(hashSetOf())
                    this.applicants = JacksonWrapper(hashSetOf())
                    this.data = JacksonWrapper(GuideTeamData())
                }
                container[GUIDE_TEAM_ID] = guideTeam.id
                true
            }
        }


        /**
         * Check if the player is in a guide team.
         */
        suspend fun Player.hasGuideTeam(): Boolean {
            return newSuspendedTransaction transaction@{
                getDataContainer()[GUIDE_TEAM_ID]?.toInt()?.let { id ->
                    findById(id)?.let {
                        return@transaction true
                    }
                }
                false
            }
        }

        /**
         * Get the guide team the player is in.
         *
         * If the player is not in a guide team, it will return null.
         */
        suspend fun Player.getGuideTeam(): GuideTeam? {
            return newSuspendedTransaction transaction@{
                getDataContainer()[GUIDE_TEAM_ID]?.toInt()?.let { id ->
                    findById(id)?.let {
                        return@transaction it
                    }
                }

                null
            }
        }

        const val PLAYER_NOT_IN_TEAM = "menu-shining-guide-team-player_not_in_team"

        /**
         * The [block] will be run asynchronously by [ShiningDispatchers.DB] if the player is in a guide team.
         */
        fun Player.letGuideTeam(block: (team: GuideTeam) -> Unit) {
            ShiningDispatchers.launchDB {
                getGuideTeam()?.also(block)
            }
        }

        /**
         * The [block] will be run asynchronously by [ShiningDispatchers.DB] if the player is in a guide team, or it will send a warning message [PLAYER_NOT_IN_TEAM] to the player.
         */
        fun Player.letGuideTeamOrWarn(block: (team: GuideTeam) -> Unit) {
            ShiningDispatchers.launchDB {
                getGuideTeam()?.also(block) ?: sendPrefixedLangText(PLAYER_NOT_IN_TEAM)
            }
        }

        /**
         * Open a menu allowing the player to choose to create or join a guide team.
         */
        fun Player.setupGuideTeam() {
            val event = ShiningGuideTeamSetupEvent(this)
            event.call()
            if (event.isCancelled) return
            
            openMenu<Basic>(getLangText("menu-shining_guide-team-setup-title")) {
                rows(3)

                map(
                    "",
                    "ooaoooboo"
                )

                set('a', createTeamItem.toLocalizedItem(this@setupGuideTeam))
                set('b', joinTeamItem.toLocalizedItem(this@setupGuideTeam))

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
            openMenu<Basic>(getLangText("menu-shining_guide-team-create-title")) {
                rows(3)

                map(
                    "",
                    "obocoodoo"
                )

                set('b', editTeamNameItem.toLocalizedItem(this@createGuideTeam).let { langItem ->
                    langItem.getSectionString("create")?.let { text ->
                        langItem.clone().setLore("", text, "", "&e$name")
                    } ?: langItem
                })
                set('c', symbol.clone().setName(getLangText("menu-shining_guide-team-create-edit_symbol")))
                set('d', createTeamItem.toLocalizedItem(this@createGuideTeam).let { langItem ->
                    langItem.getSectionString("create")?.let { text ->
                        langItem.clone().setLore("", text, "", "&f$name")
                    } ?: langItem
                })


                onClick('b') {
                    sendMsg(Shining.prefix, getLangText("menu-shining_guide-team-create-input_name"))

                    PlayerChatSubscriber(this@createGuideTeam, getLangText("menu-shining_guide-team-create-edit_id")) {
                        submit {
                            createGuideTeam(message, symbol)
                        }
                        true
                    }.register()

                    closeInventory()
                }

                onClick('c') {
                    openSearchMenu<ItemStack>(getLangText("menu-shining_guide-team-create-search_symbol-title")) {
                        searchMap { Search.allItemMap }

                        onClick { event, item ->
                            createGuideTeam(name, item)
                        }

                        onGenerate { _, element, _, _ ->
                            element
                        }

                        onBack(this@createGuideTeam) {
                            createGuideTeam(name, symbol)
                        }
                    }
                }

                onClick('d') {
                    if (name.isNotBlank()) {
                        ShiningDispatchers.launchDB {
                            if (create(this@createGuideTeam, name, symbol)) {
                                sendPrefixedLangText("menu-shining_guide-team-create-success", Shining.prefix, name)
                                submit {
                                    closeInventory()
                                    playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                                }
                            } else {
                                sendPrefixedLangText("menu-shining_guide-team-create-fail-id_already_exists")
                                playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                            }
                        }
                    } else {
                        sendPrefixedLangText("menu-shining_guide-team-create-fail-id_or_name_empty")
                        playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
                    }
                }

                onClick(lock = true)
            }
        }

        private fun Player.joinGuideTeam() {
            ShiningDispatchers.launchDB {
                val applyTeam = getDataContainer()[GUIDE_TEAM_APPLY]
                val teams = newSuspendedTransaction {
                    val allTeam = all().limit(100)
                    if (applyTeam == null) {
                        allTeam.toList()
                    } else {
                        val applyTeamId = applyTeam.toInt()
                        val list = LinkedList<GuideTeam>()
                        for (theTeam in allTeam) {
                            if (theTeam.id.value == applyTeamId) {
                                list.addFirst(theTeam)
                            } else {
                                list += theTeam
                            }
                        }
                        list
                    }
                }

                submit {
                    openMultiPageMenu<GuideTeam>(getLangText("menu-shining_guide-team-join-title")) {
                        elements { teams }

                        var applyTeamElement: GuideTeam? = null
                        onGenerate(async = true) { _, element, index, slot ->
                            if (index == 0 && applyTeam != null && element.id.value == applyTeam.toInt()) {
                                applyTeamElement = element
                                return@onGenerate buildItem(element.symbol) {
                                    this.name = "§f${element.name}"
                                    getLangListNode("menu-shining_guide-team-join-already_apply")
                                        ?.format(element.captain.offlinePlayer.name)
                                        ?.let { lore += it.colored() }
                                    element.members.value.forEach {
                                        lore += "§f${it.offlinePlayer.name}"
                                    }
                                    shiny()
                                }
                            }

                            buildItem(element.symbol) {
                                this.name = "§f${element.name}"
                                getLangListNode("menu-shining_guide-team-join-team_symbol")
                                    ?.format(element.captain.offlinePlayer.name)
                                    ?.let { lore += it.colored() }
                                element.members.value.forEach {
                                    lore += "§f${it.offlinePlayer.name}"
                                }
                            }
                        }

                        onBack(this@joinGuideTeam) {
                            setupGuideTeam()
                        }

                        onClick { event, element ->
                            if (applyTeam == null) {
                                ShiningDispatchers.launchDB {
                                    element.apply(this@joinGuideTeam)
                                    sendPrefixedLangText(
                                        "menu-shining_guide-team-join-apply-success",
                                        Shining.prefix,
                                        element.name,
                                        element.captain.playerName
                                    )
                                }
                                closeInventory()
                            } else {
                                openMenu<Basic>(getLangText("menu-shining_guide-team-join-reapply-title")) {
                                    rows(1)

                                    map("oooaobooo")

                                    set('a', buildItem(ShiningIcon.CONFIRM) {
                                        val theApplyTeamElement = applyTeamElement
                                        lore += getLangText("menu-shining_guide-team-join-reapply-existing_team").colored()
                                        lore += if (theApplyTeamElement != null) "§e\"§f${theApplyTeamElement.name}§e\"" else "§ewith ID \"§f$applyTeam§e\""
                                        getLangListNode("menu-shining_guide-team-join-reapply-cancel_and_apply")
                                            ?.format(element.name)
                                            ?.let { lore += it.colored() }
                                    }) {
                                        ShiningDispatchers.launchDB {
                                            element.apply(this@joinGuideTeam)
                                            sendPrefixedLangText(
                                                "menu-shining_guide-team-join-apply-success",
                                                Shining.prefix,
                                                element.name,
                                                element.captain.playerName
                                            )
                                        }
                                        closeInventory()
                                    }

                                    set('b', ShiningIcon.CANCEL.item) {
                                        joinGuideTeam()
                                    }

                                    onClick(lock = true)
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    object CompletedTeam : GuideTeam(EntityID(-1, GuideTeams))

}