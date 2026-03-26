package io.pillopl.library.common.events.publisher;

import io.pillopl.library.commons.events.DomainEvent;
import io.pillopl.library.commons.events.publisher.EventsStorage;
import io.vavr.collection.List;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Thread-safe in-memory implementation of {@link EventsStorage} used in integration tests.
 *
 * Replaces the production event storage mechanism with a simple synchronized list,
 * allowing tests to verify event publishing and forwarding behavior without requiring
 * an external message broker or persistent storage.
 */
public class InMemoryEventsStorage implements EventsStorage {

    //it's not thread safe, enough for testing
    private final java.util.List<DomainEvent> eventList = Collections.synchronizedList(new ArrayList<>());

    @Override
    synchronized public void save(DomainEvent event) {
        eventList.add(event);
    }

    @Override
    synchronized public List<DomainEvent> toPublish() {
        return List.ofAll(eventList);
    }

    @Override
    synchronized public void published(List<DomainEvent> events) {
        eventList.removeAll(events.asJava());
    }
}
