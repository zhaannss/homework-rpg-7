package com.narxoz.rpg.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Central event bus that maintains a list of observers and broadcasts events to all of them.
 * Acts as the publisher/subject in the Observer pattern.
 */
public class EventBus implements EventPublisher {

    private final List<GameObserver> observers = new ArrayList<>();

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
        // Iterate over a snapshot to allow observers to safely deregister during iteration
        for (GameObserver observer : new ArrayList<>(observers)) {
            observer.onEvent(event);
        }
    }
}
