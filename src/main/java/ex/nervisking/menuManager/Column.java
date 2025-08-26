package ex.nervisking.menuManager;

public enum Column {
    LEFT(0),
    MIDDLE_LEFT(1),
    CENTER(4),
    MIDDLE_RIGHT(7),
    RIGHT(8);

    private final int index;

    Column(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}