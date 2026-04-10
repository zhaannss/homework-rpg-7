package com.narxoz.rpg.combatant;

import com.narxoz.rpg.observer.*;
import com.narxoz.rpg.strategy.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The dungeon boss.
 *
 * Dual role:
 *   - EventPublisher: fires ATTACK_LANDED, BOSS_PHASE_CHANGED, BOSS_DEFEATED
 *   - GameObserver:   listens for BOSS_PHASE_CHANGED and switches its own strategy
 *
 * The engine NEVER calls a strategy-switch method directly on the boss.
 * All phase transitions flow through the event system:
 *   boss fires BOSS_PHASE_CHANGED → EventBus notifies all observers (including the boss itself)
 *   → boss.onEvent() receives the notification → boss updates its active strategy.
 */
public class DungeonBoss implements EventPublisher, GameObserver {

    private final String name;
    private int hp;
    private final int maxHp;
    private final int attackPower;
    private final int defense;
    private int currentPhase;

    private CombatStrategy strategy;

    private final CombatStrategy phase1Strategy = new BossPhase1Strategy();
    private final CombatStrategy phase2Strategy = new BossPhase2Strategy();
    private final CombatStrategy phase3Strategy = new BossPhase3Strategy();

    // Internal observer list — the boss acts as its own publisher
    private final List<GameObserver> observers = new ArrayList<>();

    public DungeonBoss(String name, int hp, int attackPower, int defense) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defense = defense;
        this.currentPhase = 1;
        this.strategy = phase1Strategy;
    }

    // ── EventPublisher ────────────────────────────────────────────────────────

    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void fireEvent(GameEvent event) {
        for (GameObserver obs : new ArrayList<>(observers)) {
            obs.onEvent(event);
        }
    }

    // ── GameObserver ──────────────────────────────────────────────────────────

    /**
     * The boss listens for its own BOSS_PHASE_CHANGED event and switches strategy accordingly.
     * This is the ONLY place where the boss strategy is updated — never from the engine.
     */
    @Override
    public void onEvent(GameEvent event) {
        if (event.getType() != GameEventType.BOSS_PHASE_CHANGED) return;

        int newPhase = event.getValue();
        strategy = switch (newPhase) {
            case 2 -> phase2Strategy;
            case 3 -> phase3Strategy;
            default -> phase1Strategy;
        };
        System.out.printf("[BOSS] %s enters Phase %d — now using strategy: [%s]%n",
                name, newPhase, strategy.getName());
    }

    // ── Combat helpers ────────────────────────────────────────────────────────

    /**
     * Applies incoming damage to the boss. Checks phase thresholds and fires events when crossed.
     *
     * @param rawDamage damage before the boss's defense is applied
     */
    public void takeDamage(int rawDamage) {
        int effective = Math.max(0, rawDamage - strategy.calculateDefense(defense));
        hp = Math.max(0, hp - effective);

        System.out.printf("[BOSS] %s takes %d damage (raw %d, def %d) — HP: %d/%d  [Phase %d | %s]%n",
                name, effective, rawDamage, strategy.calculateDefense(defense),
                hp, maxHp, currentPhase, strategy.getName());

        checkPhaseTransition();
    }

    /**
     * Checks whether the boss should change phase and, if so, fires a BOSS_PHASE_CHANGED event.
     * The strategy switch itself happens in onEvent(), not here.
     */
    private void checkPhaseTransition() {
        double pct = (double) hp / maxHp;

        if (currentPhase == 1 && pct < 0.60) {
            currentPhase = 2;
            fireEvent(new GameEvent(GameEventType.BOSS_PHASE_CHANGED, name, 2));
        } else if (currentPhase == 2 && pct < 0.30) {
            currentPhase = 3;
            fireEvent(new GameEvent(GameEventType.BOSS_PHASE_CHANGED, name, 3));
        }

        if (hp == 0) {
            fireEvent(new GameEvent(GameEventType.BOSS_DEFEATED, name, 0));
        }
    }

    /** Returns the damage the boss deals to a target with the given raw defense. */
    public int calculateAttack() {
        return strategy.calculateDamage(attackPower);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getName()             { return name; }
    public int getHp()                  { return hp; }
    public int getMaxHp()               { return maxHp; }
    public int getAttackPower()         { return attackPower; }
    public int getDefense()             { return defense; }
    public int getCurrentPhase()        { return currentPhase; }
    public CombatStrategy getStrategy() { return strategy; }
    public boolean isAlive()            { return hp > 0; }
}
