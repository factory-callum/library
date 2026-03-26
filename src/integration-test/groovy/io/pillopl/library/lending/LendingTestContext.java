package io.pillopl.library.lending;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Shared Spring test configuration for the Lending bounded context integration tests.
 *
 * Imports {@link LendingConfig} which bootstraps all lending-related beans including
 * repositories, event handlers, daily sheet read models, and patron profile infrastructure.
 * Most lending integration tests use this as their {@code @SpringBootTest} context class.
 */
@Configuration
@Import({LendingConfig.class})
public class LendingTestContext {
}