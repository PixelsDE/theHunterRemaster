/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 19.04.22, 00:18 by Carina The Latest changes made by Carina on 19.04.22, 00:18 All contents of "PlayerChat.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.events.game

import de.carina.thehunter.TheHunter
import de.carina.thehunter.util.game.GamesHandler
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerChat : Listener {

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        if (!(GamesHandler.playerInGames.containsKey(event.player) || GamesHandler.spectatorInGames.containsKey(event.player)))
            return

        event.isCancelled = true
        if (LegacyComponentSerializer.builder().build().serialize(event.message()).startsWith("@Team")) {
            var message = TheHunter.PREFIX + LegacyComponentSerializer.builder().build().serialize(event.message()).replace("@Team", "")
            val team = GamesHandler.playerInGames[event.player]!!.teams.first { it.teamMembers.contains(event.player) }
            if (team != null) {
                team.teamMembers.forEach {
                    it.sendMessage(message)
                }
                return
            }
        }
        if (GamesHandler.playerInGames.containsKey(event.player)) {
            var message = TheHunter.PREFIX + LegacyComponentSerializer.builder().build().serialize(event.message()).replace("@Team", "")
            GamesHandler.playerInGames[event.player]!!.players.forEach {
                it.sendMessage(message)
            }
            GamesHandler.playerInGames[event.player]!!.spectators.forEach {
                it.sendMessage(message)
            }
            return
        }
        var message = TheHunter.PREFIX + "§7[Dead] " + LegacyComponentSerializer.builder().build().serialize(event.message())
        GamesHandler.spectatorInGames[event.player]!!.spectators.forEach {
            it.sendMessage(message)
        }


    }
}