package net.spacegoat.buildable_campfire;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.spacegoat.buildable_campfire.config.BCConfig;
import net.spacegoat.buildable_campfire.init.BCLogs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuildableCampfire implements ModInitializer {
	public static final String MOD_ID = "buildable_campfire";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BCLogs.registerCampfireLogs();
		this.registerPacks();
	}

	private void registerPacks(){
		if (BCConfig.getConfig().Gameplay.deleteCampfireBlockRecipes){
			createPack("no_campfire_recipes");
		}
		if (BCConfig.getConfig().Gameplay.enableCampfireLogDrops){
			createPack("campfire_log_drops");
		}
		if (BCConfig.getConfig().Gameplay.enableLogToCampfireLogRecipes){
			createPack("log_to_campfirelog");
		}
		if (BCConfig.getConfig().Gameplay.enableSticksToLogRecipes){
			createPack("stick_to_campfirelog");
		}
	}

	private void createPack(String id){
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer ->
			ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
					MOD_ID, id), modContainer, ResourcePackActivationType.DEFAULT_ENABLED));
	}
}
