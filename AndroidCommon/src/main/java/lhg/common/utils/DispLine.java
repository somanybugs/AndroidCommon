package lhg.common.utils;

public class DispLine {
    public final String name, value;
    public int nameColor = 0xff3897F0;

    public DispLine(String name, CharSequence value) {
        this.name = name;
        if (value != null) {
            this.value = String.valueOf(value);
        } else {
            this.value = null;
        }
    }

    public DispLine setNameColor(int nameColor) {
        this.nameColor = nameColor;
        return this;
    }
}