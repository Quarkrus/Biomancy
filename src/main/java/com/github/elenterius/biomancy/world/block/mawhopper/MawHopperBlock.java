package com.github.elenterius.biomancy.world.block.mawhopper;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class MawHopperBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public static final VoxelShape SHAPE_UP = createVoxelShape(Direction.UP);
	public static final VoxelShape SHAPE_DOWN = createVoxelShape(Direction.DOWN);
	public static final VoxelShape SHAPE_NORTH = createVoxelShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createVoxelShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createVoxelShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createVoxelShape(Direction.EAST);

	public MawHopperBlock(Properties properties) {
		super(properties);
	}

	private static VoxelShape createVoxelShape(Direction direction) {
		return Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 7, 0, 7, 9, 2, 9),
				VoxelShapeUtil.createXZRotatedTowards(direction, 6, 2, 6, 10, 6, 10),
				VoxelShapeUtil.createXZRotatedTowards(direction, 5, 6, 5, 11, 10, 11),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3, 10, 3, 13, 13, 13),
				VoxelShapeUtil.createXZRotatedTowards(direction, 0, 13, 3, 16, 16, 13),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3, 13, 0, 13, 16, 16)
		).reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR)).orElse(Shapes.block());
	}

	public static Direction getDirection(BlockState state) {
		return state.getValue(FACING);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof MawHopperBlockEntity blockEntity) {
				blockEntity.dropContainerContents(level, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.MAW_HOPPER.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.MAW_HOPPER.get(), MawHopperBlockEntity::serverTick);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MawHopperBlockEntity blockEntity) {
			MawHopperBlockEntity.entityInside(level, pos, state, blockEntity, entity);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case DOWN -> SHAPE_DOWN;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			case EAST -> SHAPE_EAST;
			default -> SHAPE_UP;
		};
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

}