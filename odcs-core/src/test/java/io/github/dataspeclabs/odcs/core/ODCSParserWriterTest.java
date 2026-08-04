package io.github.dataspeclabs.odcs.core;

import io.github.dataspeclabs.odcs.core.model.v3.DataContract;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ODCSParserWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesFullExampleYamlFromString() throws Exception {
        String yaml = resourceAsString("odcs/full-example.odcs.yaml");
        DataContract contract = ODCSParser.parse(yaml);

        assertEquals("v3.1.0", contract.apiVersion());
        assertEquals("53581432-6c55-4ba2-a65f-72344a91553a", contract.id());
        assertNotNull(contract.schema());
        assertFalse(contract.schema().isEmpty());
    }

    @Test
    void parsesFullExampleYamlFromInputStream() throws Exception {
        try (InputStream in = resourceStream("odcs/full-example.odcs.yaml")) {
            DataContract contract = ODCSParser.parse(in, OdcsFormat.YAML);
            assertEquals("v3.1.0", contract.apiVersion());
            assertEquals("DataContract", contract.kind());
        }
    }

    @Test
    void parsesJsonRoundTripFromWriter() throws Exception {
        DataContract original = ODCSParser.parse(resourceAsString("odcs/full-example.odcs.yaml"));
        String json = ODCSWriter.toJson(original);

        assertTrue(json.trim().startsWith("{"));
        DataContract parsed = ODCSParser.parse(json, OdcsFormat.JSON);

        assertEquals(original.apiVersion(), parsed.apiVersion());
        assertEquals(original.id(), parsed.id());
        assertEquals(original.schema().size(), parsed.schema().size());
    }

    @Test
    void roundTripsYamlPreservingKeyFields() throws Exception {
        DataContract original = ODCSParser.parse(resourceAsString("odcs/full-example.odcs.yaml"));
        String yaml = ODCSWriter.toYaml(original);
        DataContract roundTripped = ODCSParser.parse(yaml);

        assertEquals(original.id(), roundTripped.id());
        assertEquals(original.apiVersion(), roundTripped.apiVersion());
        assertEquals(original.schema().size(), roundTripped.schema().size());
        assertEquals(original.team().members().size(), roundTripped.team().members().size());
    }

    @Test
    void sniffsJsonFormatFromStringWithoutExplicitFormat() {
        String json = """
                {
                  "apiVersion": "v3.1.0",
                  "kind": "DataContract",
                  "id": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
                  "version": "1.0.0",
                  "status": "active"
                }
                """;

        DataContract contract = ODCSParser.parse(json);
        assertEquals("v3.1.0", contract.apiVersion());
        assertEquals("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee", contract.id());
    }

    @Test
    void rejectsV2ApiVersion() {
        String yaml = """
                apiVersion: v2.2.2
                kind: DataContract
                id: legacy-id
                version: 1.0.0
                status: active
                """;

        OdcsParseException ex = assertThrows(OdcsParseException.class, () -> ODCSParser.parse(yaml));
        assertTrue(ex.getMessage().contains("v2.2.2"));
        assertTrue(ex.getMessage().toLowerCase().contains("v3"));
    }

    @Test
    void rejectsMissingApiVersion() {
        String yaml = """
                kind: DataContract
                id: no-api-version
                version: 1.0.0
                status: draft
                """;

        OdcsParseException ex = assertThrows(OdcsParseException.class, () -> ODCSParser.parse(yaml));
        assertTrue(ex.getMessage().toLowerCase().contains("apiversion"));
    }

    @Test
    void parsesDeprecatedTeamArrayFixture() throws Exception {
        DataContract contract = ODCSParser.parse(resourceAsString("odcs/deprecated-team-array.odcs.yaml"));

        assertEquals("v3.0.2", contract.apiVersion());
        assertNotNull(contract.team());
        assertEquals(2, contract.team().members().size());
        assertEquals("alice", contract.team().members().get(0).username());
    }

    @Test
    void writesAndParsesFileByExtension() throws Exception {
        DataContract original = ODCSParser.parse(resourceAsString("odcs/full-example.odcs.yaml"));

        Path yamlPath = tempDir.resolve("contract.odcs.yaml");
        Path jsonPath = tempDir.resolve("contract.json");

        ODCSWriter.write(original, yamlPath, OdcsFormat.YAML);
        ODCSWriter.write(original, jsonPath, OdcsFormat.JSON);

        assertTrue(Files.exists(yamlPath));
        assertTrue(Files.exists(jsonPath));

        DataContract fromYaml = ODCSParser.parse(yamlPath);
        DataContract fromJson = ODCSParser.parse(jsonPath);

        assertEquals(original.id(), fromYaml.id());
        assertEquals(original.id(), fromJson.id());
    }

    @Test
    void rejectsNullOrBlankContent() {
        assertThrows(OdcsParseException.class, () -> ODCSParser.parse((String) null));
        assertThrows(OdcsParseException.class, () -> ODCSParser.parse("   "));
        assertThrows(OdcsParseException.class, () -> ODCSWriter.toYaml(null));
    }

    private static String resourceAsString(String path) throws Exception {
        try (InputStream in = resourceStream(path)) {
            return new String(in.readAllBytes());
        }
    }

    private static InputStream resourceStream(String path) {
        InputStream in = ODCSParserWriterTest.class.getClassLoader().getResourceAsStream(path);
        assertNotNull(in, "Missing test resource: " + path);
        return in;
    }
}
