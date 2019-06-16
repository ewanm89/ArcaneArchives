package com.aranaira.arcanearchives.items.gems.oval;

import com.aranaira.arcanearchives.items.gems.ArcaneGemItem;
import com.aranaira.arcanearchives.items.gems.GemUtil;
import com.aranaira.arcanearchives.util.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class OrderstoneItem extends ArcaneGemItem {
	public static final String NAME = "orderstone";

	public OrderstoneItem () {
		super(NAME, GemCut.OVAL, GemColor.PINK, 100, 400);
	}

	@Override
	public void addInformation (ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		NBTTagCompound nbt = NBTUtils.getOrCreateTagCompound(stack);
		tooltip.add(I18n.format("arcanearchives.tooltip.gemcharge") + ": " + getTooltipData(stack));
		tooltip.add(TextFormatting.GOLD + I18n.format("arcanearchives.tooltip.gem.orderstone"));
	}

	@Override
	public boolean doesSneakBypassUse (ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return false;
	}

	@Override
	public EnumActionResult onItemUse (EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			Block block = world.getBlockState(pos).getBlock();
			if (GemUtil.getCharge(player.getHeldItemMainhand()) > 0) {
				int chargeCost = 0;
				/**
				 * Gravel -> Cobblestone -> Stone
				 */
				if (block == Blocks.GRAVEL) {
					world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), 0);
					chargeCost = 1;
				} else if (block == Blocks.COBBLESTONE) {
					world.setBlockState(pos, Blocks.STONE.getDefaultState(), 0);
					chargeCost = 1;
				} else if (block == Blocks.MOSSY_COBBLESTONE) {
					world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), 0);
					chargeCost = 1;
				}
				/**
				 * Sand -> Coarse Dirt -> Dirt -> Mycelium -> Podzol -> Grass
				 */
				else if (block == Blocks.SAND) {
					world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT), 0);
					chargeCost = 1;
				} else if (block == Blocks.DIRT) {
					IBlockState state = world.getBlockState(pos);
					BlockDirt.DirtType variant = state.getValue(BlockDirt.VARIANT);
					if (variant == BlockDirt.DirtType.COARSE_DIRT) {
						world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), 0);
						chargeCost = 1;
					} else if (variant == BlockDirt.DirtType.DIRT) {
						world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState(), 0);
						chargeCost = 1;
					} else if (variant == BlockDirt.DirtType.PODZOL) {
						world.setBlockState(pos, Blocks.GRASS.getDefaultState(), 0);
						chargeCost = 1;
					}
				} else if (block == Blocks.MYCELIUM) {
					IBlockState state = world.getBlockState(pos);
					BlockDirt.DirtType variant = Blocks.DIRT.getDefaultState().getValue(BlockDirt.VARIANT);
					world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL), 0);
					chargeCost = 1;
				}
				/**
				 * Cracked/Mossy Bricks -> Stone bricks
				 */
				else if (block == Blocks.STONEBRICK) {
					IBlockState state = world.getBlockState(pos);
					BlockStoneBrick.EnumType variant = state.getValue(BlockStoneBrick.VARIANT);
					if (variant == BlockStoneBrick.EnumType.CRACKED || variant == BlockStoneBrick.EnumType.MOSSY) {
						chargeCost = 1;
						world.setBlockState(pos, state.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT), 0);
					} else if (variant == BlockStoneBrick.EnumType.DEFAULT) {
						chargeCost = 4;
						world.setBlockState(pos, state.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED), 0);
					}
				}
				/**
				 * Anvils
				 */
				else if (block == Blocks.ANVIL) {
					IBlockState state = world.getBlockState(pos);
					int damage = state.getValue(BlockAnvil.DAMAGE);
					if (damage > 0) {
						damage--;
						chargeCost = 25;
						world.setBlockState(pos, state.withProperty(BlockAnvil.DAMAGE, damage), 0);
					}
				}
				GemUtil.consumeCharge(player.getHeldItemMainhand(), chargeCost);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}
}