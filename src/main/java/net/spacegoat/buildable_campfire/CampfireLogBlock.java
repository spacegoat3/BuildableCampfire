package net.spacegoat.buildable_campfire;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.spacegoat.buildable_campfire.config.ModConfig;

import java.util.Objects;

public class CampfireLogBlock extends Block implements Waterloggable {
    public CampfireLogBlock(Settings settings){
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(WATERLOGGED, false).with(CAMPFIRE_LOGS, 1));
    }
    
    public static final IntProperty CAMPFIRE_LOGS = IntProperty.of("campfire_logs", 1,4);
    public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");
    public static final DirectionProperty FACING = DirectionProperty.of("facing");

    public void pickLog(PlayerEntity player){
        BlockState state = this.getDefaultState();
        player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
        player.getInventory().insertStack(new ItemStack(this.asItem()));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        boolean west = state.get(FACING).equals(Direction.WEST);
        boolean east = state.get(FACING).equals(Direction.EAST);
        boolean north = state.get(FACING).equals(Direction.NORTH);
        boolean south = state.get(FACING).equals(Direction.SOUTH);
        if (state.get(CAMPFIRE_LOGS).equals(1)){
            if (west){
                return WestShapes.ONE_LOG;
            }
            if (east){
                return EastShapes.ONE_LOG;
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }


    public static class EastShapes {
        public static final VoxelShape ONE_LOG = createCuboidShape(
                0, 0, 11, 16, 4, 15
        );
    }
    public static class WestShapes {
        public static final VoxelShape ONE_LOG = createCuboidShape(
                0, 0, 1, 16, 4, 5
        );
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
        ItemStack item = player.getStackInHand(hand);
        BlockState state = blockState.with(WATERLOGGED, isNearWater(world, pos));
        if (item.isEmpty() && ModConfig.getConfig().Gameplay.campfireLogsArePickable){
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
        if (item.isIn(ModMain.CAMPFIRE_INGREDIENTS)){
            makeCampfire(Blocks.CAMPFIRE, ModConfig.getConfig().CampfireBlock.playSoundEffect, SoundEvents.BLOCK_TUFF_PLACE, ModConfig.getConfig().CampfireBlock.howMuchCoalBuildingACampfireTakes, world, pos, player, hand);
            return ActionResult.SUCCESS;
        }
        if (item.isIn(ModMain.SOUL_CAMPFIRE_INGREDIENTS)){
            makeCampfire(Blocks.SOUL_CAMPFIRE, ModConfig.getConfig().SoulCampfireBlock.playSoundEffect, SoundEvents.BLOCK_SOUL_SAND_PLACE, ModConfig.getConfig().SoulCampfireBlock.howMuchSoulSandBuildingASoulCampfireTakes, world, pos, player, hand);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public void makeCampfire(Block campfire, boolean playSound, SoundEvent sound, int cost, World world, BlockPos pos, PlayerEntity player, Hand hand){
        BlockState state = campfire.getDefaultState().with(CampfireBlock.WATERLOGGED, isNearWater(world, pos));
        world.setBlockState(pos, state.with(CampfireBlock.LIT, false));
        if (playSound){
            player.playSound(sound, 1, 1);
        }
        player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
        player.getStackInHand(hand).decrement(cost);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Direction playerDirection = context.getPlayerFacing();
        if (state.isOf(this)) {
            return state.cycle(CAMPFIRE_LOGS);
        }
        return this.getDefaultState().with(WATERLOGGED, isNearWater(world, pos)).with(FACING, playerDirection.getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CAMPFIRE_LOGS, FACING, WATERLOGGED);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(CAMPFIRE_LOGS) < 4) {
            return true;
        }
        return super.canReplace(state, context);
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

    public boolean isNearWater(WorldView world, BlockPos pos){
        BlockState upState = world.getBlockState(pos.up());
        BlockState downState = world.getBlockState(pos.down());
        BlockState southState = world.getBlockState(pos.south());
        BlockState northState = world.getBlockState(pos.north());
        BlockState eastState = world.getBlockState(pos.east());
        BlockState westState = world.getBlockState(pos.east());
        return isWater(upState) || isWater(downState) || isWater(southState) || isWater(northState) || isWater(eastState) || isWater(westState);
    }


    private boolean isWater(BlockState state){
        return state.isOf(Blocks.WATER) || state.getFluidState().isOf(Fluids.WATER);
    }
}
