package com.github.springerris.util;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Because it's better with builder syntax.
 */
public final class GridBagConstraintsBuilder {

    private int x = 0;
    private int y = 0;
    private int width = 1;
    private int height = 1;
    private int fill = GridBagConstraints.NONE;
    private Insets insets = new Insets(0, 0, 0, 0);
    private int anchor = GridBagConstraints.CENTER;
    private double weightX = 0d;
    private double weightY = 0d;

    //

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder x(int x) {
        this.x = x;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder y(int y) {
        this.y = y;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder width(int w) {
        this.width = w;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder height(int h) {
        this.height = h;
        return this;
    }

    @Contract("_, _, _, _ -> this")
    public @NotNull GridBagConstraintsBuilder dimensions(int x, int y, int w, int h) {
        return this.x(x).y(y).width(w).height(h);
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder fill(
            @MagicConstant(intValues = { GridBagConstraints.NONE, GridBagConstraints.BOTH, GridBagConstraints.HORIZONTAL, GridBagConstraints.VERTICAL })
            int fill
    ) {
        this.fill = fill;
        return this;
    }

    @Contract("_, _ -> this")
    public @NotNull GridBagConstraintsBuilder fill(boolean horizontal, boolean vertical) {
        int f = (horizontal ? 0b01 : 0b00) | (vertical ? 0b10 : 0b00);
        return switch (f) {
            case 0b00 -> this.fill(GridBagConstraints.NONE);
            case 0b01 -> this.fill(GridBagConstraints.HORIZONTAL);
            case 0b10 -> this.fill(GridBagConstraints.VERTICAL);
            case 0b11 -> this.fill(GridBagConstraints.BOTH);
            default -> throw new AssertionError();
        };
    }

    @Contract("_, _, _, _ -> this")
    public @NotNull GridBagConstraintsBuilder padding(int left, int top, int right, int bottom) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    @Contract("_, _ -> this")
    public @NotNull GridBagConstraintsBuilder padding(int x, int y) {
        return this.padding(x, y, x, y);
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder padding(int pad) {
        return this.padding(pad, pad);
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder anchor(
            @MagicConstant(intValues = { GridBagConstraints.FIRST_LINE_START, GridBagConstraints.PAGE_START, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.LINE_START, GridBagConstraints.CENTER, GridBagConstraints.LINE_END, GridBagConstraints.LAST_LINE_START, GridBagConstraints.PAGE_END, GridBagConstraints.LAST_LINE_END })
            int anchor
    ) {
        this.anchor = anchor;
        return this;
    }

    @Contract("_, _ -> this")
    public @NotNull GridBagConstraintsBuilder anchor(int dx, int dy) {
        int f = ((dx == 0) ? 0b0000 : ((dx < 0) ? 0b0011 : 0b0010)) |
                ((dy == 0) ? 0b0000 : ((dy < 0) ? 0b1100 : 0b1000));
        return switch (f) {
            case 0b0000 -> this.anchor(GridBagConstraints.CENTER);
            case 0b0010 -> this.anchor(GridBagConstraints.LINE_END);
            case 0b0011 -> this.anchor(GridBagConstraints.LINE_START);
            case 0b1000 -> this.anchor(GridBagConstraints.PAGE_END);
            case 0b1100 -> this.anchor(GridBagConstraints.PAGE_START);
            case 0b1010 -> this.anchor(GridBagConstraints.LAST_LINE_END);
            case 0b1011 -> this.anchor(GridBagConstraints.LAST_LINE_START);
            case 0b1110 -> this.anchor(GridBagConstraints.FIRST_LINE_END);
            case 0b1111 -> this.anchor(GridBagConstraints.FIRST_LINE_START);
            default -> throw new AssertionError();
        };
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder weightX(double weightX) {
        this.weightX = weightX;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull GridBagConstraintsBuilder weightY(double weightY) {
        this.weightY = weightY;
        return this;
    }

    @Contract("_, _ -> this")
    public @NotNull GridBagConstraintsBuilder weight(double weightX, double weightY) {
        return this.weightX(weightX).weightY(weightY);
    }

    //

    @Contract("-> new")
    public @NotNull GridBagConstraints build() {
        GridBagConstraints ret = new GridBagConstraints();
        ret.gridx = this.x;
        ret.gridy = this.y;
        ret.gridwidth = this.width;
        ret.gridheight = this.height;
        ret.fill = this.fill;
        ret.insets = this.insets;
        ret.anchor = this.anchor;
        ret.weightx = this.weightX;
        ret.weighty = this.weightY;
        return ret;
    }

}
