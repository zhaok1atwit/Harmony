package struct;

import java.io.Serializable;
import java.util.Objects;

/**
 * Message object that is sent between the client and server. This object was created so that we didn't have to concatenate
 * the data this object holds by some arbitrary character and then attempt to split and parse that data introducing an
 * unmaintainable set of rules because the clients input is unpredictable
 * @author Matt Lefebvre
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 8191101945355330400L;
    private final String color;
    private final String font;
    private final String content;

    /**
     * Message constructr
     * @param color color
     * @param font font
     * @param content content
     */
    public Message(String color, String font, String content) {
        this.color = color;
        this.font = font;
        this.content = content;
    }

    /**
     * Getter for the message's color
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * Getter for the message's font
     * @return font
     */
    public String getFont() {
        return font;
    }

    /**
     * Getter for the message's content
     * @return content
     */
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
