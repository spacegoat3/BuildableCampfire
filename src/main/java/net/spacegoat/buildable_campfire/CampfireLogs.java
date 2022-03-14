package net.spacegoat.buildable_campfire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.config.ModConfig;

public class CampfireLogs {
    private static FabricBlockSettings CAMPFIRE_LOG_SETTINGS(MapColor mapColor){
        return FabricBlockSettings.of(Material.WOOD).strength(Blocks.CAMPFIRE.getHardness())
                .sounds(BlockSoundGroup.WOOD).mapColor(mapColor).luminance(ModConfig.getConfig().Gameplay.campfireLogLuminance);
    }

    public static final Block CAMPFIRE_LOG = createCampfireLog("campfire_log", Blocks.CAMPFIRE.getDefaultMapColor());


    private static Block createCampfireLog(String id, MapColor mapColor){
        return Registry.register(Registry.BLOCK, new Identifier(ModMain.MOD_ID, id), new CampfireLogBlock(CAMPFIRE_LOG_SETTINGS(mapColor)));
    }
}
