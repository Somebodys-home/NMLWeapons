package io.github.NoOne.nMLWeapons;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class AttackCooldownSystem {
    private static NMLWeapons nmlWeapons;
    private BukkitTask playerAttackCooldownTask;
    private static final HashMap<UUID, BossBar> attackCooldownBars = new HashMap<>(); // the individual bars for every player
    private static final HashMap<UUID, BukkitTask> ongoingCooldownTasks = new HashMap<>(); // the currently running cooldown tasks
    private static final HashMap<UUID, Double> cooldownSeconds = new HashMap<>(); // how long every cooldown is going for
    private static final HashMap<UUID, Double> pausedCooldownPlayers = new HashMap<>(); // all the players who has their cooldown timer paused and how much time there was left

    public AttackCooldownSystem(NMLWeapons nmlWeapons) {
        this.nmlWeapons = nmlWeapons;
    }

    public void start() { // main task that runs every tick on the server
        playerAttackCooldownTask = Bukkit.getScheduler().runTaskTimer(nmlWeapons, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                BukkitTask cooldownTask = ongoingCooldownTasks.get(uuid); // gets the attack cooldown task of that player

                // give every online player that doesnt have an attack cooldown bar one
                BossBar bar = attackCooldownBars.computeIfAbsent(uuid, id -> {
                    BossBar newBar = Bukkit.createBossBar("§cAttack Cooldown", BarColor.RED, BarStyle.SOLID);
                    newBar.setProgress(0);

                    return newBar;
                });

                if (cooldownTask != null || pausedCooldownPlayers.containsKey(uuid)) {
                    bar.addPlayer(player);
                } else {
                    bar.removePlayer(player);
                }
            }
        }, 0L, 1L);
    }

    public void stop() {
        if (playerAttackCooldownTask != null) {
            playerAttackCooldownTask.cancel();
        }

        for (BukkitTask task : ongoingCooldownTasks.values()) {
            if (task != null) task.cancel();
        }
        ongoingCooldownTasks.clear();

        for (BossBar bar : attackCooldownBars.values()) {
            bar.removeAll();
        }
        attackCooldownBars.clear();
    }

    public static void setAttackCooldown(Player player, double cooldown) {
        UUID uuid = player.getUniqueId();

        if (ongoingCooldownTasks.containsKey(uuid)) return;

        BossBar attackCooldownBar = attackCooldownBars.get(uuid);
        attackCooldownBar.setProgress(1);

        int totalTicks = (int) Math.ceil(cooldown * 20);
        double decrement = 1.0 / totalTicks;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(nmlWeapons, () -> {
            double progress = attackCooldownBar.getProgress() - decrement;

            if (progress <= 0) {
                BukkitTask t = ongoingCooldownTasks.remove(uuid);

                attackCooldownBar.setProgress(0);
                cooldownSeconds.remove(uuid);

                if (t != null) {
                    t.cancel();
                }

                return;
            }

            attackCooldownBar.setProgress(progress);
        }, 0L, 1L);

        ongoingCooldownTasks.put(uuid, task);
        cooldownSeconds.put(uuid, cooldown);
    }

    public static void pauseAttackCooldown(Player player) {
        UUID uuid = player.getUniqueId();

        if (ongoingCooldownTasks.containsKey(uuid)) {
            BukkitTask cooldownTask = ongoingCooldownTasks.remove(uuid);
            BossBar bar = attackCooldownBars.get(uuid);

            pausedCooldownPlayers.put(uuid, calculateRemainingCooldownTime(player));
            cooldownTask.cancel();
            bar.setTitle("§eAttack Cooldown");
            bar.setColor(BarColor.YELLOW);
        } else {
            setAttackCooldown(player, .1);
            pauseAttackCooldown(player);
        }
    }

    public static void pauseAttackCooldown(Player player, double pauseTime) {
        pauseAttackCooldown(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                resumeAttackCooldown(player);
            }
        }.runTaskLater(nmlWeapons, (long) (pauseTime * 20));
    }

    public static void resumeAttackCooldown(Player player) {
        UUID uuid = player.getUniqueId();

        if (!pausedCooldownPlayers.containsKey(uuid)) {
            return;
        }

        double pausedTime = pausedCooldownPlayers.get(uuid);
        BossBar attackCooldownBar = attackCooldownBars.get(uuid);

        attackCooldownBar.setTitle("§cAttack Cooldown");
        attackCooldownBar.setColor(BarColor.RED);
        pausedCooldownPlayers.remove(uuid);

        /// the same as starting attack cooldown
        /// we just use the pausedTime variable
        int totalTicks = (int) Math.ceil(cooldownSeconds.get(uuid) * 20);
        double decrement = 1.0 / totalTicks;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(nmlWeapons, () -> {
            double progress = attackCooldownBar.getProgress() - decrement;

            if (progress <= 0) {
                BukkitTask t = ongoingCooldownTasks.remove(uuid);

                attackCooldownBar.setProgress(0);
                cooldownSeconds.remove(uuid);

                if (t != null) {
                    t.cancel();
                }

                return;
            }

            attackCooldownBar.setProgress(progress);
        }, 0L, 1L);

        ongoingCooldownTasks.put(uuid, task);
        cooldownSeconds.put(uuid, pausedTime);
    }

    public static boolean isOnAttackCooldown(Player player) {
        return ongoingCooldownTasks.containsKey(player.getUniqueId());
    }

    private static double calculateRemainingCooldownTime(Player player) {
        UUID uuid = player.getUniqueId();
        BossBar bar = attackCooldownBars.get(uuid);
        double cooldownTimer = cooldownSeconds.get(uuid);

        return bar.getProgress() * cooldownTimer;
    }
}