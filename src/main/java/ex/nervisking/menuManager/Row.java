package ex.nervisking.menuManager;

public enum Row {
    FIRST(0),
    SECOND(1),
    THIRD(2),
    FOURTH(3),
    FIFTH(4),
    SIXTH(5),
    LAST(-1); // marcador para la última fila

    private final int index;

    Row(int index) {
        this.index = index;
    }

    public int getIndex(int inventorySize) {
        if (this == LAST) {
            return (inventorySize / 9) - 1; // última fila según tamaño del inventario
        }
        return index;
    }
}