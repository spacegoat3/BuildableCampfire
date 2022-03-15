package net.spacegoat.buildable_campfire.blocks;

import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.spacegoat.buildable_campfire.BuildableCampfireMod;
import net.spacegoat.buildable_campfire.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import me.hypherionmc.hyperlighting.common.init.HLBlocks;
import java.util.List;

public class CampfireLogBlock extends Block implements Waterloggable {
    public String id;

    public Block campfire;
    public Block soulCampfire;
    public final String getId(){
        return this.id;
    }

    public CampfireLogBlock(String id, Block campfire, Block soulCampfire, Settings settings) {
        super(settings);
        this.id = id;
        this.campfire = campfire;
        this.soulCampfire = soulCampfire;
    }

    public static final IntProperty CAMPFIRE_LOGS = IntProperty.of("campfire_logs", 1,4);
    public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");
    public static final DirectionProperty FACING = DirectionProperty.of("facing");

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Direction playerDirection = context.getPlayerFacing();
        if (state.isOf(this)) {
            return state.cycle(CAMPFIRE_LOGS);
        }
        return this.getDefaultState().with(WATERLOGGED, isNearWater(world, pos)).with(FACING, playerDirection);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(CAMPFIRE_LOGS) < 4) {
            return true;
        }
        return super.canReplace(state, context);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }


    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack item = player.getMainHandStack();
        BlockState state = blockState.with(WATERLOGGED, isNearWater(world, pos));
        if (ModConfig.getConfig().Gameplay.campfireLogsArePickable && item.isEmpty() || item.isOf(this.asItem())){
            if (state.get(CAMPFIRE_LOGS).equals(1)){
                world.removeBlock(pos, false);
                pickLog(player);
                return ActionResult.SUCCESS;
            }
            if (state.get(CAMPFIRE_LOGS).equals(2)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 1));
                pickLog(player);
                return ActionResult.SUCCESS;
            }
            if (state.get(CAMPFIRE_LOGS).equals(3)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 2));
                pickLog(player);
                return ActionResult.SUCCESS;
            }
            if (state.get(CAMPFIRE_LOGS).equals(4)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 3));
                pickLog(player);
                return ActionResult.SUCCESS;
            }
            if (state.get(CAMPFIRE_LOGS).equals(5)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 4));
                pickLog(player);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        if (state.get(CAMPFIRE_LOGS).equals(4)) {
            if (ModConfig.getConfig().CampfireBlock.enableBuildableCampfire && item.isIn(BuildableCampfireMod.CAMPFIRE_INGREDIENTS) && item.getCount() >= ModConfig.getConfig().CampfireBlock.howMuchCoalBuildingACampfireTakes) {
                makeCampfire(campfire, ModConfig.getConfig().CampfireBlock.campfireIsLitWhenBuild, ModConfig.getConfig().CampfireBlock.playSoundEffect, SoundEvents.BLOCK_TUFF_PLACE, ModConfig.getConfig().CampfireBlock.howMuchCoalBuildingACampfireTakes, world, pos, player, hand);
                return ActionResult.SUCCESS;
            }
            if (ModConfig.getConfig().SoulCampfireBlock.enableBuildableSoulCampfire && item.isIn(BuildableCampfireMod.SOUL_CAMPFIRE_INGREDIENTS) && item.getCount() >= ModConfig.getConfig().SoulCampfireBlock.howMuchSoulSandBuildingASoulCampfireTakes) {
                makeCampfire(soulCampfire, ModConfig.getConfig().SoulCampfireBlock.soulCampfireIsLitWhenBuild, ModConfig.getConfig().SoulCampfireBlock.playSoundEffect, SoundEvents.BLOCK_SOUL_SAND_PLACE, ModConfig.getConfig().SoulCampfireBlock.howMuchSoulSandBuildingASoulCampfireTakes, world, pos, player, hand);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        Formatting color = ModConfig.getConfig().Gameplay.tooltipColor;
        String text = ModConfig.getConfig().Gameplay.tooltip;
        if (ModConfig.getConfig().Gameplay.enableItemTooltip){
            if (text == null || text.equals("")){
                tooltip.add(new TranslatableText("text.buildable_campfire.tooltip").formatted(color));
            } else {
                tooltip.add(new LiteralText(text).formatted(color));
                if (text.equals("easteregg") || text.equals("easter_egg")){
                    tooltip.add(new LiteralText("§message"));
                }
            }
        }
    }

    public Direction facing(BlockState state){
        if (state.get(FACING).equals(Direction.SOUTH)){
            return Direction.SOUTH;
        }
        if (state.get(FACING).equals(Direction.NORTH)){
            return Direction.NORTH;
        }
        if (state.get(FACING).equals(Direction.EAST)){
            return Direction.EAST;
        }
        if (state.get(FACING).equals(Direction.WEST)){
            return Direction.WEST;
        }
        return null;
    }

    public void pickLog(PlayerEntity player){
        BlockState state = this.getDefaultState();
        if (ModConfig.getConfig().Gameplay.playSoundWhenCampfireLogGetsPicked){
            player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
        }
        player.getInventory().insertStack(new ItemStack(this.asItem()));
    }

    public void makeCampfire(Block campfire, boolean lit, boolean playSound, SoundEvent sound, int cost, World world, BlockPos pos, PlayerEntity player, Hand hand){
        BlockState state = campfire.getDefaultState().with(CampfireBlock.WATERLOGGED, isNearWater(world, pos)).with(CampfireBlock.FACING, facing(world.getBlockState(pos)));
        ItemStack item = player.getStackInHand(hand);
        world.setBlockState(pos, state.with(CampfireBlock.LIT, lit));
        if (playSound) {
            player.playSound(sound, 1, 1);
        }
        player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
        if (!player.isCreative()) {
            item.decrement(cost);
        }
    }

    public static boolean isNearWater(WorldView world, BlockPos pos){
        BlockState upState = world.getBlockState(pos.up());
        BlockState downState = world.getBlockState(pos.down());
        BlockState southState = world.getBlockState(pos.south());
        BlockState northState = world.getBlockState(pos.north());
        BlockState eastState = world.getBlockState(pos.east());
        BlockState westState = world.getBlockState(pos.east());
        return isWater(upState) || isWater(downState) || isWater(southState) || isWater(northState) || isWater(eastState) || isWater(westState);
    }


    private static boolean isWater(BlockState state){
        return state.isOf(Blocks.WATER) || state.getFluidState().isOf(Fluids.WATER);
    }
}
