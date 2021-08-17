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

package com.teammoeg.frostedheart;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class FHConfig {

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FHConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FHConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, FHConfig.SERVER_CONFIG);
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue enablesTemperatureOrb;
        public final ForgeConfigSpec.IntValue tempOrbOffsetX;
        public final ForgeConfigSpec.IntValue tempOrbOffsetY;
        public final ForgeConfigSpec.EnumValue<TempOrbPos> tempOrbPosition;

        Client(ForgeConfigSpec.Builder builder) {
            enablesTemperatureOrb = builder
                    .comment("Enables the temperature orb overlay. ")
                    .define("enableTemperatureOrb", true);

            tempOrbPosition = builder
                    .comment("Position of the temperature orb in game screen. ")
                    .defineEnum("renderTempOrbAtCenter", TempOrbPos.MIDDLE);

            tempOrbOffsetX = builder
                    .comment("X Offset of the temperature orb. The anchor point is defined by the tempOrbPosition value. Only when you set tempOrbPosition to value other than MIDDLE will this value be used. ")
                    .defineInRange("tempOrbOffsetX", 0, -4096, 4096);

            tempOrbOffsetY = builder
                    .comment("Y Offset of the temperature orb. The anchor point is defined by the tempOrbPosition value. Only when you set tempOrbPosition to value other than MIDDLE will this value be used.  ")
                    .defineInRange("tempOrbOffsetY", 0, -4096, 4096);
        }
    }

    public static class Common {
        Common(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue alwaysKeepInventory;

        Server(ForgeConfigSpec.Builder builder) {
            alwaysKeepInventory = builder
                    .comment("Always keep inventory on death on every dimension and world")
                    .define("alwaysKeepInventory", false);
        }
    }

    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final Client CLIENT;
    public static final Common COMMON;
    public static final Server SERVER;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT = new Client(CLIENT_BUILDER);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON = new Common(COMMON_BUILDER);
        COMMON_CONFIG = COMMON_BUILDER.build();
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        SERVER = new Server(SERVER_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public enum TempOrbPos {
        MIDDLE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }
}