/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.frostedheart.network;

import com.teammoeg.frostedheart.climate.chunkdata.ChunkData;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkDataCache;
import com.teammoeg.frostedheart.climate.chunkdata.ChunkMatrix;
import com.teammoeg.frostedheart.util.FHUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TemperatureChangePacket {
    private final int chunkX;
    private final int chunkZ;
    private final ChunkMatrix tempMatrix;

    public TemperatureChangePacket(int chunkX, int chunkZ, ChunkMatrix tempMatrix) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.tempMatrix = tempMatrix;
    }

    TemperatureChangePacket(PacketBuffer buffer) {
        chunkX = buffer.readVarInt();
        chunkZ = buffer.readVarInt();
        tempMatrix = new ChunkMatrix(buffer);
    }

    void encode(PacketBuffer buffer) {
        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
        tempMatrix.serialize(buffer);
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ChunkPos pos = new ChunkPos(chunkX, chunkZ);
            // Update client-side chunk data capability
            World world = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> FHUtils::getWorld);
            if (world != null) {
                // First, synchronize the chunk data in the capability and cache.
                // Then, update the single data instance with the packet data
                IChunk chunk = world.chunkExists(chunkX, chunkZ) ? world.getChunk(chunkX, chunkZ) : null;
                ChunkData data = ChunkData.getCapability(chunk)
                        .map(dataIn -> {
                            ChunkDataCache.CLIENT.update(pos, dataIn);
                            return dataIn;
                        }).orElseGet(() -> ChunkDataCache.CLIENT.getOrCreate(pos));
                data.onUpdatePacket(tempMatrix);
            }
        });
        context.get().setPacketHandled(true);
    }
}