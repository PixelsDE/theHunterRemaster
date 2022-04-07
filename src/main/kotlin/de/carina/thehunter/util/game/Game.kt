/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 07.04.22, 23:06 by Carina The Latest changes made by Carina on 07.04.22, 23:06 All contents of "Game.kt" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */

package de.carina.thehunter.util.game

import de.carina.thehunter.countdowns.Countdown
import de.carina.thehunter.countdowns.EndCountdown
import de.carina.thehunter.countdowns.IngameCountdown
import de.carina.thehunter.countdowns.LobbyCountdown
import de.carina.thehunter.gamestates.*
import de.carina.thehunter.util.files.BaseFile
import de.carina.thehunter.util.misc.WorldboarderController
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class Game(private var gameName: String) {


    lateinit var currentGameState: GameState
    lateinit var currentCountdown: Countdown

    val countdowns = mutableListOf<Countdown>()
    var MAX_PLAYERS: Int = 20
    var MIN_PLAYERS: Int = 2
    var currentPlayers: Int = 0
    var gameStarted: Boolean = false
    val players = mutableSetOf<Player>()
    val spectators = mutableSetOf<Player>()
    val playerSpawns = mutableListOf<Location>()
    var randomDrop = true
    lateinit var lobbyLocation: Location
    lateinit var backLocation: Location
    lateinit var endLocation: Location
    var teamsAllowed = true
    lateinit var arenaCenter: Location
    val teams = mutableSetOf<Team>()
    lateinit var worldBoarderController: WorldboarderController
    var arenaRadius = 1000
    val gameStates = mutableListOf<GameState>()

    fun start() {
        TODO("not implemented")
    }

    fun end() {
        TODO("not implemented")
    }

    fun nextGameState() {
        currentGameState.stop()
        if (currentGameState is EndState) {
            GamesHandler.games.remove(this)
            loadGameFromConfig(gameName)
        } else {
            currentGameState = gameStates[gameStates.indexOf(currentGameState) + 1]
            currentGameState.start()
        }
    }


    fun saveGameToConfig(): Boolean {
        if (gameName == null)
            return false
        if (arenaCenter == null)
            return false
        if (backLocation == null)
            return false
        if (lobbyLocation == null)
            return false
        if (endLocation == null)
            return false
        val fileSettings = File("${BaseFile.gameFolder}/arenas/$gameName/settings.yml")
        val ymlSettings = YamlConfiguration.loadConfiguration(fileSettings)

        ymlSettings.set("game-name", gameName)
        ymlSettings.set("random-drop", randomDrop)
        ymlSettings.set("max-players", MAX_PLAYERS)
        ymlSettings.set("min-players", MIN_PLAYERS)
        ymlSettings.set("world-boarder-size", worldBoarderController.worldBoarderSize)
        ymlSettings.set("teams-allowed", teamsAllowed)
        ymlSettings.set("worldboarder-shrinkspeed", worldBoarderController.shrinkSpeed)
        ymlSettings.set("arena-radius", arenaRadius)
        ymlSettings.set("worldboarder-min-border-size", worldBoarderController.minBorderSize)
        ymlSettings.set("worldboarder-shrinkboarder", worldBoarderController.shrinkBoarder)

        val fileLocations = File("${BaseFile.gameFolder}/arenas/$gameName/locations.yml")
        val ymlLocations = YamlConfiguration.loadConfiguration(fileLocations)

        if (playerSpawns.isNotEmpty())
            ymlLocations.set("spawn-locations", playerSpawns)
        ymlLocations.set("arena-center", arenaCenter)
        ymlLocations.set("lobby-location", lobbyLocation)
        ymlLocations.set("back-location", backLocation)
        ymlLocations.set("end-location", endLocation)

        return true
    }

    companion object {
        fun loadGameFromConfig(fileName: String) {
            val fileSettings = File("${BaseFile.gameFolder}/arenas/$fileName/settings.yml")
            val ymlSettings = YamlConfiguration.loadConfiguration(fileSettings)

            val game = Game(ymlSettings.getString("game-name")!!)

            game.randomDrop = ymlSettings.getBoolean("random-drop")
            game.MAX_PLAYERS = ymlSettings.getInt("max-players")
            game.MIN_PLAYERS = ymlSettings.getInt("min-players")
            game.worldBoarderController.worldBoarderSize = ymlSettings.getInt("world-boarder-size")
            game.teamsAllowed = ymlSettings.getBoolean("teams-allowed")
            game.arenaRadius = ymlSettings.getInt("arena-radius")
            game.worldBoarderController.shrinkSpeed = ymlSettings.getInt("worldboarder-shrinkspeed")
            game.worldBoarderController.minBorderSize = ymlSettings.getInt("worldboarder-min-border-size")
            game.worldBoarderController.shrinkBoarder = ymlSettings.getBoolean("worldboarder-shrinkboarder")

            val fileLocations = File("${BaseFile.gameFolder}/arenas/$fileName/locations.yml")
            val ymlLocations = YamlConfiguration.loadConfiguration(fileLocations)

            game.playerSpawns.addAll(ymlLocations.getList("spawn-locations") as List<Location>)
            game.lobbyLocation = ymlLocations.getLocation("lobby-location")!!
            game.backLocation = ymlLocations.getLocation("back-location")!!
            game.endLocation = ymlLocations.getLocation("end-location")!!
            game.arenaCenter = ymlLocations.getLocation("arena-center")!!
            game.countdowns.addAll(listOf(LobbyCountdown(game), IngameCountdown(game), EndCountdown(game)))
            game.gameStates.addAll(listOf(LobbyState(game), IngameState(game), EndState(game)))
            game.currentGameState = game.gameStates[GameStates.LOBBY_STATE.id]
            game.worldBoarderController = WorldboarderController(game)
            game.currentGameState.start()
            GamesHandler.games.add(game)
        }
    }

}