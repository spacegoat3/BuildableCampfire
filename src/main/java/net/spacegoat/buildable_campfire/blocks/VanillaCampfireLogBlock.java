package net.spacegoat.buildable_campfire.blocks;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class VanillaCampfireLogBlock extends CampfireLogBlock {

    public VanillaCampfireLogBlock(String id, Settings settings){
        super(id, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, settings);
        this.setDefaultState(this.getStateManager().getDefaultState());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        boolean west = state.get(FACING).equals(Direction.WEST);
        boolean east = state.get(FACING).equals(Direction.EAST);
        boolean north = state.get(FACING).equals(Direction.NORTH);
        boolean south = state.get(FACING).equals(Direction.SOUTH);
        boolean horizontal = west || east;
        boolean vertical = north || south;
        if (state.get(CAMPFIRE_LOGS).equals(1)){
            if (west){
                return HorizontalShapes.ONE_WEST_LOG;
            }
            if (east){
                return HorizontalShapes.ONE_EAST_LOG;
            }
            if (south){
                return VerticalShapes.ONE_SOUTH_LOG;
            }
            if (north){
                return VerticalShapes.ONE_NORTH_LOG;
            }
        }
        if (state.get(CAMPFIRE_LOGS).equals(2)){
            if (horizontal){
                return HorizontalShapes.HORIZONTAL_TWO_LOGS;
            }
            if (vertical){
                return VerticalShapes.TWO_VERTICAL_LOGS;
            }
        }
        if (state.get(CAMPFIRE_LOGS).equals(3)){
            if (west){
                return HorizontalShapes.THREE_WEST_LOGS;
            }
            if (east){
                return HorizontalShapes.THREE_EAST_LOGS;
            }
            if (south){
                return VerticalShapes.THREE_SOUTH_LOGS;
            }
            if (north){
                return VerticalShapes.THREE_NORTH_LOGS;
            }
        }
        if (state.get(CAMPFIRE_LOGS).equals(4)){
            if (horizontal){
                return HorizontalShapes.HORIZONTAL_FOUR_LOGS;
            }
            if (vertical){
                return VerticalShapes.FOUR_VERTICAL_LOGS;
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    public static class HorizontalShapes {
        public static final VoxelShape ONE_EAST_LOG = createCuboidShape(
                0, 0, 11, 16, 4, 15);
        public static final VoxelShape ONE_WEST_LOG = createCuboidShape(
                0, 0, 1, 16, 4, 5);

        public static final VoxelShape HORIZONTAL_TWO_LOGS = VoxelShapes.union(
                ONE_WEST_LOG,
                ONE_EAST_LOG);

        public static final VoxelShape THREE_EAST_LOGS = VoxelShapes.union(
                HORIZONTAL_TWO_LOGS,
                createCuboidShape(11, 3, 0, 15, 7, 16));
        public static final VoxelShape THREE_WEST_LOGS = VoxelShapes.union(
                HORIZONTAL_TWO_LOGS,
                createCuboidShape(1, 3, 0, 5, 7, 16));

        public static final VoxelShape HORIZONTAL_FOUR_LOGS = VoxelShapes.union(
                THREE_WEST_LOGS,
                THREE_EAST_LOGS);
    }

    public static class VerticalShapes {
        public static final VoxelShape ONE_NORTH_LOG = createCuboidShape(
                1, 0, 0, 5, 4, 16);
        public static final VoxelShape ONE_SOUTH_LOG = createCuboidShape(
                11, 0, 0, 15, 4, 16);

        public static final VoxelShape TWO_VERTICAL_LOGS = VoxelShapes.union(
                ONE_NORTH_LOG,
                ONE_SOUTH_LOG);

        public static final VoxelShape THREE_NORTH_LOGS = VoxelShapes.union(
                TWO_VERTICAL_LOGS,
                createCuboidShape(0, 3, 11, 16, 7, 15));
        public static final VoxelShape THREE_SOUTH_LOGS = VoxelShapes.union(
                TWO_VERTICAL_LOGS,
                createCuboidShape(0, 3, 1, 16, 7, 5));

        public static final VoxelShape FOUR_VERTICAL_LOGS = VoxelShapes.union(
                THREE_NORTH_LOGS,
                THREE_SOUTH_LOGS);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CAMPFIRE_LOGS, FACING, WATERLOGGED);
    }
}
