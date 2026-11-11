package com.turbocraft;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurboCraft implements ModInitializer {
    public static final String MOD_ID = "turbocraft";
    public static final Logger LOG = LoggerFactory.getLogger("TurboCraft");

    @Override
    public void onInitialize() {
        TurboConfig.load();
        LOG.info("TurboCraft loaded: entity render distance {} blocks, far-mob AI every {} ticks beyond {} blocks",
                TurboConfig.get().entityRenderDistance,
                TurboConfig.get().farMobAiInterval,
                TurboConfig.get().farMobDistance);
    }
}
