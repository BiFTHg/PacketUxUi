package net.craftoriya.packetuxui.menu

import net.craftoriya.packetuxui.button.Button
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component

class SimpleMenu(
    name: Component,
    type: InventoryType,
    private val staticButtons: Map<Int, Button>,
    cooldown: CooldownComponent = CooldownComponent()
) : Menu(name, type, cooldown) {

    override fun render() {
        buttons.clear()
        buttons.putAll(staticButtons)
    }
}