package chanceCubes.client.listeners;

import chanceCubes.util.SchematicUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class RenderEvent
{
	private static boolean islookingAt = false;
	private static boolean creatingSchematic = false;
	private static int chance = -201;
	private static int chanceIncrease = 0;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onGuiRender(RenderGameOverlayEvent.Post event)
	{
		if(event.getType() != ElementType.HELMET || event.isCancelable())
			return;

		Minecraft mc = Minecraft.getInstance();

		int k = Minecraft.getInstance().getMainWindow().getScaledWidth();
		int l = Minecraft.getInstance().getMainWindow().getScaledHeight();

		FontRenderer fontrenderer = mc.fontRenderer;
		MatrixStack stack = event.getMatrixStack();

		if(islookingAt)
		{
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			if(chance == -201)
			{
				fontrenderer.drawString(stack, "The chance of this cube is: Destruction... Probably", (k / 2f) - 80, (l / 2f) - 30, 16777215);
			}
			else
			{
				fontrenderer.drawString(stack, "The chance of this cube is: " + chance, (k / 2f) - 80, (l / 2f) - 30, 16777215);
				if(chanceIncrease != 0)
				{
					int c = chance + chanceIncrease;
					fontrenderer.drawString(stack, "Chance with pendants is: " + Math.min(100, Math.max(c, -100)), (k / 2f) - 80, (l / 2f) - 15, 16777215);
				}
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
		if(creatingSchematic)
		{
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(1F, 1F, 1F, 1F);

			String text1 = "--- Creating A Chance Cube Schematic ---";
			String text2 = "Right or left click a block or air to set positions.";
			String text3 = "/ChanceCubes schematic create to continue";
			String text4 = "/ChanceCubes schematic cancel to cancel";
			String text5 = "Point 1";
			String text6 = "Point 2";

			fontrenderer.drawString(stack, text1, (k / 2f) - (fontrenderer.getStringWidth(text1) / 2f), 10, 0xFFFFFF);
			fontrenderer.drawString(stack, text2, (k / 2f) - (fontrenderer.getStringWidth(text2) / 2f), 20, 0xFFFFFF);
			fontrenderer.drawString(stack, text3, (k / 2f) - (fontrenderer.getStringWidth(text3) / 2f), 30, 0xFFFFFF);
			fontrenderer.drawString(stack, text4, (k / 2f) - (fontrenderer.getStringWidth(text4) / 2f), 40, 0xFFFFFF);
			fontrenderer.drawString(stack, text5, (k / 2f) - (fontrenderer.getStringWidth(text5) / 2f), 60, SchematicUtil.selectionPoints[0] == null ? 0xFF0000 : 0x00FF00);
			fontrenderer.drawString(stack, text6, (k / 2f) - (fontrenderer.getStringWidth(text6) / 2f), 70, SchematicUtil.selectionPoints[1] == null ? 0xFF0000 : 0x00FF00);

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

	public static void setLookingAtChance(int c)
	{
		chance = c;
	}

	public static void setLookingAt(boolean lookingAt)
	{
		islookingAt = lookingAt;
	}

	public static void setChanceIncrease(int increase)
	{
		chanceIncrease = increase;
	}

	public static void setCreatingSchematic(boolean isCreating)
	{
		creatingSchematic = isCreating;
	}

	public static boolean isCreatingSchematic()
	{
		return creatingSchematic;
	}
}