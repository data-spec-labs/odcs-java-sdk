package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Structural deserialization tests for the v3.x domain model.
 * Uses an inline ObjectMapper (not the future ODCSParser facade).
 */
class DataContractModelTest {

    private static ObjectMapper mapper;

    @BeforeAll
    static void setUpMapper() {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void deserializesOfficialFullExample() throws Exception {
        DataContract contract = readContract("odcs/full-example.odcs.yaml");

        assertEquals("v3.1.0", contract.apiVersion());
        assertEquals("DataContract", contract.kind());
        assertEquals("53581432-6c55-4ba2-a65f-72344a91553a", contract.id());
        assertEquals("1.1.0", contract.version());
        assertEquals("active", contract.status());
        assertEquals("seller", contract.domain());
        assertEquals("my quantum", contract.dataProduct());
        assertEquals("ClimateQuantumInc", contract.tenant());
        assertNotNull(contract.contractCreatedTs());

        assertNotNull(contract.description());
        assertEquals("Views built on top of the seller tables.", contract.description().purpose());

        assertNotNull(contract.servers());
        assertEquals(1, contract.servers().size());
        Server server = contract.servers().get(0);
        assertEquals("my-postgres", server.server());
        assertEquals(ServerType.POSTGRES, server.type());
        assertEquals("localhost", server.additionalProperties().get("host"));
        assertEquals(5432, ((Number) server.additionalProperties().get("port")).intValue());
        assertEquals("pypl-edw", server.additionalProperties().get("database"));

        assertNotNull(contract.schema());
        assertTrue(contract.schema().size() >= 2);
        SchemaObject tbl = contract.schema().get(0);
        assertEquals("tbl_obj", tbl.id());
        assertEquals("tbl", tbl.name());
        assertEquals("table", tbl.physicalType());
        assertNotNull(tbl.properties());
        assertFalse(tbl.properties().isEmpty());

        SchemaProperty rcvrId = tbl.properties().stream()
                .filter(p -> "rcvr_id".equals(p.name()))
                .findFirst()
                .orElseThrow();
        assertEquals(LogicalType.STRING, rcvrId.logicalType());
        assertEquals(Boolean.TRUE, rcvrId.primaryKey());
        assertNotNull(rcvrId.relationships());
        assertEquals(1, rcvrId.relationships().size());
        Relationship propRel = rcvrId.relationships().get(0);
        assertEquals(RelationshipType.FOREIGN_KEY, propRel.type());
        assertNull(propRel.from());
        assertEquals(List.of("receivers.id"), propRel.to());

        assertNotNull(tbl.relationships());
        assertFalse(tbl.relationships().isEmpty());
        Relationship schemaRel = tbl.relationships().get(0);
        assertEquals(List.of("tbl.rcvr_id", "tbl.rcvr_cntry_code"), schemaRel.from());
        assertEquals(List.of("receivers.id", "receivers.country_code"), schemaRel.to());

        assertNotNull(tbl.quality());
        assertFalse(tbl.quality().isEmpty());
        DataQuality rowCount = tbl.quality().stream()
                .filter(q -> q.metric() == DataQualityMetric.ROW_COUNT)
                .findFirst()
                .orElseThrow();
        assertEquals(DataQualityType.LIBRARY, rowCount.type());
        assertEquals(1_000_000, ((Number) rowCount.mustBeGreaterThan()).intValue());

        assertNotNull(contract.team());
        assertEquals("my-team", contract.team().name());
        assertNotNull(contract.team().members());
        assertEquals(3, contract.team().members().size());
        TeamMember first = contract.team().members().get(0);
        assertEquals("ceastwood", first.username());
        assertEquals(LocalDate.of(2022, 8, 2), first.dateIn());

        assertNotNull(contract.price());
        assertEquals(0, new BigDecimal("9.95").compareTo(contract.price().priceAmount()));
        assertEquals("USD", contract.price().priceCurrency());

        assertNotNull(contract.roles());
        assertFalse(contract.roles().isEmpty());
        assertNotNull(contract.slaProperties());
        assertFalse(contract.slaProperties().isEmpty());
        assertNotNull(contract.support());
        assertFalse(contract.support().isEmpty());
        assertEquals(SupportTool.SLACK, contract.support().get(0).tool());
        assertTrue(contract.tags().contains("transactions"));
    }

    @Test
    void deserializesDeprecatedFlatTeamArray() throws Exception {
        DataContract contract = readContract("odcs/deprecated-team-array.odcs.yaml");

        assertEquals("v3.0.2", contract.apiVersion());
        assertNotNull(contract.team());
        assertNull(contract.team().id());
        assertNull(contract.team().name());
        assertNotNull(contract.team().members());
        assertEquals(2, contract.team().members().size());
        assertEquals("alice", contract.team().members().get(0).username());
        assertEquals("Owner", contract.team().members().get(0).role());
        assertEquals("bob", contract.team().members().get(1).username());
    }

    @Test
    void roundTripsFullExampleWithoutLosingKeyFields() throws Exception {
        DataContract original = readContract("odcs/full-example.odcs.yaml");

        String yaml = mapper.writeValueAsString(original);
        DataContract roundTripped = mapper.readValue(yaml, DataContract.class);

        assertEquals(original.apiVersion(), roundTripped.apiVersion());
        assertEquals(original.id(), roundTripped.id());
        assertEquals(original.version(), roundTripped.version());
        assertEquals(original.status(), roundTripped.status());
        assertEquals(original.domain(), roundTripped.domain());
        assertEquals(original.tenant(), roundTripped.tenant());
        assertEquals(original.schema().size(), roundTripped.schema().size());
        assertEquals(original.team().members().size(), roundTripped.team().members().size());
        assertEquals(original.servers().get(0).server(), roundTripped.servers().get(0).server());
        assertEquals(
                original.servers().get(0).additionalProperties().get("host"),
                roundTripped.servers().get(0).additionalProperties().get("host"));

        JsonNode tree = mapper.readTree(yaml);
        assertEquals("v3.1.0", tree.get("apiVersion").asText());
        assertTrue(tree.has("schema"));
        assertTrue(tree.has("team"));
        assertTrue(tree.has("servers"));
    }

    private static DataContract readContract(String resourcePath) throws Exception {
        try (InputStream in = DataContractModelTest.class.getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(in, "Missing test resource: " + resourcePath);
            return mapper.readValue(in, DataContract.class);
        }
    }
}
