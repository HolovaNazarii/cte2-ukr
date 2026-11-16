package ua.cte2uk.core.install;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.cte2uk.core.install.FileInstaller.Outcome;

class FileInstallerTest {
    private static final String ORIGINAL = "english";
    private static final String OURS = "українська";
    private static final String PATH = "config/a.txt";

    @TempDir
    Path game;

    private static String sha(String text) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
    }

    private static InstallManifest manifest(String... rows) throws IOException {
        return InstallManifest.parse(new ByteArrayInputStream(String.join("\n", rows).getBytes(StandardCharsets.UTF_8)));
    }

    private Map<String, Outcome> run(InstallManifest manifest, String bundled) {
        return new FileInstaller(game, p -> new ByteArrayInputStream(bundled.getBytes(StandardCharsets.UTF_8))).install(manifest);
    }

    private void write(String text) throws IOException {
        Files.createDirectories(game.resolve(PATH).getParent());
        Files.writeString(game.resolve(PATH), text);
    }

    private String read() throws IOException {
        return Files.readString(game.resolve(PATH));
    }

    @Test
    void replacesKnownOriginal() throws Exception {
        write(ORIGINAL);
        var result = run(manifest(PATH + "\t" + sha(ORIGINAL) + "\t" + sha(OURS)), OURS);
        assertEquals(Outcome.INSTALLED, result.get(PATH));
        assertEquals(OURS, read());
    }

    @Test
    void keepsFileModifiedByPlayer() throws Exception {
        write("правки гравця");
        var result = run(manifest(PATH + "\t" + sha(ORIGINAL) + "\t" + sha(OURS)), OURS);
        assertEquals(Outcome.SKIPPED_MODIFIED, result.get(PATH));
        assertEquals("правки гравця", read());
    }

    @Test
    void alreadyInstalledIsLeftAlone() throws Exception {
        write(OURS);
        assertEquals(Outcome.UP_TO_DATE, run(manifest(PATH + "\t" + sha(ORIGINAL) + "\t" + sha(OURS)), OURS).get(PATH));
    }

    @Test
    void createsMissingFileOnlyWhenAllowed() throws Exception {
        assertEquals(Outcome.SKIPPED_MISSING, run(manifest(PATH + "\t" + sha(ORIGINAL) + "\t" + sha(OURS)), OURS).get(PATH));
        assertFalse(Files.exists(game.resolve(PATH)));

        assertEquals(Outcome.INSTALLED, run(manifest(PATH + "\t-\t" + sha(OURS)), OURS).get(PATH));
        assertEquals(OURS, read());
    }

    @Test
    void acceptsAnyOfSeveralOriginals() throws Exception {
        write("стара версія перекладу");
        var m = manifest(
            "# коментар",
            PATH + "\t" + sha(ORIGINAL) + "\t" + sha(OURS),
            PATH + "\t" + sha("стара версія перекладу") + "\t" + sha(OURS));
        assertEquals(Outcome.INSTALLED, run(m, OURS).get(PATH));
    }

    @Test
    void wrongBundledContentKeepsOldFile() throws Exception {
        write(ORIGINAL);
        var result = run(manifest(PATH + "\t" + sha(ORIGINAL) + "\t" + sha(OURS)), "пошкоджений вміст");
        assertEquals(Outcome.FAILED, result.get(PATH));
        assertEquals(ORIGINAL, read());
        try (var files = Files.list(game.resolve("config"))) {
            assertEquals(1, files.count(), "тимчасовий файл має бути видалений");
        }
    }

    @Test
    void rejectsPathsOutsideGameDir() throws Exception {
        var result = run(manifest("../escape.txt\t-\t" + sha(OURS)), OURS);
        assertEquals(Outcome.FAILED, result.get("../escape.txt"));
        assertFalse(Files.exists(game.resolveSibling("escape.txt")));
    }

    @Test
    void rejectsMalformedManifest() {
        assertThrows(IOException.class, () -> manifest("only\ttwo"));
        assertThrows(IOException.class, () -> manifest("a\tnot-a-hash\t" + "0".repeat(64)));
    }

    @Test
    void bundledManifestMatchesBundledFiles() throws Exception {
        Path root = Path.of("src/main/resources/cte2_uk/install");
        InstallManifest m;
        try (var in = Files.newInputStream(root.resolve("manifest.tsv"))) {
            m = InstallManifest.parse(in);
        }
        assertFalse(m.entries().isEmpty());
        for (var e : m.entries().values()) {
            Path file = root.resolve("files").resolve(e.path());
            assertTrue(Files.isRegularFile(file), "немає файлу " + e.path());
            assertEquals(e.ourHash(), FileInstaller.sha256(file), "хеш у маніфесті застарів: " + e.path());
        }
    }
}
