package com.narxoz.rpg.strategy;

/**
 * Boss Phase 1 strategy (100%-60% HP): Measured and calculated.
 * The boss fights carefully — moderate damage, solid defense.
 */
public class BossPhase1Strategy implements CombatStrategy {

    @Override
    public int calculateDamage(int basePower) {
        return (int) (basePower * 1.0);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return (int) (baseDefense * 1.2);
    }

    @Override
    public String getName() {
        return "Calculated Menace";
    }
}
