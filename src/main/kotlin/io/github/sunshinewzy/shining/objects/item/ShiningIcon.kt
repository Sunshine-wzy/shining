package io.github.sunshinewzy.shining.objects.item

import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.objects.SItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

enum class ShiningIcon(val item: ItemStack) : Itemable {
    BACK(SItem(Material.ENCHANTED_BOOK, "&c⇦ 返回", "", "&f左键：&7返回上一页", "&fShift + 左键：&7返回主菜单")),
    BACK_LAST_PAGE(SItem(Material.ENCHANTED_BOOK, "&b⇦ 返回")),
    SUBMIT(SItem(Material.SLIME_BALL, "&a>> 点击以提交任务 <<")),
    WORKBENCH(SItem(Material.CRAFTING_TABLE, "&a>> &d使用工作台合成 &a<<")),
    PAGE_NEXT(SItem(Material.ENCHANTED_BOOK, "&a下一页 ⇩")),
    PAGE_PRE(SItem(Material.ENCHANTED_BOOK, "&a上一页 ⇧")),
    HOME(SItem(Material.COMPASS, "&e⇨ &a返回主界面 &e⇦")),
    EDGE(SItem(Material.GRAY_STAINED_GLASS_PANE, " ")),
    EDGE_GLASS_PANE(SItem(Material.GLASS_PANE, " ")),
    PAGE_NEXT_GLASS_PANE(SItem(Material.LIME_STAINED_GLASS_PANE, "&a下一页 &e⇨")),
    PAGE_PRE_GLASS_PANE(SItem(Material.LIME_STAINED_GLASS_PANE, "&e⇦ &a上一页")),
    RENAME(SItem(Material.NAME_TAG, "&e重命名")),
    EDIT_LORE(SItem(Material.EMERALD, "&a编辑Lore")),
    REMOVE_MODE(SItem(Material.BARRIER, "&c删除模式", "&7已关闭", "", "&a> 点击开启")),
    REMOVE_MODE_SHINY(buildItem(SItem(Material.BARRIER, "&c删除模式", "&7已开启", "", "&a> 点击关闭")) { shiny() }),
    ADD_MODE(SItem(Material.SLIME_BALL, "&e增添模式", "&7已关闭", "", "&a> 点击开启")),
    ADD_MODE_SHINY(buildItem(SItem(Material.SLIME_BALL, "&e增添模式", "&7已开启", "", "&a> 点击关闭")) { shiny() }),
    SEARCH(SItem(Material.COMPASS, "&a搜索")),
    CONFIRM(SItem(Material.SLIME_BALL, "&a确认")),
    CANCEL(SItem(Material.BARRIER, "&c取消")),
    SETTINGS(SItem(Material.ENDER_EYE, "&e设置"))
    
    ;


    override fun getItemStack(): ItemStack = item
    
}