package net.spacegoat.buildable_campfire.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spacegoat.buildable_campfire.BuildableCampfire;
import net.spacegoat.buildable_campfire.common.CampfireLogBlock;
import net.spacegoat.buildable_campfire.config.BCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AxeItem.class)
public class AxeMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        BCConfig.Gameplay config = BCConfig.getConfig().Gameplay;
        ItemStack stack = context.getStack();
        Hand hand = context.getHand();
        if (player != null) {
            if (state.isIn(BlockTags.LOGS) && config.logChopping) {
                world.breakBlock(pos, false);
                Block.dropStack(world, pos, new ItemStack(BuildableCampfire.CAMPFIRE_LOG, config.logChoppingConfig.droppedCampfireLogs));
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 1, 1, false);
                player.getHungerManager().addExhaustion(config.logChoppingConfig.energyLoss);
                stack.damage(config.logChoppingConfig.toolDamage, player, p -> p.sendToolBreakStatus(hand));
                cir.setReturnValue(ActionResult.SUCCESS);
            }
            if (state.isOf(BuildableCampfire.CAMPFIRE_LOG) && config.campfireLogChopping) {
                if (state.get(CampfireLogBlock.CAMPFIRE_LOGS).equals(1)) {
                    world.breakBlock(pos, false);
                } else {
                    world.addBlockBreakParticles(pos, state);
                    world.setBlockState(pos, CampfireLogBlock.blockState(false, state));
                }
                Block.dropStack(world, pos, new ItemStack(Items.STICK, config.campfireLogChoppingConfig.droppedSticks));
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_STRIP, SoundCategory.PLAYERS, 1, 1, false);
                player.getHungerManager().addExhaustion(config.campfireLogChoppingConfig.energyLoss);
                stack.damage(config.campfireLogChoppingConfig.toolDamage, player, p -> p.sendToolBreakStatus(hand));
                cir.setReturnValue(ActionResult.SUCCESS);
            }
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
