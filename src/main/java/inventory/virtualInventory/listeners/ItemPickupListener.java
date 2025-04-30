package inventory.virtualInventory.listeners;

import inventory.virtualInventory.VirtualInventory;
import inventory.virtualInventory.managers.VirtualInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemPickupListener implements Listener {

    private final VirtualInventory plugin;
    private final VirtualInventoryManager inventoryManager;
    
    public ItemPickupListener(VirtualInventory plugin) {
        this.plugin = plugin;
        this.inventoryManager = plugin.getVirtualInventoryManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        
        // Verificar si el ítem está siendo rastreado
        if (inventoryManager.isTrackingItem(item)) {
            // Añadir al inventario virtual
            boolean added = inventoryManager.addItemToVirtualInventory(player, item);
            
            // Para debugging
            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().info("Ítem detectado: " + item.getType() + ", añadido: " + added);
                
                try {
                    String namespace = item.getType().getKey().getNamespace();
                    String key = item.getType().getKey().getKey();
                    plugin.getLogger().info("Namespace: " + namespace + ", Key: " + key);
                } catch (Exception e) {
                    plugin.getLogger().info("Error al obtener detalles del ítem: " + e.getMessage());
                }
            }
            
            if (added) {
                // Evitar que el jugador recoja físicamente el ítem
                event.setCancelled(true);
                event.getItem().remove();
            }
        } else if (plugin.getConfig().getBoolean("debug", false)) {
            // Para debugging cuando no se detecta un ítem
            try {
                String namespace = item.getType().getKey().getNamespace();
                String key = item.getType().getKey().getKey();
                plugin.getLogger().info("Ítem NO detectado: " + namespace + ":" + key);
            } catch (Exception e) {
                plugin.getLogger().info("Ítem no detectado: " + item.getType().toString());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) return;
        
        // Si el inventario es el inventario virtual, cancelar el evento para hacerlo no interactivo
        if (event.getView().getTitle().equals(plugin.getConfig().getString("inventario.nombre", "Medallas Virtuales")) ||
                event.getView().getTitle().contains(plugin.getConfig().getString("inventario.nombre", "Medallas Virtuales") + " de ")) {
            event.setCancelled(true);
        }
    }
} 