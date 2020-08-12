package chanceCubes.rewards.defaultRewards;

import chanceCubes.CCubesCore;
import chanceCubes.util.RewardsUtil;
import chanceCubes.util.Scheduler;
import chanceCubes.util.Task;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.Map;

public class WaitForItReward extends BaseCustomReward
{
	public WaitForItReward()
	{
		super(CCubesCore.MODID + ":wait_for_it", -30);
	}

	@Override
	public void trigger(final World world, BlockPos pos, final PlayerEntity player, Map<String, Object> settings)
	{
		player.sendMessage(new StringTextComponent("Wait for it......."), player.getUniqueID());

		int minDuration = super.getSettingAsInt(settings, "min_duration", 1000, 0, Integer.MAX_VALUE - 1);
		int maxDuration = minDuration - super.getSettingAsInt(settings, "max_duration", 5000, 1, Integer.MAX_VALUE);
		if(maxDuration < 1)
			maxDuration = 1;

		Scheduler.scheduleTask(new Task("Wait For It", RewardsUtil.rand.nextInt(maxDuration) + minDuration)
		{
			@Override
			public void callback()
			{
				int reward = RewardsUtil.rand.nextInt(3);
				player.sendMessage(new StringTextComponent("NOW!"), player.getUniqueID());

				if(reward == 0)
				{
					world.addEntity(new TNTEntity(world, player.getPosX(), player.getPosY() + 1, player.getPosZ(), null));
				}
				else if(reward == 1)
				{
					CreeperEntity ent = EntityType.CREEPER.create(world);
					ent.setLocationAndAngles(player.getPosX(), player.getPosY() + 1, player.getPosZ(), 0, 0);
					ent.onStruckByLightning(null);
					world.addEntity(ent);
				}
				else if(reward == 2)
				{
					RewardsUtil.placeBlock(Blocks.BEDROCK.getDefaultState(), world, new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ()));
				}
				else if(reward == 3)
				{
					RewardsUtil.placeBlock(Blocks.EMERALD_ORE.getDefaultState(), world, new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ()));
				}
				else if(reward == 4)
				{
					ZombieEntity zomb = EntityType.ZOMBIE.create(world);
					zomb.setChild(true);
					zomb.addPotionEffect(new EffectInstance(Effects.SPEED, 100000, 0));
					zomb.addPotionEffect(new EffectInstance(Effects.STRENGTH, 100000, 0));
					world.addEntity(zomb);
				}
			}
		});
	}
}
