package net.spacegoat.buildable_campfire.common;

import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireStarterItem extends ToolItem {
    public FireStarterItem(Settings settings) {
        super(FIRE_STARTER_MATERIAL, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        if (state.isIn(BCTags.CAMPFIRES) && player != null){
            if (world.random.nextInt(10) == 0){
                world.setBlockState(pos, state.with(CampfireBlock.LIT, true));
                context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    public static ToolMaterial FIRE_STARTER_MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 70;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 0;
        }

        @Override
        public float getAttackDamage() {
            return 0;
        }

        @Override
        public int getMiningLevel() {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(Items.STICK);
        }
    };
}
