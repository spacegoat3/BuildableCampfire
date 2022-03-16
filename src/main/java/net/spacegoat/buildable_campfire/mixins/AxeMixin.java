package net.spacegoat.buildable_campfire.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.spacegoat.buildable_campfire.BuildableCampfireTags;
import net.spacegoat.buildable_campfire.CampfireLogBlock;
import net.spacegoat.buildable_campfire.CampfireLogs;
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
        if (player != null) {
            if (ModConfig.getConfig().Gameplay.enableLogChopping && isLog(state) && player.getPose().equals(EntityPose.CROUCHING)) {
                world.breakBlock(pos, false, player);
                Block.dropStack(world, pos, new ItemStack(CampfireLogs.CAMPFIRE_LOG.asItem(), world.getRandom().nextInt(1, 4)));
                damage(15, context);
                info.setReturnValue(ActionResult.SUCCESS);
            }
            if (ModConfig.getConfig().Gameplay.enableCampfireLogChopping && state.getBlock() instanceof CampfireLogBlock && player.getPose().equals(EntityPose.CROUCHING)){
                damage(10, context);
                chopCampfireLogs(context);
                info.setReturnValue(ActionResult.SUCCESS);
            }
        } else {
            info.setReturnValue(ActionResult.PASS);
        }
    }

    private boolean isLog(BlockState state){
        return state.isIn(BlockTags.LOGS) || state.isIn(BuildableCampfireTags.BYG_LOGS);
    }

    private void damage(int damage, ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            context.getStack().damage(damage, player, (p) -> p.sendToolBreakStatus(context.getHand()));
        }
    }

    private void chopCampfireLogs(ItemUsageContext context){
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        IntProperty logs = CampfireLogBlock.CAMPFIRE_LOGS;
        BlockState state = world.getBlockState(pos);
        ItemStack sticks = new ItemStack(Items.STICK, 6);
        if (state.get(logs).equals(1)){
            world.removeBlock(pos, false);
        }
        if (state.get(logs).equals(2)){
            world.setBlockState(pos, state.with(logs, 1));
        }
        if (state.get(logs).equals(3)){
            world.setBlockState(pos, state.with(logs, 2));
        }
        if (state.get(logs).equals(4)){
            world.setBlockState(pos, state.with(logs, 3));
        }
        world.addBlockBreakParticles(pos, state);
        Block.dropStack(world, pos, Direction.UP, sticks);
        PlayerEntity player = context.getPlayer();
        if (player != null){
            player.playSound(state.getSoundGroup().getBreakSound(), 1, 1);
        }
    }
}
