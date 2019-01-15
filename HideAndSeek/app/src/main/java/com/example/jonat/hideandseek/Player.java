package com.example.jonat.hideandseek;


import java.io.Serializable;

public class Player implements Serializable {

    protected String playerId;
    protected String username;
    protected String team;
    protected String role;
    private double latitude;
    private double longitude;
    private String command;
    private String lastCommand;
    private boolean alive = true;
    private double score;


    public Player(){

    }

    public Player(String username, String team, String role) {
        // When you create a new Profile, it's good to build it based on username and password
        this.username = username;
        this.team = team;
        this.role = role;
        latitude = 0;
        longitude = 0;
        this.command = "";
    }

    public String getRole() {
        return role;
    }

    public String getTeam() {
        return team;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCommand() {return command;}

    public String getLastCommand() {return lastCommand;}

    public boolean getAlive() {return alive;}

    public double getScore() {return score;}
}
