package net.spacegoat.buildable_campfire;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.common.CampfireLogBlock;
import net.spacegoat.buildable_campfire.common.FireStarterItem;
import net.spacegoat.buildable_campfire.config.BCConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuildableCampfire implements ModInitializer {
	public static final String MOD_ID = "buildable_campfire";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final CampfireLogBlock CAMPFIRE_LOG = new CampfireLogBlock("campfire_log",
			Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Material.WOOD);
	public static final Item FIRE_STATER = new FireStarterItem(new FabricItemSettings()
			.group(ItemGroup.TOOLS).maxCount(1));

	@Override
	public void onInitialize() {
		this.registerLog(CAMPFIRE_LOG);
		this.registerPacks();
	}

	public void registerLog(CampfireLogBlock block){
		Registry.register(Registry.BLOCK, new Identifier(block.id, MOD_ID), block);
		Registry.register(Registry.ITEM, new Identifier(block.id, MOD_ID), new BlockItem(
				block, new FabricItemSettings().group(block.campfire.asItem().getGroup())));
	}

	public void registerPacks(){
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
