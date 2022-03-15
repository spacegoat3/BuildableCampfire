package net.spacegoat.buildable_campfire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.blocks.CampfireLogBlock;
import net.spacegoat.buildable_campfire.blocks.VanillaCampfireLogBlock;
import net.spacegoat.buildable_campfire.config.ModConfig;

public class CampfireLogs {
    private static FabricBlockSettings CAMPFIRE_LOG_SETTINGS(Block mapColor){
        return FabricBlockSettings.of(Material.WOOD).strength(Blocks.CAMPFIRE.getHardness())
                .sounds(BlockSoundGroup.WOOD).mapColor(mapColor.getDefaultMapColor()).luminance(ModConfig.getConfig().Gameplay.campfireLogLuminance);
    }

    public static final VanillaCampfireLogBlock CAMPFIRE_LOG = createCampfireLog("campfire_log", Blocks.OAK_LOG);

    private static VanillaCampfireLogBlock createCampfireLog(String id, Block mapColor){
        return new VanillaCampfireLogBlock(id, FabricBlockSettings.of(Material.WOOD).strength(Blocks.CAMPFIRE.getHardness())
                .sounds(BlockSoundGroup.WOOD).mapColor(mapColor.getDefaultMapColor()).luminance(ModConfig.getConfig().Gameplay.campfireLogLuminance));
    }

    public static void registerCampfireLogs(){
        if (ModConfig.getConfig().logs.oakLog){
            register(CAMPFIRE_LOG);
        }
    }

    private static void register(CampfireLogBlock block){
        Registry.register(Registry.BLOCK, new Identifier(BuildableCampfireMod.MOD_ID, block.getId()), block);
    }
}
