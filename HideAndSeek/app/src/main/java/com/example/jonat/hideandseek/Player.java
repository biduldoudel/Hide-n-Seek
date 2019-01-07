package com.example.jonat.hideandseek;


import java.io.Serializable;

public class Player implements Serializable {

    protected String playerId;
    protected String username;
    protected String team;
    protected String role;


    public Player(){

    }

    public Player(String username, String team, String role) {
        // When you create a new Profile, it's good to build it based on username and password
        this.username = username;
        this.team = team;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getTeam() {
        return team;
    }


}
