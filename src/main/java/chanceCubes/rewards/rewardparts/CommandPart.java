package chanceCubes.rewards.rewardparts;

import chanceCubes.rewards.variableTypes.IntVar;
import chanceCubes.rewards.variableTypes.StringVar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class CommandPart extends BasePart
{
	private StringVar command;
	private IntVar copies = new IntVar(0);

	public CommandPart(String command)
	{
		this(command, 0);
	}

	public CommandPart(String command, int delay)
	{
		this(new StringVar(command), new IntVar(delay));
	}

	public CommandPart(StringVar command)
	{
		this(command, new IntVar(0));
	}

	public CommandPart(StringVar command, IntVar delay)
	{
		this.command = command;
		this.setDelay(delay);
	}

	public String getRawCommand()
	{
		return command.getValue();
	}

	public String getParsedCommand(World world, int x, int y, int z, PlayerEntity player)
	{
		String parsedCommand = command.getValue();
		//TODO:?
		parsedCommand = parsedCommand.replace("%player", player.getName().getString());
		parsedCommand = parsedCommand.replace("%x", "" + x);
		parsedCommand = parsedCommand.replace("%y", "" + y);
		parsedCommand = parsedCommand.replace("%z", "" + z);
		parsedCommand = parsedCommand.replace("%px", "" + player.getPosX());
		parsedCommand = parsedCommand.replace("%py", "" + player.getPosY());
		parsedCommand = parsedCommand.replace("%pz", "" + player.getPosZ());
		parsedCommand = parsedCommand.replace("%puuid", "" + player.getUniqueID().toString());
		parsedCommand = parsedCommand.replace("%pdir", "" + player.getHorizontalFacing().toString());
		parsedCommand = parsedCommand.replace("%pyaw", "" + player.rotationYaw);
		parsedCommand = parsedCommand.replace("%ppitch", "" + player.rotationPitch);

		return parsedCommand;
	}

	public IntVar getCopies()
	{
		return copies;
	}

	public void setCopies(IntVar copies)
	{
		this.copies = copies;
	}
}
