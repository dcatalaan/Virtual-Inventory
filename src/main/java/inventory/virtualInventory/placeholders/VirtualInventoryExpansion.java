package inventory.virtualInventory.placeholders;

import inventory.virtualInventory.VirtualInventory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Esta clase registrará los placeholders dinámicos basados en los ítems configurados.
 * Por ejemplo: %mv_DIAMOND% mostrará la cantidad de diamantes en el inventario virtual.
 */
public class VirtualInventoryExpansion extends PlaceholderExpansion {

    private final VirtualInventory plugin;
    
    public VirtualInventoryExpansion(VirtualInventory plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }
    
    @Override
    public String getIdentifier() {
        return "mv";
    }
    
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true; // Mantener la expansión registrada hasta que el servidor se detenga
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) {
            return "";
        }
        
        // Comprobar si el placeholder es "total"
        if (identifier.equalsIgnoreCase("total")) {
            return getTotalItems(player);
        }
        
        // Verificar primero para "has_" placeholders
        if (identifier.toLowerCase().startsWith("has_")) {
            String itemId = identifier.substring(4); // Quitar el prefijo "has_"
            return processHasPlaceholder(player, itemId);
        }
        
        // Procesar placeholders normales de cantidad
        return processItemCountPlaceholder(player, identifier);
    }
    
    /**
     * Procesa placeholders que verifican si un jugador tiene un ítem
     */
    private String processHasPlaceholder(OfflinePlayer player, String itemId) {
        // Buscar primero la coincidencia directa
        for (String trackingItem : plugin.getVirtualInventoryManager().getTrackingItems()) {
            String cleanItemKey = trackingItem.replace(":", "_").toLowerCase();
            
            if (itemId.equalsIgnoreCase(cleanItemKey)) {
                return plugin.getVirtualInventoryManager().hasItem(player.getUniqueId(), trackingItem) ? "true" : "false";
            }
        }
        
        // Intentar con coincidencia aproximada para namespaces con guiones
        for (String trackingItem : plugin.getVirtualInventoryManager().getTrackingItems()) {
            if (trackingItem.contains(":")) {
                String normalizedTrackingItem = trackingItem.replace("-", "").replace(":", "_").toLowerCase();
                String normalizedRequestItem = itemId.replace("-", "").toLowerCase();
                
                if (normalizedTrackingItem.equals(normalizedRequestItem)) {
                    return plugin.getVirtualInventoryManager().hasItem(player.getUniqueId(), trackingItem) ? "true" : "false";
                }
            }
        }
        
        return "false";
    }
    
    /**
     * Procesa placeholders que muestran la cantidad de un ítem
     */
    private String processItemCountPlaceholder(OfflinePlayer player, String identifier) {
        // Buscar primero la coincidencia directa
        for (String trackingItem : plugin.getVirtualInventoryManager().getTrackingItems()) {
            String cleanItemKey = trackingItem.replace(":", "_").toLowerCase();
            
            if (identifier.equalsIgnoreCase(cleanItemKey)) {
                return String.valueOf(plugin.getVirtualInventoryManager().getItemAmount(player.getUniqueId(), trackingItem));
            }
        }
        
        // Intentar con coincidencia aproximada para namespaces con guiones
        for (String trackingItem : plugin.getVirtualInventoryManager().getTrackingItems()) {
            if (trackingItem.contains(":")) {
                String normalizedTrackingItem = trackingItem.replace("-", "").replace(":", "_").toLowerCase();
                String normalizedRequestItem = identifier.replace("-", "").toLowerCase();
                
                if (normalizedTrackingItem.equals(normalizedRequestItem)) {
                    return String.valueOf(plugin.getVirtualInventoryManager().getItemAmount(player.getUniqueId(), trackingItem));
                }
            }
        }
        
        return "0";
    }
    
    /**
     * Obtiene el número total de ítems diferentes en el inventario virtual del jugador
     */
    private String getTotalItems(OfflinePlayer player) {
        int total = 0;
        
        for (String itemKey : plugin.getVirtualInventoryManager().getTrackingItems()) {
            if (plugin.getVirtualInventoryManager().hasItem(player.getUniqueId(), itemKey)) {
                total++;
            }
        }
        
        return String.valueOf(total);
    }
} 