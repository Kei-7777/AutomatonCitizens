package me.kei.citizens.automaton.tasks;

import me.kei.citizens.automaton.AutomatonCitizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.Blocks;
import net.minecraft.server.v1_15_R1.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TransportNPCRunningTask extends BukkitRunnable {

    String dataString = "transport";
    String name = "運搬奴隷 ";

    List<Material> AirBlocks = Arrays.asList(
            Material.AIR,
            Material.LAVA,
            Material.WATER,
            Material.COBWEB,
            Material.STRING
    );

    //state
    // 0 - wait
    // 1 - moving 1st chest
    // 2 - get item from chest
    // 3 - search 2nd chest
    // 4 - moving 2nd chest

    AutomatonCitizens plugin;

    public TransportNPCRunningTask(AutomatonCitizens automatonCitizens) {
    }

    @Override
    public void run() {
        for (Iterator i = CitizensAPI.getNPCRegistry().iterator(); i.hasNext(); ) {
            NPC npc = (NPC) i.next();
            if (npc.data().get("npctype").equals(dataString)) {
                int state = npc.data().get("state");
                if (state == 1) {
                    Location base = new Location(npc.getEntity().getWorld(), npc.data().get("base_x"), npc.data().get("base_y"), npc.data().get("base_z"));
                    List<Block> chests = getNearChests(base, 10);
                    Bukkit.broadcastMessage("Find chests: " + String.valueOf(chests.size()));
                    Collections.shuffle(chests);
                    if (chests.size() > 0) {
                        Block target = null;
                        Location moveto = null;
                        for (Block b : chests) {
                            if (b.getLocation().clone().add(1, 0, 0).getBlock().getType() == Material.AIR && b.getLocation().clone().add(1, 1, 0).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(1, -1, 0).getBlock().getType())) {
                                target = b;
                                moveto = b.getLocation().clone().add(1.5, 0, 0.5);
                                break;
                            } else if (b.getLocation().clone().add(-1, 0, 0).getBlock().getType() == Material.AIR && b.getLocation().clone().add(-1, 1, 0).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(-1, -1, 0).getBlock().getType())) {
                                target = b;
                                moveto = b.getLocation().clone().add(-1 + .5, 0, 0.5);
                                break;
                            } else if (b.getLocation().clone().add(0, 0, 1).getBlock().getType() == Material.AIR && b.getLocation().clone().add(0, 1, 1).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(0, -1, 1).getBlock().getType())) {
                                target = b;
                                moveto = b.getLocation().clone().add(0.5, 0, 1.5);
                                break;
                            } else if (b.getLocation().clone().add(0, 0, -1).getBlock().getType() == Material.AIR && b.getLocation().clone().add(0, 1, -1).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(0, -1, -1).getBlock().getType())) {
                                target = b;
                                moveto = b.getLocation().clone().add(0.5, 0, -1 + .5);
                                break;
                            }
                        }

                        if (target == null || moveto == null) continue;
                        npc.getNavigator().setTarget(target.getLocation());
                        npc.data().set("state", 2);
                    }
                } else if (state == 2) {
                    Location loc = npc.getEntity().getLocation();
                    if (loc.clone().add(1, 0, 0).getBlock().getType() == Material.CHEST) {
                        chestAnimation(true, loc.clone().add(1, 0, 0));
                        npc.data().set("state", 3);
                    } else if (loc.clone().add(-1, 0, 0).getBlock().getType() == Material.CHEST) {
                        chestAnimation(true, loc.clone().add(-1, 0, 0));
                        npc.data().set("state", 3);
                    } else if (loc.clone().add(0, 0, 1).getBlock().getType() == Material.CHEST) {
                        chestAnimation(true, loc.clone().add(0, 0, 1));
                        npc.data().set("state", 3);
                    } else if (loc.clone().add(0, 0, -1).getBlock().getType() == Material.CHEST) {
                        chestAnimation(true, loc.clone().add(0, 0, -1));
                        npc.data().set("state", 3);
                    }
                } else if (state == 3) {
                    Location loc = npc.getEntity().getLocation();
                    Location chestloc = null;
                    if (loc.clone().add(1, 0, 0).getBlock().getType() == Material.CHEST) {
                        chestloc = loc.clone().add(1, 0, 0).getBlock().getLocation();
                    } else if (loc.clone().add(-1, 0, 0).getBlock().getType() == Material.CHEST) {
                        chestloc = loc.clone().add(-1, 0, 0).getBlock().getLocation();
                    } else if (loc.clone().add(0, 0, 1).getBlock().getType() == Material.CHEST) {
                        chestloc = loc.clone().add(0, 0, 1).getBlock().getLocation();
                    } else if (loc.clone().add(0, 0, -1).getBlock().getType() == Material.CHEST) {
                        chestloc = loc.clone().add(0, 0, -1).getBlock().getLocation();
                    }
                    if (chestloc == null) return;
                    Chest chest = (Chest) chestloc.getBlock().getState();
                    for (int j = 0; j < chest.getInventory().getSize(); j++) {
                        if (chest.getInventory().getItem(j) == null || chest.getInventory().getItem(j).getType() == Material.AIR) {
                            continue;
                        }

                        ItemStack item = chest.getInventory().getItem(j);
                        Equipment equipTrait = npc.getTrait(Equipment.class);
                        equipTrait.set(Equipment.EquipmentSlot.HAND, item);
                        chest.getInventory().setItem(j, new ItemStack(Material.AIR));
                        chestAnimation(false, chestloc);
                        npc.data().set("state", 4);
                        j = 999;
                        break;
                    }
                } else if (state == 4) {
                    Location base = new Location(npc.getEntity().getWorld(), npc.data().get("base_x"), npc.data().get("base_y"), npc.data().get("base_z"));
                    List<Block> chests = getNearTrapChests(base, 10);
                    Collections.shuffle(chests);
                    Bukkit.broadcastMessage("Find targets: " + String.valueOf(chests.size()));
                    if (chests.size() > 0) {
                        for (Block b : chests) {
                            Chest chest = (Chest) b.getState();
                            if (!checkInventoryRemain(chest, npc.getTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND))) {
                                chests.remove(b);
                            }
                        }
                    }

                    if (chests.size() < 1) {
                        return;
                    }
                    Block target = null;
                    Location moveto = null;
                    for (Block b : chests) {
                        if (b.getLocation().clone().add(1, 0, 0).getBlock().getType() == Material.AIR && b.getLocation().clone().add(1, 1, 0).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(1, -1, 0).getBlock().getType())) {
                            target = b;
                            moveto = b.getLocation().clone().add(1, 0, 0);
                            break;
                        } else if (b.getLocation().clone().add(-1, 0, 0).getBlock().getType() == Material.AIR && b.getLocation().clone().add(-1, 1, 0).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(-1, -1, 0).getBlock().getType())) {
                            target = b;
                            moveto = b.getLocation().clone().add(-1, 0, 0);
                            break;
                        } else if (b.getLocation().clone().add(0, 0, 1).getBlock().getType() == Material.AIR && b.getLocation().clone().add(0, 1, 1).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(0, -1, 1).getBlock().getType())) {
                            target = b;
                            moveto = b.getLocation().clone().add(0, 0, 1);
                            break;
                        } else if (b.getLocation().clone().add(0, 0, -1).getBlock().getType() == Material.AIR && b.getLocation().clone().add(0, 1, -1).getBlock().getType() == Material.AIR && !AirBlocks.contains(b.getLocation().clone().add(0, -1, -1).getBlock().getType())) {
                            target = b;
                            moveto = b.getLocation().clone().add(0, 0, -1);
                            break;
                        }
                    }

                    if (target == null || moveto == null) {
                        npc.getNavigator().setTarget(base);
                        continue;
                    }
                    npc.getNavigator().setTarget(target.getLocation());
                    npc.data().set("state", 5);
                } else if (state == 5) {
                    Location loc = npc.getEntity().getLocation();
                    if (loc.clone().add(1, 0, 0).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestAnimation(true, loc.clone().add(1, 0, 0));
                        npc.data().set("state", 6);
                    } else if (loc.clone().add(-1, 0, 0).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestAnimation(true, loc.clone().add(-1, 0, 0));
                        npc.data().set("state", 6);
                    } else if (loc.clone().add(0, 0, 1).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestAnimation(true, loc.clone().add(0, 0, 1));
                        npc.data().set("state", 6);
                    } else if (loc.clone().add(0, 0, -1).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestAnimation(true, loc.clone().add(0, 0, -1));
                        npc.data().set("state", 6);
                    }
                } else if (state == 6) {
                    Location base = new Location(npc.getEntity().getWorld(), npc.data().get("base_x"), npc.data().get("base_y"), npc.data().get("base_z"));
                    Location loc = npc.getEntity().getLocation();
                    Location chestloc = null;
                    if (loc.clone().add(1, 0, 0).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestloc = loc.clone().add(1, 0, 0).getBlock().getLocation();
                    } else if (loc.clone().add(-1, 0, 0).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestloc = loc.clone().add(-1, 0, 0).getBlock().getLocation();
                    } else if (loc.clone().add(0, 0, 1).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestloc = loc.clone().add(0, 0, 1).getBlock().getLocation();
                    } else if (loc.clone().add(0, 0, -1).getBlock().getType() == Material.TRAPPED_CHEST) {
                        chestloc = loc.clone().add(0, 0, -1).getBlock().getLocation();
                    }
                    if (chestloc == null) return;
                    Chest chest = (Chest) chestloc.getBlock().getState();
                    chest.getInventory().addItem(npc.getTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND));
                    npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.AIR));
                    chestAnimation(false, chestloc);
                    npc.data().set("state", 1);
                    npc.getNavigator().setTarget(base);
                }
            }
        }
    }

    private boolean checkInventoryRemain(Chest chest, ItemStack itemStack) {
        Inventory inv = Bukkit.createInventory(null, chest.getInventory().getSize());
        for (int j = 0; j < chest.getInventory().getSize(); j++) {
            inv.setItem(j, chest.getInventory().getItem(j));
        }

        Map<Integer, ItemStack> remainingItems = inv.addItem(itemStack);
        return remainingItems.size() < 1;
    }

    public List<Block> getNearChests(Location loc, int radius) {
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        List<Block> list = new ArrayList<>();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = (cy - radius); y < (cy + radius); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

                    if (dist < radius * radius) {
                        Location l = new Location(loc.getWorld(), x, y + 2, z);
                        if (l.getBlock().getType() == Material.CHEST) {
                            list.add(l.getBlock());
                        }
                    }
                }
            }
        }
        return list;
    }

    public List<Block> getNearTrapChests(Location loc, int radius) {
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        List<Block> list = new ArrayList<>();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = (cy - radius); y < (cy + radius); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

                    if (dist < radius * radius) {
                        Location l = new Location(loc.getWorld(), x, y + 2, z);
                        if (l.getBlock().getType() == Material.TRAPPED_CHEST) {
                            list.add(l.getBlock());
                        }
                    }
                }
            }
        }
        return list;
    }

    private static void chestAnimation(boolean open, Location loc) {
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.CHEST, (byte) 1, open ? (byte) 1 : 0);
        for (Player player : Bukkit.getOnlinePlayers())
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
