package ex.nervisking.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityName {

	public static String getDeathCause(Player player) {
        if (player.getLastDamageCause() != null) {
            EntityDamageEvent lastDamageEvent = player.getLastDamageCause();

            return switch (lastDamageEvent.getCause()) {
                case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
                    if (lastDamageEvent instanceof EntityDamageByEntityEvent entityEvent) {
                        Entity damager = entityEvent.getDamager();
                        yield "Ataque de " + getEntityName(damager);
                    } else {
                        yield "Ataque de entidad";
                    }
                }
                case FALL -> "Daño por caída";
                case DROWNING -> "Ahogamiento";
                case FIRE -> "Fuego";
                case LAVA -> "Lava";
                case SUFFOCATION -> "Sofocación";
                case VOID -> "Caída en el vacío";
                case BLOCK_EXPLOSION -> "Explosión";
                case ENTITY_EXPLOSION -> {
                    if (lastDamageEvent instanceof EntityDamageByEntityEvent entityEvent) {
                        Entity damager = entityEvent.getDamager();
                        yield getEntityName(damager);
                    } else {
                        yield "Explosión de entidad";
                    }
                }
                case PROJECTILE -> {
                    if (lastDamageEvent instanceof EntityDamageByEntityEvent entityEvent) {
                        Entity damager = entityEvent.getDamager();
                        yield "Daño por proyectil: " + getEntityName(damager);
                    } else {
                        yield "Daño por proyectil";
                    }
                }
                case MAGIC -> "Daño mágico";
                case POISON -> "Veneno";
                case STARVATION -> "Inanición";
                case SUICIDE -> "Suicidio";
                case LIGHTNING -> "Daño por relámpago";
                case CONTACT -> "Daño por contacto";
                case THORNS -> "Daño por espinas";
                case DRAGON_BREATH -> "Aliento de dragón";
                case WITHER -> "Efecto de marchitamiento";
                case FALLING_BLOCK -> "Aplastado por un bloque cayendo";
                case HOT_FLOOR -> "Daño por caminar sobre magma";
                case CRAMMING -> "(cramming)";
                case FLY_INTO_WALL -> "Chocar contra una pared mientras vuelas";
                case FREEZE -> "Congelado";
                case SONIC_BOOM -> "Explosión sónica (Warden)";
                default -> "Desconocido";
            };
        } else {
            return "Desconocido";
        }
    }

    public static String getEntityName(Entity entity) {
        if (entity.getCustomName() != null && !entity.getCustomName().isEmpty()) {
            return ChatColor.stripColor(entity.getCustomName()).replace("_", " ").toLowerCase();
        }
        if (entity instanceof Player player){
            return player.getName();
        }
        EntityType entityType = entity.getType();
        return switch (entityType) {
            case SKELETON -> "Esqueleto";
            case CREEPER -> "Creeper";
            case SPIDER -> "Araña";
            case ZOMBIE -> "Zombi";
            case ENDERMAN -> "Enderman";
            case WITCH -> "Bruja";
            case GHAST -> "Fantasma";
            case BLAZE -> "Blaze";
            case SLIME -> "Slime";
            case MAGMA_CUBE -> "Cubo de Magma";
            case PHANTOM -> "fantasma";
            case DROWNED -> "Ahogado";
            case HUSK -> "Husk";
            case STRAY -> "Stray";
            case ZOMBIFIED_PIGLIN -> "Piglin Zombificado";
            case WITHER_SKELETON -> "Esqueleto Wither";
            case PILLAGER -> "Pillager";
            case EVOKER -> "Evocador";
            case VEX -> "Vex";
            case ILLUSIONER -> "Ilusionista";
            case VINDICATOR -> "Vindicator";
            case GIANT -> "Gigante";
            case ENDER_DRAGON -> "Dragón del End";
            case WITHER -> "Wither";
            case ZOMBIE_HORSE -> "Caballo Zombi";
            case SKELETON_HORSE -> "Caballo Esqueleto";
            case STRIDER -> "Strider";
            case RAVAGER -> "Ravager";
            case ARROW -> "flecha";
            case SPECTRAL_ARROW -> "flecha espectral";
            case TRIDENT -> "tridente";
            case ENDER_PEARL -> "perla de ender";
            default -> entityType.name().replace("_", " ").toLowerCase();
        };
    }
}
