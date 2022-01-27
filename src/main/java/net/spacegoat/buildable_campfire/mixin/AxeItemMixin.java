package net.spacegoat.buildable_campfire.mixin;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spacegoat.buildable_campfire.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin extends MiningToolItem {
    protected static final Map<Block, Block> STRIPPED_CAMPFIRES = new ImmutableMap.Builder<Block, Block>().put(Blocks.CAMPFIRE, ModBlocks.STRIPPED_CAMPFIRE).put(Blocks.SOUL_CAMPFIRE, ModBlocks.STRIPPED_SOUL_CAMPFIRE).build();

    protected AxeItemMixin(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.AXE_MINEABLE, settings);
    }

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/AxeItem;getStrippedState(Lnet/minecraft/block/BlockState;)Ljava/util/Optional;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockState state){
        Optional<BlockState> optional = this.getStrippedState(state);
        if (state.isOf(Blocks.CAMPFIRE) && optional.isPresent())
        info.setReturnValue(ActionResult.PASS);
    }
    private Optional<BlockState> getStrippedState(BlockState state) {
        return Optional.ofNullable(STRIPPED_CAMPFIRES.get(state.getBlock())).map(block -> block.getDefaultState().with(CampfireBlock.LIT, true));
    }
}
