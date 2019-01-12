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
    int nExpectedPlayers;
    int nReadyPlayers;
    int nSurvivors;
    int nZombies;
    boolean gameReady;
    boolean masterZombieRegistered;
    boolean masterSurvivorRegistered;


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


    //public boolean getGameReady() {return gameReady;}
}
