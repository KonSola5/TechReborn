/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.blockentity.generator.advanced;

import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.screen.BuiltScreenHandlerProvider;
import reborncore.common.screen.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.fluid.FluidValue;
import techreborn.api.generator.EFluidGenerator;
import techreborn.blockentity.generator.BaseFluidGeneratorBlockEntity;
import techreborn.config.TechRebornConfig;
import techreborn.init.TRBlockEntities;
import techreborn.init.TRContent;

import java.util.List;

public class SemiFluidGeneratorBlockEntity extends BaseFluidGeneratorBlockEntity implements BuiltScreenHandlerProvider {

	public SemiFluidGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(TRBlockEntities.SEMI_FLUID_GENERATOR, pos, state, EFluidGenerator.SEMIFLUID, "SemiFluidGeneratorBlockEntity", FluidValue.BUCKET.multiply(10), TechRebornConfig.semiFluidGeneratorEnergyPerTick);
	}

	@Override
	public ItemStack getToolDrop(PlayerEntity playerIn) {
		return TRContent.Machine.SEMI_FLUID_GENERATOR.getStack();
	}

	@Override
	public long getBaseMaxPower() {
		return TechRebornConfig.semiFluidGeneratorMaxEnergy;
	}

	@Override
	public long getBaseMaxOutput() {
		return TechRebornConfig.semiFluidGeneratorMaxOutput;
	}

	@Override
	public BuiltScreenHandler createScreenHandler(int syncID, final PlayerEntity player) {
		return new ScreenHandlerBuilder("semifluidgenerator").player(player.getInventory()).inventory().hotbar()
				.addInventory().blockEntity(this).slot(0, 25, 35).outputSlot(1, 25, 55).syncEnergyValue()
				.sync(this::getTicksSinceLastChange, this::setTicksSinceLastChange)
				.sync(this::getTankAmount, this::setTankAmount)
				.sync(tank)
				.addInventory().create(this, syncID);
	}

	@Override
	public void addInfo(List<Text> info, boolean isReal, boolean hasData) {
		super.addInfo(info, isReal, hasData);
		info.add(
			new TranslatableText("techreborn.tooltip.generationRate")
				.formatted(Formatting.GRAY)
				.append(": ")
				.append(
					new LiteralText(PowerSystem.getLocalizedPower(TechRebornConfig.semiFluidGeneratorEnergyPerTick))
						.append(I18n.translate("techreborn.tooltip.perTick"))
						.formatted(Formatting.GOLD)
				)
		);
	}
}
