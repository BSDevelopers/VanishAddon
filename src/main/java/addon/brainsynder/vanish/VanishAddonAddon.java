package addon.brainsynder.vanish;

import de.myzelyam.api.vanish.PostPlayerHideEvent;
import de.myzelyam.api.vanish.PostPlayerShowEvent;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.event.EventHandler;
import simplepets.brainsynder.addon.AddonConfig;
import simplepets.brainsynder.addon.PetModule;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.event.entity.PetEntitySpawnEvent;
import simplepets.brainsynder.api.plugin.SimplePets;
import simplepets.brainsynder.api.user.PetUser;

@Namespace(namespace = "VanishAddon")
public class VanishAddonAddon extends PetModule {
    private boolean preventPets = true;
    private String reason;


    @Override
    public void init() {

    }

    @Override
    public void loadDefaults(AddonConfig config) {
        config.addComment("prevent-new-pets", "Handles when the player spawns a new pet while vanished");
        config.addDefault("prevent-new-pets.enabled", true);
        config.addDefault("prevent-new-pets.reason", "You are currently vanished");

        preventPets = config.getBoolean("prevent-new-pets.enabled", true);
        reason = config.getString("prevent-new-pets.reason", "You are currently vanished");
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
