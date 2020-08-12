package chanceCubes.rewards.defaultRewards;

import chanceCubes.CCubesCore;
import chanceCubes.util.CCubesDamageSource;
import chanceCubes.util.RewardsUtil;
import chanceCubes.util.Scheduler;
import chanceCubes.util.Task;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Map;

public class DigBuildReward extends BaseCustomReward
{

	public DigBuildReward()
	{
		super(CCubesCore.MODID + ":dig_build", -5);
	}

	@Override
	public void trigger(World world, BlockPos pos, PlayerEntity player, Map<String, Object> settings)
	{
		int min = super.getSettingAsInt(settings, "min", 5, 0, 100);
		int max = super.getSettingAsInt(settings, "max", 25, 0, 100);

		//Because someone will do it...
		if(min > max)
		{
			int swap = min;
			min = max;
			max = swap;
		}

		int initalY = player.getPosition().getY();
		int distance = RewardsUtil.rand.nextInt(max - min) + min;
		boolean up = (initalY + distance <= 150) && ((initalY - distance < 2) || RewardsUtil.rand.nextBoolean());

		player.sendMessage(new StringTextComponent("Quick! Go " + (up ? "up " : "down ") + distance + " blocks!"), player.getUniqueID());
		player.sendMessage(new StringTextComponent("You have " + (distance + 3) + " seconds!"), player.getUniqueID());

		Scheduler.scheduleTask(new Task("Dig_Build_Reward_Delay", (distance + 3) * 20, 5)
		{
			@Override
			public void callback()
			{
				player.world.createExplosion(player, player.getPosX(), player.getPosY(), player.getPosZ(), 1.0F, Explosion.Mode.NONE);
				player.attackEntityFrom(CCubesDamageSource.DIG_BUILD_FAIL, Float.MAX_VALUE);
			}

			@Override
			public void update()
			{
				if(up && player.getPosition().getY() >= initalY + distance)
				{
					player.sendMessage(new StringTextComponent("Good Job!"), player.getUniqueID());
					player.sendMessage(new StringTextComponent("Here, have a item!"), player.getUniqueID());
					player.world.addEntity(new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), new ItemStack(RewardsUtil.getRandomItem(), 1)));
					Scheduler.removeTask(this);
				}
				else if(!up && player.getPosition().getY() <= initalY - distance)
				{
					player.sendMessage(new StringTextComponent("Good Job!"), player.getUniqueID());
					player.sendMessage(new StringTextComponent("Here, have a item!"), player.getUniqueID());
					player.world.addEntity(new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), new ItemStack(RewardsUtil.getRandomItem(), 1)));
					Scheduler.removeTask(this);
				}

				if(this.delayLeft % 20 == 0)
					this.showTimeLeft(player, STitlePacket.Type.ACTIONBAR);
			}
		});
	}
}