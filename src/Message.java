import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {
    private static final long serialVersionUID = 8191101945355330400L;
    private final String color;
    private final String font;
    private final String content;

    public Message(String color, String font, String content) {
        this.color = color;
        this.font = font;
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public String getFont() {
        return font;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("Color: %s, Font: %s, Content: %s", color, font, content);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Message)) {
            return false;
        }

        final Message message = (Message) object;
        return this.color.equalsIgnoreCase(message.color) && this.font.equalsIgnoreCase(message.font) && this.content.equalsIgnoreCase(message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, font, content);
    }

}
