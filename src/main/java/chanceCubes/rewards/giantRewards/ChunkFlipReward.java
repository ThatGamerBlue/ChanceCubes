package chanceCubes.rewards.giantRewards;

import chanceCubes.CCubesCore;
import chanceCubes.blocks.CCubesBlocks;
import chanceCubes.config.CCubesSettings;
import chanceCubes.rewards.defaultRewards.BaseCustomReward;
import chanceCubes.sounds.CCubesSounds;
import chanceCubes.util.RewardsUtil;
import chanceCubes.util.Scheduler;
import chanceCubes.util.Task;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.Map;

public class ChunkFlipReward extends BaseCustomReward
{
	public ChunkFlipReward()
	{
		super(CCubesCore.MODID + ":chunk_flip", 0);
	}

	@Override
	public void trigger(World world, BlockPos pos, PlayerEntity player, Map<String, Object> settings)
	{
		int z = (pos.getZ() >> 4) << 4;
		int x = (pos.getX() >> 4) << 4;
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), CCubesSounds.GIANT_CUBE_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
		player.sendMessage(new StringTextComponent("Inception!!!!"), player.getUniqueID());
		Scheduler.scheduleTask(new Task("Chunk_Flip_Delay", -1, 10)
		{
			private int y = 0;

			@Override
			public void callback()
			{
			}

			@Override
			public void update()
			{
				if(y >= world.func_234938_ad_() / 2)
				{
					Scheduler.removeTask(this);
					return;
				}

				for(int zz = 0; zz < 16; zz++)
				{
					for(int xx = 0; xx < 16; xx++)
					{
						BlockPos pos1 = new BlockPos(x + xx, y, z + zz);
						BlockPos pos2 = new BlockPos(x + xx, world.func_234938_ad_() - y, z + zz);
						BlockState b = world.getBlockState(pos1);
						BlockState b2 = world.getBlockState(pos2);

						TileEntity te1 = world.getTileEntity(pos1);
						TileEntity te2 = world.getTileEntity(pos2);

						if(!b.getBlock().equals(Blocks.GRAVEL) && !b.getBlock().equals(CCubesBlocks.GIANT_CUBE) && !RewardsUtil.isBlockUnbreakable(world, pos) && !CCubesSettings.nonReplaceableBlocks.contains(world.getBlockState(pos)))
						{
							world.setBlockState(pos1, b2, 2);
							world.setBlockState(pos2, b, 2);
							world.setTileEntity(pos2, te1);
							world.setTileEntity(pos1, te2);
						}
					}
				}
				y++;
			}
		});
	}
}