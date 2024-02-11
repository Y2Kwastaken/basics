package com.github.spigotbasics.core.command

import com.github.spigotbasics.core.command.parsed2.ArgumentPath
import com.github.spigotbasics.core.command.parsed2.Command
import com.github.spigotbasics.core.command.parsed2.CommandContext
import com.github.spigotbasics.core.command.parsed2.CommandExecutor
import com.github.spigotbasics.core.messages.Message
import com.github.spigotbasics.core.module.BasicsModule
import org.bukkit.permissions.Permission

class ParsedCommandBuilder<T : CommandContext>(
    private val module: BasicsModule,
    private val name: String,
    private val permission: Permission,
) {
    private var permissionMessage: Message = module.plugin.messages.noPermission
    private var description: String? = null
    private var usage: String = ""
    private var aliases: List<String> = emptyList()
    private var executor: BasicsCommandExecutor? = null
    private var tabCompleter: BasicsTabCompleter? = null
    private var parsedExecutor: CommandExecutor<T>? = null
    private var argumentPaths: List<ArgumentPath<T>>? = null

    fun description(description: String) = apply { this.description = description }

    fun usage(usage: String) =
        apply {
            if (usage.startsWith("/")) error("Usage should not start with /<command> - only pass the arguments.")
            this.usage = usage
        }

    fun paths(argumentPaths: List<ArgumentPath<T>>) = apply { this.argumentPaths = argumentPaths }

    fun executor(executor: CommandExecutor<T>) = apply { this.parsedExecutor = executor }

    private fun executor(executor: BasicsCommandExecutor) = apply { this.executor = executor }

    private fun executor(command: Command<T>) =
        apply {
            this.executor =
                object : BasicsCommandExecutor(module) {
                    override fun execute(context: BasicsCommandContext): CommandResult? {
                        command.execute(context.args)
                        return CommandResult.SUCCESS
                    }
                }
        }

    fun register(): BasicsCommand {
        val command = build()
        module.commandManager.registerCommand(command)
        return command
    }

    private fun build(): BasicsCommand {
        val command = Command(parsedExecutor ?: error("parsedExecutor must be set"), argumentPaths ?: error("Argument paths must be set"))
        executor(command)
        val info =
            CommandInfo(
                name = name,
                permission = permission,
                permissionMessage = permissionMessage,
                description = description,
                usage = usage,
                aliases = aliases,
            )
        return BasicsCommand(
            info = info,
            executor = executor ?: error("Executor must be set"),
            tabCompleter = tabCompleter ?: executor,
            coreMessages = module.plugin.messages,
            messageFactory = module.plugin.messageFactory,
        )
    }
}
