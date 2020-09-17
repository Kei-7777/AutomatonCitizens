package me.kei.citizens.automaton;

import me.kei.citizens.automaton.item.AutomatonItemFactory;
import me.kei.citizens.automaton.listener.TransportNPCListener;
import me.kei.citizens.automaton.tasks.TransportNPCRunningTask;
import org.bukkit.plugin.java.JavaPlugin;

public class AutomatonCitizens extends JavaPlugin {

    @Override
    public void onEnable() {

        AutomatonItemFactory.init(this);

        getServer().getPluginManager().registerEvents(new TransportNPCListener(this), this);

        new TransportNPCRunningTask(this).runTaskTimer(this, 20, 20);

    }
}
