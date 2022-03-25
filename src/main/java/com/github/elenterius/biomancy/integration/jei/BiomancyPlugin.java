package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.BioLabScreen;
import com.github.elenterius.biomancy.client.gui.DecomposerScreen;
import com.github.elenterius.biomancy.client.gui.DigesterScreen;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.world.inventory.menu.BioLabMenu;
import com.github.elenterius.biomancy.world.inventory.menu.DecomposerMenu;
import com.github.elenterius.biomancy.world.inventory.menu.DigesterMenu;
import com.github.elenterius.biomancy.world.inventory.slot.ISlotZone;
import com.google.common.collect.ImmutableSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Objects;

@JeiPlugin
public class BiomancyPlugin implements IModPlugin {

	private static final ResourceLocation ID = BiomancyMod.createRL("plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
//		registration.registerSubtypeInterpreter(ModItems.GENERIC_SERUM.get(), SerumSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new DecomposerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new BioLabRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DigesterRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.DECOMPOSING_RECIPE_TYPE)), DecomposerRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.BIO_BREWING_RECIPE_TYPE)), BioLabRecipeCategory.ID);
		registration.addRecipes(ImmutableSet.copyOf(world.getRecipeManager().getAllRecipesFor(ModRecipes.DIGESTING_RECIPE_TYPE)), DigesterRecipeCategory.ID);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(DecomposerScreen.class, 75, 23, 20, 4, DecomposerRecipeCategory.ID);
		registration.addRecipeClickArea(BioLabScreen.class, 98, 67, 11, 19, BioLabRecipeCategory.ID);
		registration.addRecipeClickArea(DigesterScreen.class, 75, 23, 20, 4, DigesterRecipeCategory.ID);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registerInputSlots(registration, DecomposerMenu.class, DecomposerRecipeCategory.ID, DecomposerMenu.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, BioLabMenu.class, BioLabRecipeCategory.ID, BioLabMenu.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, DigesterMenu.class, DigesterRecipeCategory.ID, DigesterMenu.SlotZone.INPUT_ZONE);
	}

	private <C extends AbstractContainerMenu> void registerInputSlots(IRecipeTransferRegistration registration, Class<C> containerClass, ResourceLocation recipeCategoryUid, ISlotZone slotZone) {
		registration.addRecipeTransferHandler(containerClass, recipeCategoryUid, slotZone.getFirstIndex(), slotZone.getSlotCount(), 0, 36);
	}

}
