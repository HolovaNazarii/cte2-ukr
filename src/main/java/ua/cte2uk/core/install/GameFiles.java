package ua.cte2uk.core.install;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import net.minecraftforge.fml.loading.FMLPaths;
import ua.cte2uk.Cte2Uk;

/**
 * Запуск встановлення з ресурсів jar: {@code /cte2_uk/install/manifest.tsv} і файли поруч із ним.
 */
public final class GameFiles {
    private static final String ROOT = "/cte2_uk/install/";

    private GameFiles() {
    }

    public static void install() {
        InstallManifest manifest;
        try (InputStream in = GameFiles.class.getResourceAsStream(ROOT + "manifest.tsv")) {
            if (in == null) {
                Cte2Uk.LOG.error("Bundled manifest {}manifest.tsv is missing", ROOT);
                return;
            }
            manifest = InstallManifest.parse(in);
        } catch (IOException e) {
            Cte2Uk.LOG.error("Could not read install manifest", e);
            return;
        }

        FileInstaller installer = new FileInstaller(
            FMLPaths.GAMEDIR.get(),
            path -> GameFiles.class.getResourceAsStream(ROOT + "files/" + path));
        Map<String, FileInstaller.Outcome> results = installer.install(manifest);

        Map<FileInstaller.Outcome, Integer> summary = new EnumMap<>(FileInstaller.Outcome.class);
        results.values().forEach(o -> summary.merge(o, 1, Integer::sum));
        Cte2Uk.LOG.info("Translation files: {}", summary);
    }
}
