package net.spacegoat.buildable_campfire.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spacegoat.buildable_campfire.BuildableCampfire;
import net.spacegoat.buildable_campfire.common.BCTags;
import net.spacegoat.buildable_campfire.common.CampfireLogBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import potionstudios.byg.common.block.BYGBlocks;
import potionstudios.byg.common.item.BYGItems;

@Mixin(AbstractBlock.class)
public class BlockMixin {

    @Inject(method = "onUse", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
        ItemStack item = player.getMainHandStack();
        if ((state.isIn(BCTags.CAMPFIRES) && !state.get(CampfireBlock.LIT)) && item.isEmpty()) {
            if (state.isOf(Blocks.CAMPFIRE)) {
                pickBaseMaterial(Items.CHARCOAL, SoundEvents.BLOCK_TUFF_PLACE, world, pos, player);
            }
            if (state.isOf(Blocks.SOUL_CAMPFIRE)) {
                pickBaseMaterial(Items.SOUL_SAND, SoundEvents.BLOCK_SOUL_SAND_PLACE, world, pos, player);
            }
            if (state.isOf(BYGBlocks.BORIC_CAMPFIRE)) {
                pickBaseMaterial(BYGItems.BRIM_POWDER, SoundEvents.BLOCK_SAND_PLACE, world, pos, player);
            }
            if (state.isOf(BYGBlocks.CRYPTIC_CAMPFIRE)) {
                pickBaseMaterial(BYGItems.CRYPTIC_MAGMA_BLOCK, SoundEvents.BLOCK_TUFF_PLACE, world, pos, player);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    public void pickBaseMaterial(ItemConvertible material, SoundEvent sound, World world, BlockPos pos, PlayerEntity player){
        player.playSound(sound, 1, 1);
        player.getInventory().insertStack(new ItemStack(material, 1));
        world.setBlockState(pos, BuildableCampfire.CAMPFIRE_LOG.getDefaultState().with(CampfireLogBlock.CAMPFIRE_LOGS, 4));
    }
}
