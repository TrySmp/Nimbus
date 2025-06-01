package net.trysmp.nimbus;

import lombok.Getter;
import net.trysmp.nimbus.command.ModuleCommand;
import net.trysmp.nimbus.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public class Nimbus extends JavaPlugin {

    public static Logger LOGGER = Logger.getLogger("Nimbus");

    @Getter
    private static Nimbus instance;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        moduleManager = new ModuleManager();
        moduleManager.loadAll();

        new ModuleCommand();
    }

    public ClassLoader getLoader() {
        return super.getClassLoader();
    }

}
