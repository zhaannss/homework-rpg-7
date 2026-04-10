package com.narxoz.rpg.observer;

/**
 * Observer 1 — BattleLogger.
 * Reacts to all event types. Prints a formatted log line for every event.
 */
public class BattleLogger implements GameObserver {

    @Override
    public void onEvent(GameEvent event) {
        String icon = switch (event.getType()) {
            case ATTACK_LANDED      -> "⚔";
            case HERO_LOW_HP        -> "⚠";
            case HERO_DIED          -> "💀";
            case BOSS_PHASE_CHANGED -> "🔥";
            case BOSS_DEFEATED      -> "🏆";
        };
        System.out.printf("[LOG] %s [%s] source=%-14s value=%d%n",
                icon, event.getType(), event.getSourceName(), event.getValue());
    }
}
