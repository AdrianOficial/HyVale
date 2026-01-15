package com.adryan;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.adryan.listeners.PlayerJoinListener;
import com.adryan.CustomSleepSystem;
import com.adryan.config.SleepConfig;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HyVale extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static HyVale instance;
	private final SleepConfig sleepConfig = new SleepConfig();

    public HyVale(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("HyVale v" + this.getManifest().getVersion().toString() + " initialized!");
    }
    
    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up HyVale...");
		
		this.getEntityStoreRegistry().registerSystem(new CustomSleepSystem());
		
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerJoinListener::onPlayerReady);
        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, PlayerJoinListener::onPlayerJoinWorld);
        
        LOGGER.atInfo().log("HyVale setup complete! (sleep percent: " + (int) (sleepConfig.getSleepPercentage() * 100) + "%)");
    }

    public static HyVale getInstance() {
        return instance;
    }
	
    public SleepConfig getSleepConfig() {
        return this.sleepConfig;
    }
}
