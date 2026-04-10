package com.narxoz.rpg.observer;

import java.util.HashSet;
import java.util.Set;

/**
 * Observer 2 — AchievementTracker.
 * Reacts to BOSS_DEFEATED, HERO_DIED, ATTACK_LANDED.
 * Unlocks achievements when conditions are met.
 */
public class AchievementTracker implements GameObserver {

    private final Set<String> unlocked = new HashSet<>();
    private int totalAttacks = 0;
    private int heroDeaths = 0;

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {

            case ATTACK_LANDED -> {
                totalAttacks++;
                if (totalAttacks >= 10 && unlock("Flurry of Blows")) {
                    System.out.println("[ACHIEVEMENT] 🏅 'Flurry of Blows' unlocked — 10 attacks landed in one encounter!");
                }
                if (totalAttacks >= 25 && unlock("War Machine")) {
                    System.out.println("[ACHIEVEMENT] 🏅 'War Machine' unlocked — 25 total attacks in one encounter!");
                }
            }

            case HERO_DIED -> {
                heroDeaths++;
                if (heroDeaths >= 1 && unlock("Fallen Comrade")) {
                    System.out.println("[ACHIEVEMENT] 🏅 'Fallen Comrade' unlocked — a hero has perished in battle!");
                }
                if (heroDeaths >= 2 && unlock("Last Stand")) {
                    System.out.println("[ACHIEVEMENT] 🏅 'Last Stand' unlocked — two heroes have fallen!");
                }
            }

            case BOSS_DEFEATED -> {
                if (unlock("Dungeon Clear")) {
                    System.out.println("[ACHIEVEMENT] 🏅 'Dungeon Clear' unlocked — the boss has been slain!");
                }
                if (heroDeaths == 0 && unlock("Flawless Victory")) {
                    System.out.println("[ACHIEVEMENT] 🏅 'Flawless Victory' unlocked — boss defeated without losing a hero!");
                }
            }

            default -> { /* this observer ignores other events */ }
        }
    }

    private boolean unlock(String name) {
        return unlocked.add(name); // returns true only the first time
    }
}
