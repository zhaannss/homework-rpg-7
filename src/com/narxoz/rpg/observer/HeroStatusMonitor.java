package com.narxoz.rpg.observer;

import com.narxoz.rpg.combatant.Hero;

import java.util.List;

/**
 * Observer 4 — HeroStatusMonitor.
 * Reacts to HERO_LOW_HP and HERO_DIED.
 * Prints a running status summary of all heroes when a condition changes.
 */
public class HeroStatusMonitor implements GameObserver {

    private final List<Hero> party;

    public HeroStatusMonitor(List<Hero> party) {
        this.party = party;
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != GameEventType.HERO_LOW_HP &&
                event.getType() != GameEventType.HERO_DIED) {
            return;
        }

        System.out.println("[STATUS] ── Hero Status Update ──────────────────");
        for (Hero hero : party) {
            String status;
            if (!hero.isAlive()) {
                status = "DEAD";
            } else if (hero.getHp() < hero.getMaxHp() * 0.30) {
                status = "CRITICAL";
            } else if (hero.getHp() < hero.getMaxHp() * 0.60) {
                status = "Wounded";
            } else {
                status = "Healthy";
            }
            System.out.printf("[STATUS]   %-12s HP: %3d / %-3d  [%s]%n",
                    hero.getName(), hero.getHp(), hero.getMaxHp(), status);
        }
        System.out.println("[STATUS] ─────────────────────────────────────────");
    }
}
