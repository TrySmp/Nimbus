package net.trysmp.nimbus.module;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public abstract class Module {

    protected JavaPlugin plugin;

    protected String name;
    protected String version;

    public abstract void onLoad();
    public abstract void onEnable();
    public abstract void onDisable();

}
