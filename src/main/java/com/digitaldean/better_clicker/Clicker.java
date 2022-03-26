package com.digitaldean.better_clicker;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Random;

public abstract class Clicker {
    @Getter
    @Setter
    private boolean enabled = false, mobCheck = false, playerCheck = false, blockCheck = false, cooldownCheck = false, criticalCheck = false, hold = false, dynamicInterval = false, doJitter = false, dynamicJitter = false;
    @Getter
    @Setter
    private int interval = 12, intervalMin = 12, intervalMax = 16;
    @Getter
    @Setter
    private float jitter = 4, jitterMin = 4, jitterMax = 8, jitterRadius = 5, jitterOffYaw, jitterOffPitch;
    private int tick = 0;

    public boolean check() {
        MinecraftClient client = MinecraftClient.getInstance();
        return (!mobCheck || client.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof MobEntity) && (!playerCheck || client.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof PlayerEntity) && (!cooldownCheck || client.player != null && client.player.getAttackCooldownProgress(0.5f) > 0.9) && (!blockCheck || client.crosshairTarget instanceof BlockHitResult) && (!criticalCheck || client.player.getAttackCooldownProgress(0.5f) > 0.9 && client.player.fallDistance > 0.0f && !client.player.isOnGround() && !client.player.isClimbing() && !client.player.isTouchingWater() && !client.player.hasStatusEffect(StatusEffects.BLINDNESS) && !client.player.hasVehicle());
    }

    public void tick() {
        if (hold) {
            KeyBinding holdKey = getHoldKey();
            boolean b = check();
            if (b == !holdKey.isPressed()) holdKey.setPressed(b);
        } else {
            if (enabled && tick-- <= 0 && check()) {
                tick = dynamicInterval ? (int) Math.floor((intervalMax - intervalMin) * new Random().nextDouble()) + intervalMin : interval;
                click();
                if (doJitter) {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        float yaw = Math.min(Math.max(getJitterStrength(), - jitterRadius - jitterOffYaw), jitterRadius - jitterOffYaw);
                        float pitch = Math.min(Math.max(getJitterStrength(), - jitterRadius - jitterOffPitch), jitterRadius - jitterOffPitch);
                        jitterOffYaw += yaw;
                        jitterOffPitch += pitch;
                        player.updatePositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw() + yaw, player.getPitch() + pitch);
                    }
                }
            }
        }
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            jitterOffYaw = 0;
            jitterOffPitch = 0;
        }
    }

    public float getJitterStrength() {
        return (dynamicJitter ? (int) Math.floor((jitterMax - jitterMin) * new Random().nextDouble()) + jitterMin : jitter) * (new Random().nextBoolean() ? 1 : -1);
    }

    public abstract void click();

    public abstract KeyBinding getHoldKey();
}
