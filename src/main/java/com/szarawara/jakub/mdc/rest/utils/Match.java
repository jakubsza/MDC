package com.szarawara.jakub.mdc.rest.utils;

public class Match {

    public String id;
    public String date;
    public Team teamHome;
    public Team teamAway;

    public static class Team {

        public String name;
        public String id;
        public String goals;
    }
}
