package record_class;

import java.util.Objects;

//código repetitivo e longo
public class Circle {
    private final int x;
    private final int y;

    public Circle(int x, int y) {
        this.x = x;
        this.y = y;

    }
    @Override
    public String toString() {
        return "X:" + this.x + " Y:" + this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Circle circle = (Circle) obj;
        return x == circle.x && y == circle.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
