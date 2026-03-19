package it.unicam.hackhub.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

public class Team {
    private String name;
    private final Set<Utente> members;

    public Team(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Utente> getMembers() {
        return members;
    }

    public void addMember(Utente member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
            member.setTeam(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean isMembro(Utente u) {
        return this.members.contains(u);
    }
}