package net.craftoriya.packetuxui.menu

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.button.Button
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

abstract class Menu(
    var name: Component,
    val type: InventoryType,
    val cooldown: CooldownComponent = CooldownComponent()
) {
    val buttons: ConcurrentMap<Int, Button> = ConcurrentHashMap()
    val uniqueId: UUID = UUID.randomUUID()

    @Volatile var contentPacket: WrapperPlayServerWindowItems? = null
    @Volatile var menuPacket: WrapperPlayServerOpenWindow? = null

    /**
     * Called by MenuService to (re)build the button map before
     * sending packets. Subclasses override this to push their
     * current page/tab state into [buttons].
     */
    open fun render() {}
}

