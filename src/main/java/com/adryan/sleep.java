package com.adryan;

import com.hypixel.hytale.builtin.beds.BedsPlugin;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep.Slumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.StartSlumberSystem;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.logger.HytaleLogger.Api;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.adryan.HyVale;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

class CustomSleepSystem extends DelayedSystem<EntityStore> {

    private static final float INTERVAL_TICK_SEC = 2.0F;

    public CustomSleepSystem() {
        super(INTERVAL_TICK_SEC);
    }

    @Override
    public void delayedTick(float dt, int tick, Store<EntityStore> store) {
        BedsPlugin beds = BedsPlugin.getInstance();
        if (beds == null) {
            return;
        }

        WorldSomnolence worldSomnolence = (WorldSomnolence) store.getResource(beds.getWorldSomnolenceResourceType());

        if (worldSomnolence == null) {
            return;
        }

        WorldTimeResource time = (WorldTimeResource) store.getResource(WorldTimeResource.getResourceType());

        double[] intervalOreSomn = store.getExternalData().getWorld().getGameplayConfig().getWorldConfig().getSleepConfig().getAllowedSleepHoursRange();

        if (intervalOreSomn == null) {
            return;
        }

        int oraCurenta = time.getCurrentHour();
        if (!esteOraDeSomn(oraCurenta, intervalOreSomn)) {
            return;
        }

        AtomicInteger totalJucatori = new AtomicInteger();
        AtomicInteger jucatoriCareDorm = new AtomicInteger();

        store.forEachChunk(Player.getComponentType(), (chunk, cmd) -> {
            for (int i = 0; i < chunk.size(); i++) {
                totalJucatori.incrementAndGet();
                if (StartSlumberSystem.isReadyToSleep(store, chunk.getReferenceTo(i))) {
                    jucatoriCareDorm.incrementAndGet();
                }
            }
        });

        int total = totalJucatori.get();
        int dorm = jucatoriCareDorm.get();

        if (total == 0 || dorm == 0) {
            return;
        }

        float pragProcentSomn = HyVale.getInstance().getSleepConfig().getSleepPercentage();
        float procentActualSomn = (float) dorm / (float) total;

        if (procentActualSomn < pragProcentSomn) {
            return;
        }

        Api logger = HyVale.getInstance().getLogger().at(Level.INFO);
        logger.log("Sărim peste noapte: " + dorm + "/" + total + " jucători dorm (prag: " + (int) (pragProcentSomn * 100.0F) + "%)");

        float oraTrezirii = store.getExternalData().getWorld().getGameplayConfig().getWorldConfig().getSleepConfig().getWakeUpHour();
        double dayTime = oraTrezirii / WorldTimeResource.HOURS_PER_DAY;

        try {
            time.setDayTime(dayTime, store.getExternalData().getWorld(), store);
        } catch (Exception e) {
            HyVale.getInstance().getLogger().at(Level.WARNING).log("Nu am reușit să setez ora dimineții: " + e.getMessage());
            return;
        }

        ComponentType<EntityStore, PlayerSomnolence> tipSomnolenta = beds.getPlayerSomnolenceComponentType();
        PlayerSomnolence stareSomnJucator = Slumber.createComponent(time);

        store.forEachChunk(tipSomnolenta, (chunk, cmd) -> {
            for (int i = 0; i < chunk.size(); i++) {
                cmd.putComponent(chunk.getReferenceTo(i), tipSomnolenta, stareSomnJucator);
            }
        });
    }

    private boolean esteOraDeSomn(int ora, double[] interval) {
        double start = interval[0];
        double end = interval[1];

        if (start > end) {
            return ora >= start || ora < end;
        }

        return ora >= start && ora < end;
    }
}
