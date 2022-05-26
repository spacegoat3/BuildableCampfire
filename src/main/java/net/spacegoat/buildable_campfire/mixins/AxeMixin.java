package net.spacegoat.buildable_campfire.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.Sound;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spacegoat.buildable_campfire.CampfireLogBlock;
import net.spacegoat.buildable_campfire.config.BCConfig;
import net.spacegoat.buildable_campfire.init.BCBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AxeItem.class)
public class AxeMixin {

    @Inject(method = "useOnBlock", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (state.isOf(BCBlocks.CAMPFIRE_LOG) && BCConfig.getConfig().Gameplay.enableCampfireLogChopping) {
            if (state.get(CampfireLogBlock.CAMPFIRE_LOGS).equals(1)){
                world.removeBlock(pos, false);
            } else {
                world.setBlockState(pos, CampfireLogBlock.blockState(false, state));
            }
            Block.dropStack(world, pos, new ItemStack(Items.STICK, 4));
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_STRIP, SoundCategory.PLAYERS, 1, 1, false);
        }
    }
}
