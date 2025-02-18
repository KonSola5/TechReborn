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

package reborncore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reborncore.api.ToolManager;
import reborncore.api.blockentity.UnloadHandler;
import reborncore.common.RebornCoreCommands;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blocks.BlockWrenchEventHandler;
import reborncore.common.chunkloading.ChunkLoaderManager;
import reborncore.common.config.Configuration;
import reborncore.common.misc.ModSounds;
import reborncore.common.misc.RebornCoreTags;
import reborncore.common.multiblock.MultiblockRegistry;
import reborncore.common.network.ServerBoundPackets;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.recipes.PaddedShapedRecipe;
import reborncore.common.util.CalenderUtils;
import reborncore.common.util.GenericWrenchHelper;
import team.reborn.energy.api.EnergyStorage;

import java.io.File;
import java.util.Locale;
import java.util.function.Supplier;

public class RebornCore implements ModInitializer {
	public static final String MOD_NAME = "Reborn Core";
	public static final String MOD_ID = "reborncore";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Locale locale = Locale.ROOT;

	@Override
	public void onInitialize() {
		new Configuration(RebornCoreConfig.class, MOD_ID);
		PowerSystem.init();
		CalenderUtils.loadCalender(); // Done early as some features need this

		// Hey, you forgot to update your Wrenches.
		// Here, I'll do it myself.

		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("create:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("ae2:certus_quartz_wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("ae2:certus_quartz_wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("modern_industrialization:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("indrev:wrench"), false));

		ModSounds.setup();
		BlockWrenchEventHandler.setup();

		/*
		  This is a generic multiblock tick handler. If you are using this code on your
		  own, you will need to register this with the Forge TickRegistry on both the
		  client AND server sides. Note that different types of ticks run on different
		  parts of the system. CLIENT ticks only run on the client, at the start/end of
		  each game loop. SERVER and WORLD ticks only run on the server. WORLDLOAD
		  ticks run only on the server, and only when worlds are loaded.
		 */
		WorldTickCallback.EVENT.register(MultiblockRegistry::tickStart);

		// packets
		ServerBoundPackets.init();

		RebornCoreCommands.setup();

		RebornCoreTags.WATER_EXPLOSION_ITEM.toString();
		PaddedShapedRecipe.PADDED.toString();
		
		/* register UnloadHandler */
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof UnloadHandler) ((UnloadHandler) blockEntity).onUnload();
		});

		ServerWorldEvents.LOAD.register((server, world) -> ChunkLoaderManager.get(world).onServerWorldLoad(world));
		ServerTickEvents.START_WORLD_TICK.register(world -> ChunkLoaderManager.get(world).onServerWorldTick(world));

		FluidStorage.SIDED.registerFallback((world, pos, state, be, direction) -> {
			if (be instanceof MachineBaseBlockEntity machineBase) {
				return machineBase.getTank();
			}
			return null;
		});
		EnergyStorage.SIDED.registerFallback((world, pos, state, be, direction) -> {
			if (be instanceof PowerAcceptorBlockEntity powerAcceptor) {
				return powerAcceptor.getSideEnergyStorage(direction);
			}
			return null;
		});
	}

	public static EnvType getSide() {
		return FabricLoader.getInstance().getEnvironmentType();
	}
}
