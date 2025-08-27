package record_class;

public class record_example {
    public static void main(String[] args) {
        int x = 10;
        int y = 20;

        var circle = new Circle(x, y);

        var circle_string = circle.toString();
        System.out.println(circle_string);

        var circle_record = new CircleRecord(x, y);
        var circle_record_string = circle_record.toString();

        System.out.println(circle_record_string);
    }
}
