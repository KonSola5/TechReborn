package techreborn.blocks;

import me.modmuss50.jsonDestroyer.api.ITexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import reborncore.RebornCore;
import techreborn.client.TechRebornCreativeTabMisc;
import techreborn.init.ModParts;
import techreborn.items.ItemParts;
import techreborn.items.tools.ItemTreeTap;

import java.util.List;
import java.util.Random;

/**
 * Created by mark on 19/02/2016.
 */
public class BlockRubberLog extends Block implements ITexturedBlock {

	public static PropertyDirection SAP_SIDE = PropertyDirection.create("sapSide", EnumFacing.Plane.HORIZONTAL);
	public static PropertyBool HAS_SAP = PropertyBool.create("hasSap");

	public BlockRubberLog() {
		super(Material.wood);
		setUnlocalizedName("techreborn.rubberlog");
		setCreativeTab(TechRebornCreativeTabMisc.instance);
		this.setHardness(2.0F);
		this.setStepSound(soundTypeWood);
		RebornCore.jsonDestroyer.registerObject(this);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SAP_SIDE, EnumFacing.NORTH).withProperty(HAS_SAP, false));
		this.setTickRandomly(true);
	}

	protected BlockState createBlockState() {
		return new BlockState(this, SAP_SIDE, HAS_SAP);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean hasSap = false;
		int tempMeta = meta;
		if(meta > 3){
			hasSap = true;
			tempMeta -= 3;
		}
		EnumFacing facing = EnumFacing.getHorizontal(tempMeta);
		return this.getDefaultState().withProperty(SAP_SIDE, facing).withProperty(HAS_SAP, hasSap);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int tempMeta = 0;
		EnumFacing facing = state.getValue(SAP_SIDE);
		switch (facing){
			case SOUTH:
				tempMeta = 0;
				break;
			case WEST:
				tempMeta = 1;
				break;
			case NORTH:
				tempMeta = 2;
				break;
			case EAST:
				tempMeta = 3;
		}
		if(state.getValue(HAS_SAP)){
			tempMeta += 3;
		}
		return tempMeta;
	}

	@Override
	public String getTextureNameFromState(IBlockState iBlockState, EnumFacing enumFacing) {
		if(enumFacing == EnumFacing.DOWN || enumFacing == EnumFacing.UP){
			return  "techreborn:blocks/rubber_log_side";
		}
		if(iBlockState.getValue(HAS_SAP)){
			if(iBlockState.getValue(SAP_SIDE) == enumFacing){
				return "techreborn:blocks/rubber_log_sap";
			}
		}
		return "techreborn:blocks/rubber_log";
	}

	@Override
	public int amountOfStates() {
		return 8;
	}

	@Override
	public boolean canSustainLeaves(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isWood(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return true;
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		int i = 4;
		int j = i + 1;
		if (worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
			for (BlockPos blockpos : BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
				IBlockState iblockstate = worldIn.getBlockState(blockpos);
				if (iblockstate.getBlock().isLeaves(worldIn, blockpos)) {
					iblockstate.getBlock().beginLeavesDecay(worldIn, blockpos);
				}
			}
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if(!state.getValue(HAS_SAP)){
			if(rand.nextInt(50) == 0){
				EnumFacing facing = EnumFacing.getHorizontal(rand.nextInt(3));
				if(worldIn.getBlockState(pos.down()).getBlock() == this && worldIn.getBlockState(pos.up()).getBlock() == this && worldIn.isAirBlock(pos.offset(facing))){
					worldIn.setBlockState(pos, state.withProperty(HAS_SAP, true).withProperty(SAP_SIDE, facing));
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
		if(playerIn.getCurrentEquippedItem() != null && playerIn.getCurrentEquippedItem().getItem() instanceof ItemTreeTap)
			if(state.getValue(HAS_SAP)){
				if(state.getValue(SAP_SIDE) == side){
					worldIn.setBlockState(pos, state.withProperty(HAS_SAP, false).withProperty(SAP_SIDE, EnumFacing.getHorizontal(0)));
					if(!worldIn.isRemote){
						Random rand = new Random();
						BlockPos itemPos = pos.offset(side);
						EntityItem item = new EntityItem(worldIn, itemPos.getX(), itemPos.getY(), itemPos.getZ(), ItemParts.getPartByName("rubberSap").copy());
						float factor = 0.05F;
						playerIn.getCurrentEquippedItem().damageItem(1, playerIn);
						item.motionX = rand.nextGaussian() * factor;
						item.motionY = rand.nextGaussian() * factor + 0.2F;
						item.motionZ = rand.nextGaussian() * factor;
						worldIn.spawnEntityInWorld(item);
					}
					return true;
				}
			}
		return false;
	}
}
