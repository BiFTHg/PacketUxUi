package net.craftoriya.packetuxui.button

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.dto.CooldownComponent

data class Button(
    val item: ItemStack,
    var execute: ((ExecuteComponent) -> Unit)? = null,
    val cooldown: CooldownComponent
)