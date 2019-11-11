package mart.firefly.block;

import epicsquid.mysticallib.util.Util;
import mart.firefly.Firefly;
import mart.firefly.tile.FireflyPressTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FireflyPressBlock extends Block implements ITile<FireflyPressTile> {

    public FireflyPressBlock() {
        super(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.5F));
        setRegistryName(new ResourceLocation(Firefly.MODID, "firefly_press"));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote){
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof INamedContainerProvider){
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
                return true;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }



    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FireflyPressTile();
    }

    @Override
    public Supplier<FireflyPressTile> getTile() {
        return FireflyPressTile::new;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        FireflyPressTile tile = (FireflyPressTile)worldIn.getTileEntity(pos);
        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(handler -> Util.spawnInventoryInWorld(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler));

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
}
