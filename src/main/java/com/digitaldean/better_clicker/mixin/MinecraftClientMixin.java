package com.digitaldean.better_clicker.mixin;

import com.digitaldean.better_clicker.duck.MinecraftClientDuck;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientDuck {

    @Shadow
    protected int attackCooldown;

    @Shadow
    private int itemUseCooldown;

    @Override
    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    @Override
    public void setItemUseCooldown(int itemUseCooldown) {
        this.itemUseCooldown = itemUseCooldown;
    }
}
