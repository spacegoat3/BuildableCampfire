package net.spacegoat.buildable_campfire.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin extends Block {
    public CampfireBlockMixin(Settings settings){
        super(settings);
    }
    @Inject(method = "onUse", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info, BlockEntity blockEntity){
        ItemStack playerStack = player.getStackInHand(hand);
        Item item = playerStack.getItem();
        if (!world.isClient && item == Items.STICK && CampfireBlock.isLitCampfire(state) && state.isOf(Blocks.CAMPFIRE)){
            player.setStackInHand(hand, new ItemStack(Items.TORCH));
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            info.setReturnValue(ActionResult.SUCCESS);
        }
        if (!world.isClient && item == Items.STICK && CampfireBlock.isLitCampfire(state) && state.isOf(Blocks.SOUL_CAMPFIRE)){
            player.setStackInHand(hand, new ItemStack(Items.SOUL_TORCH));
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            info.setReturnValue(ActionResult.SUCCESS);
        }
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }
}
