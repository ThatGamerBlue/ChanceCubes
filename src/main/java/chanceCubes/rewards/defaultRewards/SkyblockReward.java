package chanceCubes.rewards.defaultRewards;

import chanceCubes.CCubesCore;
import chanceCubes.util.RewardsUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.OakTree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.Random;

public class SkyblockReward extends BaseCustomReward
{
	// @formatter:off

	private static final Random TREE_RAND = new Random(System.currentTimeMillis());
	
	ItemStack[] chestStuff = { 
		new ItemStack(Items.STRING, 12), new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BONE), new ItemStack(Items.SUGAR_CANE),
		new ItemStack(Blocks.RED_MUSHROOM), new ItemStack(Blocks.ICE, 2), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Blocks.OAK_SAPLING),
		new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Items.MELON), new ItemStack(Blocks.CACTUS), new ItemStack(Blocks.OAK_LOG, 6)
		};

	// @formatter:on
	public SkyblockReward()
	{
		super(CCubesCore.MODID + ":sky_block", 10);
	}

	@Override
	public void trigger(World world, BlockPos pos, PlayerEntity player, Map<String, Object> settings)
	{
		if(world.isRemote())
			return;

		int skyblockHeight = world.func_234938_ad_() - 16;
		if(!world.func_230315_m_().hasSkyLight())
			skyblockHeight = pos.getY();
		Block b = Blocks.DIRT;
		BlockPos skyblockPos = new BlockPos(pos.getX(), skyblockHeight, pos.getZ());
		for(int i = 0; i < 3; i++)
		{
			if(i == 2)
				b = Blocks.GRASS_BLOCK;
			for(int c = 0; c < 3; c++)
			{
				int xOffset = c == 0 ? -1 : 2;
				int zOffset = c == 2 ? 2 : -1;
				for(int xx = 0; xx < 3; xx++)
				{
					for(int zz = 0; zz < 3; zz++)
					{
						world.setBlockState(skyblockPos.add(xOffset + xx, i, zOffset + zz), b.getDefaultState(), 3);
						// RewardsUtil.placeBlock(b.getDefaultState(), world, skyblockPos.add(xOffset + xx, i, zOffset + zz));
					}
				}
			}
		}
		RewardsUtil.placeBlock(Blocks.BEDROCK.getDefaultState(), world, skyblockPos.add(0, 1, 0));

		OakTree tree = new OakTree();
		tree.attemptGrowTree((ServerWorld) world, ((ServerWorld) world).getChunkProvider().getChunkGenerator(), skyblockPos.add(3, 3, 3), Blocks.OAK_SAPLING.getDefaultState().with(SaplingBlock.STAGE, 1), TREE_RAND);

		RewardsUtil.placeBlock(Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.WEST), world, skyblockPos.add(-1, 3, 0));
		ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(skyblockPos.add(-1, 3, 0));
		for(int i = 0; i < chestStuff.length; i++)
		{
			int slot = ((i < 4 ? 0 : i < 8 ? 1 : 2) * 9) + i % 4;
			chest.setInventorySlotContents(slot, chestStuff[i].copy());
		}

		player.setPositionAndUpdate(pos.getX(), skyblockHeight + 3, pos.getZ());
	}
}