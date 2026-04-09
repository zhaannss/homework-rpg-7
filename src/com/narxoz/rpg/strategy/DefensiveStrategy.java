package com.narxoz.rpg.strategy;

/**
 * Defensive strategy: only 60% damage output but 160% defense.
 * Favors absorbing damage over dealing it.
 */
public class DefensiveStrategy implements CombatStrategy {

    @Override
    public int calculateDamage(int basePower) {
        return (int) (basePower * 0.6);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return (int) (baseDefense * 1.6);
    }

    @Override
    public String getName() {
        return "Defensive";
    }
}
