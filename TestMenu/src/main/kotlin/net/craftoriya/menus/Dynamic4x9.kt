package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.craftoriya.common.asyncRepeat
import net.craftoriya.common.toComponent
import net.craftoriya.packetuxui.PacketUxUiAPI
import net.craftoriya.packetuxui.button.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.menu.Menu
import net.craftoriya.packetuxui.menu.SimpleMenu
import net.craftoriya.packetuxui.service.MenuService
import net.craftoriya.packetuxui.types.InventoryType
import org.bukkit.Bukkit
import kotlin.random.Random

class Dynamic4x9 {
    private val stone: ItemStack = ItemStack.builder().type(ItemTypes.STONE).build()
    private val air: ItemStack = ItemStack.builder().type(ItemTypes.AIR).build()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val name = "<gradient:#ff1493:#1e90ff><bold>Styled Background".toComponent()

    init {
        scope.asyncRepeat(200) {
            for (player in Bukkit.getOnlinePlayers()) {
                val playerMenu = PacketUxUiAPI.getService().getMenu(player) ?: continue
                if (playerMenu.name != name) continue

                for (i in 0 until 27) {
                    if (chance(10)) {
                        val item = if (chance(50)) playerMenu.buttons[i]?.item ?: air else air
                        PacketUxUiAPI.getService().updateItem(player, item, i)
                    }
                }
            }
        }
    }

    fun getMenu(): Menu {
        return SimpleMenu(
            name = name,
            type = InventoryType.GENERIC9X3,
            staticButtons = (0 until 27).associateWith { slot ->
                ButtonBuilder()
                    .item(
                        ItemBuilder()
                            .itemType(if (slot % 2 == 0) ItemTypes.BLUE_STAINED_GLASS_PANE else ItemTypes.PINK_STAINED_GLASS_PANE)
                            .name("<dark_gray><italic>Background Tile".toComponent())
                            .build()
                    )
                    .click {
                        PacketUxUiAPI.getService().updateItem(it.player, stone, slot)
                    }
                    .build()
            }
        )
    }

    private fun chance(percent: Int): Boolean {
        require(percent in 0..100) { "Percentage must be between 0 and 100" }
        return Random.nextFloat() * 100 < percent
    }
}

