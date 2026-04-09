package com.narxoz.rpg.strategy;

/**
 * Boss Phase 2 strategy (60%-30% HP): Aggressive, presses the attack.
 * The boss starts hitting harder and neglects some defense.
 */
public class BossPhase2Strategy implements CombatStrategy {

    @Override
    public int calculateDamage(int basePower) {
        return (int) (basePower * 1.4);
    }

    @Override
    public int calculateDefense(int baseDefense) {
        return (int) (baseDefense * 0.8);
    }

    @Override
    public String getName() {
        return "Frenzied Assault";
    }
}
