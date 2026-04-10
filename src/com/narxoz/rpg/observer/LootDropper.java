package com.narxoz.rpg.observer;

import java.util.Random;

/**
 * Observer 5 — LootDropper.
 * Reacts to BOSS_PHASE_CHANGED and BOSS_DEFEATED.
 * Generates and prints a loot drop for each transition.
 */
public class LootDropper implements GameObserver {

    private static final String[][] PHASE_LOOT = {
            { "Enchanted Gauntlets", "Shadow Cloak", "Ring of Fortitude" },
            { "Runic Sword", "Voidsteel Armor", "Amulet of Power" },
            { "Cursed Crown", "Dragonsoul Blade", "Tome of the Abyss", "Boss Soul Shard" }
    };

    private static final String[] BOSS_DEFEATED_LOOT = {
            "Heart of the Dungeon Lord",
            "Legendary Cursed Blade",
            "Title: Dungeon Conqueror"
    };

    private final Random random = new Random(13L);

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {

            case BOSS_PHASE_CHANGED -> {
                int phase = event.getValue(); // 2 or 3
                String[] pool = PHASE_LOOT[Math.min(phase - 1, PHASE_LOOT.length - 1)];
                String item = pool[random.nextInt(pool.length)];
                System.out.printf("[LOOT] 💎 Phase %d transition drop: [%s]%n", phase, item);
            }

            case BOSS_DEFEATED -> {
                System.out.println("[LOOT] 👑 Boss defeated! Rare loot drops:");
                for (String item : BOSS_DEFEATED_LOOT) {
                    System.out.println("[LOOT]   → " + item);
                }
            }

            default -> { /* ignores other events */ }
        }
    }
}
