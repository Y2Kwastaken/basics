package com.github.spigotbasics.core.command.parsed

import com.github.spigotbasics.core.Basics
import com.github.spigotbasics.core.messages.Message

abstract class CommandArgument<T>(open val name: String? = null) {
    abstract fun parse(value: String): T?

    open fun tabComplete(typing: String): List<String> = emptyList()

    // TODO: This is using the static Singleton :/
    open fun errorMessage(value: String? = null): Message = Basics.messages.invalidValueForArgument(getArgumentName(), value ?: "null")

    fun getArgumentName(): String = name ?: this::class.simpleName ?: "unknown"
}
