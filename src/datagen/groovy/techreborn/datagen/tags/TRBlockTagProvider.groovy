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

package techreborn.datagen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.Blocks
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import techreborn.items.tool.DrillItem
import techreborn.items.tool.JackhammerItem
import techreborn.items.tool.industrial.OmniToolItem

class TRBlockTagProvider extends FabricTagProvider.BlockTagProvider {

	TRBlockTagProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator)
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(DrillItem.DRILL_MINEABLE)
			.addOptionalTag(BlockTags.PICKAXE_MINEABLE.id())
			.addOptionalTag(BlockTags.SHOVEL_MINEABLE.id())

		getOrCreateTagBuilder(OmniToolItem.OMNI_TOOL_MINEABLE)
			.addOptionalTag(DrillItem.DRILL_MINEABLE.id())
			.addOptionalTag(BlockTags.AXE_MINEABLE.id())
			.addOptionalTag(FabricMineableTags.SHEARS_MINEABLE.id())
			.addOptionalTag(FabricMineableTags.SWORD_MINEABLE.id())

		getOrCreateTagBuilder(JackhammerItem.SKIPPED_BY_JACKHAMMER)
			.addOptionalTag(ConventionalBlockTags.ORES.id())
			.addOptional(new Identifier("minecraft:ancient_debris"))
			.addOptional(new Identifier("minecraft:obsidian"))
			.addOptional(new Identifier("minecraft:crying_obsidian"))
	}
}
