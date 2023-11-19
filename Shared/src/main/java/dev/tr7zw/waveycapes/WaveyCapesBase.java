package dev.tr7zw.waveycapes;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public abstract class WaveyCapesBase extends ModBase {

    public static WaveyCapesBase INSTANCE;

    public void init() {
        INSTANCE = this;
        super.init();
        initSupportHooks();
    }

    public Screen createConfigScreen(Screen parent) {
        CustomConfigScreen screen = new CustomConfigScreen(parent, "text.wc.title") {

            private int rotationX = 164;
            private int rotationY = 5;

            @Override
            public void initialize() {
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> config.capeStyle,
                        (v) -> config.capeStyle = v));
                options.add(getEnumOption("text.wc.setting.windmode", WindMode.class, () -> config.windMode,
                        (v) -> config.windMode = v));
                options.add(getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> config.capeMovement,
                        (v) -> config.capeMovement = v));
                // options.add(getIntOption("text.wc.setting.capeparts", 16, 64, () ->
                // config.capeParts, (v) -> config.capeParts = v));
                options.add(getIntOption("text.wc.setting.gravity", 5, 32, () -> config.gravity,
                        (v) -> config.gravity = v));
                options.add(getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> config.heightMultiplier,
                        (v) -> config.heightMultiplier = v));
                // options.add(getIntOption("text.wc.setting.maxBend", 1, 20, () ->
                // config.maxBend, (v) -> config.maxBend = v));

                getOptions().addSmall(options.toArray(new OptionInstance[0]));

            }

            @Override
            public void save() {
                writeConfig();
            }

            @Override
            public boolean keyPressed(int i, int j, int k) {
                if (i == 263) { // left
                    rotationX--;
                }
                if (i == 262) { // right
                    rotationX++;
                }
                if (i == 264) { // down
                    rotationY--;
                }
                if (i == 265) { // up
                    rotationY++;
                }
                return super.keyPressed(i, j, k);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int xMouse, int yMouse, float f) {
                super.render(guiGraphics, xMouse, yMouse, f);
                if (this.minecraft.level != null) {
                    int x = minecraft.getWindow().getGuiScaledWidth() / 2;
                    int y = minecraft.getWindow().getGuiScaledHeight()
                            - (minecraft.getWindow().getGuiScaledHeight() / 3);
                    int size = (int) (40f * (minecraft.getWindow().getGuiScaledHeight() / 200f));
                    drawEntity(x, y, size, rotationX, rotationY, this.minecraft.player, f);
                }
            }

            @Override
            public void reset() {
                config = new Config();
                writeConfig();
            }

        };

        return screen;
    }

    // Modified version from InventoryScreen
    private void drawEntity(int x, int y, int size, float lookX, float lookY, LivingEntity livingEntity, float delta) {
        float rotationModifyer = 3;
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(x, y, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 1, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion2 = Axis.XP.rotationDegrees(lookY * rotationModifyer);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        matrixStack.translate(0.0D, -1, 0D);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = livingEntity.getYRot();
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = livingEntity.getXRot();
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 vel = livingEntity.getDeltaMovement();
        livingEntity.yBodyRot = 180.0F + (lookX * rotationModifyer);
        livingEntity.setYRot(180.0F + (lookX * rotationModifyer));
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = livingEntity.getYRot();
        livingEntity.setDeltaMovement(Vec3.ZERO);
        livingEntity.setXRot(0);
        livingEntity.xRotO = livingEntity.getXRot();
        livingEntity.yHeadRot = livingEntity.getYRot();
        livingEntity.yHeadRotO = livingEntity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, matrixStack, bufferSource, 15728880);
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        livingEntity.setYRot(yRot);
        livingEntity.yRotO = yRotO;
        livingEntity.setXRot(xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(vel);
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

}
