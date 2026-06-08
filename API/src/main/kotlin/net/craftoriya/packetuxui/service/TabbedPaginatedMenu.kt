package net.craftoriya.packetuxui.service

import net.craftoriya.packetuxui.PacketUxUiAPI
import net.craftoriya.packetuxui.dto.Tab
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class TabbedPaginatedMenu(
    private val name: Component,
    private val type: InventoryType,
    private val tabs: Map<Int, Tab>,
    private val paginationSlots: List<Int>,
    private val nextPageButtons: Map<Int, Button>,
    private val prevPageButtons: Map<Int, Button>,
    private val buttons: Map<Int, Button>
) {
    private var activeTabSlot = tabs.keys.firstOrNull() ?: throw IllegalArgumentException("Tabs map cannot be empty.")
    private val pageStates = mutableMapOf<Int, Int>().withDefault { 0 }
    private val pageSize = paginationSlots.size

    fun open(player: Player) {
        PacketUxUiAPI.getService().openMenu(player, buildMenuForState(player))
    }

    private fun draw(player: Player) {
        PacketUxUiAPI.getService().updateButtons(player, buildMenuForState(player).buttons)
    }

    private fun buildMenuForState(player: Player): Menu {
        val allButtons = mutableMapOf<Int, Button>()
        val activeTab = tabs[activeTabSlot] ?: return Menu(name, type, allButtons)
        val currentPage = pageStates.getValue(activeTabSlot)

        // 1. Add Custom Buttons
        allButtons.putAll(buttons)

        // 2. Add Tab Buttons
        for ((slot, tab) in tabs) {
            val isActive = slot == activeTabSlot

            if (isActive) {
                for ((individualSlot, button) in tab.tabIndividualButtons) {
                    allButtons.putIfAbsent(individualSlot, button)
                }
            }

            val execute = tab.tabButton.execute
            allButtons[slot] = tab.tabButton.copy(execute = { ec ->
                execute?.invoke(ec)
                activeTabSlot = slot
                draw(player)
            })
        }

        // 3. Add Paginated Items for the Active Tab
        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(activeTab.buttons.size)
        val pageItems = activeTab.buttons.subList(start, end)

        pageItems.forEachIndexed { index, button ->
            allButtons[paginationSlots[index]] = button
        }

        // 4. Add Pagination Buttons
        if (currentPage > 0) {
            for ((slot, button) in prevPageButtons) {
                val execute = button.execute
                allButtons[slot] = button.copy(execute = { ec ->
                    execute?.invoke(ec)
                    pageStates[activeTabSlot] = currentPage - 1
                    draw(player)
                })
            }
        }
        if (end < activeTab.buttons.size) {
            for ((slot, button) in nextPageButtons) {
                val execute = button.execute
                allButtons[slot] = button.copy(execute = { ec ->
                    execute?.invoke(ec)
                    pageStates[activeTabSlot] = currentPage + 1
                    draw(player)
                })
            }
        }

        return Menu(name, type, allButtons)
    }
}