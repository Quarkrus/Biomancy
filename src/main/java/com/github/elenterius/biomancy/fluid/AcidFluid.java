package com.github.elenterius.biomancy.fluid;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class AcidFluid extends ForgeFlowingFluid {

	protected AcidFluid(Properties properties) {
		super(properties);
	}

	public static void onEntityInside(LivingEntity livingEntity) {
		if (livingEntity.tickCount % 5 == 0 && livingEntity.isInFluidType(ModFluids.ACID_TYPE.get())) {
			if (!livingEntity.level.isClientSide) {
				CombatUtil.applyAcidEffect(livingEntity, 4);
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

	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}

	@Override
	protected void randomTick(Level level, BlockPos liquidPos, FluidState fluidState, RandomSource random) {
		if (level.random.nextFloat() > 0.6f) return;

		for (int i = 0; i < 3; i++) {
			float p = level.random.nextFloat();
			int yOffset = 0;
			if (p < 0.2f) yOffset = 1;
			else if (p < 0.6f) yOffset = -1;
			BlockPos blockPos = liquidPos.offset(random.nextInt(3) - 1, yOffset, random.nextInt(3) - 1);

			if (!level.isLoaded(blockPos)) return;

			BlockState blockState = level.getBlockState(blockPos);
			if (blockState.isAir()) return;

			if (level.random.nextFloat() >= 0.057f) {
				corrodeCopper(level, liquidPos, blockState, blockPos);
			}
		}
	}

	protected void corrodeCopper(Level level, BlockPos liquidPos, BlockState blockState, BlockPos pos) {
		Block block = blockState.getBlock();
		if (block instanceof WeatheringCopper weatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
			weatheringCopper.getNext(blockState).ifPresent(state -> level.setBlockAndUpdate(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, liquidPos, state)));
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		}
	}

	@Override
	protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		if (!state.isSource() && Boolean.FALSE.equals(state.getValue(FALLING))) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
			}
		}
		else if (random.nextInt(10) == 0) {
			level.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0, 0, 0);
		}
	}

	public static class Flowing extends AcidFluid {
		public Flowing(Properties properties) {
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends AcidFluid {

		public Source(Properties properties) {
			super(properties);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}
}