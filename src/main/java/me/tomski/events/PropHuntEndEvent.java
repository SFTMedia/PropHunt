package me.tomski.events;

import me.tomski.utils.Reason;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;


public class PropHuntEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Reason reason;
    private List<String> seekers;
    private List<String> hiders;
    private List<String> spectators;

    public PropHuntEndEvent(Reason reason, List<String> seekers, List<String> hiders, List<String> spectators) {
        this.reason = reason;
        this.seekers = seekers;
        this.hiders = hiders;
        this.spectators = spectators;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Reason getReason() {
        return reason;
    }

    /**
     *
     * @return String list of spectators, they may not be online, be careful!
     */
    public List<String> getSpectators() {
        return spectators;
    }

    /**
     *
     * @return String list of seekers, they may not be online, be careful!
     */
    public List<String> getSeekers() {
        return seekers;
    }

    /**
     *
     * @return String list of hiders, they may not be online, be careful!
     */
    public List<String> getHiders() {
        return hiders;
    }

}
