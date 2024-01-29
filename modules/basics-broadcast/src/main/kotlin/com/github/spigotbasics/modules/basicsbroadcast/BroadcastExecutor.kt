package com.github.spigotbasics.modules.basicsbroadcast

import com.github.spigotbasics.core.command.BasicsCommandContext
import com.github.spigotbasics.core.command.BasicsCommandExecutor
import com.github.spigotbasics.core.extensions.debug
import com.github.spigotbasics.core.module.BasicsModule
import org.bukkit.util.StringUtil

class BroadcastExecutor(private val module: BasicsBroadcastModule) : BasicsCommandExecutor(module) {
    override fun execute(context: BasicsCommandContext): Boolean {

        debug("Executing broadcast command")
        context.readFlags()

        val parseMini = context.popFlag("--parsed")
        debug("parseMini: $parseMini")
        failIfFlagsLeft(context)
        debug("Flags: ${context.flags}")

        val args = context.args

        val text = args.joinToString(" ")
        var message = messageFactory.createPlainMessage(text)

        if(parseMini) {
            debug("Parsing message w/ mini")
            requirePermission(context.sender, module.parsedPerm)
            debug("permission check passed")
            message = messageFactory.createMessage(text)
        }

        message.sendToAllPlayers()
        message.sendToConsole()
        return true
    }

    override fun tabComplete(context: BasicsCommandContext): MutableList<String> {
        val options = mutableListOf<String>()
        if(context.sender.hasPermission(module.parsedPerm)) {
            options += "--parsed"
        }

        return if (context.args.size == 1) {
            StringUtil.copyPartialMatches(
                context.args[0],
                options,
                mutableListOf())
        } else {
            mutableListOf()
        }
    }
}