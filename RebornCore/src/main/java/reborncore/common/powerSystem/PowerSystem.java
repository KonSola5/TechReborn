/*
 * This file is part of RebornCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 TeamReborn
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

package reborncore.common.powerSystem;

import net.minecraft.text.LiteralText;
import reborncore.RebornCore;
import reborncore.common.RebornCoreConfig;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class PowerSystem {
	private static EnergySystem selectedSystem = EnergySystem.values()[0];

	private static final char[] magnitude = new char[] { 'k', 'M', 'G', 'T' };

	private static final long[] POWERS_OF_ONE_THOUSAND = IntStream.range(1, 4)
		.mapToLong(i -> (long) Math.pow(1000, i)).toArray();

	public static String getLocalizedPower(double power) {

		return getRoundedString(power, selectedSystem.abbreviation, true);
	}

	public static String getLocalizedPowerNoSuffix(double power) {
		return getRoundedString(power, "", true);
	}

	public static String getLocalizedPowerNoFormat(double power){
		return getRoundedString(power, selectedSystem.abbreviation, false);
	}

	public static String getLocalizedPowerNoSuffixNoFormat(double power){
		return getRoundedString(power, "", false);
	}

	public static String getLocalizedPowerFull(double power){
		return getFullPower(power, selectedSystem.abbreviation);
	}

	public static String getLocalizedPowerFullNoSuffix(double power){
		return getFullPower(power, "");
	}

	private static String getFullPower(double power, String units){
		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(RebornCore.locale);
		return formatter.format(power) + " " + units;
	}

	// Only used to power, rounded to 2 decimal places instead of 1.
	private static String getRoundedString(double value, String units, boolean doFormat) {
		String toReturn = "";
		int chosenMagnitude = -1; // -1 = no magnitude

		for (long x : POWERS_OF_ONE_THOUSAND) {
			if (x <= value) {
				chosenMagnitude++;
			} else {
				break;
			}
		}

		if (chosenMagnitude > POWERS_OF_ONE_THOUSAND.length) {
			toReturn += "∞";
		} else {
			if (chosenMagnitude != -1) {
				value /= POWERS_OF_ONE_THOUSAND[chosenMagnitude]; // Get the value to be joint with the magnitude
			}
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(RebornCore.locale); // Always use dot
			NumberFormat formatter = new DecimalFormat("##.##", symbols); // Round to 2 decimal places

			toReturn += formatter.format(value);

			if (chosenMagnitude != -1) {
				toReturn += magnitude[chosenMagnitude];
			}
		}

		if (!units.equals("")) {
			toReturn += " " + units;
		}

		return toReturn;
	}

	public static EnergySystem getDisplayPower() {
		if(!selectedSystem.enabled.get()){
			bumpPowerConfig();
		}
		return selectedSystem;
	}

	public static void bumpPowerConfig() {
		int value = selectedSystem.ordinal() + 1;
		if (value == EnergySystem.values().length) {
			value = 0;
		}
		selectedSystem = EnergySystem.values()[value];
	}

	public static void init(){
		selectedSystem = Arrays.stream(EnergySystem.values()).filter(energySystem -> energySystem.abbreviation.equalsIgnoreCase(RebornCoreConfig.selectedSystem)).findFirst().orElse(EnergySystem.values()[0]);
		if(!selectedSystem.enabled.get()){
			bumpPowerConfig();
		}
	}

	public enum EnergySystem {
		EU(0xFF800600, "E", 141, 151, 0xFF670000);

		public int colour;
		public int altColour;
		public String abbreviation;
		public int xBar;
		public int yBar;
		public Supplier<Boolean> enabled = () -> true;

		EnergySystem(int colour, String abbreviation, int xBar, int yBar, int altColour) {
			this.colour = colour;
			this.abbreviation = abbreviation;
			this.xBar = xBar;
			this.yBar = yBar;
			this.altColour = altColour;
		}
	}
}
