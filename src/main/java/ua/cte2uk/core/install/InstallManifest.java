package ua.cte2uk.core.install;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Маніфест файлів, які мод кладе в теку гри.
 *
 * <p>Формат: рядки з трьома полями через табуляцію:
 * {@code шлях	sha256_відомого_оригіналу|-	sha256_нашої_версії}.
 * Один шлях може мати кілька рядків (кілька відомих оригіналів).
 * {@code -} означає «файлу може не бути — тоді створити».
 * Порожні рядки та рядки з {@code #} на початку пропускаються.
 */
public final class InstallManifest {
    public static final String ABSENT = "-";

    /** Опис одного файлу. */
    public record Entry(String path, Set<String> knownOriginals, boolean mayBeAbsent, String ourHash) {
    }

    private final Map<String, Entry> entries;

    private InstallManifest(Map<String, Entry> entries) {
        this.entries = Collections.unmodifiableMap(entries);
    }

    public Map<String, Entry> entries() {
        return entries;
    }

    public static InstallManifest parse(InputStream in) throws IOException {
        Map<String, Set<String>> originals = new LinkedHashMap<>();
        Map<String, String> ours = new LinkedHashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        int number = 0;
        while ((line = reader.readLine()) != null) {
            number++;
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] fields = line.split("\t", -1);
            if (fields.length != 3) {
                throw new IOException("Line " + number + ": expected 3 tab-separated fields");
            }
            String path = fields[0].trim().replace('\\', '/');
            String original = normalizeHash(fields[1], true, number);
            String our = normalizeHash(fields[2], false, number);

            String previous = ours.putIfAbsent(path, our);
            if (previous != null && !previous.equals(our)) {
                throw new IOException("Line " + number + ": conflicting bundled hashes for " + path);
            }
            originals.computeIfAbsent(path, p -> new LinkedHashSet<>()).add(original);
        }

        Map<String, Entry> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : ours.entrySet()) {
            Set<String> known = new LinkedHashSet<>(originals.get(e.getKey()));
            boolean mayBeAbsent = known.remove(ABSENT);
            result.put(e.getKey(), new Entry(e.getKey(), Set.copyOf(known), mayBeAbsent, e.getValue()));
        }
        return new InstallManifest(result);
    }

    private static String normalizeHash(String raw, boolean allowAbsent, int line) throws IOException {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (allowAbsent && ABSENT.equals(value)) {
            return ABSENT;
        }
        if (!value.matches("[0-9a-f]{64}")) {
            throw new IOException("Line " + line + ": invalid SHA-256 '" + raw + "'");
        }
        return value;
    }
}
