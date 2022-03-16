package net.spacegoat.buildable_campfire;

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
import net.minecraft.state.StateManager;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.spacegoat.buildable_campfire.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import potionstudios.byg.common.block.BYGBlocks;
import potionstudios.byg.common.item.BYGItems;

import java.util.List;

public class CampfireLogBlock extends Block implements Waterloggable {

    public String id;
    public Block campfire;
    public Block soulCampfire;

    public final String getId(){
        return this.id;
    }

    public CampfireLogBlock(String id, Block campfire, @Nullable Block soulCampfire, Settings settings) {
        super(settings.mapColor(campfire.getDefaultMapColor()));
        this.id = id + "_log";
        this.campfire = campfire;
        this.soulCampfire = soulCampfire;
        this.setDefaultState(this.getStateManager().getDefaultState().with(WATERLOGGED, false));
    }

    public static final IntProperty CAMPFIRE_LOGS = IntProperty.of("campfire_logs", 1,4);
    public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");
    public static final DirectionProperty FACING = DirectionProperty.of("facing");

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CAMPFIRE_LOGS, WATERLOGGED, FACING);
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
            }
            if (state.get(CAMPFIRE_LOGS).equals(2)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 1));
                pickLog(player);
            }
            if (state.get(CAMPFIRE_LOGS).equals(3)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 2));
                pickLog(player);
            }
            if (state.get(CAMPFIRE_LOGS).equals(4)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 3));
                pickLog(player);
            }
            if (state.get(CAMPFIRE_LOGS).equals(5)){
                world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 4));
                pickLog(player);
            }
            return ActionResult.SUCCESS;
        }
        if (state.get(CAMPFIRE_LOGS).equals(4)) {
            if (ModConfig.getConfig().CampfireBlock.enableBuildableCampfire && item.isIn(BuildableCampfire.CAMPFIRE_INGREDIENTS) && item.getCount() >= ModConfig.getConfig().CampfireBlock.howMuchCoalBuildingACampfireCosts) {
                makeCampfire(campfire, ModConfig.getConfig().CampfireBlock.campfireIsLitWhenBuild, ModConfig.getConfig().CampfireBlock.playTuffSound, SoundEvents.BLOCK_TUFF_PLACE, ModConfig.getConfig().CampfireBlock.howMuchCoalBuildingACampfireCosts, world, pos, player, hand);
                return ActionResult.SUCCESS;
            }
            if (ModConfig.getConfig().SoulCampfireBlock.enableBuildableSoulCampfire && item.isIn(BuildableCampfire.SOUL_CAMPFIRE_INGREDIENTS) && item.getCount() >= ModConfig.getConfig().SoulCampfireBlock.howMuchSoulSandBuildingASoulCampfireCosts) {
                if (soulCampfire != null) {
                    makeCampfire(soulCampfire, ModConfig.getConfig().SoulCampfireBlock.soulCampfireIsLitWhenBuild, ModConfig.getConfig().SoulCampfireBlock.playSoulSandSound, SoundEvents.BLOCK_SOUL_SAND_PLACE, ModConfig.getConfig().SoulCampfireBlock.howMuchSoulSandBuildingASoulCampfireCosts, world, pos, player, hand);
                    return ActionResult.SUCCESS;
                } else {
                    BuildableCampfire.LOGGER.debug("Soul Campfire for Campfire Log Block returned as 'null', method can't work.");
                }
            }
            if (ModConfig.getConfig().ModdedCampfires.BYGCampfires.BoricCampfire.enableBuildableBoricCampfire && item.isOf(BYGItems.BRIM_POWDER) && item.getCount() >= ModConfig.getConfig().ModdedCampfires.BYGCampfires.BoricCampfire.howMuchBrimPowderBuildingABoricCampfireCosts){
                makeCampfire(BYGBlocks.BORIC_CAMPFIRE, ModConfig.getConfig().ModdedCampfires.BYGCampfires.BoricCampfire.boricCampfireIsLitWhenBuild, ModConfig.getConfig().ModdedCampfires.BYGCampfires.BoricCampfire.playSandSound, SoundEvents.BLOCK_SAND_PLACE, ModConfig.getConfig().ModdedCampfires.BYGCampfires.BoricCampfire.howMuchBrimPowderBuildingABoricCampfireCosts, world, pos, player, hand);
                return ActionResult.SUCCESS;
            }
            if (ModConfig.getConfig().ModdedCampfires.BYGCampfires.CrypticCampfire.enableBuildableCrypticCampfire && item.isOf(BYGItems.CRYPTIC_MAGMA_BLOCK) && item.getCount() >= ModConfig.getConfig().ModdedCampfires.BYGCampfires.CrypticCampfire.howMuchCrypticMagmaBlockBuildingACrypticCampfireCosts){
                makeCampfire(BYGBlocks.CRYPTIC_CAMPFIRE, ModConfig.getConfig().ModdedCampfires.BYGCampfires.CrypticCampfire.crypticCampfireIsLitWhenBuild, ModConfig.getConfig().ModdedCampfires.BYGCampfires.CrypticCampfire.playTuffSound, SoundEvents.BLOCK_TUFF_PLACE, ModConfig.getConfig().ModdedCampfires.BYGCampfires.CrypticCampfire.howMuchCrypticMagmaBlockBuildingACrypticCampfireCosts, world, pos, player, hand);
                player.playSound(SoundEvents.BLOCK_STONE_HIT, 0.5F, 1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
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
        if (!player.isCreative()) {
            item.decrement(cost);
        }
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
                0, 0, 11, 16, 4, 15
        );
        public static final VoxelShape ONE_WEST_LOG = createCuboidShape(
                0, 0, 1, 16, 4, 5
        );
        public static final VoxelShape HORIZONTAL_TWO_LOGS = VoxelShapes.union(
                ONE_WEST_LOG,
                ONE_EAST_LOG
        );
        public static final VoxelShape THREE_EAST_LOGS = VoxelShapes.union(
                HORIZONTAL_TWO_LOGS,
                createCuboidShape(11, 3, 0, 15, 7, 16)
        );
        public static final VoxelShape THREE_WEST_LOGS = VoxelShapes.union(
                HORIZONTAL_TWO_LOGS,
                createCuboidShape(1, 3, 0, 5, 7, 16)
        );
        public static final VoxelShape HORIZONTAL_FOUR_LOGS = VoxelShapes.union(
                THREE_WEST_LOGS,
                THREE_EAST_LOGS
        );
    }

    public static class VerticalShapes {
        public static final VoxelShape ONE_NORTH_LOG = createCuboidShape(
                1, 0, 0, 5, 4, 16
        );
        public static final VoxelShape ONE_SOUTH_LOG = createCuboidShape(
                11, 0, 0, 15, 4, 16
        );
        public static final VoxelShape TWO_VERTICAL_LOGS = VoxelShapes.union(
                ONE_NORTH_LOG,
                ONE_SOUTH_LOG
        );
        public static final VoxelShape THREE_NORTH_LOGS = VoxelShapes.union(
                TWO_VERTICAL_LOGS,
                createCuboidShape(0, 3, 11, 16, 7, 15)
        );
        public static final VoxelShape THREE_SOUTH_LOGS = VoxelShapes.union(
                TWO_VERTICAL_LOGS,
                createCuboidShape(0, 3, 1, 16, 7, 5)
        );
        public static final VoxelShape FOUR_VERTICAL_LOGS = VoxelShapes.union(
                THREE_NORTH_LOGS,
                THREE_SOUTH_LOGS
        );
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
