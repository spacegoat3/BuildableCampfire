package net.spacegoat.buildable_campfire;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
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

public class CampfireLogBlock extends Block implements Waterloggable {
    public CampfireLogBlock(Settings settings){
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateManager().getDefaultState().with(WATERLOGGED, false).with(CAMPFIRE_LOGS, 1))))));
    }
    public static final int MAX_LOGS = 4;
    public static final IntProperty CAMPFIRE_LOGS = IntProperty.of("campfire_logs", 1,4);
    public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");

    public static final VoxelShape ONE_LOG = createCuboidShape(
            11,0,0,15,4,16
    );
    public static final VoxelShape TWO_LOGS = VoxelShapes.union(
            createCuboidShape(11,0,0,15,4,16),
            createCuboidShape(1,0,0,5,4,16)
    );
    public static final VoxelShape THREE_LOGS = VoxelShapes.union(
            createCuboidShape(11,0,0,15,4,16),
            createCuboidShape(1,0,0,5,4,16),
            createCuboidShape(0,3,1,16,7,5)
    );
    public static final VoxelShape FOUR_LOGS = VoxelShapes.union(
            createCuboidShape(11,0,0,15,4,16),
            createCuboidShape(1,0,0,5,4,16),
            createCuboidShape(0,3,1,16,7,5),
            createCuboidShape(0,3,11,16,7,15)
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        switch (state.get(CAMPFIRE_LOGS)){
            default -> {
                return ONE_LOG;
            }
            case 2 -> {
                return TWO_LOGS;
            }
            case 3 -> {
                return THREE_LOGS;
            }
            case 4 -> {
                return FOUR_LOGS;
            }
        }
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack playerItem = player.getStackInHand(hand);
        if (playerItem.getItem() == Items.COAL || playerItem.getItem() == Items.CHARCOAL && state.get(CAMPFIRE_LOGS).equals(4) && ModConfig.getConfig().campfireBlock.enableCampfireLog){
            replace(state, Blocks.CAMPFIRE.getDefaultState().with(Properties.LIT, ModConfig.getConfig().campfireBlock.campfireIsLitWhenBuild), world, pos, 0);
            playerItem.decrement(ModConfig.getConfig().campfireBlock.howMuchCoalBuildingCampfireTakes);
        }
        if (playerItem.getItem() == Blocks.SOUL_SAND.asItem() && state.get(CAMPFIRE_LOGS).equals(4) && ModConfig.getConfig().soulCampfireBlock.enableSoulCampfireLog){
            replace(state, Blocks.SOUL_CAMPFIRE.getDefaultState().with(Properties.LIT, ModConfig.getConfig().soulCampfireBlock.soulCampfireIsLitWhenBuild), world, pos, 0);
            playerItem.decrement(ModConfig.getConfig().soulCampfireBlock.howMuchCoalBuildingSoulCampfireTakes);
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world;
        PlayerEntity player;
        Hand hand;
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return (BlockState)blockState.cycle(CAMPFIRE_LOGS);
        }
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)super.getPlacementState(ctx).with(WATERLOGGED, bl);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CAMPFIRE_LOGS, WATERLOGGED);
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
}
