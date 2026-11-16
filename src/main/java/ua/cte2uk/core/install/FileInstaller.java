package ua.cte2uk.core.install;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Кладе українські версії файлів у теку гри за {@link InstallManifest}.
 *
 * <p>Файл замінюється тільки тоді, коли в грі лежить відомий оригінал (або файлу немає і це дозволено).
 * Змінені гравцем файли не чіпаємо. Новий вміст спершу пишеться в тимчасовий файл і перевіряється,
 * тож навіть при збої старий файл лишається цілим.
 */
public final class FileInstaller {
    private static final Logger LOG = LoggerFactory.getLogger("cte2_uk/install");

    /** Що сталося з файлом. */
    public enum Outcome { UP_TO_DATE, INSTALLED, SKIPPED_MODIFIED, SKIPPED_MISSING, FAILED }

    /** Джерело вмісту за шляхом із маніфесту. */
    @FunctionalInterface
    public interface Source {
        InputStream open(String path) throws IOException;
    }

    private final Path gameDir;
    private final Source source;

    public FileInstaller(Path gameDir, Source source) {
        this.gameDir = gameDir.toAbsolutePath().normalize();
        this.source = source;
    }

    /** Обробляє всі файли; помилка одного не зупиняє решту. */
    public Map<String, Outcome> install(InstallManifest manifest) {
        Map<String, Outcome> results = new LinkedHashMap<>();
        for (InstallManifest.Entry entry : manifest.entries().values()) {
            Outcome outcome;
            try {
                outcome = installOne(entry);
            } catch (IOException | RuntimeException e) {
                LOG.error("Failed to install {}", entry.path(), e);
                outcome = Outcome.FAILED;
            }
            results.put(entry.path(), outcome);
        }
        return results;
    }

    private Outcome installOne(InstallManifest.Entry entry) throws IOException {
        Path target = resolveInsideGame(entry.path());

        if (Files.isRegularFile(target)) {
            String current = sha256(target);
            if (current.equals(entry.ourHash())) {
                return Outcome.UP_TO_DATE;
            }
            if (!entry.knownOriginals().contains(current)) {
                LOG.warn("Skipping {}: file was modified (does not match a known CTE2 version)", entry.path());
                return Outcome.SKIPPED_MODIFIED;
            }
        } else if (Files.exists(target)) {
            throw new IOException("Not a regular file: " + target);
        } else if (!entry.mayBeAbsent()) {
            LOG.warn("Skipping {}: file is missing from the modpack", entry.path());
            return Outcome.SKIPPED_MISSING;
        }

        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), ".cte2_uk-", ".tmp");
        try {
            try (InputStream in = source.open(entry.path())) {
                if (in == null) {
                    throw new IOException("Missing bundled file " + entry.path());
                }
                Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            String written = sha256(temp);
            if (!written.equals(entry.ourHash())) {
                throw new IOException("Bundled file " + entry.path() + " does not match manifest hash: " + written);
            }
            move(temp, target);
        } finally {
            Files.deleteIfExists(temp);
        }
        LOG.info("Installed {}", entry.path());
        return Outcome.INSTALLED;
    }

    private Path resolveInsideGame(String relative) throws IOException {
        Path rel = Path.of(relative);
        if (rel.isAbsolute()) {
            throw new IOException("Absolute path in manifest: " + relative);
        }
        Path target = gameDir.resolve(rel).normalize();
        if (!target.startsWith(gameDir) || target.equals(gameDir)) {
            throw new IOException("Path escapes the game directory: " + relative);
        }
        return target;
    }

    private static void move(Path from, Path to) throws IOException {
        try {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static String sha256(Path file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
