package net.craftoriya.packetuxui.menu

import net.craftoriya.packetuxui.PacketUxUiAPI
import net.craftoriya.packetuxui.dto.Tab
import net.craftoriya.packetuxui.button.Button
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import kotlin.collections.iterator

class TabbedPaginatedMenu(
    name: Component,
    type: InventoryType,
    private val tabs: Map<Int, Tab>,
    private val paginationSlots: List<Int>,
    private val nextPageButtons: Map<Int, Button>,
    private val prevPageButtons: Map<Int, Button>,
    private val staticButtons: Map<Int, Button>,
    cooldown: CooldownComponent = CooldownComponent()
) : Menu(name, type, cooldown) {

    private var activeTabSlot = tabs.keys.firstOrNull()
        ?: throw IllegalArgumentException("Tabs map cannot be empty.")
    private val pageStates = mutableMapOf<Int, Int>().withDefault { 0 }
    private val pageSize = paginationSlots.size

    override fun render() {
        buttons.clear()
        buttons.putAll(staticButtons)

        val activeTab = tabs[activeTabSlot] ?: return
        val currentPage = pageStates.getValue(activeTabSlot)

        for ((slot, tab) in tabs) {
            val isActive = slot == activeTabSlot
            if (isActive) {
                tab.tabIndividualButtons.forEach { (s, b) -> buttons.putIfAbsent(s, b) }
            }
            val orig = tab.tabButton.execute
            buttons[slot] = tab.tabButton.copy(execute = { ec ->
                orig?.invoke(ec)
                activeTabSlot = slot
                PacketUxUiAPI.getService().redraw(ec.player)
            })
        }

        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(activeTab.buttons.size)
        activeTab.buttons.subList(start, end).forEachIndexed { i, btn ->
            buttons[paginationSlots[i]] = btn
        }

        if (currentPage > 0) {
            for ((slot, button) in prevPageButtons) {
                val orig = button.execute
                buttons[slot] = button.copy(execute = { ec ->
                    orig?.invoke(ec)
                    pageStates[activeTabSlot] = currentPage - 1
                    PacketUxUiAPI.getService().redraw(ec.player)
                })
            }
        }
        if (end < activeTab.buttons.size) {
            for ((slot, button) in nextPageButtons) {
                val orig = button.execute
                buttons[slot] = button.copy(execute = { ec ->
                    orig?.invoke(ec)
                    pageStates[activeTabSlot] = currentPage + 1
                    PacketUxUiAPI.getService().redraw(ec.player)
                })
            }
        }
    }
}