package com.narxoz.rpg.combatant;

import com.narxoz.rpg.strategy.CombatStrategy;

/**
 * Represents a player-controlled hero participating in the dungeon encounter.
 * Adapted from Homework 6.
 *
 * Extended to hold and use an active CombatStrategy that can be swapped at any time.
 */
public class Hero {

    private final String name;
    private int hp;
    private final int maxHp;
    private final int attackPower;
    private final int defense;

    // Strategy pattern — can be switched at runtime
    private CombatStrategy strategy;

    public Hero(String name, int hp, int attackPower, int defense) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defense = defense;
    }

    // ── Strategy ──────────────────────────────────────────────────────────────

    public void setStrategy(CombatStrategy strategy) {
        this.strategy = strategy;
    }

    public CombatStrategy getStrategy() {
        return strategy;
    }

    /** Returns the effective damage this hero deals using the active strategy. */
    public int calculateAttack() {
        if (strategy == null) return attackPower;
        return strategy.calculateDamage(attackPower);
    }

    /** Returns the effective defense this hero applies using the active strategy. */
    public int calculateDefense() {
        if (strategy == null) return defense;
        return strategy.calculateDefense(defense);
    }

    // ── HP management ─────────────────────────────────────────────────────────

    /**
     * Reduces this hero's HP by the given amount, clamped to zero.
     *
     * @param amount the damage to apply; must be non-negative
     */
    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    /**
     * Restores this hero's HP by the given amount, clamped to maxHp.
     *
     * @param amount the HP to restore; must be non-negative
     */
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getName()         { return name; }
    public int getHp()              { return hp; }
    public int getMaxHp()           { return maxHp; }
    public int getAttackPower()     { return attackPower; }
    public int getDefense()         { return defense; }
    public boolean isAlive()        { return hp > 0; }
}
