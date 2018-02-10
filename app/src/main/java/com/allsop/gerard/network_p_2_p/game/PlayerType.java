package com.allsop.gerard.network_p_2_p.game;

/**
 * Edited by Skye  on 09/11/2015.
 */
public enum PlayerType {
    CROSS(0),
    NOUGHT(1),
    FREE(2),
    NO_WINNER(3);

    private final int id;

    PlayerType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

}