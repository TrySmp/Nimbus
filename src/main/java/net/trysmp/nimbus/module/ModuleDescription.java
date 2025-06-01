package net.trysmp.nimbus.module;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;

@Getter
public class ModuleDescription {

    private final String name;
    private final String version;
    private final String github;

    public ModuleDescription(InputStream inputStream) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
        this.name = yaml.getString("name");
        this.version = yaml.getString("version");
        this.github = yaml.getString("github");
    }

}
