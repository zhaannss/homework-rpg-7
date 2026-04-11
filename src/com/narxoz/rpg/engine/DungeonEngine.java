package com.narxoz.rpg.engine;

import com.narxoz.rpg.combatant.DungeonBoss;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.observer.*;
import com.narxoz.rpg.strategy.AggressiveStrategy;

import java.util.List;

/**
 * Runs the dungeon encounter in rounds.
 *
 * Each round:
 *   1. Every living hero attacks the boss (using their active strategy).
 *   2. The boss attacks every living hero (using its current phase strategy).
 *
 * The engine NEVER calls a strategy-switch method directly on the boss.
 * All boss phase transitions flow through the event system:
 *   DungeonBoss.takeDamage() → fires BOSS_PHASE_CHANGED → EventBus → DungeonBoss.onEvent()
 *   → boss switches its own strategy internally.
 *
 * The engine uses a shared EventBus for ATTACK_LANDED, HERO_LOW_HP, and HERO_DIED events.
 * The boss uses its own publisher (it is itself the publisher) for boss-specific events,
 * but all observers are registered on both so they receive everything.
 */
public class DungeonEngine {

    private static final int MAX_ROUNDS = 50;

    // Round at which the second hero switches to AggressiveStrategy (demo requirement)
    private static final int STRATEGY_SWITCH_ROUND = 3;

    private final List<Hero> heroes;
    private final DungeonBoss boss;
    private final EventBus eventBus;

    // The hero whose strategy will be switched mid-battle (index 1)
    private final int strategySwitchHeroIndex;

    public DungeonEngine(List<Hero> heroes, DungeonBoss boss, EventBus eventBus) {
        this.heroes = heroes;
        this.boss = boss;
        this.eventBus = eventBus;
        this.strategySwitchHeroIndex = 1; // second hero switches strategy in round 3
    }

    /**
     * Runs the full encounter and returns the result.
     *
     * @return a completed EncounterResult
     */
    public EncounterResult runEncounter() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║        THE CURSED DUNGEON — ENCOUNTER START      ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        int round = 0;

        while (round < MAX_ROUNDS && boss.isAlive() && anyHeroAlive()) {
            round++;
            System.out.printf("%n┌── Round %d ─────────────────────────────────────┐%n", round);

            // Mid-battle strategy switch (demo requirement: at least one hero switches)
            if (round == STRATEGY_SWITCH_ROUND) {
                Hero switcher = heroes.get(strategySwitchHeroIndex);
                if (switcher.isAlive()) {
                    String oldName = switcher.getStrategy().getName();
                    switcher.setStrategy(new AggressiveStrategy());
                    System.out.printf("[ENGINE] %s switches strategy: %s → %s%n",
                            switcher.getName(), oldName, switcher.getStrategy().getName());
                }
            }

            // ── Hero attacks ─────────────────────────────────────────────────
            for (Hero hero : heroes) {
                if (!hero.isAlive() || !boss.isAlive()) continue;

                int rawDamage = hero.calculateAttack();
                int bossDefense = boss.getStrategy().calculateDefense(boss.getDefense());
                int effective = Math.max(0, rawDamage - bossDefense);

                // Fire ATTACK_LANDED before applying damage so observers see it
                eventBus.fireEvent(new GameEvent(GameEventType.ATTACK_LANDED, hero.getName(), effective));

                // Actually apply damage to boss (boss handles its own phase events internally)
                boss.takeDamage(rawDamage);

                if (!boss.isAlive()) break;
            }

            // ── Boss attacks ─────────────────────────────────────────────────
            if (boss.isAlive()) {
                for (Hero hero : heroes) {
                    if (!hero.isAlive()) continue;

                    int rawDamage = boss.calculateAttack();
                    int heroDefense = hero.calculateDefense();
                    int effective = Math.max(0, rawDamage - heroDefense);

                    boolean wasAlive = hero.isAlive();
                    boolean wasLowHp = hero.getHp() < hero.getMaxHp() * 0.30;

                    hero.takeDamage(effective);

                    // Fire ATTACK_LANDED for the boss strike
                    eventBus.fireEvent(new GameEvent(GameEventType.ATTACK_LANDED, boss.getName(), effective));

                    System.out.printf("[HERO]  %s takes %d damage (raw %d, def %d) — HP: %d/%d%n",
                            hero.getName(), effective, rawDamage, heroDefense,
                            hero.getHp(), hero.getMaxHp());

                    // Fire HERO_LOW_HP if hero just crossed the 30% threshold
                    boolean nowLowHp = hero.getHp() < hero.getMaxHp() * 0.30;
                    if (wasAlive && nowLowHp && !wasLowHp) {
                        eventBus.fireEvent(new GameEvent(GameEventType.HERO_LOW_HP,
                                hero.getName(), hero.getHp()));
                    }

                    // Fire HERO_DIED if hero just died
                    if (wasAlive && !hero.isAlive()) {
                        eventBus.fireEvent(new GameEvent(GameEventType.HERO_DIED,
                                hero.getName(), 0));
                    }
                }
            }

            System.out.printf("└────────────────────────────────────────────────┘%n");
        }

        // ── Encounter over ───────────────────────────────────────────────────
        boolean heroesWon = !boss.isAlive();
        int survivors = (int) heroes.stream().filter(Hero::isAlive).count();

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf( "║  ENCOUNTER OVER — %s%-31s║%n",
                heroesWon ? "HEROES WIN! " : "BOSS WINS!  ", "");
        System.out.println("╚══════════════════════════════════════════════════╝");

        return new EncounterResult(heroesWon, round, survivors);
    }

    private boolean anyHeroAlive() {
        return heroes.stream().anyMatch(Hero::isAlive);
    }
}
