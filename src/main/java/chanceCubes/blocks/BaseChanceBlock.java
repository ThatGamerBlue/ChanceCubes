package chanceCubes.blocks;

import chanceCubes.CCubesCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseChanceBlock extends Block
{
	public BaseChanceBlock(Properties builder, String name)
	{
		super(builder);
		this.setRegistryName(CCubesCore.MODID, name);
	}

	@Override
	public boolean canDropFromExplosion(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
	{
		return false;
	}

	protected static Properties getBuilder()
	{
		return Properties.create(Material.EARTH).hardnessAndResistance(0.5f, Float.MAX_VALUE);
	}
}