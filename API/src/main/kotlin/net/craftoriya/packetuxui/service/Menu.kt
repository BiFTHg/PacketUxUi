package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.kyori.adventure.text.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

open class Menu(
    val name: Component,
    val type: net.craftoriya.packetuxui.types.InventoryType,
    buttons: Map<Int, Button>,
    val cooldown: CooldownComponent = CooldownComponent()
) {
    val buttons: ConcurrentMap<Int, Button> = ConcurrentHashMap(buttons)

    @Volatile var contentPacket: WrapperPlayServerWindowItems? = null
    @Volatile var menuPacket: WrapperPlayServerOpenWindow? = null

    fun copy(): Menu {
        val menu = Menu(name, type, buttons, cooldown)
        menu.menuPacket = this.menuPacket
        menu.contentPacket = this.contentPacket
        return menu
    }

    init {
        if (buttons.size > type.size) {
            throw IllegalArgumentException("Too many items in menu")
        }
    }
}

