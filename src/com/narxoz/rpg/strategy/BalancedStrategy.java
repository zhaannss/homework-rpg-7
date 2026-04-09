package com.narxoz.rpg.strategy;

/**
 * Balanced strategy: keeps base values unchanged.
 * Serves as the default fighting style.
 */
public class BalancedStrategy implements CombatStrategy {

    @Override
    public int calculateDamage(int basePower) {
        return basePower;
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return baseDefense;
    }

    @Override
    public String getName() {
        return "Balanced";
    }
}
