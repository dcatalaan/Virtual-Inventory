package inventory.virtualInventory;

import inventory.virtualInventory.commands.MedallasCommand;
import inventory.virtualInventory.commands.ReloadCommand;
import inventory.virtualInventory.integrations.ItemsAdderIntegration;
import inventory.virtualInventory.listeners.ItemPickupListener;
import inventory.virtualInventory.managers.VirtualInventoryManager;
import inventory.virtualInventory.placeholders.VirtualInventoryExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class VirtualInventory extends JavaPlugin {

    private VirtualInventoryManager virtualInventoryManager;
    private VirtualInventoryExpansion placeholderExpansion;
    private ItemsAdderIntegration itemsAdderIntegration;

    @Override
    public void onEnable() {
        // Guardar configuración por defecto
        saveDefaultConfig();
        
        // Inicializar integración con ItemsAdder
        itemsAdderIntegration = new ItemsAdderIntegration(this);
        
        // Inicializar el gestor de inventario virtual
        virtualInventoryManager = new VirtualInventoryManager(this, itemsAdderIntegration);
        
        // Registrar eventos
        getServer().getPluginManager().registerEvents(new ItemPickupListener(this), this);
        
        // Registrar comandos
        MedallasCommand medallasCommand = new MedallasCommand(this);
        getCommand("medallas").setExecutor(medallasCommand);
        getCommand("medallas").setTabCompleter(medallasCommand);
        
        // Registrar comando de sistema con subcomandos
        ReloadCommand reloadCommand = new ReloadCommand(this);
        getCommand("mvs").setExecutor(reloadCommand);
        getCommand("mvs").setTabCompleter(reloadCommand);
        
        // Registrar expansión de PlaceholderAPI si está disponible
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("¡PlaceholderAPI encontrado! Registrando expansión...");
            placeholderExpansion = new VirtualInventoryExpansion(this);
            if (placeholderExpansion.register()) {
                getLogger().info("¡Expansión de PlaceholderAPI registrada correctamente!");
                getLogger().info("Puedes usar placeholders como %mv_DIAMOND%, %mv_has_DIAMOND%, etc.");
            }
        } else {
            getLogger().warning("PlaceholderAPI no encontrado. Los placeholders no estarán disponibles.");
        }
        
        getLogger().info("¡Inventario Virtual activado correctamente!");
    }

    @Override
    public void onDisable() {
        // Guardar datos al desactivar el plugin
        if (virtualInventoryManager != null) {
            virtualInventoryManager.saveAllPlayerData();
        }
        
        // Desregistrar la expansión de PlaceholderAPI si estaba registrada
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
        
        getLogger().info("¡Inventario Virtual desactivado correctamente!");
    }
    
    /**
     * Recarga el gestor de inventario virtual con la nueva configuración
     */
    public void reloadVirtualInventoryManager() {
        // Guardar datos antes de recargar
        if (virtualInventoryManager != null) {
            virtualInventoryManager.saveAllPlayerData();
        }
        
        // Recargar la integración con ItemsAdder
        itemsAdderIntegration = new ItemsAdderIntegration(this);
        
        // Recargar el gestor
        virtualInventoryManager = new VirtualInventoryManager(this, itemsAdderIntegration);
        
        // Recargar PlaceholderAPI si está disponible
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && placeholderExpansion != null) {
            placeholderExpansion.unregister();
            placeholderExpansion = new VirtualInventoryExpansion(this);
            placeholderExpansion.register();
        }
    }
    
    public VirtualInventoryManager getVirtualInventoryManager() {
        return virtualInventoryManager;
    }
    
    public ItemsAdderIntegration getItemsAdderIntegration() {
        return itemsAdderIntegration;
    }
}
