# SimpleConfig
 Simple YAML Library for Spigot Development (Java 17)

 [![](https://jitpack.io/v/NaTorOG/SimpleConfig.svg)](https://jitpack.io/#NaTorOG/SimpleConfig)
  
#### ADD TO YOUR PROJECT
` Replace RELEASE with jitpack version above`

Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.NaTorOG</groupId>
    <artifactId>SimpleConfig</artifactId>
    <version>RELEASE</version>
</dependency>
```
Gradle
```sh
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```
```sh
dependencies {
        implementation 'com.github.NaTorOG:SimpleConfig:RELEASE'
}
```

### INTRODUCTION
SimpleConfig is a library designed to easily handle YAML Configs for your Spigot/Paper plugins. 

Every serializable Object is fully supported!


`BaseConfig`
- [x] Does not require a file in resources folder
- [x] Automatically updates on register existing file with missing entries

```java
@Config // Tells SimpleConfig this Class represents a Config
@ConfigFile("settings.yml") // Name of the file that will be created (Paths are supported)
public class ExampleConfig extends BaseConfig {

    public ExampleConfig(TestLib plugin) {
        registerConfig(plugin);
    }

    @Path("database.port") // Path for this setting in your File
    @Comment({"Set the database port for MySQL", "By default is 3306"}) // Some comments
    public int port = 3306; // Default value that will be written if file doesn't exists

    @Path("item.diamond")
    @Comment({"Set the item to give on join", "to new players!"})
    public ItemStack diamond = new ItemStack(Material.DIAMOND);

    @Path("players.allowed")
    @Comment("Allowed players")
    public List<String> players = List.of("Pino", "Gino");
}
```

`LightConfig`
- [x] Requires a file in your resources folder
- [x] Does not automatically updates missing entries
- [x] Requires a method to call your values
```java
@Config
@ConfigFile("lang/language.yml")
public class LangConfig extends LightConfig {

    public LangConfig(TestLib plugin){
        registerLightConfig(plugin);
    }

    public String ciao(){
       return fileConfiguration.getString("ciao");
    }
}
```

### USING YOUR CONFIGS
```java
public final class TestLib extends JavaPlugin{

    public ExampleConfig exampleConfig;
    public LangConfig langConfig;

    @Override
    public void onEnable() {
        exampleConfig = new ExampleConfig(this);
        langConfig = new LangConfig(this);

        // Getting values
        int port = exampleConfig.port;

        String message = langConfig.ciao();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reloadConfigs(){
        exampleConfig.reload();
        exampleConfig.saveAndReload(this); // When you have update a value with fileConfiguration.set

        langConfig.reload();
        langConfig.saveAndReload(); // When you have update a value with fileConfiguration.set
    }
}
```