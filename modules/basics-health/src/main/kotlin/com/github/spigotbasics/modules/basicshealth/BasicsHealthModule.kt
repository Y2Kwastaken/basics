package com.github.spigotbasics.modules.basicshealth

import com.github.spigotbasics.core.command.BasicsCommandContext
import com.github.spigotbasics.core.command.BasicsCommandExecutor
import com.github.spigotbasics.core.command.CommandResult
import com.github.spigotbasics.core.module.AbstractBasicsModule
import com.github.spigotbasics.core.module.loader.ModuleInstantiationContext
import org.bukkit.entity.Player

class BasicsHealthModule(context: ModuleInstantiationContext) : AbstractBasicsModule(context) {
    val permHeal =
        permissionManager.createSimplePermission(
            "basics.heal",
            "Allows the player to heal themself",
        )
    val permHealOthers =
        permissionManager.createSimplePermission(
            "basics.heal.others",
            "Allows to heal other players",
        )
    val permFeed =
        permissionManager.createSimplePermission(
            "basics.feed",
            "Allows the player to feed themself",
        )
    val permFeedOthers =
        permissionManager.createSimplePermission(
            "basics.feed.others",
            "Allows to feed other players",
        )
    private val msgHealed
        get() = messages.getMessage("healed")
    private val msgFed
        get() = messages.getMessage("fed")

    private fun msgHealedOthers(player: Player) = messages.getMessage("healed-others").concerns(player)

    private fun msgFedOthers(player: Player) = messages.getMessage("fed-others").concerns(player)

    override fun onEnable() {
        createCommand("heal", permHeal)
            .description("Heals Players")
            .usage("[player]")
            .executor(HealCommandExecutor(this))
            .register();
        createCommand("feed", permFeed)
            .description("Feeds Players")
            .usage("[player]")
            .executor(FeedCommandExecutor(this))
            .register();
    }

    inner class HealCommandExecutor(private val module: BasicsHealthModule) : BasicsCommandExecutor(module) {
        override fun execute(context: BasicsCommandContext): CommandResult {
            val player =
                if (context.args.size == 1) {
                    requirePermission(context.sender, module.permHealOthers)
                    requirePlayer(context.args[0])
                } else {
                    requirePlayer(context.sender)
                }

            player.health = player.healthScale

            val message =
                if (context.sender == player){
                    module.msgHealed
                } else {
                    module.msgHealedOthers(player)
                }
            message.sendToSender(context.sender)
            return CommandResult.SUCCESS
        }
    }

    inner class FeedCommandExecutor(private val module: BasicsHealthModule) : BasicsCommandExecutor(module) {
        override fun execute(context: BasicsCommandContext): CommandResult {
            val player =
                if (context.args.size == 1) {
                    requirePermission(context.sender, module.permFeedOthers)
                    requirePlayer(context.args[0])
                } else {
                    requirePlayer(context.sender)
                }

            player.foodLevel = 20

            val message =
                if (context.sender == player){
                    module.msgFed
                } else {
                    module.msgFedOthers(player)
                }
            message.sendToSender(context.sender)
            return CommandResult.SUCCESS
        }
    }
}
