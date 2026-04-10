package com.narxoz.rpg.observer;

import com.narxoz.rpg.combatant.Hero;

import java.util.List;
import java.util.Random;

/**
 * Observer 3 — PartySupport.
 * Reacts to HERO_LOW_HP.
 * Heals a random living ally (not the hero that triggered the event) for a fixed amount.
 */
public class PartySupport implements GameObserver {

    private static final int HEAL_AMOUNT = 20;

    private final List<Hero> party;
    private final Random random;

    public PartySupport(List<Hero> party) {
        this.party = party;
        this.random = new Random(7L); // fixed seed for reproducibility
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != GameEventType.HERO_LOW_HP) return;

        // Collect living allies (anyone alive, including the one in danger)
        List<Hero> candidates = party.stream().filter(Hero::isAlive).toList();
        if (candidates.isEmpty()) return;

        Hero target = candidates.get(random.nextInt(candidates.size()));
        int before = target.getHp();
        target.heal(HEAL_AMOUNT);
        int after = target.getHp();
        System.out.printf("[SUPPORT] Party Support heals %s for %d HP (%d → %d)%n",
                target.getName(), HEAL_AMOUNT, before, after);
    }
}
