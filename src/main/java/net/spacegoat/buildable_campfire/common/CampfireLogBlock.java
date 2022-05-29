package net.spacegoat.buildable_campfire.common;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.TagKey;
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
import net.spacegoat.buildable_campfire.config.BCConfig;
import org.jetbrains.annotations.Nullable;
import potionstudios.byg.BYG;
import potionstudios.byg.common.block.BYGBlocks;
import potionstudios.byg.common.item.BYGItems;

import java.util.List;

public class CampfireLogBlock extends Block implements Waterloggable {
    public Block campfire;
    public Block soulCampfire;


    public CampfireLogBlock(Block campfire, Block soulCampfire, Material material) {
        super(FabricBlockSettings.of(material).sounds(campfire.getSoundGroup(campfire.getDefaultState()))
                .strength(campfire.getHardness() - 0.5F));
        this.campfire = campfire;
        this.soulCampfire = soulCampfire;
        this.setDefaultState(this.getStateManager().getDefaultState().with(WATERLOGGED, false));
    }

    public static BlockState blockState(boolean increasing, BlockState state) {
        var logs = state.get(CAMPFIRE_LOGS);
        if (!increasing){
            switch (logs){
                case 2 -> state.with(CAMPFIRE_LOGS, 1);
                case 3 -> state.with(CAMPFIRE_LOGS, 2);
                case 4 -> state.with(CAMPFIRE_LOGS, 3);
            }
        } else {
            switch (logs){
                case 1 -> state.with(CAMPFIRE_LOGS, 2);
                case 2 -> state.with(CAMPFIRE_LOGS, 3);
                case 3 -> state.with(CAMPFIRE_LOGS, 4);
            }
        }
        return state;
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
        BCConfig config = BCConfig.getConfig();
        if ((item.isEmpty() && item.isOf(this.asItem())) && config.Gameplay.campfireLogsArePickable){
            switch (state.get(CAMPFIRE_LOGS)){
                case 1 -> world.removeBlock(pos, false);
                case 2 -> world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 1));
                case 3 -> world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 2));
                case 4 -> world.setBlockState(pos, state.with(CAMPFIRE_LOGS, 3));
            }
            pickLog(player);
            return ActionResult.SUCCESS;
        }
        if (state.get(CAMPFIRE_LOGS).equals(4)){
            BCConfig.CampfireBlockConfig campfire = BCConfig.getConfig().CampfireBlock;
            BCConfig.SoulCampfireBlockConfig soulCampfire = BCConfig.getConfig().SoulCampfireBlock;
            BCConfig.ModdedCampfiresConfig.BYGCampfiresConfig.BoricCampfireBlockConfig boricCampfire = BCConfig.getConfig().ModdedCampfires.BYGCampfires.BoricCampfire;
            BCConfig.ModdedCampfiresConfig.BYGCampfiresConfig.CrypticCampfireBlockConfig crypticCampfire = BCConfig.getConfig().ModdedCampfires.BYGCampfires.CrypticCampfire;
            if (condition(campfire.enableBuildableCampfire, BCTags.CAMPFIRE_INGREDIENTS, campfire.howMuchCoalBuildingACampfireCosts, item)){
                makeCampfire(Blocks.CAMPFIRE, campfire.campfireIsLitWhenBuild, campfire.playTuffSound, SoundEvents.BLOCK_TUFF_PLACE, campfire.howMuchCoalBuildingACampfireCosts, world, pos, player, hand);
            }
            if (condition(soulCampfire.enableBuildableSoulCampfire, BCTags.SOUL_CAMPFIRE_INGREDIENTS, soulCampfire.howMuchSoulSandBuildingASoulCampfireCosts, item)){
                makeCampfire(Blocks.SOUL_CAMPFIRE, soulCampfire.soulCampfireIsLitWhenBuild, soulCampfire.playSoulSandSound, SoundEvents.BLOCK_SOUL_SAND_PLACE, soulCampfire.howMuchSoulSandBuildingASoulCampfireCosts, world, pos, player, hand);
            }
            if (condition(boricCampfire.enableBuildableBoricCampfire, BYGItems.BRIM_POWDER, boricCampfire.howMuchBrimPowderBuildingABoricCampfireCosts, item)){
                makeCampfire(BYGBlocks.BORIC_CAMPFIRE, boricCampfire.boricCampfireIsLitWhenBuild, boricCampfire.playSandSound, SoundEvents.BLOCK_SAND_PLACE, boricCampfire.howMuchBrimPowderBuildingABoricCampfireCosts, world, pos, player, hand);
            }
            if (condition(crypticCampfire.enableBuildableCrypticCampfire, BYGItems.CRYPTIC_MAGMA_BLOCK.asItem(), crypticCampfire.howMuchCrypticMagmaBlockBuildingACrypticCampfireCosts, item)){
                makeCampfire(BYGBlocks.CRYPTIC_CAMPFIRE, crypticCampfire.crypticCampfireIsLitWhenBuild, crypticCampfire.playTuffSound, SoundEvents.BLOCK_TUFF_PLACE, crypticCampfire.howMuchCrypticMagmaBlockBuildingACrypticCampfireCosts, world, pos, player, hand);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public boolean condition(boolean enableOption, Item ingredient, int countOption, ItemStack stack){
        return enableOption && stack.isOf(ingredient) && stack.getCount() >= countOption;
    }
    public boolean condition(boolean enableOption, TagKey<Item> tag, int countOption, ItemStack stack){
        return enableOption && stack.isIn(tag) && stack.getCount() >= countOption;
    }

    public void pickLog(PlayerEntity player){
        BlockState state = this.getDefaultState();
        if (BCConfig.getConfig().Gameplay.playSoundWhenCampfireLogGetsPicked){
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
        Formatting color = BCConfig.getConfig().Gameplay.tooltipConfig.tooltipColor;
        String text = BCConfig.getConfig().Gameplay.tooltipConfig.tooltip;
        if (BCConfig.getConfig().Gameplay.enableItemTooltip){
            if (text == null || text.equals("")){
                tooltip.add(new TranslatableText("text.buildable_campfire.tooltip1").formatted(color));
                tooltip.add(new TranslatableText("text.buildable_campfire.tooltip2").formatted(color));
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
