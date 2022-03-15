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

public class BuildableCampfireMod implements ModInitializer {
	public static final String MOD_ID = "buildable_campfire";

	@Override
	public void onInitialize() {

		//RESOURCE PACK REGISTRIES
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

	public static Tag.Identified<Item> createItemTag(String id){
		return TagFactory.ITEM.create(new Identifier(MOD_ID, id));
	}

	//TAGS
	//If you are a developer and want your item to be used as an ingredient, you can put it to these tags.
	public static final Tag.Identified<Item> CAMPFIRE_INGREDIENTS =
			createItemTag("campfire_ingredients");
	public static final Tag.Identified<Item> SOUL_CAMPFIRE_INGREDIENTS =
			createItemTag("soul_campfire_ingredients");
	public static final Tag.Identified<Item> STICKS =
			createItemTag("sticks");
}
