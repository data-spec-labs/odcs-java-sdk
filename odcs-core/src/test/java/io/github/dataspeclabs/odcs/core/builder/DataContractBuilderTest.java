package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.ODCSParser;
import io.github.dataspeclabs.odcs.core.ODCSSpecValidator;
import io.github.dataspeclabs.odcs.core.ODCSWriter;
import io.github.dataspeclabs.odcs.core.SpecValidationReport;
import io.github.dataspeclabs.odcs.core.model.v3.DataContract;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.ServerType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataContractBuilderTest {

    @Test
    void buildsMinimalContractWithDefaults() {
        DataContract contract = DataContractBuilder.create()
                .id("53581432-6c55-4ba2-a65f-72344a91553a")
                .version("1.0.0")
                .status("active")
                .build();

        assertEquals("v3.1.0", contract.apiVersion());
        assertEquals("DataContract", contract.kind());
        assertEquals("53581432-6c55-4ba2-a65f-72344a91553a", contract.id());
        assertEquals("1.0.0", contract.version());
        assertEquals("active", contract.status());
    }

    @Test
    void buildsNestedSchemaObjectAndProperty() {
        DataContract contract = DataContractBuilder.create()
                .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                .version("1.0.0")
                .status("active")
                .name("seller_payments_v1")
                .domain("seller")
                .schemaObject(obj -> obj
                        .name("payments")
                        .physicalType("table")
                        .property(p -> p
                                .name("payment_id")
                                .logicalType(LogicalType.STRING)
                                .primaryKey(true)
                                .required(true)))
                .build();

        assertEquals(1, contract.schema().size());
        assertEquals("payments", contract.schema().get(0).name());
        assertEquals(1, contract.schema().get(0).properties().size());
        assertEquals("payment_id", contract.schema().get(0).properties().get(0).name());
        assertEquals(LogicalType.STRING, contract.schema().get(0).properties().get(0).logicalType());
        assertEquals(Boolean.TRUE, contract.schema().get(0).properties().get(0).primaryKey());
    }

    @Test
    void buildsServerWithAdditionalProperties() {
        DataContract contract = DataContractBuilder.create()
                .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                .version("1.0.0")
                .status("active")
                .server(s -> s
                        .server("my-postgres")
                        .type(ServerType.POSTGRES)
                        .environment("prod")
                        .property("host", "localhost")
                        .property("port", 5432)
                        .property("database", "analytics"))
                .build();

        assertEquals(1, contract.servers().size());
        assertEquals("my-postgres", contract.servers().get(0).server());
        assertEquals(ServerType.POSTGRES, contract.servers().get(0).type());
        assertEquals("localhost", contract.servers().get(0).additionalProperties().get("host"));
        assertEquals(5432, contract.servers().get(0).additionalProperties().get("port"));
        assertEquals("analytics", contract.servers().get(0).additionalProperties().get("database"));
    }

    @Test
    void buildsTeamWithMembers() {
        DataContract contract = DataContractBuilder.create()
                .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                .version("1.0.0")
                .status("active")
                .team(t -> t
                        .name("data-team")
                        .member(m -> m
                                .username("alice")
                                .role("Owner")
                                .dateIn(LocalDate.of(2024, 1, 1)))
                        .member(m -> m.username("bob").role("Engineer")))
                .build();

        assertNotNull(contract.team());
        assertEquals("data-team", contract.team().name());
        assertEquals(2, contract.team().members().size());
        assertEquals("alice", contract.team().members().get(0).username());
        assertEquals(LocalDate.of(2024, 1, 1), contract.team().members().get(0).dateIn());
    }

    @Test
    void buildRejectsMissingRequiredFields() {
        assertThrows(IllegalStateException.class, () -> DataContractBuilder.create().build());
        assertThrows(IllegalStateException.class, () -> DataContractBuilder.create()
                .id("x")
                .version("1.0.0")
                .build());
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                DataContractBuilder.create()
                        .id("x")
                        .version("1.0.0")
                        .status(" ")
                        .build());
        assertTrue(ex.getMessage().contains("status"));
    }

    @Test
    void roundTripsThroughWriterAndParser() {
        DataContract original = DataContractBuilder.create()
                .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                .version("1.2.0")
                .status("active")
                .name("orders_v1")
                .schemaObject(obj -> obj
                        .name("orders")
                        .physicalType("table")
                        .property(p -> p.name("order_id").logicalType(LogicalType.STRING)))
                .build();

        String yaml = ODCSWriter.toYaml(original);
        DataContract parsed = ODCSParser.parse(yaml);

        assertEquals(original.id(), parsed.id());
        assertEquals(original.name(), parsed.name());
        assertEquals(original.schema().get(0).name(), parsed.schema().get(0).name());
        assertEquals(
                original.schema().get(0).properties().get(0).name(),
                parsed.schema().get(0).properties().get(0).name());
    }

    @Test
    void builtMinimalContractPassesSpecValidation() {
        DataContract contract = DataContractBuilder.create()
                .id("53581432-6c55-4ba2-a65f-72344a91553a")
                .version("1.0.0")
                .status("active")
                .build();

        String yaml = ODCSWriter.toYaml(contract);
        SpecValidationReport report = ODCSSpecValidator.validate(yaml);

        assertTrue(report.valid(), () -> "Expected valid, errors=" + report.errors());
        assertEquals("v3.1.0", report.resolvedVersion());
    }

    @Test
    void fromExistingContractAllowsEdits() {
        DataContract original = DataContractBuilder.create()
                .id("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
                .version("1.0.0")
                .status("draft")
                .name("payments")
                .build();

        DataContract updated = DataContractBuilder.from(original)
                .status("active")
                .build();

        assertEquals("active", updated.status());
        assertEquals(original.id(), updated.id());
        assertEquals(original.name(), updated.name());
        assertEquals("draft", original.status());
    }
}
