/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.postgresql;

import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.LEGACY_POSTGRESQL_DRIVER;
import static io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.OPTIONS;
import static io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.POSTGRESQL_DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;
import static io.r2dbc.spi.ConnectionFactoryOptions.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class PostgresqlConnectionFactoryProviderTest {

    private final PostgresqlConnectionFactoryProvider provider = new PostgresqlConnectionFactoryProvider();

    @Test
    void doesNotSupportWithWrongDriver() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, "test-driver")
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isFalse();
    }

    @Test
    void doesNotSupportWithoutDriver() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isFalse();
    }

    @Test
    void createFailsWithoutHost() {
        assertThatThrownBy(() -> this.provider.create(ConnectionFactoryOptions.builder()
            .option(DRIVER, POSTGRESQL_DRIVER)
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void supportsWithoutHost() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, POSTGRESQL_DRIVER)
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isTrue();
    }

    @Test
    void supportsWithoutPassword() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, POSTGRESQL_DRIVER)
            .option(HOST, "test-host")
            .option(USER, "test-user")
            .build())).isTrue();
    }

    @Test
    void returnsDriverIdentifier() {
        assertThat(this.provider.getDriver()).isEqualTo(POSTGRESQL_DRIVER);
    }

    @Test
    void supports() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, POSTGRESQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isTrue();
    }

    @Test
    void supportsPostgresDriver() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, LEGACY_POSTGRESQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .build())).isTrue();
    }

    @Test
    void supportsWithoutUser() {
        assertThat(this.provider.supports(ConnectionFactoryOptions.builder()
            .option(DRIVER, POSTGRESQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .build())).isTrue();
    }

    @Test
    void providerShouldparseAndHandleConnectionParameters() {
        Map<String, String> expectedOptions = new HashMap<>();
        expectedOptions.put("lock_timeout", "5s");
        expectedOptions.put("statement_timeout", "6000");
        PostgresqlConnectionFactory factory = this.provider.create(builder()
            .option(DRIVER, LEGACY_POSTGRESQL_DRIVER)
            .option(HOST, "test-host")
            .option(PASSWORD, "test-password")
            .option(USER, "test-user")
            .option(OPTIONS, expectedOptions)
            .build());

        Map<String, String> actualOptions = factory.getConfiguration().getOptions();

        assertThat(actualOptions).isNotNull();
        assertThat(actualOptions).isEqualTo(expectedOptions);
    }

}
