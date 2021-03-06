/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 6/6/22, 10:54 PM by Carina The Latest changes made by Carina on 6/6/22, 10:54 PM All contents of "Team.kt" are protected by copyright.
 * The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */
package de.carina.thehunter.util.game

import de.carina.thehunter.TheHunter
import de.carina.thehunter.util.misc.Permissions
import org.bukkit.entity.Player

class Team(var teamLeader: Player) {
    val invites = mutableSetOf<Player>()
    val teamMembers = mutableSetOf<Player>()
    lateinit var game: Game
    private fun inviteTeamMember(playerToAdd: Player, leader: Player, game: Game): Boolean {
        if (!game.teamsAllowed) {
            TheHunter.instance.messages.sendMessageToPlayer(leader, "teams-not-allowed")
            return false
        }

        if (teamLeader != leader) {
            TheHunter.instance.messages.sendMessageToPlayer(leader, "team-player-not-leader")
            return false
        }
        if (teamMembers.contains(playerToAdd)) {
            TheHunter.instance.messages.sendMessageToPlayer(leader, "team-player-already-in-team")
            return false
        }

        if (!GamesHandler.playerInGames.containsKey(playerToAdd)) {
            leader.sendMessage(TheHunter.instance.messages.messagesMap["player-not-in-game"]!!.replace("%player%", playerToAdd.name))
            return false
        }

        GamesHandler.playerInGames[playerToAdd]!!.teams.forEach {
            if (it.teamMembers.contains(playerToAdd)) {
                leader.sendMessage(TheHunter.instance.messages.messagesMap["team-player-already-in-other-team"]!!.replace("%player%", playerToAdd.name))
                return false
            }
        }
        if (!leader.hasPermission(Permissions.TEAM_COMMAND)) {
            TheHunter.instance.messages.sendMessageToPlayer(leader, "player-not-permissions")
            return false
        }


        if (invites.contains(playerToAdd)) {
            leader.sendMessage(TheHunter.instance.messages.messagesMap["player-already-invited"]!!.replace("%player%", playerToAdd.name))
            return false
        }

        if (teamMembers.size >= game.teamMaxSize) {
            leader.sendMessage(TheHunter.instance.messages.messagesMap["team-full"]!!)
            return false
        }

        invites.add(playerToAdd)
        playerToAdd.sendMessage(TheHunter.instance.messages.messagesMap["player-is-invited"]!!.replace("%leader%", leader.name))
        leader.sendMessage(TheHunter.instance.messages.messagesMap["player-invited"]!!.replace("%player%", playerToAdd.name))

        return true
    }

    private fun promoteTeamLeader(player: Player, leader: Player) {
        if (!teamMembers.contains(player)) {
            leader.sendMessage(TheHunter.instance.messages.messagesMap["player-not-in-team"]!!.replace("%player%", player.name))
            return
        }
        if (teamLeader != leader) {
            leader.sendMessage(TheHunter.instance.messages.messagesMap["team-player-not-leader"]!!)
            return
        }

        teamLeader.sendMessage(TheHunter.instance.messages.messagesMap["player-promote-leader"]!!.replace("%player%", player.name))
        teamLeader = player
        teamLeader.sendMessage(TheHunter.instance.messages.messagesMap["player-new-leader"]!!)
        teamMembers.filter { it.name != player.name && it.name != leader.name }.forEach {
            it.sendMessage(TheHunter.instance.messages.messagesMap["player-new-leader-all"]!!.replace("%player%", teamLeader.name))
        }
    }


    companion object {
        fun teamsCleanUp(game: Game) {
            game.teams.forEach {
                it.invites.clear()
                if (it.teamMembers.size < 2)
                    game.teams.remove(it)
                it.teamMembers.first().performCommand("theHunter team leave")
            }
        }

        fun isTeamMember(player: Player, other: Player): Boolean {
            if (GamesHandler.playerInGames.containsKey(player)) {
                if (GamesHandler.playerInGames[player]!!.teams.any { it.teamMembers.contains(other) }) {
                    return true
                }
            }
            return false
        }

        fun removePlayerFromTeam(player: Player, leader: Player) {

            val game = GamesHandler.playerInGames[player] ?: return
            val team = game.teams.find { it.teamMembers.contains(player) }
            if (team == null) {
                player.sendMessage(TheHunter.instance.messages.messagesMap["player-not-in-team"]!!)
                return
            }
            if (leader != player && team.teamLeader != leader) {
                leader.sendMessage(TheHunter.instance.messages.messagesMap["player-not-leader"]!!)
                return
            }
            team.teamMembers.remove(player)
            player.sendMessage(TheHunter.instance.messages.messagesMap["player-left-team"]!!)
            if (team.teamMembers.isEmpty()) {
                GamesHandler.playerInGames[player]!!.teams.remove(team)
                return
            }
            if (team.teamLeader == player) {
                team.teamLeader = team.teamMembers.first()
                team.teamLeader.sendMessage(TheHunter.instance.messages.messagesMap["player-new-leader"]!!)
                team.teamMembers.forEach {
                    it.sendMessage(TheHunter.instance.messages.messagesMap["player-new-leader-all"]!!.replace("%player%", team.teamLeader.name))
                }
            } else {
                team.teamMembers.forEach {
                    it.sendMessage(TheHunter.instance.messages.messagesMap["player-left-team-all"]!!.replace("%player%", player.name))
                }
            }

        }

        fun invitePlayerToTeam(playerToInvite: Player, leader: Player) {
            val game = GamesHandler.playerInGames[leader] ?: return
            val team = game.teams.find { it.teamMembers.contains(leader) }
            if (team != null) {
                team.inviteTeamMember(playerToInvite, leader, GamesHandler.playerInGames[leader]!!)
            } else {
                GamesHandler.playerInGames[leader]!!.teams.add(Team(leader))
                GamesHandler.playerInGames[leader]!!.teams.last().teamMembers.add(leader)
                GamesHandler.playerInGames[leader]!!.teams.last().inviteTeamMember(playerToInvite, leader, GamesHandler.playerInGames[leader]!!)
            }
        }

        fun promoteNewTeamLeader(player: Player, leader: Player) {
            val game = GamesHandler.playerInGames[leader] ?: return
            val team = game.teams.find { it.teamMembers.contains(leader) }
            if (team != null) {
                team.promoteTeamLeader(player, leader)
            } else {
                player.sendMessage(TheHunter.instance.messages.messagesMap["player-not-in-team"]!!)
            }
        }

    }


}






