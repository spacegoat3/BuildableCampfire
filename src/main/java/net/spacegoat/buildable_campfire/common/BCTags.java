package net.spacegoat.buildable_campfire.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.spacegoat.buildable_campfire.BuildableCampfire;

public class BCTags {

    //TAGS
    //If you are a developer and want your item to be used as an ingredient, you can put it to these tags.
    public static final TagKey<Item> CAMPFIRE_INGREDIENTS =
            createItemTag("campfire_ingredients");
    public static final TagKey<Item> SOUL_CAMPFIRE_INGREDIENTS =
            createItemTag("soul_campfire_ingredients");
    public static final TagKey<Block> CAMPFIRES =
            createBlockTag("campfires");


    private static TagKey<Item> createItemTag(String id){
        return TagKey.of(Registry.ITEM_KEY, new Identifier(BuildableCampfire.MOD_ID, id));
    }
    private static TagKey<Block> createBlockTag(String id){
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(BuildableCampfire.MOD_ID, id));
    }
}
