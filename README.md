[Download Quarters on Modrinth](https://modrinth.com/plugin/quarters)

# Quarters
<sup>Because residents deserve the bare minimum.</sup>

Quarters allows for much finer control over properties within a Towny town.

Players can use a wand or a series of user-friendly commands to subdivide their town into 3D areas. The advantage this provides over stock Towny is not being limited by the perms applying to an entire town block, you can give players permissions to much more specific areas and stop wasting your land.

The wand item is flint by default, this can be used to select two corners that form a rectangular cuboid area that can be turned into a "quarter" (as in "living quarters") by a player using `/q selection add` and then `/q create`. The flint also makes a handy tool to covertly scratch your tenant's floors to ensure you keep the security deposit.

While a player is holding the wand item, both their currently selected area and quarters that currently exist within the town they are standing in will be displayed with an outline of particles.

![2023-10-21_17.34.33.png](images/2023-10-21_17.34.33.png)

## Developer API

To modify Quarter's behaviour, you can hook into our Bukkit events.

- `QuarterPre<Action>Event` fires before the action and is cancellable.
- `Quarter<Action>Event` fires after a successful action and cannot be cancelled.

For example, `QuarterPreClaimEvent` can stop a claim before any outstanding rent or sale price is charged. `QuarterClaimEvent` is fired only after the ownership change has been saved.

### Javadocs

The API reference for the latest release is available at:

<https://jitpack.io/com/github/jwkerr/Quarters/latest/javadoc/>

For a specific release, replace `2.0.0` in the following URL with its Git tag:

<https://jitpack.io/com/github/jwkerr/Quarters/2.0.0/javadoc/>

### Gradle

Add JitPack to your repositories and declare Quarters as a compile-only dependency. Replace `2.0.0` with the release tag you target.

```groovy
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("com.github.jwkerr:Quarters:2.0.0")
}
```

Your plugin should also declare Quarters as a dependency in its `plugin.yml`:

```yaml
depend: [Quarters]
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.jwkerr</groupId>
        <artifactId>Quarters</artifactId>
        <version>2.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Example

```java
import au.lupine.quarters.api.event.ownership.QuarterPreClaimEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ClaimListener implements Listener {

    @EventHandler
    public void onQuarterPreClaim(QuarterPreClaimEvent event) {
        if (event.getPrice() > 10_000) {
            event.setCancelled(true);
            event.setCancelMessage("This quarter is too expensive to claim.");
        }
    }
}
```
