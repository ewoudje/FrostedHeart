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

package com.teammoeg.frostedheart.mixin.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireTileEntity.class)
public abstract class CampfireTileEntityMixin extends TileEntity implements ICampfireExtra {
    @Shadow
    public abstract void dropAllItems();

    private int lifeTime = -1337;

    private static boolean isSoul(BlockState state) {
        return (state.getBlock().getRegistryName().toString().indexOf("soul") != -1) || (state.getBlock().getRegistryName().toString().indexOf("ender") != -1);
    }

    @Override
    public int getLifeTime() {
        return lifeTime;
    }

    @Override
    public void addLifeTime(int add) {
        lifeTime += add;
    }

    @Override
    public void setLifeTime(int set) {
        lifeTime = set;
    }

    public CampfireTileEntityMixin(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn.CAMPFIRE);
    }

    private void extinguishCampfire() {
        if (!this.world.isRemote) {
            this.world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.world.setBlockState(this.pos, this.getBlockState().with(CampfireBlock.LIT, false));
        }
    }

    private void breakCampfire() {
        this.dropAllItems();
        if (!this.world.isRemote) {
            this.world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
        }
    }

    @Inject(at = @At("RETURN"), method = "tick()V")
    private void tick(CallbackInfo ci) {
        if (world != null) {
            if (lifeTime != -1337) {
                if (CampfireBlock.isLit(world.getBlockState(getPos())))
                    if (lifeTime > 0)
                        lifeTime--;
                    else {
                        lifeTime = 0;
                        if (isSoul(this.getBlockState())) {
                            extinguishCampfire();
                        } else {
                            extinguishCampfire();
                        }
                    }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "read(Lnet/minecraft/block/BlockState;Lnet/minecraft/nbt/CompoundNBT;)V")
    private void readAdditional(BlockState state, CompoundNBT nbt, CallbackInfo ci) {
        if (nbt.contains("LifeTime", 3)) {
            setLifeTime(nbt.getInt("LifeTime"));
        }
    }

    @Inject(at = @At("RETURN"), method = "write(Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/nbt/CompoundNBT;", cancellable = true)
    private void writeAdditional(CompoundNBT compound, CallbackInfoReturnable<CompoundNBT> cir) {
        CompoundNBT nbt = cir.getReturnValue();
        nbt.putInt("LifeTime", lifeTime);
        cir.setReturnValue(nbt);
    }
}