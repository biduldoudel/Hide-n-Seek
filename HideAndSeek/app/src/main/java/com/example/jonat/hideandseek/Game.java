package com.example.jonat.hideandseek;

import java.io.Serializable;

public class Game implements Serializable {

    protected String gameId;
    protected String gameCode;
    protected String gameStatus;
    protected String zone_center;
    protected String zone_diameter;
    int playerNumber;
    int instantPlayerNumber;


    public Game(int playerNumber, String gameCode) {
        //this.gameId = gameId;
        this.gameCode = gameCode;
        this.gameStatus = "Waiting";
        this.instantPlayerNumber = 0;
        this.playerNumber = playerNumber;
    }

    public String getGameId() {
        return this.gameId;
    }

    public String getGameCode() {
        return this.gameCode;
    }

    public String getGameStatus(){return this.gameStatus;}


}
