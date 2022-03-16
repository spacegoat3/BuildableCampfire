package net.spacegoat.buildable_campfire;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class BuildableCampfireTags {

    //TAGS
    //If you are a developer and want your item to be used as an ingredient, you can put it to these tags.
    public static final Tag.Identified<Item> CAMPFIRE_INGREDIENTS =
            createItemTag("campfire_ingredients");
    public static final Tag.Identified<Item> SOUL_CAMPFIRE_INGREDIENTS =
            createItemTag("soul_campfire_ingredients");
    public static final Tag.Identified<Block> BYG_LOGS =
            createBlockTag("byg_logs");

    private static Tag.Identified<Item> createItemTag(String id){
        return TagFactory.ITEM.create(new Identifier(BuildableCampfire.MOD_ID, id));
    }
    private static Tag.Identified<Block> createBlockTag(String id){
        return TagFactory.BLOCK.create(new Identifier(BuildableCampfire.MOD_ID, id));
    }
}
