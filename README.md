# 🌩️ Nimbus

**Nimbus** is a lightweight, modular plugin system - built with care for [trysmp.net](https://trysmp.net).<br>
<br>
Modules are JAR files placed in the `/modules` folder, each with full api access.<br>
They can be loaded, unloaded, and reloaded at runtime - no server restart needed.

****

Gradle Dependency

````groovy
implementation 'net.trysmp:nimbus:VERSION'
````

Maven Dependency
````xml
<dependency>
    <groupId>net.trysmp</groupId>
    <artifactId>nimbus</artifactId>
    <version>VERSION</version>
</dependency>
````

****

Example Module
```java
public class ExampleModule extends Module {

    @Override
    public void onLoad() {
        LOGGER.info(name + " v" + version + " is loading...");
    }

    @Override
    public void onEnable() {
        LOGGER.info(name + " was enabled!");
    }

    @Override
    public void onDisable() {
        LOGGER.info(name + " was disabled!");
    }

}
```

Nimbus automatically injects `plugin`, `name` and `version` into each module.

We require a `module.yml` file in the root of each module JAR to provide metadata.<br>
Github is optional, but used for version checking if enabled.

```yml
name: ExampleModule
version: 1.0
github: https://github.com/YourUser/ExampleModule
```

