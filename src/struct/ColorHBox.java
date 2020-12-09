package struct;

import javafx.scene.layout.HBox;

/**
 * Wrapper for {@link HBox} that allows a String to be associated with the HBox. This component is used in the color list selector
 * @author Matt Lefebvre
 */
public class ColorHBox extends HBox {
    private final String color;

    /**
     * ColorHBox constructor
     * @param color color
     */
    public ColorHBox(String color) {
        this.color = color;
    }

    /**
     * Getter for the color
     * @return
     */
    public String getColor() {
        return this.color;
    }

}
