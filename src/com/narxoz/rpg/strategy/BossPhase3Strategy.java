package com.narxoz.rpg.strategy;

/**
 * Boss Phase 3 strategy (below 30% HP): Desperate — maximum aggression, ignores defense.
 * The boss throws everything into offense, leaving itself wide open.
 */
public class BossPhase3Strategy implements CombatStrategy {

    @Override
    public int calculateDamage(int basePower) {
        return (int) (basePower * 2.0);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return 0; // ignores defense entirely
    }

    @Override
    public String getName() {
        return "Death Throes";
    }
}
