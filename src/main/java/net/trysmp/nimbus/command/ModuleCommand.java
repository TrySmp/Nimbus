package net.trysmp.nimbus.command;

import net.trysmp.nimbus.Nimbus;
import net.trysmp.nimbus.module.ModuleWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleCommand implements CommandExecutor, TabCompleter {

    public ModuleCommand() {
        Objects.requireNonNull(Nimbus.getInstance().getCommand("nimbus")).setExecutor(this);
        Objects.requireNonNull(Nimbus.getInstance().getCommand("nimbus")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender.hasPermission("nimbus.command"))) return false;

        if (args.length == 2) {
            String identifier = args[1];

            if (args[0].equalsIgnoreCase("load")) {
                if (!(identifier.endsWith(".jar"))) {
                    identifier += ".jar";
                }

                File file = new File(Nimbus.getInstance().getDataFolder().getParentFile().getParentFile(), "modules/" + identifier);
                if (!file.exists()) {
                    commandSender.sendMessage("§cModule file not found: " + identifier);
                    return false;
                }

                Nimbus.getInstance().getModuleManager().load(file);
                commandSender.sendMessage("§aModule loaded: " + identifier);
                return false;
            }

            if (args[0].equalsIgnoreCase("unload")) {
                ModuleWrapper wrapper = Nimbus.getInstance().getModuleManager().getLoadedModules().get(identifier);
                if (wrapper == null) {
                    // module not found by jar file name
                    // get by module name
                    String finalIdentifier = identifier;
                    wrapper = Nimbus.getInstance().getModuleManager().getLoadedModules().values().stream()
                            .filter(moduleWrapper -> moduleWrapper.getModule().getName().equalsIgnoreCase(finalIdentifier))
                            .findFirst()
                            .orElse(null);

                    // still not found
                    if (wrapper == null) {
                        commandSender.sendMessage("§cModule not found: " + identifier);
                        return false;
                    }
                }

                Nimbus.getInstance().getModuleManager().unload(wrapper);
                commandSender.sendMessage("§aModule unloaded: " + wrapper.getModule().getName());
                return false;
            }
        }

        commandSender.sendMessage("§7Usage: /nimbus <load|unload> [module]");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender.hasPermission("nimbus.command"))) return new ArrayList<>();
        List<String> list = new ArrayList<>();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("load")) {
                list.addAll(Nimbus.getInstance().getModuleManager().getDirectoryFiles().stream().map(File::getName).toList());
            }
            if (args[0].equalsIgnoreCase("unload")) {
                list.addAll(Nimbus.getInstance().getModuleManager().getLoadedModules().keySet());
                list.addAll(Nimbus.getInstance().getModuleManager().getLoadedModules().values().stream().map(moduleWrapper -> moduleWrapper.getModule().getName()).toList());
            }
        }

        if (args.length == 1) {
            list.add("load");
            list.add("unload");
        }

        return list.stream().filter(content -> content.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).sorted().toList();
    }

}
