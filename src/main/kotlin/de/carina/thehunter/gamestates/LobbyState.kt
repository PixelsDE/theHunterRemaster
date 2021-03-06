/*
 * Copyright Notice for theHunterRemaster
 * Copyright (c) at Carina Sophie Schoppe 2022
 * File created on 6/6/22, 10:54 PM by Carina The Latest changes made by Carina on 6/6/22, 10:54 PM All contents of "LobbyState.kt" are protected by copyright.
 * The copyright law, unless expressly indicated otherwise, is
 * at Carina Sophie Schoppe. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Carina Sophie Schoppe.
 */
package de.carina.thehunter.gamestates

import de.carina.thehunter.countdowns.Countdowns
import de.carina.thehunter.util.game.Game

class LobbyState(game: Game) : GameState(game) {
    override fun start() {
        game.currentCountdown = game.countdowns[Countdowns.LOBBY_COUNTDOWN.id]
        game.currentCountdown.start()

    }

    override fun stop() {
    }

    override val gameStateID: Int = GameStates.LOBBY_STATE.id
}