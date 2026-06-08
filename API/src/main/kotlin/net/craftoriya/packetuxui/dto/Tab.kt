package net.craftoriya.packetuxui.dto

import net.craftoriya.packetuxui.button.Button

data class Tab(
    val tabButton: Button,
    val buttons: List<Button>,
    val tabIndividualButtons: Map<Int, Button> = mapOf()
)