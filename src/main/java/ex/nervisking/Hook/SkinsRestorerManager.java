package ex.nervisking.Hook;

import ex.nervisking.utils.ExLog;
import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class SkinsRestorerManager {

    private static final SkinsRestorer skinsRestorer;

    static {
        skinsRestorer = SkinsRestorerProvider.get();
    }

    public static String getSkin(OfflinePlayer offlinePlayer) {
        PlayerStorage playerStorage = skinsRestorer.getPlayerStorage();
        try {
            // Obtener la propiedad del skin del jugador
            Optional<SkinProperty> skinPropertyOptional = playerStorage.getSkinOfPlayer(offlinePlayer.getUniqueId());

            if (skinPropertyOptional.isPresent()) {
                SkinProperty skinProperty = skinPropertyOptional.get();
                // Convertir la propiedad del skin en una URL de textura
                //return PropertyUtils.getSkinTextureUrl(skinProperty);
                return PropertyUtils.getSkinTextureHash(skinProperty);
            } else {
                // Si el jugador no tiene skin personalizado
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void getS(OfflinePlayer offlinePlayer) {
        Optional<MojangSkinDataResult> m;
        try {
            m = skinsRestorer.getSkinStorage().getPlayerSkin(offlinePlayer.getName(), true);
        } catch (DataRequestException e) {
            return;
        }

        for (MojangSkinDataResult v : m.stream().toList()) {
            ExLog.sendInfo("Signature: &8" + v.getSkinProperty().getSignature());
            ExLog.sendInfo("Value: &8" + v.getSkinProperty().getValue());
        }
    }
}