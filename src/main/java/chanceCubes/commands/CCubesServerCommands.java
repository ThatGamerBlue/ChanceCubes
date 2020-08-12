package chanceCubes.commands;

import chanceCubes.CCubesCore;
import chanceCubes.client.ClientProxy;
import chanceCubes.client.listeners.RenderEvent;
import chanceCubes.config.CCubesSettings;
import chanceCubes.config.ConfigLoader;
import chanceCubes.config.CustomProfileLoader;
import chanceCubes.config.CustomRewardsLoader;
import chanceCubes.profiles.GlobalProfileManager;
import chanceCubes.registry.global.GlobalCCRewardRegistry;
import chanceCubes.registry.player.PlayerRewardInfo;
import chanceCubes.rewards.DefaultGiantRewards;
import chanceCubes.rewards.DefaultRewards;
import chanceCubes.sounds.CCubesSounds;
import chanceCubes.util.GiantCubeUtil;
import chanceCubes.util.NonreplaceableBlockOverride;
import chanceCubes.util.RewardsUtil;
import chanceCubes.util.SchematicUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;

import java.util.List;

public class CCubesServerCommands
{
	public CCubesServerCommands(CommandDispatcher<CommandSource> dispatcher)
	{
		// @formatter:off
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("chancecubes").requires(cs -> {
			return cs.getEntity() instanceof ServerPlayerEntity;
		})
				.then(Commands.literal("reload").executes(this::executeReload))
				.then(Commands.literal("version").executes(this::executeVersion))
				.then(Commands.literal("handNBT").executes(this::executeHandNBT))
				.then(Commands.literal("handID").executes(this::executeHandID))
				.then(Commands.literal("disableReward").then(Commands.argument("rewardName", new RewardArgument())
						.executes(ctx -> executeDisableReward(ctx, RewardArgument.func_212592_a(ctx, "rewardName")))))
				.then(Commands.literal("enableReward").then(Commands.argument("rewardName", new RewardArgument())
						.executes(ctx -> executeEnableReward(ctx, RewardArgument.func_212592_a(ctx, "rewardName")))))
				.then(Commands.literal("schematic").requires(cs -> cs.hasPermissionLevel(2)).requires(cs -> cs.getWorld().isRemote)
						.then(Commands.literal("create").executes(this::executeSchematicCreate))
						.then(Commands.literal("cancel").executes(this::executeSchematicCancel)))
				.then(Commands.literal("rewardsInfo").executes(this::executeRewardInfo))
				.then(Commands.literal("test").executes(this::executeTest))
				.then(Commands.literal("testRewards").executes(this::executeTestRewards))
				.then(Commands.literal("testCustomRewards").executes(this::executeTestCustomRewards))
				.then(Commands.literal("spawnGiantCube").then(Commands.argument("pos", BlockPosArgument.blockPos())
						.executes(ctx -> executeSpawnGiantCube(ctx, BlockPosArgument.getBlockPos(ctx, "pos")))))
				.then(Commands.literal("profiles").requires(cs -> cs.getWorld().isRemote)
						.executes(this::executeProfilesView)));
		// @formatter:on
	}

	public ServerPlayerEntity getPlayer(CommandSource source)
	{
		try
		{
			return source.asPlayer();
		} catch(CommandSyntaxException e)
		{
			CCubesCore.logger.log(Level.ERROR, "You should never see this. If you do you broke everything. Report to Turkey");
		}
		//Should never get here.
		return null;
	}


	public int executeReload(CommandContext<CommandSource> ctx)
	{
		new Thread(() ->
		{
			GlobalCCRewardRegistry.DEFAULT.ClearRewards();
			GlobalCCRewardRegistry.GIANT.ClearRewards();
			GlobalProfileManager.clearProfiles();
			ConfigLoader.reload();
			DefaultRewards.loadDefaultRewards();
			DefaultGiantRewards.loadDefaultRewards();
			CustomRewardsLoader.instance.loadCustomRewards();
			GlobalCCRewardRegistry.loadCustomUserRewards(ServerLifecycleHooks.getCurrentServer());
			NonreplaceableBlockOverride.loadOverrides();
			GlobalProfileManager.initProfiles();
			CustomProfileLoader.instance.loadProfiles();
			// TODO: 1.16: this is getOverworld()
			GlobalProfileManager.updateProfilesForWorld(ServerLifecycleHooks.getCurrentServer().func_241755_D_());
			getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Rewards Reloaded"), getPlayer(ctx.getSource()).getUniqueID());
		}).start();
		return 0;
	}

	public int executeVersion(CommandContext<CommandSource> ctx)
	{
		String ver = ModList.get().getModContainerById(CCubesCore.MODID).get().getModInfo().getVersion().toString();
		getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Chance Cubes Version " + ver), getPlayer(ctx.getSource()).getUniqueID());
		return 0;
	}

	public int executeHandNBT(CommandContext<CommandSource> ctx)
	{
		PlayerEntity player = getPlayer(ctx.getSource());
		CompoundNBT nbt = player.inventory.getCurrentItem().getOrCreateTag();
		player.sendMessage(new StringTextComponent(nbt.toString()), player.getUniqueID());
		return 0;
	}

	public int executeHandID(CommandContext<CommandSource> ctx)
	{
		PlayerEntity player = getPlayer(ctx.getSource());
		ItemStack stack = player.inventory.getCurrentItem();
		if(!stack.isEmpty())
		{
			ResourceLocation res = stack.getItem().getRegistryName();
			player.sendMessage(new StringTextComponent(res.getNamespace() + ":" + res.getPath()), player.getUniqueID());
			player.sendMessage(new StringTextComponent("meta: " + stack.getDamage()), player.getUniqueID());
		}
		return 0;
	}

	public int executeDisableReward(CommandContext<CommandSource> ctx, String reward)
	{
		//TODO:
		/*
		if(args.length > 2)
			{
				if(args[1].equalsIgnoreCase("global"))
				{
					//TODO: Giant Cube Rewards
					if(GlobalCCRewardRegistry.DEFAULT.disableReward(args[1]))
						sender.sendMessage(new StringTextComponent(args[1] + " Has been temporarily disabled."), sender.getUniqueID());
					else
						sender.sendMessage(new StringTextComponent(args[1] + " is either not currently enabled or is not a valid reward name."), sender.getUniqueID());
				}
				else
				{
					//TODO: per user disable
				}

			}
			else
			{
				sender.sendMessage(new StringTextComponent("Try /chancecubes enableReward <global|playername> <Reward Name>"), sender.getUniqueID());
			}
		 */
		return 0;
	}

	public int executeEnableReward(CommandContext<CommandSource> ctx, String reward)
	{
		//TODO:
		/*
		if(args.length > 2)
			{
				if(args[1].equalsIgnoreCase("global"))
				{
					//TODO: Giant Cube Rewards
					if(GlobalCCRewardRegistry.DEFAULT.enableReward(args[1]))
						sender.sendMessage(new StringTextComponent(args[1] + " Has been enabled."), sender.getUniqueID());
					else
						sender.sendMessage(new StringTextComponent(args[1] + " is either not currently disabled or is not a valid reward name."), sender.getUniqueID());
				}
				else
				{
					//TODO: per user enable
				}
			}
			else
			{
				sender.sendMessage(new StringTextComponent("Try /chancecubes disableReward <Reward Name>"), sender.getUniqueID());
			}
		 */
		return 0;
	}

	public int executeSchematicCreate(CommandContext<CommandSource> ctx)
	{
		if(RenderEvent.isCreatingSchematic())
		{
			//Possibly make own packet
			if(SchematicUtil.selectionPoints[0] != null && SchematicUtil.selectionPoints[1] != null)
			{
				DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
				{
					ClientProxy.openSchematicCreatorGUI(getPlayer(ctx.getSource()));
				});
			}
			else
			{
				getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Please set both points before moving on!"), getPlayer(ctx.getSource()).getUniqueID());
			}
		}
		else
		{
			RenderEvent.setCreatingSchematic(true);
		}
		return 0;
	}

	public int executeSchematicCancel(CommandContext<CommandSource> ctx)
	{
		RenderEvent.setCreatingSchematic(false);
		SchematicUtil.selectionPoints[0] = null;
		SchematicUtil.selectionPoints[1] = null;
		return 0;
	}

	public int executeRewardInfo(CommandContext<CommandSource> ctx)
	{
		PlayerEntity player = getPlayer(ctx.getSource());
		List<PlayerRewardInfo> defaultrewards = GlobalCCRewardRegistry.DEFAULT.getPlayerRewardRegistry(player.getUniqueID().toString()).getPlayersRewards();
		List<PlayerRewardInfo> giantrewards = GlobalCCRewardRegistry.GIANT.getPlayerRewardRegistry(player.getUniqueID().toString()).getPlayersRewards();
		int defaultEnabled = defaultrewards.size();
		int giantEnabled = giantrewards.size();
		player.sendMessage(new StringTextComponent("===DEFAULT REWARDS==="), player.getUniqueID());
		for(String reward : GlobalCCRewardRegistry.DEFAULT.getRewardNames())
			player.sendMessage(new StringTextComponent(reward), player.getUniqueID());
//		if(args.length > 1 && args[1].equalsIgnoreCase("list"))
//		{
//			if(args.length > 2 && args[2].equalsIgnoreCase("default"))
//			{
//				sender.sendMessage(new StringTextComponent("===DEFAULT REWARDS==="), //				sender.getUniqueID());
//				for(PlayerRewardInfo reward : defaultrewards)
//					sender.sendMessage(new StringTextComponent(reward.reward.getName()), //					sender.getUniqueID());
//			}
//			else if(args.length > 2 && args[2].equalsIgnoreCase("giant"))
//			{
//				sender.sendMessage(new StringTextComponent("===GIANT REWARDS==="), //				sender.getUniqueID());
//				for(PlayerRewardInfo reward : giantrewards)
//					sender.sendMessage(new StringTextComponent(reward.reward.getName()), //					sender.getUniqueID());
//			}
//			else if(args.length > 2 && args[2].equalsIgnoreCase("defaultall"))
//			{
//				sender.sendMessage(new StringTextComponent("===DEFAULT REWARDS==="), //				sender.getUniqueID());
//				for(String reward : GlobalCCRewardRegistry.DEFAULT.getRewardNames())
//					sender.sendMessage(new StringTextComponent(reward), //					sender.getUniqueID());
//			}
//			else if(args.length > 2 && args[2].equalsIgnoreCase("giantall"))
//			{
//				sender.sendMessage(new StringTextComponent("===GIANT REWARDS==="), //				sender.getUniqueID());
//				for(String reward : GlobalCCRewardRegistry.GIANT.getRewardNames())
//					sender.sendMessage(new StringTextComponent(reward), //					sender.getUniqueID());
//			}
//			else if(args.length > 2 && args[2].equalsIgnoreCase("defaultdisabled"))
//			{
//				sender.sendMessage(new StringTextComponent("===DEFAULT REWARDS DISABLED==="), //				sender.getUniqueID());
//				List<String> playerRewards = new ArrayList<>();
//				for(PlayerRewardInfo reward : defaultrewards)
//					playerRewards.add(reward.reward.getName());
//				for(String reward : GlobalCCRewardRegistry.DEFAULT.getRewardNames())
//					if(!playerRewards.contains(reward))
//						sender.sendMessage(new StringTextComponent(reward), //						sender.getUniqueID());
//			}
//			else if(args.length > 2 && args[2].equalsIgnoreCase("giantdisabled"))
//			{
//				sender.sendMessage(new StringTextComponent("===GIANT REWARDS DISABLED==="), //				sender.getUniqueID());
//				List<String> playerRewards = new ArrayList<>();
//				for(PlayerRewardInfo reward : giantrewards)
//					playerRewards.add(reward.reward.getName());
//				for(String reward : GlobalCCRewardRegistry.GIANT.getRewardNames())
//					if(!playerRewards.contains(reward))
//						sender.sendMessage(new StringTextComponent(reward), //						sender.getUniqueID());
//			}
//		}

		getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("There are currently " + GlobalCCRewardRegistry.DEFAULT.getNumberOfLoadedRewards() + " regular rewards loaded and you have " + defaultEnabled + " rewards enabled"), getPlayer(ctx.getSource()).getUniqueID());
		getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("There are currently " + GlobalCCRewardRegistry.GIANT.getNumberOfLoadedRewards() + " giant rewards loaded and you have " + giantEnabled + " rewards enabled"), getPlayer(ctx.getSource()).getUniqueID());
		return 0;
	}

	public int executeTestRewards(CommandContext<CommandSource> ctx)
	{
		CCubesSettings.testRewards = !CCubesSettings.testRewards;
		CCubesSettings.testingRewardIndex = 0;
		if(CCubesSettings.testRewards)
			getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Reward testing is now enabled for all rewards!"), getPlayer(ctx.getSource()).getUniqueID());
		else
			getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Reward testing is now disabled and normal randomness is back."), getPlayer(ctx.getSource()).getUniqueID());
		return 0;
	}

	public int executeTestCustomRewards(CommandContext<CommandSource> ctx)
	{
		CCubesSettings.testCustomRewards = !CCubesSettings.testCustomRewards;
		CCubesSettings.testingRewardIndex = 0;
		if(CCubesSettings.testCustomRewards)
			getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Reward testing is now enabled for custom rewards!"), getPlayer(ctx.getSource()).getUniqueID());
		else
			getPlayer(ctx.getSource()).sendMessage(new StringTextComponent("Reward testing is now disabled and normal randomness is back."), getPlayer(ctx.getSource()).getUniqueID());
		return 0;
	}

	public int executeTest(CommandContext<CommandSource> ctx)
	{
		return 0;
	}

	public int executeSpawnGiantCube(CommandContext<CommandSource> ctx, BlockPos pos)
	{
		ServerPlayerEntity player = getPlayer(ctx.getSource());
		World world = player.getEntityWorld();

		if(RewardsUtil.isBlockUnbreakable(world, pos.add(0, 0, 0)) && CCubesSettings.nonReplaceableBlocks.contains(world.getBlockState(pos.add(0, 0, 0))))
			return 0;

		GiantCubeUtil.setupStructure(pos.add(-1, -1, -1), world, true);

		world.playSound(null, pos, CCubesSounds.GIANT_CUBE_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
		return 0;
	}

	public int executeProfilesView(CommandContext<CommandSource> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
		{
			ClientProxy.openProfilesGUI();
		});
		return 0;
	}
}