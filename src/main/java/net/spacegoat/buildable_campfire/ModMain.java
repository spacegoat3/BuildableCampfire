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

	public static final Block CAMPFIRE_LOG =
			new CampfireLogBlock(FabricBlockSettings.of(Material.WOOD)
					.mapColor(Blocks.CAMPFIRE.getDefaultMapColor()).sounds(BlockSoundGroup.WOOD)
					.strength(2.0f).breakByHand(true).luminance(ModConfig.getConfig().Gameplay.campfireLogLuminance));

	@Override
	public void onInitialize() {
			registerBlock("campfire_log", CAMPFIRE_LOG);
			registerBlockItem("campfire_log", CAMPFIRE_LOG);

		//RESOURCE PACK REGISTRIES
		if (ModConfig.getConfig().Gameplay.deleteCampfireBlockRecipes){
			noCampfireRecipes();
		}
		if (ModConfig.getConfig().Gameplay.enableCampfireLogDrops){
			campfireLogDrops();
		}
		if (ModConfig.getConfig().Gameplay.enableLogToCampfireLogRecipes){
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
