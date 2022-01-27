package net.spacegoat.buildable_campfire;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModMain implements ModInitializer {
	public static final String MOD_ID = "buildable_campfire";
	public static final Logger LOGGER = LoggerFactory.getLogger("buildable_campfire");

	public static Block registerBlock(String name, Block block){
		return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
	}
	public static Item registerBlockItem(String name, Block block){
		return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
				new BlockItem(block, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
	}

	@Override
	public void onInitialize() {
		//BLOCK REGISTRIES
		if (ModConfig.getConfig().Default.enableBuildableCampfire) {
			registerBlock("campfire_log", ModBlocks.CAMPFIRE_LOG);
			registerBlockItem("campfire_log", ModBlocks.CAMPFIRE_LOG);
		}
		if (ModConfig.getConfig().StrippedCampfires.enableStrippedCampfire){
			registerBlock("stripped_campfire", ModBlocks.STRIPPED_CAMPFIRE);
			registerBlockItem("stripped_campfire", ModBlocks.STRIPPED_CAMPFIRE);
		}
		if (ModConfig.getConfig().StrippedCampfires.enableStrippedSoulCampfire){
			registerBlock("stripped_soul_campfire", ModBlocks.STRIPPED_SOUL_CAMPFIRE);
			registerBlockItem("stripped_soul_campfire", ModBlocks.STRIPPED_SOUL_CAMPFIRE);
		}


		//RESOURCE PACK REGISTRIES
		if (ModConfig.getConfig().BuildableCampfire.deleteCampfireBlockRecipes && ModConfig.getConfig().Default.enableBuildableCampfire){
			noCampfireRecipes();
		}
		if (ModConfig.getConfig().BuildableCampfire.enableCampfireLogDrops && ModConfig.getConfig().Default.enableBuildableCampfire){
			campfireLogDrops();
		}
		if (ModConfig.getConfig().BuildableCampfire.enableLogToCampfireLogRecipes && ModConfig.getConfig().Default.enableBuildableCampfire){
			campfireLogRecipes();
		}
	}
	//RESOURCE PACKS
	private void noCampfireRecipes() {
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
			var added = ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
					MOD_ID, "no_campfire_recipes"), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
		});
	}
	private void campfireLogDrops() {
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
			var added = ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
					MOD_ID, "campfire_log_drops"), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
		});
	}
	private void campfireLogRecipes(){
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
			var added = ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
					MOD_ID, "campfire_log_recipes"), modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
		});
	}
}
