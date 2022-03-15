package net.spacegoat.buildable_campfire.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import potionstudios.byg.common.block.BYGBlocks;
import potionstudios.byg.common.item.BYGItems;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @Shadow @Final public static BooleanProperty LIT;

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void pickFuels(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info){
        ItemStack item = player.getMainHandStack();
        if (item.isEmpty() || item.isOf(fuels(state)) && !state.get(LIT)){
            player.getInventory().insertStack(new ItemStack(fuels(state)));
        }
    }

    private Item fuels(BlockState state){
        if (state.isOf(Blocks.CAMPFIRE)){
            return Items.CHARCOAL;
        }
        if (state.isOf(Blocks.SOUL_CAMPFIRE)){
            return Items.SOUL_SAND;
        }
        if (state.isOf(BYGBlocks.BORIC_CAMPFIRE)){
            return BYGItems.BRIM_POWDER;
        }
        if (state.isOf(BYGBlocks.CRYPTIC_CAMPFIRE)){
            return BYGItems.CRYPTIC_MAGMA_BLOCK;
        }
        return null;
    }
}
