package io.github.NoOne.nMLWeapons;

import io.github.NoOne.damagePlugin.customDamage.DamageType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArrowTracker {
    private static NMLWeapons nmlWeapons = NMLWeapons.getInstance();
    public static ArrayList<Arrow> flyingArrows = new ArrayList<>();
    public static HashMap<Arrow, Integer> arrowTimers = new HashMap<>();
    public static HashMap<Arrow, Integer> arrowTimeSaves = new HashMap<>(); // for arrows that have a preset despawn time
    public static ArrayList<Arrow> noTrailArrows = new ArrayList<>(); // for arrows that won't have a trail

    public static void startArrowTracker() {
        new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<Arrow> flyingArrowsIterator = flyingArrows.iterator();

                while (flyingArrowsIterator.hasNext()) { // dead arrow check
                    Arrow arrow = flyingArrowsIterator.next();

                    if (arrow.isDead() || arrow.isInBlock()) {
                        arrowTimers.put(arrow, arrowTimeSaves.getOrDefault(arrow, 80));
                        flyingArrowsIterator.remove();
                    }
                }

                Iterator<Map.Entry<Arrow, Integer>> arrowTimerIterator = arrowTimers.entrySet().iterator();

                while (arrowTimerIterator.hasNext()) { // arrow despawn check
                    Map.Entry<Arrow, Integer> entry = arrowTimerIterator.next();
                    int time = entry.getValue();

                    if (time == 0) {
                        arrowTimerIterator.remove();
                    } else {
                        entry.setValue(time - 1);
                    }
                }
            }
        }.runTaskTimer(nmlWeapons, 0, 1);
    }

    public static void startArrowTrailTracker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Arrow arrow : flyingArrows) {
                    if (!noTrailArrows.contains(arrow)) {
                        double speed = arrow.getVelocity().length();
                        int particleCount = (int) (Math.pow(speed, 2) * 5);

                        if (particleCount > 0) {
                            Location loc = arrow.getLocation();
                            arrow.getWorld().spawnParticle(Particle.CRIT, loc, particleCount,0, 0, 0, 0);
                        }
                    }
                }
            }
        }.runTaskTimer(nmlWeapons, 0, 1);
    }

    public static void makeCustomArrow(Arrow arrow, HashMap<DamageType, Double> damage) {
        arrow.setMetadata("custom_arrow", new FixedMetadataValue(nmlWeapons, damage));
        arrow.setCritical(false);
        flyingArrows.add(arrow);
    }

    public static void makeCustomArrow(Arrow arrow, HashMap<DamageType, Double> damage, boolean trail) {
        makeCustomArrow(arrow, damage);

        if (!trail) {
            noTrailArrows.add(arrow);
        }
    }

    public static void makeCustomArrow(Arrow arrow, HashMap<DamageType, Double> damage, boolean trail, int despawnTicks) {
        makeCustomArrow(arrow, damage, trail);
        arrowTimeSaves.put(arrow, despawnTicks);

        if (!trail) {
            noTrailArrows.add(arrow);
        }
    }
}
