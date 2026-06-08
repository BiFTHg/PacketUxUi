package net.craftoriya.packetuxui.menu

import net.craftoriya.packetuxui.PacketUxUiAPI
import net.craftoriya.packetuxui.button.Button
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component
import kotlin.collections.iterator

class PaginatedMenu(
    name: Component,
    type: InventoryType,
    private val staticButtons: Map<Int, Button>,
    private val paginationButtons: List<Button>,
    private val paginationSlots: List<Int>,
    private val nextPageButtons: Map<Int, Button>,
    private val prevPageButtons: Map<Int, Button>,
    cooldown: CooldownComponent = CooldownComponent()
) : Menu(name, type, cooldown) {

    private var currentPage = 0
    private val pageSize = paginationSlots.size

    override fun render() {
        buttons.clear()
        buttons.putAll(staticButtons)

        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(paginationButtons.size)
        paginationButtons.subList(start, end).forEachIndexed { i, btn ->
            buttons[paginationSlots[i]] = btn
        }

        if (currentPage > 0) {
            for ((slot, button) in prevPageButtons) {
                val orig = button.execute
                buttons[slot] = button.copy(execute = { ec ->
                    orig?.invoke(ec)
                    currentPage--
                    // tell the service to re-render and push
                    PacketUxUiAPI.getService().redraw(ec.player)
                })
            }
        }
        if (end < paginationButtons.size) {
            for ((slot, button) in nextPageButtons) {
                val orig = button.execute
                buttons[slot] = button.copy(execute = { ec ->
                    orig?.invoke(ec)
                    currentPage++
                    PacketUxUiAPI.getService().redraw(ec.player)
                })
            }
        }
    }
}