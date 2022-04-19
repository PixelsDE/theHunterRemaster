/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 19.04.22, 13:09 by Carina The Latest changes made by Carina on 19.04.22, 13:09 All contents of "CreateGame.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.commands

import de.carina.thehunter.TheHunter
import de.carina.thehunter.util.game.Game
import de.carina.thehunter.util.game.GamesHandler
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateGame {
    fun create(sender: CommandSender, command: String, args: Array<out String>) {
        if (!CommandUtil.checkCommandBasics(sender, command, args, "setup", 2, "theHunter.setup"))
            return

        when (args[0].lowercase()) {
            "create" -> {
                if (GamesHandler.games.find { it.name != args[1] } != null) {
                    sender.sendMessage(TheHunter.instance.messages.messagesMap["game-already-exists"]!!.replace("%game%", args[0]))
                    return
                }
                val game = Game(args[1])
                GamesHandler.setupGames.add(game)
                sender.sendMessage(TheHunter.instance.messages.messagesMap["game-successfully-created"]!!.replace("%game%", args[0]))
            }
            "setup" -> {
                if (args.size != 3) {
                    sender.sendMessage(TheHunter.instance.messages.messagesMap["not-enough-arguments"]!!)
                    return
                }
                val game = GamesHandler.setupGames.find { it.name == args[2] }
                if (game == null) {
                    sender.sendMessage(TheHunter.instance.messages.messagesMap["game-not-exists"]!!)
                    return
                }
                when (args[1].lowercase()) {
                    "lobbyspawn" -> {
                        game.lobbyLocation = (sender as Player).location
                        sender.sendMessage(TheHunter.instance.messages.messagesMap["game-lobby-set"]!!.replace("%game%", args[2]))
                    }
                    "spectatorspawn" -> {
                        game.spectatorLocation = (sender as Player).location
                        sender.sendMessage(TheHunter.instance.messages.messagesMap["game-spectator-set"]!!.replace("%game%", args[2]))
                    }
                    "backspawn" -> {
                        game.backLocation = (sender as Player).location
                        sender.sendMessage(TheHunter.instance.messages.messagesMap["game-back-set"]!!.replace("%game%", args[2]))
                    }
                    "endspawn" -> {
                        game.endLocation = (sender as Player).location
                        sender.sendMessage(TheHunter.instance.messages.messagesMap["game-end-set"]!!.replace("%game%", args[2]))
                    }
                    "finish" -> {
                        if (!game.isGameValidConfigured()) {
                            sender.sendMessage(TheHunter.instance.messages.messagesMap["wrong-config"]!!.replace("%game%", args[2]))
                            return
                        }
                        GamesHandler.games.add(game)
                        GamesHandler.setupGames.remove(game)
                        sender.sendMessage(TheHunter.instance.messages.messagesMap["game-successfully-created"]!!.replace("%game%", args[2]))
                        game.saveGameToConfig()
                        sender.sendMessage(TheHunter.instance.messages.messagesMap["game-successfully-saved"]!!.replace("%game%", args[2]))
                        game.create()
                    }
                }

            }
        }
    }
}