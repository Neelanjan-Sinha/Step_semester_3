// Save as Main.java
abstract class Shape {
    private static int counter = 1000;           // shared across all shapes

    private final String shapeId;                // final: assigned once in the constructor
    protected double dimension;                  // radius for a circle, side for a square

    protected Shape(double dimension) {
        if (dimension <= 0) throw new IllegalArgumentException("dimension must be positive");
        this.dimension = dimension;
        counter++;
        this.shapeId = "SHP-" + counter;
    }

    public abstract double calculateArea();      // formula differs per shape

    // Overload 1: grows the shape equally
    void scale(double factor) {
        if (factor <= 0) throw new IllegalArgumentException("factor must be positive");
        dimension *= factor;
    }

    // Overload 2: grows it unevenly (for a single-dimension shape both factors apply in turn)
    void scale(double xFactor, double yFactor) {
        scale(xFactor);
        scale(yFactor);
    }

    String getShapeId() { return shapeId; }
}

class CircleShape extends Shape {
    public CircleShape(double radius) { super(radius); }

    @Override
    public double calculateArea() { return Math.PI * dimension * dimension; }
}

class SquareShape extends Shape {
    public SquareShape(double side) { super(side); }

    @Override
    public double calculateArea() { return dimension * dimension; }
}

public class Main {
    // Works for any Shape without checking its subclass
    static void printArea(Shape s) {
        System.out.printf("%s area: %.2f%n", s.getShapeId(), s.calculateArea());
    }

    public static void main(String[] args) {
        // Shape s = new Shape(1);   // will NOT compile: Shape is abstract

        CircleShape c = new CircleShape(5.0);
        System.out.println(c.calculateArea());       // ~78.54

        SquareShape sq = new SquareShape(4.0);
        System.out.println(sq.calculateArea());      // 16.0

        sq.scale(2.0);                               // one-argument overload
        System.out.println(sq.calculateArea());      // 64.0

        printArea(c);
    }
}
