/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 TechReborn
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

package techreborn.rei;

import me.shedaniel.rei.api.REIPluginEntry;
import me.shedaniel.rei.api.RecipeHelper;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import reborncore.common.crafting.RebornRecipe;
import reborncore.common.crafting.RebornRecipeType;
import reborncore.common.crafting.RecipeManager;
import techreborn.TechReborn;
import techreborn.init.ModRecipes;
import techreborn.init.TRContent.Machine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ReiPlugin implements REIPluginEntry {

	public static final Identifier PLUGIN = new Identifier(TechReborn.MOD_ID, "techreborn_plugin");

	public static final Map<RebornRecipeType<?>, ItemConvertible> iconMap = new HashMap<>();

	public ReiPlugin() {
		iconMap.put(ModRecipes.ALLOY_SMELTER, Machine.ALLOY_SMELTER);
		iconMap.put(ModRecipes.BLAST_FURNACE, Machine.INDUSTRIAL_BLAST_FURNACE);
		iconMap.put(ModRecipes.CENTRIFUGE, Machine.INDUSTRIAL_CENTRIFUGE);
		iconMap.put(ModRecipes.CHEMICAL_REACTOR, Machine.CHEMICAL_REACTOR);
		iconMap.put(ModRecipes.COMPRESSOR, Machine.COMPRESSOR);
		iconMap.put(ModRecipes.GRINDER, Machine.GRINDER);
		
		//TODO add the others here
	}

	@Override
	public Identifier getPluginIdentifier() {
		return PLUGIN;
	}

	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		RecipeManager.getRecipeTypes("techreborn").forEach(rebornRecipeType -> recipeHelper.registerCategory(new MachineRecipeCategory(rebornRecipeType)));
	}

	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		RecipeManager.getRecipeTypes("techreborn").forEach(rebornRecipeType -> registerMachineRecipe(recipeHelper, rebornRecipeType));
	}
	
	@Override
	public void registerOthers(RecipeHelper recipeHelper) {
		recipeHelper.registerWorkingStations(ModRecipes.ALLOY_SMELTER.getName(),
				new ItemStack[] { new ItemStack(Machine.ALLOY_SMELTER.asItem()),
								  new ItemStack(Machine.IRON_ALLOY_FURNACE.asItem())});
		recipeHelper.registerWorkingStations(ModRecipes.GRINDER.getName(), new ItemStack(Machine.GRINDER.asItem()));
		recipeHelper.registerWorkingStations(ModRecipes.BLAST_FURNACE.getName(), new ItemStack(Machine.INDUSTRIAL_BLAST_FURNACE.asItem()));
		recipeHelper.registerWorkingStations(ModRecipes.CENTRIFUGE.getName(), new ItemStack(Machine.INDUSTRIAL_CENTRIFUGE.asItem()));
		recipeHelper.registerWorkingStations(ModRecipes.CHEMICAL_REACTOR.getName(), new ItemStack(Machine.CHEMICAL_REACTOR.asItem()));
		recipeHelper.registerWorkingStations(ModRecipes.COMPRESSOR.getName(), new ItemStack(Machine.COMPRESSOR.asItem()));
	}

	private <R extends RebornRecipe> void registerMachineRecipe(RecipeHelper recipeHelper, RebornRecipeType<R> recipeType){
		recipeHelper.registerRecipes(recipeType.getName(), (Predicate<Recipe>) recipe -> {
			if (recipe instanceof RebornRecipe) {
				return ((RebornRecipe) recipe).getRebornRecipeType() == recipeType;
			}
			return false;
		}, recipe -> new MachineRecipeDisplay<RebornRecipe>((RebornRecipe) recipe));
	}
}
