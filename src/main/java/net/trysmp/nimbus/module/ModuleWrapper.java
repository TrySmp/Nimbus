package net.trysmp.nimbus.module;

import lombok.Getter;

import java.io.IOException;
import java.net.URLClassLoader;

@Getter
public class ModuleWrapper {

    private final Module module;
    private final URLClassLoader classLoader;

    public ModuleWrapper(Module module, URLClassLoader classLoader) {
        this.module = module;
        this.classLoader = classLoader;
    }

    public void unload() throws IOException {
        module.onDisable();
        classLoader.close(); // important for GC
    }

}
