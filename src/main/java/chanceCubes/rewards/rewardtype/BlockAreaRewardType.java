package chanceCubes.rewards.rewardtype;

import chanceCubes.rewards.rewardparts.BlockAreaPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BlockAreaRewardType extends BaseRewardType<BlockAreaPart>
{
	public BlockAreaRewardType(BlockAreaPart... parts)
	{
		super(parts);
	}

	@Override
	protected void trigger(BlockAreaPart blockPart, World world, int x, int y, int z, PlayerEntity player)
	{
		blockPart.placeBlocks(world, player, x, y, z);
	}
}
