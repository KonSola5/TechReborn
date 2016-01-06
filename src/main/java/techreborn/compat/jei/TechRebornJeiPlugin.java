package techreborn.compat.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import techreborn.Core;
import techreborn.api.reactor.FusionReactorRecipeHelper;
import techreborn.api.recipe.RecipeHandler;
import techreborn.api.recipe.machines.AssemblingMachineRecipe;
import techreborn.api.recipe.machines.ImplosionCompressorRecipe;
import techreborn.client.container.ContainerAlloyFurnace;
import techreborn.client.container.ContainerAlloySmelter;
import techreborn.client.container.ContainerAssemblingMachine;
import techreborn.client.container.ContainerBlastFurnace;
import techreborn.client.container.ContainerCentrifuge;
import techreborn.client.container.ContainerChemicalReactor;
import techreborn.client.container.ContainerFusionReactor;
import techreborn.client.container.ContainerGrinder;
import techreborn.client.container.ContainerImplosionCompressor;
import techreborn.client.container.ContainerIndustrialElectrolyzer;
import techreborn.client.container.ContainerIndustrialSawmill;
import techreborn.client.container.ContainerVacuumFreezer;
import techreborn.compat.jei.alloySmelter.AlloySmelterRecipeCategory;
import techreborn.compat.jei.alloySmelter.AlloySmelterRecipeHandler;
import techreborn.compat.jei.assemblingMachine.AssemblingMachineRecipeCategory;
import techreborn.compat.jei.assemblingMachine.AssemblingMachineRecipeHandler;
import techreborn.compat.jei.blastFurnace.BlastFurnaceRecipeCategory;
import techreborn.compat.jei.blastFurnace.BlastFurnaceRecipeHandler;
import techreborn.compat.jei.centrifuge.CentrifugeRecipeCategory;
import techreborn.compat.jei.centrifuge.CentrifugeRecipeHandler;
import techreborn.compat.jei.chemicalReactor.ChemicalReactorRecipeCategory;
import techreborn.compat.jei.chemicalReactor.ChemicalReactorRecipeHandler;
import techreborn.compat.jei.fusionReactor.FusionReactorRecipeCategory;
import techreborn.compat.jei.fusionReactor.FusionReactorRecipeHandler;
import techreborn.compat.jei.grinder.GrinderRecipeCategory;
import techreborn.compat.jei.grinder.GrinderRecipeHandler;
import techreborn.compat.jei.implosionCompressor.ImplosionCompressorRecipeCategory;
import techreborn.compat.jei.implosionCompressor.ImplosionCompressorRecipeHandler;
import techreborn.compat.jei.industrialElectrolyzer.IndustrialElectrolyzerRecipeCategory;
import techreborn.compat.jei.industrialElectrolyzer.IndustrialElectrolyzerRecipeHandler;
import techreborn.compat.jei.industrialSawmill.IndustrialSawmillRecipeCategory;
import techreborn.compat.jei.industrialSawmill.IndustrialSawmillRecipeHandler;
import techreborn.compat.jei.rollingMachine.RollingMachineRecipeCategory;
import techreborn.compat.jei.rollingMachine.RollingMachineRecipeHandler;
import techreborn.compat.jei.rollingMachine.RollingMachineRecipeMaker;
import techreborn.compat.jei.vacuumFreezer.VacuumFreezerRecipeCategory;
import techreborn.compat.jei.vacuumFreezer.VacuumFreezerRecipeHandler;

