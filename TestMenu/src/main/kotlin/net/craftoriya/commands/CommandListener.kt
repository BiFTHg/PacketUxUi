package net.craftoriya.commands

import net.craftoriya.menus.*
import net.craftoriya.packetuxui.service.MenuService
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper
import org.incendo.cloud.paper.util.sender.PlayerSource
import org.incendo.cloud.paper.util.sender.Source


class CommandListener(
    private val plugin: JavaPlugin,
    private val service: MenuService,
) {
    private val static3x9 = Static3x9()
    private val dynamic4x9 = Dynamic4x9()
    private val paginated = PaginatedTest()
    private val tabbed = TabbedTest()

    init {
        val coordinator = ExecutionCoordinator.simpleCoordinator<Source>()
        val commandManager: PaperCommandManager<Source> = PaperCommandManager
            .builder(PaperSimpleSenderMapper.simpleSenderMapper())
            .executionCoordinator(coordinator)
            .buildOnEnable(plugin)

        val openMenuBuilder = commandManager.commandBuilder("open_menu")
            .senderType(PlayerSource::class.java)

        val subcommands = listOf(
            "static_3x9",
            "dynamic_4x9",
            "paginated",
            "tabbed"
        )

        for (command in subcommands) {
            commandManager.command(
                openMenuBuilder.literal(command)
                    .handler { context ->
                        // Створюємо нове меню САМЕ В МОМЕНТ виконання команди гравцем
                        val menu = when(command) {
                            "static_3x9" -> static3x9.getMenu()
                            "paginated" -> paginated.getMenu()
                            "tabbed" -> tabbed.getMenu()
                            else -> dynamic4x9.getMenu()
                        }
                        service.openMenu(context.sender().source(), menu)
                    }
            )
        }
    }
}