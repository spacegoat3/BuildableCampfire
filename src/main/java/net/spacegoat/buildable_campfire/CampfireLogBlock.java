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
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
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

    //Since HorizontalFacingBlock or the Direction Properties doesn't work with Campfire Log Block, we used Boolean Properties to identify directions.
    public static final BooleanProperty FACING_EAST = BooleanProperty.of("east");
    public static final BooleanProperty FACING_WEST = BooleanProperty.of("west");
    public static final BooleanProperty FACING_SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty FACING_NORTH = BooleanProperty.of("north");

    public void pickLog(PlayerEntity player){
        BlockState state = this.getDefaultState();
        player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
        player.getInventory().insertStack(new ItemStack(this.asItem()));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        boolean west = state.get(FACING_WEST).equals(true);
        boolean east = state.get(FACING_EAST).equals(true);
        boolean north = state.get(FACING_NORTH).equals(true);
        boolean south = state.get(FACING_NORTH).equals(true);
        if (state.get(CAMPFIRE_LOGS).equals(1)){
            if (west){
                return WestShapes.ONE_LOG;
            }
        }
        if (state.get(CAMPFIRE_LOGS).equals(2)){
            if (west || east){
                return TWO_HORIZONTAL_LOGS;
            }
        }
        if (state.get(CAMPFIRE_LOGS).equals(3)){
            if (west){
                return WestShapes.THREE_LOGS;
            }
        }
        if (state.get(CAMPFIRE_LOGS).equals(4)){
            if (west || east){
                return FOUR_HORIZONTAL_LOGS;
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }


    public static final VoxelShape TWO_HORIZONTAL_LOGS = VoxelShapes.union(
            createCuboidShape(11,0,0,15,4,16),
            createCuboidShape(1,0,0,5,4,16)
    );
    public static final VoxelShape FOUR_HORIZONTAL_LOGS = VoxelShapes.union(
            TWO_HORIZONTAL_LOGS,
            createCuboidShape(0,3,1,16,7,5),
            createCuboidShape(0, 3, 11, 16, 7, 15)
    );
    public static class WestShapes {
        public static final VoxelShape ONE_LOG = createCuboidShape(
                11,0,0,15,4,16
        );
        public static final VoxelShape THREE_LOGS = VoxelShapes.union(
                TWO_HORIZONTAL_LOGS,
                createCuboidShape(0,3,1,16,7,5)
        );
    }
    public static class EastShapes {
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
        return Objects.requireNonNull(super.getPlacementState(context)).with(WATERLOGGED, isNearWater(world, pos))
                .with(FACING_EAST, playerDirection == Direction.WEST)
                .with(FACING_WEST, playerDirection == Direction.EAST)
                .with(FACING_NORTH, playerDirection == Direction.SOUTH)
                .with(FACING_SOUTH, playerDirection == Direction.NORTH);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CAMPFIRE_LOGS, WATERLOGGED, FACING_WEST, FACING_EAST, FACING_NORTH, FACING_SOUTH);
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
