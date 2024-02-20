/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Original: Copyright (c) 2020 TechReborn
 * Modifications: Copyright (c) 2024 KonSola5
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

package techreborn.client.events;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import reborncore.RebornCore;
import techreborn.config.TechRebornConfig;
import techreborn.init.TRContent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class ToolTipAssistUtils {

	// Colour constants
	private static final Formatting INSTRUCTION_COLOR = Formatting.BLUE;

	private static final Formatting INFO_COLOR = Formatting.GOLD;
	private static final Formatting STAT_COLOR = Formatting.GRAY;

	private static final Formatting POSITIVE_COLOR = Formatting.GREEN;
	private static final Formatting NEGATIVE_COLOR = Formatting.RED;

	public static double SPEED_CAP = 97.5;

	public static List<Text> getUpgradeStats(TRContent.Upgrades upgradeType, int count, boolean shiftHeld) {
		List<Text> tips = new ArrayList<>();
		boolean shouldStackCalculate = count > 1;

		switch (upgradeType) {
			case OVERCLOCKER -> {
				tips.add(getStatStringUnit(I18n.translate("techreborn.tooltip.upgrade.speed_increase"),
					calculateSpeed(-TechRebornConfig.overclockerSpeed * 100, count, shiftHeld),
					"%",
					"",
					true));
				tips.add(getStatStringUnit(I18n.translate("techreborn.tooltip.upgrade.energy_increase"),
					calculateEnergyIncrease(TechRebornConfig.overclockerPower + 1, count, shiftHeld),
					"x",
					"",
					false));
			}
			case TRANSFORMER -> shouldStackCalculate = false;
			case ENERGY_STORAGE ->
				tips.add(
					getStatStringUnit(
						I18n.translate("techreborn.tooltip.upgrade.storage_increase"),
						calculateValue(TechRebornConfig.energyStoragePower, count, shiftHeld),
						" E",
						"+",
						true));
			case SUPERCONDUCTOR ->
				tips.add(getStatStringUnit(I18n.translate("techreborn.tooltip.upgrade.flow_increase"),
					calculateValue(Math.pow(2, (TechRebornConfig.superConductorCount + 2)) + 1, count, shiftHeld),
					"x",
					"",
					true));
		}

		// Add reminder that they can use shift to calculate the entire stack
		if (shouldStackCalculate && !shiftHeld) {
			tips.add(new LiteralText(INSTRUCTION_COLOR + I18n.translate("techreborn.tooltip.stack_info")));
		}

		return tips;
	}

	public static void addInfo(String inKey, List<Text> list) {
		addInfo(inKey, list, true);
	}

	public static void addInfo(String inKey, List<Text> list, boolean hidden) {
		String key = ("techreborn.message.info." + inKey);

		if (I18n.hasTranslation(key)) {
			if (!hidden || Screen.hasShiftDown()) {
				String info = I18n.translate(key);
				String[] infoLines = info.split("\\r?\\n");

				for (String infoLine : infoLines) {
					list.add(1, new LiteralText(INFO_COLOR + infoLine));
				}
			} else {
				list.add(new TranslatableText("techreborn.tooltip.more_info").formatted(INSTRUCTION_COLOR));
			}
		}
	}


	private static int calculateValue(double value, int count, boolean shiftHeld) {
		int calculatedVal;

		if (shiftHeld) {
			calculatedVal = (int) value * Math.min(count, 4);
		} else {
			calculatedVal = (int) value;
		}

		return calculatedVal;
	}

	private static double calculateEnergyIncrease(double value, int count, boolean shiftHeld) {
		double calculatedVal;

		if (shiftHeld) {
			calculatedVal = Math.pow(value, Math.min(count, 4));
		} else {
			calculatedVal = value;
		}

		return calculatedVal;
	}

	private static double calculateSpeed(double value, int count, boolean shiftHeld) {
		double calculatedVal;

		if (shiftHeld) {
			calculatedVal = Math.max(value * Math.min(count, 4), -SPEED_CAP);
		} else {
			calculatedVal = value;
		}

		return calculatedVal;
	}

	private static Text getStatStringUnit(String text, double value, String unit, String prefix, boolean isPositive) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(RebornCore.locale); // Always use dot
		NumberFormat formatter = new DecimalFormat("##.##", symbols); // Round to 2 decimal places
		return new LiteralText(text)
			.append(": ")
			.formatted(STAT_COLOR)
			.append(
				new LiteralText(formatter.format(value))
					.append(unit)
					.formatted(((isPositive) ? POSITIVE_COLOR : NEGATIVE_COLOR))
			);
	}
}