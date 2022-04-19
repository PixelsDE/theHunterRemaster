/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 19.04.22, 11:37 by Carina The Latest changes made by Carina on 19.04.22, 11:37 All contents of "StatsPlayer.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.commands

import de.carina.thehunter.TheHunter
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StatsPlayer {

    fun stats(sender: CommandSender, command: String, args: Array<out String>) {
        if (!CommandUtil.checkCommandBasics(sender, command, args, "stats", 0, "theHunter.stats"))
            return

        var player: Player
        player = if (args.isEmpty())
            sender as Player
        else
            Bukkit.getPlayer(args[0])!!
        if (player == null)
            player = sender as Player
        TheHunter.instance.statsSystem.generateStatsMessageForPlayer(sender as Player, player)

    }

}