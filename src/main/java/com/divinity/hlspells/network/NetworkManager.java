package com.divinity.hlspells.network;

import com.divinity.hlspells.HLSpells;
import com.divinity.hlspells.network.packets.TotemPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = HLSpells.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkManager {

    static int index = 0;

    public static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HLSpells.MODID, "main"), () -> NetworkManager.PROTOCOL_VERSION,
            NetworkManager.PROTOCOL_VERSION::equals, NetworkManager.PROTOCOL_VERSION::equals);

    @SubscribeEvent
    public static void registerNetworkStuff(FMLCommonSetupEvent event)
    {
        INSTANCE.registerMessage(index++, TotemPacket.class, TotemPacket::encode, TotemPacket::decode, TotemPacket::whenThisPacketIsReceived);
    }
}