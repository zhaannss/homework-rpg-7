package com.narxoz.rpg;

import com.narxoz.rpg.combatant.DungeonBoss;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.engine.DungeonEngine;
import com.narxoz.rpg.engine.EncounterResult;
import com.narxoz.rpg.observer.*;
import com.narxoz.rpg.strategy.*;

import java.util.List;

/**
 * Entry point for Homework 7 — The Cursed Dungeon: Boss Encounter System.
 *
 * Demonstrates:
 *   1. 3 heroes each starting with a different combat strategy
 *   2. A DungeonBoss with high enough HP that all 3 phases are visible
 *   3. All 5 observers registered before the encounter starts
 *   4. Boss visibly transitioning through all 3 phases
 *   5. One hero switching strategy mid-battle (round 3)
 *   6. All 5 observer types producing output
 *   7. Final EncounterResult printed at the end
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=== Homework 7 Demo: Strategy + Observer Patterns ===\n");

        // ── 1. Create heroes with different starting strategies ───────────────
        Hero aisha  = new Hero("Aisha",  120, 22, 10);  // starts Balanced
        Hero erlan  = new Hero("Erlan",  100, 28, 6);   // starts Defensive → switches to Aggressive in round 3
        Hero zara   = new Hero("Zara",   80,  18, 14);  // starts Aggressive

        aisha.setStrategy(new BalancedStrategy());
        erlan.setStrategy(new DefensiveStrategy());
        zara.setStrategy(new AggressiveStrategy());

        List<Hero> party = List.of(aisha, erlan, zara);

        System.out.println("── Party ───────────────────────────────────────────");
        for (Hero h : party) {
            System.out.printf("  %-8s HP=%-4d ATK=%-4d DEF=%-4d Strategy=[%s]%n",
                    h.getName(), h.getMaxHp(), h.getAttackPower(),
                    h.getDefense(), h.getStrategy().getName());
        }

        // ── 2. Create the boss with high HP so all 3 phases are visible ───────
        // phase thresholds: Phase2 at 60% (180 HP), Phase3 at 30% (90 HP)
        DungeonBoss boss = new DungeonBoss("Dungeon Lord Malachar", 300, 30, 12);

        System.out.printf("%n── Boss ────────────────────────────────────────────%n");
        System.out.printf("  %-26s HP=%-4d ATK=%-4d DEF=%-4d%n",
                boss.getName(), boss.getMaxHp(), boss.getAttackPower(), boss.getDefense());

        // ── 3. Create the shared EventBus and register the boss as listener ──
        EventBus eventBus = new EventBus();

        // The boss must be registered on the eventBus so it receives events
        // fired by the engine (e.g. hero attacks). But the boss fires its own
        // phase events through its own publisher — we register all observers there too.
        eventBus.addObserver(boss); // boss observes the global bus (for potential future global events)

        // Register boss on itself so it receives its own BOSS_PHASE_CHANGED events
        boss.addObserver(boss);

        // ── 4. Instantiate all 5 observers ────────────────────────────────────
        BattleLogger      battleLogger      = new BattleLogger();
        AchievementTracker achievementTracker = new AchievementTracker();
        PartySupport      partySupport      = new PartySupport(party);
        HeroStatusMonitor heroStatusMonitor = new HeroStatusMonitor(party);
        LootDropper       lootDropper       = new LootDropper();

        // Register all 5 observers on the shared event bus (for hero/engine events)
        eventBus.addObserver(battleLogger);
        eventBus.addObserver(achievementTracker);
        eventBus.addObserver(partySupport);
        eventBus.addObserver(heroStatusMonitor);
        eventBus.addObserver(lootDropper);

        // Register all 5 observers on the boss publisher (for boss-specific events)
        boss.addObserver(battleLogger);
        boss.addObserver(achievementTracker);
        boss.addObserver(partySupport);
        boss.addObserver(heroStatusMonitor);
        boss.addObserver(lootDropper);

        System.out.println("\n── Observers registered ────────────────────────────");
        System.out.println("  1. BattleLogger       — logs all events");
        System.out.println("  2. AchievementTracker — unlocks achievements");
        System.out.println("  3. PartySupport       — heals allies on HERO_LOW_HP");
        System.out.println("  4. HeroStatusMonitor  — prints party status on HP changes");
        System.out.println("  5. LootDropper        — drops loot on phase change / boss defeat");

        // ── 5. Run the encounter ──────────────────────────────────────────────
        DungeonEngine engine = new DungeonEngine(party, boss, eventBus);
        EncounterResult result = engine.runEncounter();

        // ── 6. Print EncounterResult ──────────────────────────────────────────
        System.out.println("\n── Encounter Result ────────────────────────────────");
        System.out.println("  Outcome          : " + (result.isHeroesWon() ? "Heroes Won!" : "Boss Victorious"));
        System.out.println("  Rounds played    : " + result.getRoundsPlayed());
        System.out.println("  Surviving heroes : " + result.getSurvivingHeroes());
        System.out.println("\n=== Demo Complete ===");
    }
}
