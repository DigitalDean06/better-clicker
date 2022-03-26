package com.digitaldean.better_clicker;

import com.digitaldean.better_clicker.duck.MinecraftClientDuck;
import com.digitaldean.better_clicker.gui.ClickerScreen;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterClicker implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("better_clicker");
    @Getter
    private static BetterClicker instance;
    @Getter
    private Clicker leftClicker, rightClicker;
    private ClickerScreen clickerScreen;
    @Getter
    private boolean enabled = false;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");
        MinecraftClient client = MinecraftClient.getInstance();
        leftClicker = new Clicker() {
            @Override
            public void click() {
                if (client.player == null || client.crosshairTarget == null || client.interactionManager == null || client.world == null)
                    return;
                if (client.player.isRiding()) return;
                switch (client.crosshairTarget.getType()) {
                    case ENTITY: {
                        client.interactionManager.attackEntity(client.player, ((EntityHitResult) client.crosshairTarget).getEntity());
                        break;
                    }
                    case BLOCK: {
                        BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        if (!client.world.getBlockState(blockPos).isAir()) {
                            client.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                            if (!client.world.getBlockState(blockPos).isAir()) break;
                            break;
                        }
                    }
                    case MISS: {
                        if (client.interactionManager.hasLimitedAttackSpeed()) {
                            ((MinecraftClientDuck) client).setAttackCooldown(10);
                        }
                        client.player.resetLastAttackedTicks();
                    }
                }
                client.player.swingHand(Hand.MAIN_HAND);
            }

            @Override
            public KeyBinding getHoldKey() {
                return client.options.attackKey;
            }
        };
        rightClicker = new Clicker() {
            @Override
            public void click() {
                if (client.interactionManager == null || client.player == null || client.world == null || client.interactionManager.isBreakingBlock() || client.player.isRiding() || client.crosshairTarget == null)
                    return;
                ((MinecraftClientDuck) client).setItemUseCooldown(4);
                if (client.player.isRiding()) return;
                for (Hand hand : Hand.values()) {
                    ActionResult actionResult3;
                    ItemStack itemStack = client.player.getStackInHand(hand);
                    if (client.crosshairTarget != null) {
                        switch (client.crosshairTarget.getType()) {
                            case ENTITY -> {
                                EntityHitResult entityHitResult = (EntityHitResult) client.crosshairTarget;
                                Entity entity = entityHitResult.getEntity();
                                if (!client.world.getWorldBorder().contains(entity.getBlockPos())) {
                                    return;
                                }
                                ActionResult actionResult = client.interactionManager.interactEntityAtLocation(client.player, entity, entityHitResult, hand);
                                if (!actionResult.isAccepted()) {
                                    actionResult = client.interactionManager.interactEntity(client.player, entity, hand);
                                }
                                if (!actionResult.isAccepted()) break;
                                if (actionResult.shouldSwingHand()) {
                                    client.player.swingHand(hand);
                                }
                                return;
                            }
                            case BLOCK -> {
                                BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
                                int i = itemStack.getCount();
                                ActionResult actionResult2 = client.interactionManager.interactBlock(client.player, client.world, hand, blockHitResult);
                                if (actionResult2.isAccepted()) {
                                    if (actionResult2.shouldSwingHand()) {
                                        client.player.swingHand(hand);
                                        if (!itemStack.isEmpty() && (itemStack.getCount() != i || client.interactionManager.hasCreativeInventory())) {
                                            client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                        }
                                    }
                                    return;
                                }
                                if (actionResult2 != ActionResult.FAIL) break;
                                return;
                            }
                        }
                    }
                    if (itemStack.isEmpty() || !(actionResult3 = client.interactionManager.interactItem(client.player, client.world, hand)).isAccepted())
                        continue;
                    if (actionResult3.shouldSwingHand()) {
                        client.player.swingHand(hand);
                    }
                    client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                    return;
                }
            }

            @Override
            public KeyBinding getHoldKey() {
                return client.options.useKey;
            }
        };
        KeyBinding openScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding("better_clicker.open_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "better_clicker"));
        KeyBinding toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding("better_clicker.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "better_clicker"));
        leftClicker.toggle();
        ClientTickEvents.END_CLIENT_TICK.register(client1 -> {
            if (openScreen.wasPressed()) {
                client1.setScreen(clickerScreen);
            }
            if (toggle.wasPressed()) {
                toggle();
                if (client1.player != null) client1.player.sendMessage(Text.of(Formatting.DARK_GRAY.toString() + Formatting.BOLD + "\u00BB " + Formatting.GOLD + Formatting.BOLD + "BetterClicker " + Formatting.WHITE + "is now " + (enabled ? Formatting.GREEN.toString() + Formatting.BOLD + "ENABLED" : Formatting.RED.toString() + Formatting.BOLD + "DISABLED") + " " + Formatting.DARK_GRAY + Formatting.BOLD + "\u00AB"), true);
            }
            if (enabled) {
                leftClicker.tick();
                rightClicker.tick();
            }
        });
        instance = this;
        clickerScreen = new ClickerScreen(this);
        LOGGER.info("Initialized");
    }

    public void toggle() {
        enabled = !enabled;
    }
}
