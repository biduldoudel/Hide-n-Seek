package com.example.jonat.hideandseek;

import java.io.Serializable;

//Game class to automatically create required field in database
public class Game implements Serializable {

    protected String gameId;
    protected String gameCode;
    protected String gameStatus;
    protected String winner;
    int playerNumber;
    int instantPlayerNumber;
    int nExpectedPlayers;
    int nReadyPlayers;
    int nSurvivors;
    int nZombies;
    boolean masterZombieRegistered;
    boolean masterSurvivorRegistered;
    int totSurvivorsScore = 0;
    long startTime;



    public Game(int playerNumber, String gameCode, int nExpectedPlayer) {
        //this.gameId = gameId;
        this.gameCode = gameCode;
        this.gameStatus = "WaitingForPlayers";
        this.instantPlayerNumber = 0;
        this.playerNumber = playerNumber;
        this.nExpectedPlayers = nExpectedPlayer;
        this.nReadyPlayers = 0;
        this.nSurvivors=0;
        this.nZombies=0;
        this.masterZombieRegistered = false;
        this.masterSurvivorRegistered = false;
        this.winner = "";
        //this.gameReady = false;
    }

    public boolean isMasterZombieRegistered() {
        return masterZombieRegistered;
    }

    public boolean isMasterSurvivorRegistered() {
        return masterSurvivorRegistered;
    }

    public String getGameId() {return this.gameId;}

    public String getGameCode() {return this.gameCode;}

    public String getGameStatus(){return this.gameStatus;}

    public int getnExpectedPlayers() {return nExpectedPlayers;}

    public int getnReadyPlayers() {return nReadyPlayers;}

    public int getnSurvivors() {return nSurvivors;}

    public int getnZombies() {return nZombies;}

    public int getTotSurvivorsScore() {return totSurvivorsScore;}

    public long getStartTime() {return startTime;}

    public String getWinner() {return winner;}

}
