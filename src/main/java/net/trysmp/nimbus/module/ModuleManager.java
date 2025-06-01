package net.trysmp.nimbus.module;

import lombok.Getter;
import net.trysmp.nimbus.Nimbus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Getter
public class ModuleManager {

    private final Map<String, ModuleWrapper> loadedModules = new HashMap<>();

    public ModuleManager() {
    }

    public void loadAll() {
        for (File jar : getDirectoryFiles()) {
            load(jar);
        }
    }

    public void load(File jarFile) {
        Nimbus.LOGGER.info("Loading module " + jarFile.getName() + "...");

        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jarFile.toURI().toURL()},
                    Nimbus.getInstance().getLoader() // important: get bukkit classloader
            );

            JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile));
            JarEntry entry;

            ModuleDescription description = null;

            // 1. get module.yml
            while ((entry = jarStream.getNextJarEntry()) != null) {
                if (entry.getName().equalsIgnoreCase("module.yml")) {
                    description = new ModuleDescription(jarStream);
                    break;
                }
            }

            if (description == null) {
                Nimbus.LOGGER.severe("No module.yml found in " + jarFile.getName());
                classLoader.close();
                return;
            }

            // 2. find the module main class
            jarStream = new JarInputStream(new FileInputStream(jarFile));
            while ((entry = jarStream.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");

                    Class<?> clazz = classLoader.loadClass(className);

                    if (Module.class.isAssignableFrom(clazz)) {
                        Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                        module.setPlugin(Nimbus.getInstance());
                        module.setName(description.getName());
                        module.setVersion(description.getVersion());

                        module.onLoad();
                        module.onEnable();
                        loadedModules.put(description.getName(), new ModuleWrapper(module, classLoader));
                        Nimbus.LOGGER.info("Module " + description.getName() + " v" + description.getVersion() + " loaded successfully!");
                        return;
                    }
                }
            }

            classLoader.close();
            Nimbus.LOGGER.severe("No valid module class found in " + jarFile.getName());
        } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            Nimbus.LOGGER.severe("Failed to load module " + jarFile.getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void unload(ModuleWrapper wrapper) {
        Nimbus.LOGGER.info("Unloading module " + wrapper.getModule().getName() + "...");

        try {
            wrapper.unload();
            loadedModules.remove(wrapper.getModule().getName());
            Nimbus.LOGGER.info("Module " + wrapper.getModule().getName() + " was unloaded successfully!");
        } catch (IOException exception) {
            Nimbus.LOGGER.severe("Failed to unload module " + wrapper.getModule().getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public List<File> getDirectoryFiles() {
        File directory = new File(Nimbus.getInstance().getDataFolder().getParentFile().getParentFile(), "modules");
        if (!(directory.exists())) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles();
        if (files == null) return new ArrayList<>();

        return Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".jar"))
                .toList();
    }

}
