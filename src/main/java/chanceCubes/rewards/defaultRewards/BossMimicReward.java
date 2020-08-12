package chanceCubes.rewards.defaultRewards;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class BossMimicReward extends BossBaseReward
{
	public BossMimicReward()
	{
		super("mimic");
	}

	@Override
	public void spawnBoss(World world, BlockPos pos, PlayerEntity player, Map<String, Object> settings)
	{
		ZombieEntity mimic = EntityType.ZOMBIE.create(world);
		mimic.setCustomName(new StringTextComponent("Mimic"));
		mimic.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
		mimic.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getBossHealthDynamic(player, settings));
		mimic.setHealth(mimic.getMaxHealth());
		List<ItemStack> playerArmorInv = player.inventory.armorInventory;

		mimic.setItemStackToSlot(EquipmentSlotType.HEAD, playerArmorInv.get(3).copy());
		mimic.setDropChance(EquipmentSlotType.HEAD, 0);
		mimic.setItemStackToSlot(EquipmentSlotType.CHEST, playerArmorInv.get(2).copy());
		mimic.setDropChance(EquipmentSlotType.CHEST, 0);
		mimic.setItemStackToSlot(EquipmentSlotType.LEGS, playerArmorInv.get(1).copy());
		mimic.setDropChance(EquipmentSlotType.LEGS, 0);
		mimic.setItemStackToSlot(EquipmentSlotType.FEET, playerArmorInv.get(0).copy());
		mimic.setDropChance(EquipmentSlotType.FEET, 0);

		mimic.setItemStackToSlot(EquipmentSlotType.MAINHAND, getHighestDamageItem(player));
		mimic.setItemStackToSlot(EquipmentSlotType.OFFHAND, player.inventory.offHandInventory.get(0).copy());

		world.addEntity(mimic);
		super.trackEntities(mimic);
		super.trackedPlayers(player);
	}

	@Override
	public void onBossFightEnd(World world, BlockPos pos, PlayerEntity player)
	{

	}
}
