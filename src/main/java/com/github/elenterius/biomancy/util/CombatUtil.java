package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class CombatUtil {
	private CombatUtil() {}

	public static boolean canPierceThroughArmor(ItemStack weapon, LivingEntity target) {
		int pierceLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, weapon);
		float pct = CombatRules.getDamageAfterAbsorb(20f, target.getArmorValue(), (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) / 20f;
		return target.getRandom().nextFloat() < pct + 0.075f * pierceLevel;
	}

	public static void performWaterAOE(Level world, Entity attacker, double maxDistance) {
		AABB aabb = attacker.getBoundingBox().inflate(maxDistance, maxDistance / 2d, maxDistance);
		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, aabb, ThrownPotion.WATER_SENSITIVE);
		if (!entities.isEmpty()) {
			double maxDistSq = maxDistance * maxDistance;
			for (LivingEntity victim : entities) {
				if (attacker.distanceToSqr(victim) < maxDistSq) {
					victim.hurt(DamageSource.indirectMagic(victim, attacker), 1f);
				}
			}
		}
	}

	public static void doAcidFluidTick(LivingEntity livingEntity) {
		if (livingEntity.tickCount % 5 == 0 && isInAcidFluid(livingEntity)) {
			if (!livingEntity.level.isClientSide) {
				applyAcidEffect(livingEntity, 4);
			}
			else if (livingEntity.tickCount % 10 == 0 && livingEntity.getRandom().nextFloat() < 0.4f) {
				Level level = livingEntity.level;
				RandomSource random = livingEntity.getRandom();
				Vec3 pos = livingEntity.position();
				double height = livingEntity.getBoundingBox().getYsize() * 0.5f;

				level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
				for (int i = 0; i < 4; i++) {
					level.addParticle(ParticleTypes.LARGE_SMOKE, pos.x + random.nextDouble(), pos.y + random.nextDouble() * height, pos.z + random.nextDouble(), 0, 0.1d, 0);
				}
			}
		}
	}

	public static boolean isInAcidFluid(Entity entity) {
		//return entity.isInFluidType(ModFluids.ACID_TYPE.get());
		return false; //FIXME
	}

	public static boolean hasAcidEffect(LivingEntity livingEntity) {
		return livingEntity.hasEffect(ModMobEffects.CORROSIVE.get());
	}

	public static void applyAcidEffect(LivingEntity livingEntity, int seconds) {
		if (livingEntity.hasEffect(ModMobEffects.CORROSIVE.get())) return;

		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), seconds * 20, 0));
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), (seconds + 3) * 20, 0));
	}

	public static void hurtWithAcid(LivingEntity livingEntity, float damage) {
		livingEntity.invulnerableTime = 0; //bypass invulnerable ticks
		livingEntity.hurt(ModDamageSources.CORROSIVE_ACID, damage);
	}

	public static void hurtWithBleed(LivingEntity livingEntity, float damage) {
		livingEntity.invulnerableTime = 0; //bypass invulnerable ticks
		livingEntity.hurt(ModDamageSources.BLEED, damage);
	}

	public static void applyBleedEffect(LivingEntity livingEntity, int seconds) {
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.BLEED.get(), seconds * 20, 0, false, false, true));
	}

	public static int getBleedEffectLevel(LivingEntity target) {
		MobEffectInstance effectInstance = target.getEffect(ModMobEffects.BLEED.get());
		if (effectInstance == null) {
			return 0;
		}
		return effectInstance.getAmplifier() + 1;
	}

}
