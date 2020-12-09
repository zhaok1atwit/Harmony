package struct;

import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Miscellaneous utilities used throughout the program
 * @author Matt Lefebvre
 */
public final class Utils {

    private Utils() {}

    /**
     * Fully capitalizes a {@link String} (e.g. - "matt lefebvre" to "Matt Lefebvre")
     * @param string string
     * @return fully capitalized string
     */
    public static String fullyCapitalize(String string) {
        final String[] splitString = string.split(" ");
        for (int n = 0; n < splitString.length; n++) {
            final String temp = splitString[n];
            splitString[n] = (temp.length() > 1) ? temp.substring(0, 1).toUpperCase() + temp.substring(1).toLowerCase() : temp.toUpperCase();
        }
        return String.join(" ", splitString);
    }

    /**
     * Returns a list of all colors {@link Color}
     * @return
     */
    public static List<String> getAllColors() {
        return Arrays.stream(Color.class.getDeclaredFields()).filter(field -> field.getType() == Color.class && Modifier.isStatic(field.getModifiers())).map(Field::getName).collect(Collectors.toList());
    }

}
