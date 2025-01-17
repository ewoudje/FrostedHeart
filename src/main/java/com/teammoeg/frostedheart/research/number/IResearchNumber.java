/*
 * Copyright (c) 2024 TeamMoeg
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
 *
 */

package com.teammoeg.frostedheart.research.number;

import com.google.gson.JsonElement;
import com.teammoeg.frostedheart.research.data.ResearchData;

public interface IResearchNumber {
    default int getInt(ResearchData rd) {
        return (int) getVal(rd);
    }

    default long getLong(ResearchData rd) {
        return (long) getVal(rd);
    }

    double getVal(ResearchData rd);
    JsonElement serialize();
}
