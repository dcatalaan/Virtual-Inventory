package inventory.virtualInventory.integrations;

import inventory.virtualInventory.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Clase utilitaria para la integración con ItemsAdder
 */
public class ItemsAdderIntegration {

    private final VirtualInventory plugin;
    private final boolean isItemsAdderEnabled;
    
    public ItemsAdderIntegration(VirtualInventory plugin) {
        this.plugin = plugin;
        boolean detected = false;
        
        try {
            // Verificar si ItemsAdder está presente
            detected = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
            
            if (detected) {
                // Intentar cargar la clase de ItemsAdder para verificar que está disponible
                Class.forName("dev.lone.itemsadder.api.CustomStack");
                plugin.getLogger().info("¡ItemsAdder detectado! El plugin soportará ítems personalizados.");
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // ItemsAdder está instalado pero no podemos acceder a la API
            detected = false;
            plugin.getLogger().warning("ItemsAdder se detectó, pero no se pudo acceder a su API.");
            plugin.getLogger().warning("Asegúrate de tener la versión correcta de ItemsAdder o coloca el archivo JAR en la carpeta 'libs'.");
        } catch (Exception e) {
            detected = false;
            plugin.getLogger().warning("Error al intentar integrar con ItemsAdder: " + e.getMessage());
        }
        
        this.isItemsAdderEnabled = detected;
        
        if (!detected) {
            plugin.getLogger().info("ItemsAdder no detectado. La integración con ítems personalizados estará deshabilitada.");
        }
    }
    
    /**
     * Verifica si la integración con ItemsAdder está habilitada
     */
    public boolean isEnabled() {
        return isItemsAdderEnabled;
    }
    
    /**
     * Obtiene un ítem personalizado de ItemsAdder por su ID
     */
    public ItemStack getCustomItem(String namespacedId, int amount) {
        if (!isItemsAdderEnabled) {
            return null;
        }
        
        try {
            // Intentar cargar la clase de ItemsAdder usando reflexión
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            
            // Formato esperado: itemsadder:item_id o simplemente item_id
            String itemId = namespacedId;
            if (namespacedId.contains(":")) {
                String[] parts = namespacedId.split(":");
                if (parts.length == 2 && parts[0].equalsIgnoreCase("itemsadder")) {
                    itemId = parts[1];
                }
            }
            
            // Llamar al método getInstance usando reflexión
            Object customStack = customStackClass.getMethod("getInstance", String.class).invoke(null, itemId);
            if (customStack != null) {
                ItemStack item = (ItemStack) customStackClass.getMethod("getItemStack").invoke(customStack);
                item.setAmount(amount);
                return item;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error al obtener el ítem personalizado de ItemsAdder: " + namespacedId);
            if (plugin.getConfig().getBoolean("debug", false)) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * Verifica si un ítem es un ítem personalizado de ItemsAdder
     */
    public boolean isCustomItem(ItemStack item) {
        if (!isItemsAdderEnabled || item == null) {
            return false;
        }
        
        try {
            // Intentar cargar la clase de ItemsAdder usando reflexión
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            
            // Llamar al método byItemStack usando reflexión
            Object customStack = customStackClass.getMethod("byItemStack", ItemStack.class).invoke(null, item);
            return customStack != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene el ID de un ítem personalizado de ItemsAdder
     */
    public String getCustomItemId(ItemStack item) {
        if (!isItemsAdderEnabled || item == null) {
            return null;
        }
        
        try {
            // Intentar cargar la clase de ItemsAdder usando reflexión
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            
            // Llamar al método byItemStack usando reflexión
            Object customStack = customStackClass.getMethod("byItemStack", ItemStack.class).invoke(null, item);
            if (customStack != null) {
                String namespacedId = (String) customStackClass.getMethod("getNamespacedID").invoke(customStack);
                return "itemsadder:" + namespacedId.replace(":", "_");
            }
        } catch (Exception e) {
            // No es un ítem personalizado de ItemsAdder
        }
        
        return null;
    }
    
    /**
     * Actualiza la configuración del menú con texturas personalizadas de ItemsAdder
     * @param menuId El ID de la textura personalizada para el menú en ItemsAdder
     */
    public ItemStack getCustomMenuBackground(String menuId) {
        if (!isItemsAdderEnabled || menuId == null || menuId.isEmpty()) {
            return null;
        }
        
        try {
            return getCustomItem(menuId, 1);
        } catch (Exception e) {
            plugin.getLogger().warning("Error al obtener la textura de menú personalizada: " + menuId);
            return null;
        }
    }
} 