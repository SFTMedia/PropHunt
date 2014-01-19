package me.tomski.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PropHuntHiderDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String player;
    private final int hidersLeftCount;

    public PropHuntHiderDeathEvent(String player, int hidersLeftCount) {
        this.player = player;
        this.hidersLeftCount = hidersLeftCount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     *
     * @return int the number of hiders left in the arena
     */
    public int getHidersLeftCount() {
        return hidersLeftCount;
    }

    /**
     *
     * @return String name of the now dead hider.
     */
    public String getPlayerName() {
        return player;
    }

}
