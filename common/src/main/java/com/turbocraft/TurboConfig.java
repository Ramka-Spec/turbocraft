package com.turbocraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Settings stored in config/turbocraft.json. */
public final class TurboConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static TurboConfig instance = new TurboConfig();

    /** Entities farther than this (blocks) are not drawn. Players are always drawn. 0 = off. */
    public int entityRenderDistance = 64;

    /** Mobs farther than this (blocks) from every player think less often. 0 = off. */
    public int farMobDistance = 48;

    /** Far mobs run their AI once every N ticks (1 = every tick, like vanilla). */
    public int farMobAiInterval = 4;

    /** Particles spawned farther than this (blocks) from the player are skipped. 0 = off. */
    public int particleDistance = 24;

    // Precomputed squares, not saved
    public transient double entityRenderDistanceSq;
    public transient double particleDistanceSq;

    public static TurboConfig get() {
        return instance;
    }

    public static void load() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve(TurboCraft.MOD_ID + ".json");
        TurboConfig cfg = null;
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                cfg = GSON.fromJson(r, TurboConfig.class);
            } catch (Exception e) {
                TurboCraft.LOG.warn("Bad config {}, using defaults", file, e);
            }
        }
        if (cfg == null) cfg = new TurboConfig();

        cfg.entityRenderDistance = Math.max(0, cfg.entityRenderDistance);
        cfg.farMobDistance = Math.max(0, cfg.farMobDistance);
        cfg.farMobAiInterval = Math.max(1, cfg.farMobAiInterval);
        cfg.particleDistance = Math.max(0, cfg.particleDistance);
        cfg.particleDistanceSq = (double) cfg.particleDistance * cfg.particleDistance;
        cfg.entityRenderDistanceSq = (double) cfg.entityRenderDistance * cfg.entityRenderDistance;
        instance = cfg;

        try {
            Files.createDirectories(file.getParent());
            try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(cfg, w);
            }
        } catch (Exception e) {
            TurboCraft.LOG.warn("Could not save config {}", file, e);
        }
    }
}
