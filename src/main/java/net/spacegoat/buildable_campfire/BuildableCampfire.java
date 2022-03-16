package net.spacegoat.buildable_campfire;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.spacegoat.buildable_campfire.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuildableCampfire implements ModInitializer {
	public static final String MOD_ID = "buildable_campfire";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CampfireLogs.registerCampfireLogs();
		registerPacks();
	}

	private void registerPacks(){
		if (ModConfig.getConfig().Gameplay.deleteCampfireBlockRecipes){
			createPack("no_campfire_recipes");
		}
		if (ModConfig.getConfig().Gameplay.enableCampfireLogDrops){
			createPack("campfire_log_drops");
		}
		if (ModConfig.getConfig().Gameplay.enableLogToCampfireLogRecipes){
			createPack("log_to_campfirelog");
		}
		if (ModConfig.getConfig().Gameplay.enableSticksToLogRecipes){
			createPack("stick_to_campfirelog");
		}
	}

	private void createPack(String id){
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
			var added = ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
					MOD_ID, id), modContainer, ResourcePackActivationType.DEFAULT_ENABLED);
		});
	}
}
