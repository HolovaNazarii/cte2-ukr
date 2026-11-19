package ua.cte2uk.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import ua.cte2uk.Cte2Uk;

/**
 * Англійські оригінали перекладених назв (вміння, ефекти, класи Mine and Slash).
 *
 * <p>Дані — {@code assets/cte2_uk/original_names.json} (ключ -> англійська назва), їх генерує
 * {@code tools/apply_mns_names.py}. Для пошуку в підказках будуємо зворотну мапу «українська назва -> англійська».
 */
public final class OriginalNames {
    private static final String RESOURCE = "/assets/cte2_uk/original_names.json";
    /** Назву шукаємо лише серед перших рядків підказки. */
    public static final int TITLE_LINES = 4;

    private static Map<String, String> byKey;
    private static Object cachedFor;
    private static Map<String, String> byUkrainian = Map.of();

    private OriginalNames() {
    }

    /** Мапа «українська назва -> англійська» для поточних перекладів. Перебудовується після зміни мови. */
    public static synchronized Map<String, String> byUkrainian(Object languageInstance, Function<String, String> translate) {
        if (languageInstance != cachedFor) {
            byUkrainian = reverse(keys(), translate);
            cachedFor = languageInstance;
        }
        return byUkrainian;
    }

    /** Будує «переклад -> оригінал», пропускаючи ключі без перекладу й неоднозначні переклади. */
    public static Map<String, String> reverse(Map<String, String> originals, Function<String, String> translate) {
        Map<String, String> result = new HashMap<>();
        Map<String, Boolean> ambiguous = new HashMap<>();
        for (Map.Entry<String, String> e : originals.entrySet()) {
            String uk = translate.apply(e.getKey());
            if (uk == null || uk.isBlank() || uk.equals(e.getValue())) {
                continue;
            }
            String previous = result.putIfAbsent(uk, e.getValue());
            if (previous != null && !previous.equals(e.getValue())) {
                ambiguous.put(uk, true);
            }
        }
        ambiguous.keySet().forEach(result::remove);
        return Collections.unmodifiableMap(result);
    }

    /**
     * @return індекс рядка з назвою серед перших {@link #TITLE_LINES} або -1
     */
    public static int findTitle(List<String> lines, Map<String, String> ukToEn) {
        for (int i = 0; i < Math.min(lines.size(), TITLE_LINES); i++) {
            String line = lines.get(i);
            if (line != null && ukToEn.containsKey(line.trim())) {
                return i;
            }
        }
        return -1;
    }

    private static synchronized Map<String, String> keys() {
        if (byKey == null) {
            Map<String, String> map = new HashMap<>();
            try (InputStream in = OriginalNames.class.getResourceAsStream(RESOURCE)) {
                if (in != null) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> e : json.entrySet()) {
                        map.put(e.getKey(), e.getValue().getAsString());
                    }
                }
            } catch (Exception e) {
                Cte2Uk.LOG.warn("Could not read {}", RESOURCE, e);
            }
            byKey = Collections.unmodifiableMap(map);
        }
        return byKey;
    }
}
