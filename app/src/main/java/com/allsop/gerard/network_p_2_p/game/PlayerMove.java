package com.allsop.gerard.network_p_2_p.game;

/**
 * Edited by Skye on 10/11/2015.
 */
public class PlayerMove {
    PlayerType player;
    String name;
    int move;

    public PlayerMove(PlayerType player, int move){
        this.player = player;
        this.move = move;
        this.name = GameState.getThisGame().getMyName();
    }

    public int getMove(){
        return move;
    }

    public PlayerType getPlayer(){
        return player;
    }

    public void setPlayerName(String name){
        this.name=name;
    }

    public String getPlayerName(){
        return name;
    }
}