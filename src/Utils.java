import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {

    private Utils() {}

    public static String fullyCapitalize(String string) {
        final String[] splitString = string.split(" ");
        for (int n = 0; n < splitString.length; n++) {
            final String temp = splitString[n];
            splitString[n] = (temp.length() > 1) ? temp.substring(0, 1).toUpperCase() + temp.substring(1).toLowerCase() : temp.toUpperCase();
        }
        return String.join(" ", splitString);
    }

    public static List<String> getAllColors() {
        return Arrays.stream(Color.class.getDeclaredFields()).filter(field -> field.getType() == Color.class && Modifier.isStatic(field.getModifiers())).map(Field::getName).collect(Collectors.toList());
    }

}
