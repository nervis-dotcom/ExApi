package ex.nervisking.ModelManager;

public enum ExPl {

    EXMAGIC("ExMagic"),
    EXDELUXE("ExDeluxe"),
    EXDISCORD("ExDiscord"),
    EXAFK("ExAfk"),
    EXGATHERERS("ExGatherers"),
    EXCOFFI("ExCoffi"),
    EXBLAERO10("ExBlaero10"),
    EXRANKUP("ExRankup"),
    EXREWARDS("ExRewards"),
    EXPETS("ExPets"),
    EXUSEFUL("ExUseful"),
    EXBLOCKPROTECTOR("ExBlockProtector"),
    EXNAMETAGS("ExNameTags"),
    EXINVENTORYPANEL("ExInventoryPanel");

    private final String name;

    ExPl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ExPl fromString(String name) {
        try {
            return ExPl.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}