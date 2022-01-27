package net.spacegoat.buildable_campfire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.spacegoat.buildable_campfire.config.ModConfig;

import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final Block CAMPFIRE_LOG =
            new CampfireLogBlock(FabricBlockSettings.of(Material.WOOD)
                    .mapColor(Blocks.CAMPFIRE.getDefaultMapColor()).sounds(BlockSoundGroup.WOOD)
                    .strength(2.0f).breakByHand(true).luminance(ModConfig.getConfig().BuildableCampfire.campfireLogLuminance));


    public static final CampfireBlock STRIPPED_CAMPFIRE =
            new CampfireBlock(true, 1, FabricBlockSettings.of(Material.WOOD)
                    .mapColor(Blocks.STRIPPED_OAK_LOG.getDefaultMapColor()).sounds(BlockSoundGroup.WOOD)
                    .strength(2.0f).breakByHand(true).luminance(createLightLevelFromLitBlockState(ModConfig.getConfig().StrippedCampfires.strippedCampfiresLuminance)));

    public static final Block STRIPPED_SOUL_CAMPFIRE =
            new CampfireBlock(false, 2, FabricBlockSettings.of(Material.WOOD)
                    .mapColor(Blocks.STRIPPED_OAK_LOG.getDefaultMapColor()).sounds(BlockSoundGroup.WOOD)
                    .strength(2.0f).breakByHand(true).luminance(createLightLevelFromLitBlockState(ModConfig.getConfig().StrippedCampfires.strippedCampfiresLuminance)));

    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }
}
