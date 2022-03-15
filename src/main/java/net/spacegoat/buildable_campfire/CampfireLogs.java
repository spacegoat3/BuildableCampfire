package net.spacegoat.buildable_campfire;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.config.ModConfig;

public class CampfireLogs {
    private static final FabricBlockSettings CAMPFIRE_LOG_SETTINGS = FabricBlockSettings.of(Material.WOOD).strength(Blocks.CAMPFIRE.getHardness())
            .sounds(BlockSoundGroup.WOOD).luminance(ModConfig.getConfig().Gameplay.campfireLogLuminance);

    public static final CampfireLogBlock CAMPFIRE_LOG = createCampfireLog
            ("campfire", Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, CAMPFIRE_LOG_SETTINGS);

    private static CampfireLogBlock createCampfireLog(String id, Block campfire, Block soulCampfire, FabricBlockSettings settings){
        return new CampfireLogBlock(id, campfire, soulCampfire, FabricBlockSettings.of(Material.WOOD).strength(Blocks.CAMPFIRE.getHardness())
                .sounds(BlockSoundGroup.WOOD).luminance(ModConfig.getConfig().Gameplay.campfireLogLuminance));
    }

    public static void registerCampfireLogs(){
        register(CAMPFIRE_LOG);
    }

    private static void register(CampfireLogBlock block){
        Identifier identifier = new Identifier(BuildableCampfire.MOD_ID, block.getId());
        Registry.register(Registry.BLOCK, identifier, block);
        Registry.register(Registry.ITEM, identifier, new BlockItem(block, new FabricItemSettings()
                .group(ItemGroup.DECORATIONS)));
    }
}
