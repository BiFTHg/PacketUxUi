package net.craftoriya.packetuxui.types

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.menu.Menu
import org.bukkit.entity.Player

data class ExecuteComponent(
    val player: Player,
    val buttonType: ButtonType,
    val slot: Int,
    val menu: Menu,
    val itemStack: ItemStack?
)
