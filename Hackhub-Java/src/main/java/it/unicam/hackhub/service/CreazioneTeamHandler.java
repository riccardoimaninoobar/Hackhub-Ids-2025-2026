package it.unicam.hackhub.service;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.User;

import java.util.HashSet;
import java.util.Set;

public class CreazioneTeamHandler {

    private Set<Team> teams = new HashSet<>();

    public boolean teamExists(String teamName) {
        return teams.stream().anyMatch(team -> team.getName().equalsIgnoreCase(teamName));
    }

    public Team createTeam(String teamName, User creator) {
        if (creator.getTeam() != null) {
            throw new IllegalStateException("User is already in a team.");
        }
        if (teamExists(teamName)) {
            throw new IllegalArgumentException("Team with this name already exists.");
        }

        Team newTeam = new Team(teamName);
        newTeam.addMember(creator);
        teams.add(newTeam);
        return newTeam;
    }
}
