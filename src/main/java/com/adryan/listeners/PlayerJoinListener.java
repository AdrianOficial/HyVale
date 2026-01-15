package com.adryan.listeners;

import com.adryan.HyVale;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;

import java.awt.Color;

public class PlayerJoinListener {
    
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
	
    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        String name = player.getDisplayName();

        String msg = "Bine ai venit pe server, " + name + "!";
        player.sendMessage(
            Message.join(
                Message.raw("[HyVale] ").color(Color.MAGENTA),
                Message.raw(" " + msg).color(Color.WHITE)
            )
        );
    }
	
    public static void onPlayerJoinWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(false);
    }
}
