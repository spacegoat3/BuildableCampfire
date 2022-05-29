package net.spacegoat.buildable_campfire;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.common.CampfireLogBlock;
import net.spacegoat.buildable_campfire.config.BCConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuildableCampfire implements ModInitializer {
	public static final String MOD_ID = "buildable_campfire";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final Block CAMPFIRE_LOG = new CampfireLogBlock(
			Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Material.WOOD);

	@Override
	public void onInitialize() {
		this.registerBlock("campfire_log", CAMPFIRE_LOG, new FabricItemSettings().group(ItemGroup.DECORATIONS));
		this.registerPacks();
	}

	private void registerBlock(String id, Block block, Item.Settings itemSettings){
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, id), block);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, id), new BlockItem(block, itemSettings));
	}

	public void registerPacks(){
		if (BCConfig.getConfig().Gameplay.disableCampfireBlockRecipes){
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
