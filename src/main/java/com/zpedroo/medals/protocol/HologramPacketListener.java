package com.zpedroo.medals.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.zpedroo.medals.Main;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HologramPacketListener extends PacketAdapter {

    private Main main;

    public HologramPacketListener(Main main) {
        super(main, PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.ENTITY_DESTROY);
        this.main = main;
    }

    public void onPacketSending(PacketEvent packetEvent) {
        if (packetEvent.isAsync() || packetEvent.isAsynchronous()) return;

        if (packetEvent.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY) {
            int[] array = packetEvent.getPacket().getIntegerArrays().read(0);
            World world = packetEvent.getPlayer().getWorld();
            for (int length = array.length, i = 0; i < length; ++i) {
                Entity entityFromID = ProtocolLibrary.getProtocolManager().getEntityFromID(world, array[i]);
                if (entityFromID instanceof Player) {
                    main.getHologramManager().removeHologram((Player) entityFromID);
                }
            }
        }
        else {
            if (packetEvent.getPacket().getIntegers().read(0) < 0) return;

            Entity entityFromID2 = ProtocolLibrary.getProtocolManager().getEntityFromID(packetEvent.getPlayer().getWorld(), packetEvent.getPacket().getIntegers().read(0));
            Player player = null;
            if (entityFromID2 instanceof Player) {
                player = (Player) entityFromID2;
            }
            if (player != null) {
                main.getHologramManager().updateHologram(player);
            }
        }
    }
}
