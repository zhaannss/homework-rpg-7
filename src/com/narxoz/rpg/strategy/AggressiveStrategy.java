package com.narxoz.rpg.strategy;

/**
 * Aggressive strategy: deals 150% damage but only 50% defense.
 * Favors attacking over defending.
 */
public class AggressiveStrategy implements CombatStrategy {

    @Override
    public int calculateDamage(int basePower) {
        return (int) (basePower * 1.5);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return (int) (baseDefense * 0.5);
    }

    @Override
    public String getName() {
        return "Aggressive";
    }
}
