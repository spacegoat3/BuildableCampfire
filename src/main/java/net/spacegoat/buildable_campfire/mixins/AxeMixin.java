package net.spacegoat.buildable_campfire.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.spacegoat.buildable_campfire.ModMain;
import net.spacegoat.buildable_campfire.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AxeItem.class)
public class AxeMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void chopLogs(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info){
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack item = context.getStack();
        BlockState upState = world.getBlockState(pos.up());
        if (player != null) {
            if (ModConfig.getConfig().Gameplay.enableLogChopping && state.isIn(BlockTags.LOGS) && player.getPose().equals(EntityPose.CROUCHING)) {
                world.breakBlock(pos, false, player);
                Block.dropStack(world, pos.up(), new ItemStack(ModMain.CAMPFIRE_LOG.asItem(), world.getRandom().nextInt(1, 4)));
                item.damage(1, player, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                info.setReturnValue(ActionResult.SUCCESS);
            }
        } else {
            info.setReturnValue(ActionResult.PASS);
        }
    }
}
