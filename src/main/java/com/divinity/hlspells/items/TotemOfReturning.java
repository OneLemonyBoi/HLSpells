package com.divinity.hlspells.items;

import com.divinity.hlspells.init.ItemInit;
import com.divinity.hlspells.network.NetworkManager;
import com.divinity.hlspells.network.packets.TotemPacket;
import com.divinity.hlspells.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class TotemOfReturning extends Item {

    public TotemOfReturning() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
                .tab(ItemGroup.TAB_COMBAT));
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 0;
    }

    @Override
    public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
        return 1F;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        BlockPos teleportPos = new BlockPos(itemstack.getOrCreateTag().getDouble("dX"),
                itemstack.getOrCreateTag().getDouble("dY"),
                itemstack.getOrCreateTag().getDouble("dZ"));
        String dimension = itemstack.getOrCreateTag().getString("registryKey");

        if (dimension.equals("")) return ActionResult.fail(itemstack);
        doReturn(worldIn, playerIn, teleportPos, dimension);

        itemstack.shrink(1);

        return ActionResult.success(itemstack);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        ActionResultType action = super.onItemUseFirst(stack, context);

        BlockPos teleportPos = new BlockPos(stack.getOrCreateTag().getDouble("dX"),
                stack.getOrCreateTag().getDouble("dY"),
                stack.getOrCreateTag().getDouble("dZ"));
        String dimension = stack.getOrCreateTag().getString("registryKey");

        if ((context.getPlayer() == null) || (dimension.equals(""))) return ActionResultType.FAIL;
        doReturn(context.getLevel(), context.getPlayer(), teleportPos, dimension);

        stack.shrink(1);

        return action;
    }

    public static void doReturn(World world, PlayerEntity entity, BlockPos teleportPos, String dimension) {
        if (entity.level.isClientSide || !(entity instanceof ServerPlayerEntity)) return;
        RegistryKey<World> newKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimension));
        ServerWorld _newWorld = ((ServerWorld) world).getServer().getLevel(newKey);
        if (world != _newWorld) {
            {
                if (_newWorld != null) {
                    ((ServerPlayerEntity) entity).connection
                            .send(new SChangeGameStatePacket(SChangeGameStatePacket.WIN_GAME, 0));
                    ((ServerPlayerEntity) entity).teleportTo(_newWorld, _newWorld.getSharedSpawnPos().getX(),
                            _newWorld.getSharedSpawnPos().getY() + 1, _newWorld.getSharedSpawnPos().getZ(), entity.yRot,
                            entity.xRot);
                    ((ServerPlayerEntity) entity).connection
                            .send(new SPlayerAbilitiesPacket(entity.abilities));
                    for (EffectInstance effectinstance : entity.getActiveEffects()) {
                        ((ServerPlayerEntity) entity).connection
                                .send(new SPlayEntityEffectPacket(entity.getId(), effectinstance));
                    }
                    ((ServerPlayerEntity) entity).connection.send(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
                }
            }
        }
        entity.level.broadcastEntityEvent(entity, (byte) 35);
        NetworkManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new TotemPacket(new ItemStack(ItemInit.TOTEM_OF_RETURNING.get())));
        Util.teleport(_newWorld, entity.blockPosition(), teleportPos, entity);
    }
}