@mezz.jei.api.JEIPlugin
public class TechRebornJeiPlugin implements IModPlugin {
    public static IJeiHelpers jeiHelpers;

    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
        TechRebornJeiPlugin.jeiHelpers = jeiHelpers;
    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry) {

    }

    @Override
    public void register(IModRegistry registry) {
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(
                new AlloySmelterRecipeCategory(guiHelper),
                new AssemblingMachineRecipeCategory(guiHelper),
                new BlastFurnaceRecipeCategory(guiHelper),
                new CentrifugeRecipeCategory(guiHelper),
                new ChemicalReactorRecipeCategory(guiHelper),
                new FusionReactorRecipeCategory(guiHelper),
                new GrinderRecipeCategory(guiHelper),
                new ImplosionCompressorRecipeCategory(guiHelper),
                new IndustrialElectrolyzerRecipeCategory(guiHelper),
                new IndustrialSawmillRecipeCategory(guiHelper),
                new RollingMachineRecipeCategory(guiHelper),
                new VacuumFreezerRecipeCategory(guiHelper)
        );

        registry.addRecipeHandlers(
                new AlloySmelterRecipeHandler(),
                new AssemblingMachineRecipeHandler(),
                new BlastFurnaceRecipeHandler(),
                new CentrifugeRecipeHandler(),
                new ChemicalReactorRecipeHandler(),
                new FusionReactorRecipeHandler(),
                new GrinderRecipeHandler(),
                new ImplosionCompressorRecipeHandler(),
                new IndustrialElectrolyzerRecipeHandler(),
                new IndustrialSawmillRecipeHandler(),
                new RollingMachineRecipeHandler(),
                new VacuumFreezerRecipeHandler()
        );

        registry.addRecipes(RecipeHandler.recipeList);
        registry.addRecipes(FusionReactorRecipeHelper.reactorRecipes);

        try {
            registry.addRecipes(RollingMachineRecipeMaker.getRecipes());
        } catch (RuntimeException e) {
            Core.logHelper.error("Could not register rolling machine recipes. JEI may have changed its internal recipe wrapper locations.");
            e.printStackTrace();
        }

        if (mezz.jei.config.Config.isDebugModeEnabled()) {
            addDebugRecipes(registry);
        }

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloyFurnace.class, RecipeCategoryUids.ALLOY_SMELTER, 0, 2, 4, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloySmelter.class, RecipeCategoryUids.ALLOY_SMELTER, 0, 2, 8, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloyFurnace.class, VanillaRecipeCategoryUid.FUEL, 3, 1, 4, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerAssemblingMachine.class, RecipeCategoryUids.ASSEMBLING_MACHINE, 0, 2, 8, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerBlastFurnace.class, RecipeCategoryUids.BLAST_FURNACE, 0, 2, 4, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerCentrifuge.class, RecipeCategoryUids.CENTRIFUGE, 0, 2, 11, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerChemicalReactor.class, RecipeCategoryUids.CHEMICAL_REACTOR, 0, 2, 8, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerFusionReactor.class, RecipeCategoryUids.FUSION_REACTOR, 0, 2, 3, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerGrinder.class, RecipeCategoryUids.GRINDER, 0, 2, 6, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerImplosionCompressor.class, RecipeCategoryUids.IMPLOSION_COMPRESSOR, 0, 2, 4, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerIndustrialElectrolyzer.class, RecipeCategoryUids.INDUSTRIAL_ELECTROLYZER, 0, 2, 7, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerIndustrialSawmill.class, RecipeCategoryUids.INDUSTRIAL_SAWMILL, 0, 2, 5, 36);
        recipeTransferRegistry.addRecipeTransferHandler(ContainerVacuumFreezer.class, RecipeCategoryUids.VACUUM_FREEZER, 0, 1, 2, 36);
    }

    @Override
    public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {

    }

    private static void addDebugRecipes(IModRegistry registry) {
        ItemStack diamondBlock = new ItemStack(Blocks.diamond_block);
        ItemStack dirtBlock = new ItemStack(Blocks.dirt);
        List<Object> debugRecipes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int time = (int) Math.round(200 + Math.random() * 100);
            AssemblingMachineRecipe assemblingMachineRecipe = new AssemblingMachineRecipe(diamondBlock, diamondBlock, dirtBlock, time, 120);
            debugRecipes.add(assemblingMachineRecipe);
        }
        for (int i = 0; i < 10; i++) {
            int time = (int) Math.round(200 + Math.random() * 100);
            ImplosionCompressorRecipe recipe = new ImplosionCompressorRecipe(diamondBlock, diamondBlock, dirtBlock, dirtBlock, time, 120);
            debugRecipes.add(recipe);
        }
        registry.addRecipes(debugRecipes);
    }
}
