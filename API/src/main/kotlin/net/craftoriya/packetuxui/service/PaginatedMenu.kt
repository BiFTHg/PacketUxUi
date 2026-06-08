package net.craftoriya.packetuxui.service

import net.craftoriya.packetuxui.PacketUxUiAPI
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PaginatedMenu(
    private val name: Component,
    private val type: InventoryType,
    private val buttons: Map<Int, Button>,
    private val paginationButtons: List<Button>,
    private val paginationSlots: List<Int>,
    private val nextPageButtons: Map<Int, Button>,
    private val prevPageButtons: Map<Int, Button>,
) {
    private var currentPage = 0
    private val pageSize = paginationSlots.size

    fun open(player: Player) {
        val menuForPage = getMenuForPage(currentPage, player)
        PacketUxUiAPI.getService().openMenu(player, menuForPage)
    }

    private fun getMenuForPage(page: Int, player: Player): Menu {
        val start = page * pageSize
        val end = (start + pageSize).coerceAtMost(paginationButtons.size)
        val pageItems = paginationButtons.subList(start, end)

        val buttons = mutableMapOf<Int, Button>()

        // 1. Add custom static buttons
        buttons.putAll(this@PaginatedMenu.buttons)

        // 2. Add paginated items
        pageItems.forEachIndexed { index, button ->
            val slot = paginationSlots[index]
            buttons[slot] = button
        }

        // 3. Add Previous Page Buttons
        if (page > 0) {
            for ((slot, button) in prevPageButtons) {
                val execute = button.execute
                buttons[slot] = button.copy(execute = { ec ->
                    execute?.invoke(ec)
                    currentPage--
                    PacketUxUiAPI.getService().updateButtons(player, getMenuForPage(currentPage, player).buttons)
                })
            }
        }

        // 4. Add Next Page Buttons
        if (end < this.paginationButtons.size) {
            for ((slot, button) in nextPageButtons) {
                val execute = button.execute
                buttons[slot] = button.copy(execute = { ec ->
                    execute?.invoke(ec)
                    currentPage++
                    PacketUxUiAPI.getService().updateButtons(player, getMenuForPage(currentPage, player).buttons)
                })
            }
        }

        return Menu(
            name = name,
            type = type,
            buttons = buttons
        )
    }
}