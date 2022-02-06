package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.world.inventory.menu.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {

	private ModMenuTypes() {}

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BiomancyMod.MOD_ID);

	public static final RegistryObject<MenuType<DecomposerMenu>> DECOMPOSER = MENUS.register("decomposer", () -> IForgeMenuType.create(DecomposerMenu::createClientMenu));
	public static final RegistryObject<MenuType<BioLabMenu>> BIO_LAB = MENUS.register("bio_lab", () -> IForgeMenuType.create(BioLabMenu::createClientMenu));
	public static final RegistryObject<MenuType<GlandMenu>> GLAND = MENUS.register("gland", () -> IForgeMenuType.create(GlandMenu::createClientMenu));
	public static final RegistryObject<MenuType<SacMenu>> SAC = MENUS.register("sac", () -> IForgeMenuType.create(SacMenu::createClientMenu));
	public static final RegistryObject<MenuType<GulgeMenu>> GULGE = MENUS.register("gulge", () -> IForgeMenuType.create(GulgeMenu::createClientMenu));
	public static final RegistryObject<MenuType<BioInjectorMenu>> BIO_INJECTOR = MENUS.register("bio_injector", () -> IForgeMenuType.create(BioInjectorMenu::createClientMenu));

	@OnlyIn(Dist.CLIENT)
	static void registerMenuScreens() {
		MenuScreens.register(DECOMPOSER.get(), DecomposerScreen::new);
		MenuScreens.register(BIO_LAB.get(), BioLabScreen::new);
		MenuScreens.register(GLAND.get(), GlandScreen::new);
		MenuScreens.register(SAC.get(), SacScreen::new);
		MenuScreens.register(GULGE.get(), GulgeScreen::new);
		MenuScreens.register(BIO_INJECTOR.get(), BioInjectorScreen::new);
	}

}
