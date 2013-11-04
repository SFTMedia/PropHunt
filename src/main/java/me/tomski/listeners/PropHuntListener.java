package me.tomski.listeners;


import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.tomski.arenas.ArenaManager;
import me.tomski.blocks.SolidBlock;
import me.tomski.bungee.Pinger;
import me.tomski.language.LanguageManager;
import me.tomski.language.MessageBank;
import me.tomski.prophunt.*;
import me.tomski.utils.ItemMessage;
import me.tomski.utils.PropHuntMessaging;
import me.tomski.utils.Reason;
import me.tomski.utils.SolidBlockTracker;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropHuntListener implements Listener {

    public static List<Player> tempIgnoreUndisguise = new ArrayList<Player>();
    private GameManager GM = null;
    private PropHunt PH = null;
    private List<String> allowedcmds = new ArrayList<String>();
    public static Map<Player, Integer> playerOnBlocks = new HashMap<Player, Integer>();

    public PropHuntListener(PropHunt plugin, GameManager Gamem) {
        this.PH = plugin;
        this.GM = Gamem;
        allowedcmds.add("/ph leave");
        allowedcmds.add("/ph status");
        allowedcmds.add("/ph balance");
        allowedcmds.add("/ph shop");
        allowedcmds.add("/ph chooser");
        allowedcmds.add("/ph balance");
        allowedcmds.add("/prophunt leave");
        allowedcmds.add("/prophunt status");
        allowedcmds.add("/prophunt balance");
        allowedcmds.add("/prophunt shop");
        allowedcmds.add("/prophunt chooser");
        allowedcmds.add("/prophunt balance");
        allowedcmds.add("/prophunt start");
        allowedcmds.add("/prophunt stop");
        allowedcmds.add("/ph start");
        allowedcmds.add("/ph stop");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            if (cancelItemUse(e)) {
                e.setCancelled(true);
            }
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            if (cancelItemUse(e)) {
                e.setCancelled(true);
            }
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            if (cancelItemUse(e)) {
                e.setCancelled(true);
            }
        }
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            if (cancelItemUse(e)) {
                e.setCancelled(true);
            }
        }
    }

    private boolean cancelItemUse(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            switch (e.getClickedBlock().getType()) {
                case ENDER_CHEST:
                    return true;
                case CHEST:
                    return true;
                case STORAGE_MINECART:
                    return true;
                case LOCKED_CHEST:
                    return true;
                case TRAPPED_CHEST:
                    return true;
                case DISPENSER:
                    return true;
                case POWERED_MINECART:
                    return true;
                case ANVIL:
                    return true;
                case BREWING_STAND:
                    return true;
                case HOPPER:
                    return true;
                case HOPPER_MINECART:
                    return true;
                case DROPPER:
                    return true;
                case BEACON:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @EventHandler
    public void playerKickEvent(PlayerKickEvent e) {
        if (e.getReason().contains("Flying")) {
            if (GameManager.hiders.contains(e.getPlayer().getName())) {
                if (playerOnBlocks.containsKey(e.getPlayer())) {
                    e.setCancelled(true);
                    return;
                }
                int x = e.getPlayer().getLocation().getBlockX();
                int y = e.getPlayer().getLocation().getBlockY() - 1;
                int z = e.getPlayer().getLocation().getBlockZ();
                for (SolidBlock s : SolidBlockTracker.solidBlocks.values()) {
                    if (s.loc.getBlockX() < x + 2 && s.loc.getBlockX() > x - 2) {
                        if (s.loc.getBlockY() < y + 2 && s.loc.getBlockY() > y - 2) {
                            if (s.loc.getBlockZ() < z + 2 && s.loc.getBlockZ() > z - 2) {
                                if (!playerOnBlocks.containsKey(e.getPlayer())) {
                                    playerOnBlocks.put(e.getPlayer(), 20);
                                }
                                e.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
            if (GameManager.seekers.contains(e.getPlayer().getName())) {
                if (playerOnBlocks.containsKey(e.getPlayer())) {
                    e.setCancelled(true);
                    return;
                }
                int x = e.getPlayer().getLocation().getBlockX();
                int y = e.getPlayer().getLocation().getBlockY() - 1;
                int z = e.getPlayer().getLocation().getBlockZ();
                for (SolidBlock s : SolidBlockTracker.solidBlocks.values()) {
                    if (s.loc.getBlockX() < x + 2 && s.loc.getBlockX() > x - 2) {
                        if (s.loc.getBlockY() < y + 2 && s.loc.getBlockY() > y - 2) {
                            if (s.loc.getBlockZ() < z + 2 && s.loc.getBlockZ() > z - 2) {
                                if (!playerOnBlocks.containsKey(e.getPlayer())) {
                                    playerOnBlocks.put(e.getPlayer(), 20);
                                }
                                e.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCrouchEvent(PlayerToggleSneakEvent e) {
        if (GameManager.crouchBlockLock) {
            if (GameManager.hiders.contains(e.getPlayer().getName())) {
                if (SolidBlockTracker.solidBlocks.containsKey(e.getPlayer().getName())) {
                    return;
                }
                if (PH.dm.isDisguised(e.getPlayer())) {
                        PH.dm.toggleBlockLock(e);
                }
            }
        }
    }


    @EventHandler
    public void onPLayerDrop(PlayerDropItemEvent e) {
        if (GameManager.hiders.contains(e.getPlayer().getName()) || GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_ITEM_SHARING.getMsg());
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("prophunt.admin.commandoverride")) {
            return;
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            if (!allowedcmds.contains(e.getMessage().toLowerCase())) {
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_GAME_COMMANDS.getMsg());
                e.setCancelled(true);
            }
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            if (!allowedcmds.contains(e.getMessage().toLowerCase())) {
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_GAME_COMMANDS.getMsg());
                e.setCancelled(true);
            }
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            if (!allowedcmds.contains(e.getMessage().toLowerCase())) {
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_GAME_COMMANDS.getMsg());
                e.setCancelled(true);
            }
        }
    }

    private void refreshDisguises() {
        PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

            @Override
            public void run() {
                for (Player p : PH.getServer().getOnlinePlayers()) {
                    if (p.isOnline() && PH.dm.isDisguised(p)) {
                        if (GameManager.seekers.contains(p.getName())) {
                            PH.dm.undisguisePlayer(p);
                        }
                    }
                }
            }
        }, 20L);

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(final PlayerRespawnEvent e) {

        if (GameManager.playersQuit.contains(e.getPlayer().getName())) {
            e.setRespawnLocation(GameManager.currentGameArena.getExitSpawn());
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    PlayerManagement.gameRestorePlayer(e.getPlayer());
                    if (PropHunt.usingTABAPI) {
                        GameManager.SB.removeTab(e.getPlayer());
                    }
                    if (GameManager.useSideStats) {
                        PH.SBS.removeScoreboard(PH, e.getPlayer());
                    }

                }
            }, 20L);
            GameManager.playersQuit.remove(e.getPlayer().getName());
            refreshDisguises();
            if (PH.dm.isDisguised(e.getPlayer())) {
                PH.dm.undisguisePlayer(e.getPlayer());
                return;
            }
        }
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            PH.SBS.addPlayerToGame(PH, e.getPlayer());
            e.setRespawnLocation(GameManager.currentGameArena.getSpectatorSpawn());
            if (PH.dm.isDisguised(e.getPlayer())) {
                PH.dm.undisguisePlayer(e.getPlayer());
            }
            refreshDisguises();
            return;
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setRespawnLocation(GameManager.currentGameArena.getSeekerSpawn());
            if (GameManager.seekerDelayTime != 0) {
                if (GameManager.sd.isDelaying) {
                    GameManager.sd.addPlayer(e.getPlayer());
                }
            }
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    PH.showPlayer(e.getPlayer(), false);

                    if (PH.dm.isDisguised(e.getPlayer())) {
                        PH.dm.undisguisePlayer(e.getPlayer());
                    }
                    ArenaManager.arenaConfigs.get(GameManager.currentGameArena).getArenaSeekerClass().givePlayer(e.getPlayer());
                    PH.SBS.addPlayerToGame(PH, e.getPlayer());

                }
            }, 20L);
            refreshDisguises();
            return;
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            PH.SBS.addPlayerToGame(PH, e.getPlayer());
            e.setRespawnLocation(GameManager.currentGameArena.getSeekerSpawn());
            ArenaManager.arenaConfigs.get(GameManager.currentGameArena).getArenaHiderClass().givePlayer(e.getPlayer());
            refreshDisguises();
            return;
        }
        if (PH.dm.isDisguised(e.getPlayer())) {
            PH.dm.undisguisePlayer(e.getPlayer());
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) throws IllegalAccessException, InvocationTargetException, IOException {
        if (GameManager.hiders.contains(e.getEntity().getName())) {
            if (e.getEntity().getKiller() != null) {
                if (ShopSettings.enabled) {
                    giveHiderKillWinnings(e.getEntity().getKiller());
                }
            }
            if (ShopSettings.enabled) {
                giveHiderBonusTimeWinnings(e.getEntity());
            }
            e.getDrops().clear();
            if (isLastHider()) {
                if (GameManager.useSideStats) {
                    PH.SBS.removeScoreboard(PH, e.getEntity());
                }
                GameManager.playersQuit.add(e.getEntity().getName());
                GameManager.hiders.remove(e.getEntity().getName());
                respawnQuick(e.getEntity());
                GM.endGame(Reason.SEEKERWON, false);
                return;
            }
            GameManager.hiders.remove(e.getEntity().getName());
            GameManager.seekers.add(e.getEntity().getName());
            GameManager.seekerLives.put(e.getEntity(), GameManager.seekerLivesAmount);
            if (SolidBlockTracker.solidBlocks.containsKey(e.getEntity().getName())) {
                SolidBlockTracker.solidBlocks.get(e.getEntity().getName()).unSetBlock(PH);
            }
            respawnQuick(e.getEntity());
            PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, e.getEntity().getName() + MessageBank.HIDER_DEATH_MESSAGE.getMsg());
            GameManager.GT.timeleft = GameManager.GT.timeleft + GameManager.time_reward;
            if (GameManager.time_reward != 0) {
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, MessageBank.TIME_INCREASE_MESSAGE.getMsg() + GameManager.time_reward);
            }
            return;
        } else if (GameManager.seekers.contains(e.getEntity().getName())) {
            if (e.getEntity().getKiller() != null) {
                if (ShopSettings.enabled) {
                    giveSeekerKillWinnings(e.getEntity().getKiller());
                }
            }
            e.getDrops().clear();
            if (isLastSeeker()) {
                if (GameManager.useSideStats) {
                    PH.SBS.removeScoreboard(PH, e.getEntity());
                }
                if (GameManager.chooseNewSeeker && GameManager.firstSeeker.equalsIgnoreCase(e.getEntity().getName())) {
                    GameManager.playersQuit.add(e.getEntity().getName());
                    GameManager.seekers.remove(e.getEntity().getName());
                    respawnQuick(e.getEntity());
                    if (GM.chooseNewSeekerMeth()) {
                        return;
                    } else {
                        GM.endGame(Reason.HIDERSWON, false);
                        return;
                    }
                }
                if (noLivesLeft(e.getEntity())) {
                    GameManager.playersQuit.add(e.getEntity().getName());
                    GameManager.seekers.remove(e.getEntity().getName());
                    respawnQuick(e.getEntity());
                    GM.endGame(Reason.HIDERSWON, false);
                    return;
                } else {
                    String msg = MessageBank.SEEKER_LIVES_MESSAGE.getMsg();
                    msg = LanguageManager.regex(msg, "\\{seeker\\}", e.getEntity().getName());
                    msg = LanguageManager.regex(msg, "\\{lives\\}", GameManager.seekerLives.get(e.getEntity()).toString());
                    PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, msg);
                    respawnQuick(e.getEntity());
                    return;
                }
            }
            if (noLivesLeft(e.getEntity())) {
                if (GameManager.useSideStats) {
                    PH.SBS.removeScoreboard(PH, e.getEntity());
                }
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, MessageBank.SEEKER_DEATH_MESSAGE.getMsg());
                GameManager.spectators.add(e.getEntity().getName());
                GameManager.seekers.remove(e.getEntity().getName());
                respawnQuick(e.getEntity());

            } else {
                String msg = MessageBank.SEEKER_LIVES_MESSAGE.getMsg();
                msg = LanguageManager.regex(msg, "\\{seeker\\}", e.getEntity().getName());
                msg = LanguageManager.regex(msg, "\\{lives\\}", GameManager.seekerLives.get(e.getEntity()).toString());
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, msg);
                respawnQuick(e.getEntity());
            }
        }
    }

    private void giveCredits(Player p, double amount) {
        if (amount <= 0) {
            return;
        }
        switch (ShopSettings.economyType) {
            case PROPHUNT:
                int credits = PH.SQL.getCredits(p.getName());
                credits += (int) amount;
                PH.SQL.setCredits(p.getName(), credits);
                break;
            case VAULT:
                double vaultCredits  = PH.vaultUtils.economy.getBalance(p.getName());
                vaultCredits += amount;
                PH.vaultUtils.economy.bankDeposit(p.getName(), vaultCredits);
                break;
        }
        ItemMessage im = new ItemMessage(PH);
        String message = MessageBank.CREDITS_EARN_POPUP.getMsg();
        message = message.replace("\\{credits\\}", amount + " " + ShopSettings.currencyName);
        im.sendMessage(p, ChatColor.translateAlternateColorCodes('&', message));
    }

    private void giveHiderKillWinnings(Player p) {
        giveCredits(p, ShopSettings.pricePerHiderKill);
    }

    private void giveHiderBonusTimeWinnings(Player p) {
        double bonusTime = (System.currentTimeMillis()-GM.gameStartTime)/1000;
        bonusTime *= ShopSettings.pricePerSecondsHidden;
        giveCredits(p, bonusTime);
    }

    private void giveSeekerKillWinnings(Player p) {
        giveCredits(p, ShopSettings.pricePerSeekerKill);
    }

    private boolean noLivesLeft(Player p) {
        if (GameManager.seekerLives.get(p) <= 1) {
            return true;
        } else {
            GameManager.seekerLives.put(p, GameManager.seekerLives.get(p) - 1);
            return false;
        }
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e) {
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e) {
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
    }

    private boolean isLastHider() {
        return GameManager.hiders.size() == 1;
    }

    private boolean isLastSeeker() {
        return GameManager.seekers.size() == 1;
    }

    private void respawnQuick(final Player player) {
        PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

            @Override
            public void run() {
                PacketContainer packet = new PacketContainer(Packets.Client.CLIENT_COMMAND);
                packet.getIntegers().write(0, 1);

                try {
                    ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot recieve packet.", e);
                }

            }
        }, 5L);

    }

    private void playHitMarkerEffect(Location loc) {
        if (GameManager.usingHitmarkers) {
            loc.setY(loc.getY() + 1);
            loc.getWorld().playEffect(loc, Effect.POTION_BREAK, 19);
        }
    }

    private void playerHitSoundEffect(Location loc) {
        if (GameManager.usingHitsounds) {
            loc.getWorld().playSound(loc, Sound.ORB_PICKUP, 1, 1);
        }
    }

    @EventHandler
    public void playerDamange(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player defend = (Player) e.getEntity();
            Player attacker = (Player) e.getDamager();
            if (GameManager.hiders.contains(defend.getName()) && GameManager.hiders.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.seekers.contains(defend.getName()) && GameManager.seekers.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.spectators.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.playersWaiting.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.hiders.contains(defend.getName())) {
                playHitMarkerEffect(e.getEntity().getLocation());
                playerHitSoundEffect(e.getEntity().getLocation());
            }
        }
        if (e.getDamager() instanceof Projectile) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                Player attacker = (Player) ((Projectile) e.getDamager()).getShooter();
                if (e.getEntity() instanceof Player) {
                    Player defend = (Player) e.getEntity();
                    if (GameManager.hiders.contains(defend.getName()) && GameManager.hiders.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.seekers.contains(defend.getName()) && GameManager.seekers.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.spectators.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.playersWaiting.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.hiders.contains(defend.getName())) {
                        playHitMarkerEffect(e.getEntity().getLocation());
                        playerHitSoundEffect(e.getEntity().getLocation());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onlogin(final PlayerJoinEvent e) {
        if (GameManager.dedicated) {
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    GM.addPlayerToGame(e.getPlayer().getName());
                }
            }, 10L);
        }
        if (GameManager.playersQuit.contains(e.getPlayer().getName())) {
            GM.teleportToExit(e.getPlayer(), false);
            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.QUIT_GAME_MESSAGE.getMsg());
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    PlayerManagement.gameRestorePlayer(e.getPlayer());

                }
            }, 20L);
            GameManager.playersQuit.remove(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) throws IOException {
        if (GameManager.useSideStats) {
            PH.SBS.removeScoreboard(PH, e.getPlayer());
        }
        if (BungeeSettings.usingBungee && PH.getServer().getOnlinePlayers().length == 1) {
            final Pinger p = new Pinger(PH);
            p.sentData = true;
            p.sendServerDataEmpty();
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    p.sentData = false;
                }
            }, 20L);
        }
        if (GameManager.dedicated) {
            if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
                GameManager.playersWaiting.remove(e.getPlayer().getName());
            }
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            GameManager.playersWaiting.remove(e.getPlayer().getName());
            GameManager.playersQuit.add(e.getPlayer().getName());
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            GM.kickPlayer(e.getPlayer().getName(), true);
            GameManager.playersQuit.add(e.getPlayer().getName());
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            GM.kickPlayer(e.getPlayer().getName(), true);
            GameManager.playersQuit.add(e.getPlayer().getName());
        }

    }

}
