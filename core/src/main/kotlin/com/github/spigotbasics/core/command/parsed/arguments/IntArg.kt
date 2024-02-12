package com.github.spigotbasics.core.command.parsed.arguments

import com.github.spigotbasics.core.command.parsed.CommandArgument

class IntArg(name: String) : CommandArgument<Int>(name) {
    override fun parse(value: String): Int? = value.toIntOrNull()
}
