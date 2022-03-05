package net.spacegoat.buildable_campfire;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack item = context.getStack();
        if (state.isIn(BlockTags.LOGS))
    }
}
