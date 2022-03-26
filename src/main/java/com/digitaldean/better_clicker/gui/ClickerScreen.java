package com.digitaldean.better_clicker.gui;

import com.digitaldean.better_clicker.BetterClicker;
import com.digitaldean.better_clicker.Clicker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClickerScreen extends Screen {

    private Clicker clicker;

    private int counter;

    public ClickerScreen(BetterClicker instance) {
        super(Text.of("BetterClicker"));
        clicker = instance.getLeftClicker();
    }

    @Override
    protected void init() {
        BetterClicker betterClicker = BetterClicker.getInstance();
        counter = 0;
        ClickableWidget widget;
        widget = addDrawableChild(new ButtonWidget(width / 2 - 155, height / 6, 150, 20, Text.of("Left Clicker: " + (clicker == betterClicker.getLeftClicker() ? Formatting.GREEN.toString() + Formatting.BOLD + "SHOWING" : Formatting.RED.toString() + Formatting.BOLD + "HIDING")), button -> {
            if (clicker == betterClicker.getRightClicker()) {
                clicker = betterClicker.getLeftClicker();
                refresh();
            }
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Show configurations for " + Formatting.GOLD + Formatting.BOLD + "LEFT " + Formatting.WHITE + "mouse button clicker"), mouseX, mouseY)));
        if (clicker == betterClicker.getLeftClicker()) widget.active = false;
        widget = addDrawableChild(new ButtonWidget(width / 2 + 5, height / 6, 150, 20, Text.of("Right Clicker: " + (clicker == betterClicker.getRightClicker() ? Formatting.GREEN.toString() + Formatting.BOLD + "SHOWING" : Formatting.RED.toString() + Formatting.BOLD + "HIDING")), button -> {
            if (clicker == betterClicker.getLeftClicker()) {
                clicker = betterClicker.getRightClicker();
                refresh();
            }
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Show configurations for " + Formatting.GOLD + Formatting.BOLD + "RIGHT " + Formatting.WHITE + "mouse button clicker"), mouseX, mouseY)));
        if (clicker == betterClicker.getRightClicker()) widget.active = false;
        addDrawableChild(new ButtonWidget(width / 2 - 155, height / 6 + 24, 310, 20, Text.of(Formatting.GOLD.toString() + Formatting.BOLD + "BetterClicker" + Formatting.WHITE + ": "+ getStatusMessage(betterClicker.isEnabled())), button -> {
            betterClicker.toggle();
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Enable all enabled clickers"), mouseX, mouseY)));
        addDrawableChild(new ButtonWidget(width / 2 - 155, height / 6 + 48, 310, 20, Text.of(Formatting.GOLD.toString() + Formatting.BOLD + (clicker == betterClicker.getLeftClicker() ? "LEFT" : "RIGHT") + " " + Formatting.WHITE + "Clicker: " + getStatusMessage(clicker.isEnabled())), button -> {
            clicker.toggle();
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Enable " + Formatting.GOLD + Formatting.BOLD + (clicker == betterClicker.getLeftClicker() ? "LEFT" : "RIGHT") + " " + Formatting.WHITE + "mouse button clicker"), mouseX, mouseY)));
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Mob Check: " + getStatusMessage(clicker.isMobCheck())), button -> {
            clicker.setMobCheck(!clicker.isMobCheck());
            if (clicker.isMobCheck()) {
                clicker.setPlayerCheck(false);
                clicker.setBlockCheck(false);
            }
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Click when there is a mob"), mouseX, mouseY)));
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Player Check: " + getStatusMessage(clicker.isPlayerCheck())), button -> {
            clicker.setPlayerCheck(!clicker.isPlayerCheck());
            if (clicker.isPlayerCheck()) {
                clicker.setMobCheck(false);
                clicker.setBlockCheck(false);
            }
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Click when there is a player"), mouseX, mouseY)));
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Block Check: " + getStatusMessage(clicker.isBlockCheck())), button -> {
            clicker.setBlockCheck(!clicker.isBlockCheck());
            if (clicker.isBlockCheck()) {
                clicker.setMobCheck(false);
                clicker.setPlayerCheck(false);
            }
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Click when there is a block"), mouseX, mouseY)));
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Cooldown Check: " + getStatusMessage(clicker.isCooldownCheck())), button -> {
            clicker.setCooldownCheck(!clicker.isCooldownCheck());
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Click when the cooldown is over"), mouseX, mouseY)));
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Critical Check: " + getStatusMessage(clicker.isCriticalCheck())), button -> {
            clicker.setCriticalCheck(!clicker.isCriticalCheck());
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Click when there is a critical"), mouseX, mouseY)));
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Hold: " + getStatusMessage(clicker.isHold())), button -> {
            clicker.setHold(!clicker.isHold());
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Hold the mouse button instead of spam clicking it"), mouseX, mouseY)));
        if (!clicker.isHold()) {
            addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Dynamic Interval: " + getStatusMessage(clicker.isDynamicInterval())), button -> {
                clicker.setDynamicInterval(!clicker.isDynamicInterval());
                refresh();
            }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Enable dynamic interval"), mouseX, mouseY)));
            if (clicker.isDynamicInterval()) {
                addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, (clicker.getIntervalMin() - 1) / 150.0) {
                    {
                        updateMessage();
                    }

                    @Override
                    protected void updateMessage() {
                        setMessage(Text.of("Min Interval: " + Formatting.GOLD + Formatting.BOLD + (clicker.getIntervalMin() * 50.0 / 1000.0) + " SECONDS"));
                    }

                    @Override
                    protected void applyValue() {
                        clicker.setIntervalMin(Math.min((int) Math.floor(value * 150) + 1, clicker.getIntervalMax()));
                        value = (clicker.getIntervalMin() - 1) / 150.0;
                    }
                });
                addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, (clicker.getIntervalMax() - 1) / 150.0) {
                    {
                        updateMessage();
                    }

                    @Override
                    protected void updateMessage() {
                        setMessage(Text.of("Max Interval: " + Formatting.GOLD + Formatting.BOLD + (clicker.getIntervalMax() * 50.0 / 1000.0) + " SECONDS"));
                    }

                    @Override
                    protected void applyValue() {
                        clicker.setIntervalMax(Math.max((int) Math.floor(value * 150) + 1, clicker.getIntervalMin()));
                        value = (clicker.getIntervalMax() - 1) / 150.0;
                    }
                });
            } else {
                addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, (clicker.getInterval() - 1) / 150.0) {
                    {
                        updateMessage();
                    }

                    @Override
                    protected void updateMessage() {
                        setMessage(Text.of("Interval: " + Formatting.GOLD + Formatting.BOLD + (clicker.getInterval() * 50.0 / 1000.0) + " SECONDS"));
                    }

                    @Override
                    protected void applyValue() {
                        clicker.setInterval((int) Math.floor(value * 150) + 1);
                        value = (clicker.getInterval() - 1) / 150.0;
                    }
                });
            }
        }
        addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Jitter: " + getStatusMessage(clicker.isDoJitter())), button -> {
            clicker.setDoJitter(!clicker.isDoJitter());
            refresh();
        }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Jitter when click"), mouseX, mouseY)));
        if (clicker.isDoJitter()) {
            addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, ((clicker.getJitterRadius() * 10.0) - 1.0) / 150.0) {
                {
                    updateMessage();
                }

                @Override
                protected void updateMessage() {
                    setMessage(Text.of("Jitter Radius: " + Formatting.GOLD + Formatting.BOLD + clicker.getJitterRadius()));
                }

                @Override
                protected void applyValue() {
                    clicker.setJitterRadius(((int) Math.floor(value * 150) + 1) / 10.0f);
                    value = ((clicker.getJitterRadius() * 10.0) - 1.0) / 150.0;
                }
            });
            addWidget(new ButtonWidget(0, 0, 150, 20, Text.of("Dynamic Jitter Strength: " + getStatusMessage(clicker.isDynamicJitter())), button -> {
                clicker.setDynamicJitter(!clicker.isDynamicJitter());
                refresh();
            }, (button, matrices, mouseX, mouseY) -> renderTooltip(matrices, Text.of("Enable dynamic jitter strength"), mouseX, mouseY)));
            if (clicker.isDynamicJitter()) {
                addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, ((clicker.getJitterMin() * 10.0) - 1.0) / 150.0) {
                    {
                        updateMessage();
                    }

                    @Override
                    protected void updateMessage() {
                        setMessage(Text.of("Jitter Min Strength: " + Formatting.GOLD + Formatting.BOLD + clicker.getJitterMin()));
                    }

                    @Override
                    protected void applyValue() {
                        clicker.setJitterMin(Math.min(((int) Math.floor(value * 150) + 1) / 10.0f, clicker.getJitterMax()));
                        value = ((clicker.getJitterMin() * 10.0) - 1.0) / 150.0;
                    }
                });
                addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, ((clicker.getJitterMax() * 10.0) - 1.0) / 150.0) {
                    {
                        updateMessage();
                    }

                    @Override
                    protected void updateMessage() {
                        setMessage(Text.of("Jitter Max Strength: " + Formatting.GOLD + Formatting.BOLD + clicker.getJitterMax()));
                    }

                    @Override
                    protected void applyValue() {
                        clicker.setJitterMax(Math.max(((int) Math.floor(value * 150) + 1) / 10.0f, clicker.getJitterMin()));
                        value = ((clicker.getJitterMax() * 10.0) - 1.0) / 150.0;
                    }
                });
            } else {
                addWidget(new SliderWidget(0, 0, 150, 20, LiteralText.EMPTY, ((clicker.getJitter() * 10.0) - 1.0) / 150.0) {
                    {
                        updateMessage();
                    }

                    @Override
                    protected void updateMessage() {
                        setMessage(Text.of("Jitter Strength: " + Formatting.GOLD + Formatting.BOLD + clicker.getJitter()));
                    }

                    @Override
                    protected void applyValue() {
                        clicker.setJitter(((int) Math.floor(value * 150) + 1) / 10.0f);
                        value = ((clicker.getJitter() * 10.0) - 1.0) / 150.0;
                    }
                });
            }
        }
    }

    public void addWidget(ClickableWidget widget) {
        int counter = this.counter++;
        int widgetHeight = counter / 2 * 24;
        widget.x = width / 2 + (counter % 2 == 0 ? -155 : 5);
        widget.y = height / 6 + 72 + widgetHeight;
        addDrawableChild(widget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void refresh() {
        clearChildren();
        init();
    }

    public String getStatusMessage(boolean b) {
        return b ? Formatting.GREEN.toString() + Formatting.BOLD + "ON" : Formatting.RED.toString() + Formatting.BOLD + "OFF";
    }
}
