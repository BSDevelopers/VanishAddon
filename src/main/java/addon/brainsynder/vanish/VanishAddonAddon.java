package addon.brainsynder.vanish;

import com.google.common.collect.Lists;
import de.myzelyam.api.vanish.PostPlayerHideEvent;
import de.myzelyam.api.vanish.PostPlayerShowEvent;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import simplepets.brainsynder.addon.AddonConfig;
import simplepets.brainsynder.addon.PetAddon;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.event.entity.PetEntitySpawnEvent;
import simplepets.brainsynder.api.plugin.SimplePets;
import simplepets.brainsynder.api.user.PetUser;
import simplepets.brainsynder.debug.DebugBuilder;

import java.util.List;

@Namespace(namespace = "VanishAddon")
public class VanishAddonAddon extends PetAddon {
    private boolean preventPets = true;
    private String reason;


    @Override
    public void init() {

    }


    @Override
    public boolean shouldEnable() {
        Plugin superVanish = Bukkit.getPluginManager().getPlugin("SuperVanish");
        if ((superVanish != null) && superVanish.isEnabled()) return true;

        Plugin premiumVanish = Bukkit.getPluginManager().getPlugin("PremiumVanish");
        if ((premiumVanish != null) && premiumVanish.isEnabled()) return true;

        SimplePets.getDebugLogger().debug(DebugBuilder.build(getClass()).setLevel(SimplePets.ADDON).setMessages(
                "You seem to be missing either SuperVanish/PremiumVanish...",
                "SuperVanish: https://www.spigotmc.org/resources/1331/",
                "PremiumVanish: https://www.spigotmc.org/resources/14404/"
        ));
        return false;
    }

    @Override
    public void loadDefaults(AddonConfig config) {
        config.addComment("prevent-new-pets", "Handles when the player spawns a new pet while vanished");
        config.addDefault("prevent-new-pets.enabled", true);
        config.addDefault("prevent-new-pets.reason", "You are currently vanished");

        preventPets = config.getBoolean("prevent-new-pets.enabled", true);
        reason = config.getString("prevent-new-pets.reason", "You are currently vanished");
    }

    @Override
    public double getVersion() {
        return 0.1;
    }

    @Override
    public String getAuthor() {
        return "brainsynder";
    }

    @Override
    public List<String> getDescription() {
        return Lists.newArrayList(
                "&7This addon links into SuperVanish/PremiumVanish",
                "&7To handle when a player toggles being in vanish"
        );
    }

    @EventHandler
    public void onHide (PostPlayerHideEvent event) {
        SimplePets.getUserManager().getPetUser(event.getPlayer()).ifPresent(PetUser::cacheAndRemove);
    }

    @EventHandler
    public void onShow (PostPlayerShowEvent event) {
        SimplePets.getUserManager().getPetUser(event.getPlayer()).ifPresent(PetUser::summonCachedPets);
    }

    @EventHandler
    public void onPreSpawn (PetEntitySpawnEvent event) {
        if (!preventPets) return;
        if (!VanishAPI.isInvisible(event.getUser().getPlayer())) return;
        event.setCancelled(true, reason);
    }
}
