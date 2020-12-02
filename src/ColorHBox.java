import javafx.scene.layout.HBox;

public class ColorHBox extends HBox {
    private final String color;

    public ColorHBox(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

}
