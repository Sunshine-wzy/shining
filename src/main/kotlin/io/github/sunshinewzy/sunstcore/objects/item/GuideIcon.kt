package io.github.sunshinewzy.sunstcore.objects.item

import io.github.sunshinewzy.sunstcore.interfaces.Itemable
import io.github.sunshinewzy.sunstcore.objects.SItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class GuideIcon(val item: ItemStack) : Itemable {
    BACK(SItem(Material.ENCHANTED_BOOK, "&c⇦ 返回", "", "&f左键：&7返回上一页", "&fShift + 左键：&7返回主菜单")),
    SUBMIT(SItem(Material.SLIME_BALL, "&a>> 点击以提交任务 <<")),
    WORKBENCH(SItem(Material.CRAFTING_TABLE, "&a>> &d使用工作台合成 &a<<")),
    PAGE_NEXT(SItem(Material.ENCHANTED_BOOK, "&a下一页 ⇩")),
    PAGE_PRE(SItem(Material.ENCHANTED_BOOK, "&a上一页 ⇧")),
    HOME(SItem(Material.COMPASS, "&e⇨ &a返回主界面 &e⇦")),
    EDGE(SItem(Material.GRAY_STAINED_GLASS_PANE, " ")),
    PAGE_NEXT_GLASS_PANE(SItem(Material.LIME_STAINED_GLASS_PANE, "&a下一页 &e⇨")),
    PAGE_PRE_GLASS_PANE(SItem(Material.LIME_STAINED_GLASS_PANE, "&e⇦ &a上一页"))
    
    ;


    override fun getSItem(): ItemStack = item
    
}