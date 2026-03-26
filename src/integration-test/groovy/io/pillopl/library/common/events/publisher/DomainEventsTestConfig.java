package io.pillopl.library.common.events.publisher;

import io.micrometer.core.instrument.MeterRegistry;
import io.pillopl.library.commons.events.DomainEvents;
import io.pillopl.library.commons.events.publisher.JustForwardDomainEventPublisher;
import io.pillopl.library.commons.events.publisher.MeteredDomainEventPublisher;
import io.pillopl.library.commons.events.publisher.StoreAndForwardDomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Test-specific Spring configuration that assembles the domain event publishing pipeline
 * for integration tests requiring eventual consistency semantics.
 *
 * Wires together:
 * <ul>
 *   <li>{@link JustForwardDomainEventPublisher} - delegates to Spring's {@link ApplicationEventPublisher}</li>
 *   <li>{@link MeteredDomainEventPublisher} - adds Micrometer metrics tracking</li>
 *   <li>{@link StoreAndForwardDomainEventPublisher} - stores events in {@link InMemoryEventsStorage}
 *       before forwarding, simulating asynchronous event delivery</li>
 * </ul>
 *
 * Marked as {@link Primary} to override the default {@link DomainEvents} bean in tests
 * that import this configuration.
 */
@Configuration
public class DomainEventsTestConfig {

    @Bean
    @Primary
    DomainEvents domainEventsWithStorage(ApplicationEventPublisher applicationEventPublisher, MeterRegistry meterRegistry) {
        return new StoreAndForwardDomainEventPublisher(
                new MeteredDomainEventPublisher(
                        new JustForwardDomainEventPublisher(applicationEventPublisher), meterRegistry),
                new InMemoryEventsStorage()
        );
    }
}
