package pl.autozmianka;

import net.minecraft.client.MinecraftClient;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public final class AutoZmiankaConfig {
    public int threshold = 50;
    public int clickDelayMs = 500;

    private Path path() {
        return MinecraftClient.getInstance().runDirectory.toPath().resolve("config/autozmianka.properties");
    }

    public void load() {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(path().toFile())) {
            p.load(in);
            threshold = clamp(parse(p.getProperty("threshold"), 50), 1, 100);
            clickDelayMs = clamp(parse(p.getProperty("clickDelayMs"), 500), 200, 2000);
        } catch (IOException ignored) { }
    }

    public void save() {
        Properties p = new Properties();
        p.setProperty("threshold", Integer.toString(threshold));
        p.setProperty("clickDelayMs", Integer.toString(clickDelayMs));
        try {
            File file = path().toFile();
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            try (OutputStream out = new FileOutputStream(file)) {
                p.store(out, "Auto-Zmianka settings");
            }
        } catch (IOException e) {
            System.err.println("[Auto-Zmianka] Błąd zapisu konfiguracji: " + e.getMessage());
        }
    }

    private static int parse(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception ignored) { return fallback; }
    }
    private static int clamp(int n, int min, int max) { return Math.max(min, Math.min(max, n)); }
}
