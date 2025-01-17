/*
 * Copyright (c) 2022-2024 TeamMoeg
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

package com.teammoeg.frostedheart.client.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.primitives.Ints;
import com.teammoeg.frostedheart.FHMain;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class LiningFinalizedModel implements IBakedModel {

    public static ResourceLocation buffCoatFeetTexture = FHMain.rl("item/lining_overlay/buff_coat_feet");
    public static ResourceLocation buffCoatHelmetTexture = FHMain.rl("item/lining_overlay/buff_coat_helmet");

    public static ResourceLocation buffCoatLegsTexture = FHMain.rl("item/lining_overlay/buff_coat_legs");

    public static ResourceLocation buffCoatTorsoTexture = FHMain.rl("item/lining_overlay/buff_coat_torso");

    public static ResourceLocation gambesonFeetTexture = FHMain.rl("item/lining_overlay/gambeson_feet");

    public static ResourceLocation gambesonHelmetTexture = FHMain.rl("item/lining_overlay/gambeson_helmet");

    public static ResourceLocation gambesonLegsTexture = FHMain.rl("item/lining_overlay/gambeson_legs");

    public static ResourceLocation gambesonTorsoTexture = FHMain.rl("item/lining_overlay/gambeson_torso");

    public static ResourceLocation kelpLiningFeetTexture = FHMain.rl("item/lining_overlay/kelp_lining_feet");

    public static ResourceLocation kelpLiningHelmetTexture = FHMain.rl("item/lining_overlay/kelp_lining_helmet");

    public static ResourceLocation kelpLiningLegsTexture = FHMain.rl("item/lining_overlay/kelp_lining_legs");

    public static ResourceLocation kelpLiningTorsoTexture = FHMain.rl("item/lining_overlay/kelp_lining_torso");

    public static ResourceLocation strawLiningFeetTexture = FHMain.rl("item/lining_overlay/straw_lining_feet");

    public static ResourceLocation strawLiningHelmetTexture = FHMain.rl("item/lining_overlay/straw_lining_helmet");

    public static ResourceLocation strawLiningLegsTexture = FHMain.rl("item/lining_overlay/straw_lining_legs");

    public static ResourceLocation strawLiningTorsoTexture = FHMain.rl("item/lining_overlay/straw_lining_torso");

    private IBakedModel parentModel;
    private ResourceLocation overlay;
    public LiningFinalizedModel(IBakedModel i_parentModel, ResourceLocation texture) {
        parentModel = i_parentModel;
        overlay = texture;
    }
    /**
     * Calculate the normal vector based on four input coordinates
     * Follows minecraft convention that the coordinates are given in anticlockwise direction from the point of view of
     * someone looking at the front of the face
     * assumes that the quad is coplanar but should produce a 'reasonable' answer even if not.
     *
     * @return the packed normal, ZZYYXX
     */
    private int calculatePackedNormal(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4) {

        float xp = x4 - x2;
        float yp = y4 - y2;
        float zp = z4 - z2;

        float xq = x3 - x1;
        float yq = y3 - y1;
        float zq = z3 - z1;

        //Cross Product
        float xn = yq * zp - zq * yp;
        float yn = zq * xp - xq * zp;
        float zn = xq * yp - yq * xp;

        //Normalize
        float norm = (float) Math.sqrt(xn * xn + yn * yn + zn * zn);
        final float SMALL_LENGTH = 1.0E-4F;  //Vec3d.normalise() uses this
        if (norm < SMALL_LENGTH) norm = 1.0F;  // protect against degenerate quad

        norm = 1.0F / norm;
        xn *= norm;
        yn *= norm;
        zn *= norm;

        int x = ((byte) (xn * 127)) & 0xFF;
        int y = ((byte) (yn * 127)) & 0xFF;
        int z = ((byte) (zn * 127)) & 0xFF;
        return x | (y << 0x08) | (z << 0x10);
    }

    /**
     * // Creates a baked quad for the given face.
     * // When you are directly looking at the face, the quad is centred at [centreLR, centreUD]
     * // The left<->right "width" of the face is width, the bottom<-->top "height" is height.
     * // The amount that the quad is displaced towards the viewer i.e. (perpendicular to the flat face you can see) is forwardDisplacement
     * //   - for example, for an EAST face, a value of 0.00 lies directly on the EAST face of the cube.  a value of 0.01 lies
     * //     slightly to the east of the EAST face (at x=1.01).  a value of -0.01 lies slightly to the west of the EAST face (at x=0.99).
     * // The orientation of the faces is as per the diagram on this page
     * //   http://greyminecraftcoder.blogspot.com.au/2014/12/block-models-texturing-quads-faces.html
     * // Read this page to learn more about how to draw a textured quad
     * //   http://greyminecraftcoder.blogspot.co.at/2014/12/the-tessellator-and-worldrenderer-18.html
     *
     * @param centreLR            the centre point of the face left-right
     * @param width               width of the face
     * @param centreUD            centre point of the face top-bottom
     * @param height              height of the face from top to bottom
     * @param forwardDisplacement the displacement of the face (towards the front)
     * @param itemRenderLayer     which item layer the quad is on
     * @param texture             the texture to use for the quad
     * @param face                the face to draw this quad on
     * @return
     */
    private BakedQuad createBakedQuadForFace(float centreLR, float width, float centreUD, float height, float forwardDisplacement,
                                             int itemRenderLayer,
                                             TextureAtlasSprite texture, Direction face) {
        float x1, x2, x3, x4;
        float y1, y2, y3, y4;
        float z1, z2, z3, z4;
        int packednormal;
        final float CUBE_MIN = 0.0F;
        final float CUBE_MAX = 1.0F;

        switch (face) {
            case UP: {
                x1 = x2 = centreLR + width / 2.0F;
                x3 = x4 = centreLR - width / 2.0F;
                z1 = z4 = centreUD + height / 2.0F;
                z2 = z3 = centreUD - height / 2.0F;
                y1 = y2 = y3 = y4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            case DOWN: {
                x1 = x2 = centreLR + width / 2.0F;
                x3 = x4 = centreLR - width / 2.0F;
                z1 = z4 = centreUD - height / 2.0F;
                z2 = z3 = centreUD + height / 2.0F;
                y1 = y2 = y3 = y4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case WEST: {
                z1 = z2 = centreLR + width / 2.0F;
                z3 = z4 = centreLR - width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                x1 = x2 = x3 = x4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case EAST: {
                z1 = z2 = centreLR - width / 2.0F;
                z3 = z4 = centreLR + width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                x1 = x2 = x3 = x4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            case NORTH: {
                x1 = x2 = centreLR - width / 2.0F;
                x3 = x4 = centreLR + width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                z1 = z2 = z3 = z4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case SOUTH: {
                x1 = x2 = centreLR + width / 2.0F;
                x3 = x4 = centreLR - width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                z1 = z2 = z3 = z4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            default: {
                throw new AssertionError("Unexpected Direction in createBakedQuadForFace:" + face);
            }
        }

        // the order of the vertices on the face is (from the point of view of someone looking at the front face):
        // 1 = bottom right, 2 = top right, 3 = top left, 4 = bottom left

        packednormal = calculatePackedNormal(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

        // give our item maximum lighting
        final int BLOCK_LIGHT = 15;
        final int SKY_LIGHT = 15;
        int lightMapValue = LightTexture.packLight(BLOCK_LIGHT, SKY_LIGHT);

        final int minU = 0;
        final int maxU = 16;
        final int minV = 0;
        final int maxV = 16;
        int[] vertexData1 = vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, maxU, maxV, lightMapValue, packednormal);
        int[] vertexData2 = vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, maxU, minV, lightMapValue, packednormal);
        int[] vertexData3 = vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, minU, minV, lightMapValue, packednormal);
        int[] vertexData4 = vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), texture, minU, maxV, lightMapValue, packednormal);
        int[] vertexDataAll = Ints.concat(vertexData1, vertexData2, vertexData3, vertexData4);
        final boolean APPLY_DIFFUSE_LIGHTING = true;
        return new BakedQuad(vertexDataAll, itemRenderLayer, face, texture, APPLY_DIFFUSE_LIGHTING);
    }
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return parentModel.getItemCameraTransforms();
    }
    private TextureAtlasSprite getItemSprite(ResourceLocation modelLocation) {
        AtlasTexture blocksStitchedTextures = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        return blocksStitchedTextures.getSprite(modelLocation);
    }
    private List<BakedQuad> getLiningQuads() {
        List<BakedQuad> returnList = new ArrayList<BakedQuad>(1);
        TextureAtlasSprite liningOverlayTexture = getItemSprite(overlay);
        BakedQuad liningQuad = createBakedQuadForFace(0.5F, 1.0F, 0.5F, 1.0F, -0.4375F, 0, liningOverlayTexture, Direction.SOUTH);
        returnList.add(liningQuad);
        return returnList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        throw new UnsupportedOperationException("The finalised model does not have an override list.");
    }
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return parentModel.getParticleTexture();
    }
    /**
     * We return a list of quads here which is used to draw the chessboard.
     * We do this by getting the list of quads for the base model (the chessboard itself), then adding an extra quad for
     * every piece on the chessboard.  The number of pieces was provided to the constructor of the finalised model.
     *
     * @param state
     * @param side  which side: north, east, south, west, up, down, or null.  NULL is a different kind to the others
     *              see here for more information: http://minecraft.gamepedia.com/Block_models#Item_models
     * @param rand
     * @return the list of quads to be rendered
     */

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        // our texture are only drawn when side is NULL.
        if (side != null) {
            return parentModel.getQuads(state, side, rand);
        }
        List<BakedQuad> combinedQuadsList = new ArrayList(parentModel.getQuads(state, side, rand));
        combinedQuadsList.addAll(getLiningQuads());
        return combinedQuadsList;
    }
    @Override
    public boolean isAmbientOcclusion() {
        return parentModel.isAmbientOcclusion();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return parentModel.isBuiltInRenderer();
    }
    @Override
    public boolean isGui3d() {
        return parentModel.isGui3d();
    }
    @Override
    public boolean isSideLit() {
        return false;
    }
    /**
     * Converts the vertex information to the int array format expected by BakedQuads.  Useful if you don't know
     * in advance what it should be.
     *
     * @param x             x coordinate
     * @param y             y coordinate
     * @param z             z coordinate
     * @param color         RGBA colour format - white for no effect, non-white to tint the face with the specified colour
     * @param texture       the texture to use for the face
     * @param u             u-coordinate of the texture (0 - 16) corresponding to [x,y,z]
     * @param v             v-coordinate of the texture (0 - 16) corresponding to [x,y,z]
     * @param lightmapvalue the blocklight+skylight packed light map value (generally: set this to maximum for items)
     *                      http://greyminecraftcoder.blogspot.com/2020/04/lighting-1144.html
     * @param normal        the packed representation of the normal vector, see calculatePackedNormal().  Used for lighting item.
     * @return
     */
    private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v, int lightmapvalue, int normal) {
        // based on FaceBakery::storeVertexData and FaceBakery::fillVertexData

        final int DUMMY_LIGHTMAP_VALUE = 0xffff;

        return new int[]{
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                lightmapvalue,
                normal
        };
    }

}
