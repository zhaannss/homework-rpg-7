package com.narxoz.rpg.observer;

/**
 * Implemented by any entity that can publish game events to registered observers.
 */
public interface EventPublisher {

    /**
     * Registers a new observer to receive future events.
     *
     * @param observer the observer to add
     */
    void addObserver(GameObserver observer);

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    void removeObserver(GameObserver observer);

    /**
     * Fires a game event to all registered observers.
     *
     * @param event the event to broadcast
     */
    void fireEvent(GameEvent event);
}
