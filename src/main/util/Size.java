package main.util;

import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.util.Objects;

public class Size {

    public static final Size ZERO = new Size(0, 0);

    public final double width;
    public final double height;

    public Size(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Size(@NotNull Size size) {
        this(size.width, size.height);
    }

    public Size(@NotNull Point2D start, @NotNull Point2D end) {
        this(end.getX() - start.getX(), end.getY() - start.getY());
    }

    public Size(@NotNull Point2D point) {
        this(point.getX(), point.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof Size) {
            final Size size = (Size) o;
            return width == size.width && height == size.height;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @NotNull
    public Size add(@NotNull Size size) {
        return new Size(width + size.width, height + size.height);
    }

    @NotNull
    public Size subtract(@NotNull Size size) {
        return new Size(width - size.width, height - size.height);
    }

    @NotNull
    public Size scale(double scale) {
        return new Size(width * scale, height * scale);
    }

    @NotNull
    public Size negate() {
        return scale(-1);
    }

    @NotNull
    public Size copy() {
        return new Size(this);
    }
}
