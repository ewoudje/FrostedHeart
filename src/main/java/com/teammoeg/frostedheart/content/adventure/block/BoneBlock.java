package com.teammoeg.frostedheart.content.adventure.block;

import com.simibubi.create.foundation.utility.VoxelShaper;
import com.teammoeg.frostedheart.FHItems;
import com.teammoeg.frostedheart.base.block.FHBaseBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class BoneBlock extends FHBaseBlock {
    private static IntegerProperty BNT = IntegerProperty.create("bonetype", 0, 5);
    static final VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 16, 3, 16);
    static final VoxelShape shape2 = Block.makeCuboidShape(0, 0, 0, 16, 15, 16);
    public BoneBlock(AbstractBlock.Properties blockProps) {
        super(blockProps);
        this.setDefaultState(this.stateContainer.getBaseState().with(BNT, 0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BNT);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Integer finalType = Math.abs(RANDOM.nextInt()) % 5;
        BlockState newState = this.stateContainer.getBaseState().with(BNT, finalType);
        worldIn.setBlockState(pos, newState);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        Integer count = Math.abs(RANDOM.nextInt()) % 5 + 1;
        spawnAsEntity(worldIn, pos, new ItemStack(Items.BONE, count));
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        super.onExplosionDestroy(worldIn, pos, explosionIn);
        Integer count = Math.abs(RANDOM.nextInt()) % 2 + 1;
        spawnAsEntity(worldIn, pos, new ItemStack(Items.BONE, count));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
                                        ISelectionContext context) {
        if (state.get(BNT) <= 0)
            return shape;
        return shape2;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(BNT) <= 0)
            return shape;
        return shape2;
    }
}
