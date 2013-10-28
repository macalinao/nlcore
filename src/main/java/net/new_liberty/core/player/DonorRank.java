package net.new_liberty.core.player;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Represents a donor rank.
 */
public enum DonorRank {

    PREMIUM, HERO, ELITE, GUARDIAN, CHAMPION;

    public String getPermission() {
        return "ranks.donor." + name().toLowerCase();
    }

}
