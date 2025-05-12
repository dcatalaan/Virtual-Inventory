package inventory.virtualInventory.managers;

import inventory.virtualInventory.VirtualInventory;
import inventory.virtualInventory.integrations.ItemsAdderIntegration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class VirtualInventoryManager {

    private final VirtualInventory plugin;
    private final ItemsAdderIntegration itemsAdderIntegration;
    private final List<String> trackingItems;
    private final Map<UUID, Map<String, Integer>> playerItems;
    private final String inventoryName;
    private final int inventorySize;
    private final boolean useItemsAdderGui;
    private final String backgroundItemId;
    private final String emptySlotItemId;
    
    public VirtualInventoryManager(VirtualInventory plugin, ItemsAdderIntegration itemsAdderIntegration) {
        this.plugin = plugin;
        this.itemsAdderIntegration = itemsAdderIntegration;
        this.playerItems = new HashMap<>();
        
        // Cargar configuración
        FileConfiguration config = plugin.getConfig();
        this.inventoryName = ChatColor.translateAlternateColorCodes('&', 
                config.getString("inventario.nombre", "Medallas Virtuales"));
        this.inventorySize = config.getInt("inventario.tamaño", 27);
        
        // Configuración de ItemsAdder
        this.useItemsAdderGui = itemsAdderIntegration.isEnabled() && 
                config.getBoolean("inventario.itemsadder.habilitado", false);
        this.backgroundItemId = config.getString("inventario.itemsadder.fondo_menu", "custom_gui_background");
        this.emptySlotItemId = config.getString("inventario.itemsadder.slot_vacio", "custom_gui_empty_slot");
        
        // Cargar lista de ítems a rastrear
        this.trackingItems = config.getStringList("items");
        
        // Cargar datos de jugadores
        loadAllPlayerData();
    }
    
    /**
     * Verifica si un ítem debe ser rastreado para el inventario virtual
     */
    public boolean isTrackingItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        
        // Comprobar si es un ítem de ItemsAdder
        if (itemsAdderIntegration.isEnabled()) {
            String customItemId = itemsAdderIntegration.getCustomItemId(item);
            if (customItemId != null && trackingItems.contains(customItemId)) {
                return true;
            }
        }
        
        // Comprobar ítems de vanilla Minecraft por nombre
        String itemName = item.getType().toString();
        if (trackingItems.contains(itemName)) {
            return true;
        }
        
        try {
            // Obtener el namespace y key del ítem
            String namespace = item.getType().getKey().getNamespace();
            String key = item.getType().getKey().getKey();
            
            // Identificadores de ítem (primero comprobar la forma directa)
            String fullKey = namespace + ":" + key;
            if (trackingItems.contains(fullKey)) {
                return true;
            }
            
            // Identificador de ítem específico para badges de Cobblemon
            if ("cobblemon".equals(namespace) && key.endsWith("_badge")) {
                String cobblemonProgressKey = "cobblemon-progress-items:" + key;
                if (trackingItems.contains(cobblemonProgressKey)) {
                    if (plugin.getConfig().getBoolean("debug", false)) {
                        plugin.getLogger().info("Detectado badge de Cobblemon: " + key);
                    }
                    return true;
                }
            }
            
            // Para detectar directamente los ítems de cobblemon-progress-items
            if (namespace.equals("cobblemon-progress-items") || namespace.contains("cobblemon")) {
                String testKey = "cobblemon-progress-items:" + key;
                if (trackingItems.contains(testKey)) {
                    if (plugin.getConfig().getBoolean("debug", false)) {
                        plugin.getLogger().info("Detectado ítem de cobblemon-progress-items: " + key);
                    }
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().warning("Error al comprobar el ítem: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Compara si un ítem coincide con un identificador de configuración,
     * teniendo en cuenta posibles guiones en el namespace
     */
    private boolean isItemMatch(ItemStack item, String configItem) {
        try {
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().info("Comparando item: " + item.getType() + " con configItem: " + configItem);
            }
            
            String[] configParts = configItem.split(":");
            if (configParts.length != 2) {
                if (plugin.getConfig().getBoolean("debug")) {
                    plugin.getLogger().warning("Formato incorrecto en config: " + configItem);
                }
                return false;
            }
            
            String configNamespace = configParts[0];
            String configKey = configParts[1];

            // Obtener namespace y key del ItemStack actual
            String itemNamespace = item.getType().getKey().getNamespace();
            String itemKey = item.getType().getKey().getKey();
            
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().info("Config namespace: " + configNamespace + ", Config key: " + configKey);
                plugin.getLogger().info("Item namespace: " + itemNamespace + ", Item key: " + itemKey);
            }

            // Comparar keys
            boolean keyMatch = itemKey.equalsIgnoreCase(configKey);
            
            // Estrategias para comparar namespaces
            boolean namespaceMatch = false;
            
            // 1. Comparación directa
            if (itemNamespace.equalsIgnoreCase(configNamespace)) {
                namespaceMatch = true;
                if (plugin.getConfig().getBoolean("debug")) {
                    plugin.getLogger().info("Namespaces coinciden directamente");
                }
            } 
            // 2. Comparación normalizada (quitar guiones, guiones bajos y convertir a minúsculas)
            else {
                String normalizedItemNamespace = itemNamespace.replace("-", "").replace("_", "").toLowerCase();
                String normalizedConfigNamespace = configNamespace.replace("-", "").replace("_", "").toLowerCase();
                
                if (normalizedItemNamespace.equals(normalizedConfigNamespace)) {
                    namespaceMatch = true;
                    if (plugin.getConfig().getBoolean("debug")) {
                        plugin.getLogger().info("Namespaces coinciden después de normalizar");
                    }
                }
                
                // 3. Verificar si es un subnamespace (para casos como cobblemon vs cobblemon-progress-items)
                if (!namespaceMatch && (
                    (itemNamespace.startsWith(configNamespace + "-") || configNamespace.startsWith(itemNamespace + "-")) ||
                    (normalizedItemNamespace.contains(normalizedConfigNamespace) || normalizedConfigNamespace.contains(normalizedItemNamespace))
                )) {
                    namespaceMatch = true;
                    if (plugin.getConfig().getBoolean("debug")) {
                        plugin.getLogger().info("Detectado como subnamespace relacionado");
                    }
                }
            }
            
            // Si solo coincide el key pero no el namespace, podría necesitar configuración manual
            if (keyMatch && !namespaceMatch) {
                if (plugin.getConfig().getBoolean("debug")) {
                    plugin.getLogger().warning("Key coincide pero namespace no: item=" + 
                                           itemNamespace + ":" + itemKey + ", config=" + 
                                           configNamespace + ":" + configKey);
                }
            }
            
            boolean match = keyMatch && namespaceMatch;
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().info("Resultado de comparación: " + match);
            }
            
            return match;
        } catch (Exception e) {
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().severe("Error al comparar items: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }
    
    /**
     * Añade un ítem al inventario virtual de un jugador
     */
    public boolean addItemToVirtualInventory(Player player, ItemStack item) {
        if (!isTrackingItem(item)) {
            return false;
        }
        
        UUID playerUUID = player.getUniqueId();
        
        // Obtener o crear el mapa de ítems del jugador
        Map<String, Integer> items = playerItems.computeIfAbsent(playerUUID, k -> new HashMap<>());
        
        String itemKey = determineItemKey(item);
        
        // Incrementar la cantidad (hasta un máximo de 64)
        int currentAmount = items.getOrDefault(itemKey, 0);
        int newAmount = Math.min(currentAmount + item.getAmount(), 64);
        items.put(itemKey, newAmount);
        
        // Guardar datos
        savePlayerData(playerUUID);
        
        // Enviar mensaje de confirmación
        String message = plugin.getConfig().getString("mensajes.item_añadido", 
                "¡Has conseguido un nuevo ítem para tu colección de medallas!");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        
        return true;
    }
    
    /**
     * Determina la clave para almacenar el ítem en el inventario virtual
     */
    private String determineItemKey(ItemStack item) {
        // Comprobar si es un ítem de ItemsAdder
        if (itemsAdderIntegration.isEnabled()) {
            String customItemId = itemsAdderIntegration.getCustomItemId(item);
            if (customItemId != null) {
                return customItemId;
            }
        }
        
        try {
            String namespace = item.getType().getKey().getNamespace();
            String key = item.getType().getKey().getKey();
            
            if (namespace.equals("minecraft")) {
                return item.getType().toString();
            }
            
            // ID original del ítem
            String fullKey = namespace + ":" + key;
            
            // Caso especial para las medallas de Cobblemon
            if ("cobblemon".equals(namespace) && key.endsWith("_badge")) {
                String cobblemonProgressKey = "cobblemon-progress-items:" + key;
                if (trackingItems.contains(cobblemonProgressKey)) {
                    return cobblemonProgressKey;
                }
            }
            
            // Si es un ítem de cobblemon-progress-items o está relacionado
            if (namespace.contains("cobblemon") && key.endsWith("_badge")) {
                String testKey = "cobblemon-progress-items:" + key;
                if (trackingItems.contains(testKey)) {
                    return testKey;
                }
            }
            
            // Si hay una configuración exacta para este ítem
            if (trackingItems.contains(fullKey)) {
                return fullKey;
            }
            
            return fullKey;
        } catch (Exception e) {
            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().warning("Error al determinar clave del ítem: " + e.getMessage());
            }
            // Fallback a toString para cualquier error
            return item.getType().toString();
        }
    }
    
    /**
     * Abre el inventario virtual para un jugador
     */
    public void openVirtualInventory(Player player) {
        openVirtualInventory(player, player);
    }
    
    /**
     * Abre el inventario virtual de un jugador para otro jugador
     * @param viewer El jugador que verá el inventario
     * @param owner El dueño del inventario
     */
    public void openVirtualInventory(Player viewer, Player owner) {
        UUID ownerUUID = owner.getUniqueId();
        Map<String, Integer> items = playerItems.getOrDefault(ownerUUID, new HashMap<>());
        
        // Crear el inventario con un título personalizado si no es el propio
        String title = inventoryName;
        if (!viewer.equals(owner)) {
            title = ChatColor.translateAlternateColorCodes('&', 
                    inventoryName + " de " + owner.getName());
        }
        
        Inventory inventory = Bukkit.createInventory(null, inventorySize, title);
        
        // Aplicar fondo personalizado de ItemsAdder si está habilitado
        if (useItemsAdderGui) {
            applyCustomBackground(inventory);
        }
        
        // Añadir cabeza del jugador en la posición central superior
        if (!viewer.equals(owner)) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(owner);
            meta.setDisplayName(ChatColor.GOLD + owner.getName());
            head.setItemMeta(meta);
            
            // Colocar la cabeza en diferentes posiciones según el tamaño del inventario
            int headSlot = (inventorySize == 9) ? 0 : 4;
            inventory.setItem(headSlot, head);
        }
        
        // Añadir ítems al inventario
        int slot = 9; // Comenzar después de la fila superior
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            if (slot >= inventorySize) break;
            
            String itemKey = entry.getKey();
            int amount = entry.getValue();
            
            ItemStack displayItem = createDisplayItem(itemKey, amount);
            if (displayItem != null) {
                inventory.setItem(slot, displayItem);
                slot++;
            }
        }
        
        // Abrir el inventario
        viewer.openInventory(inventory);
        
        // Enviar mensaje
        if (viewer.equals(owner)) {
            String message = plugin.getConfig().getString("mensajes.inventario_abierto", 
                    "Abriendo tu colección de medallas virtuales...");
            viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&6Viendo la colección de medallas de &e" + owner.getName() + "&6..."));
        }
    }
    
    /**
     * Crea un ítem para mostrar en el inventario virtual
     */
    private ItemStack createDisplayItem(String itemKey, int amount) {
        // Comprobar si es un ítem de ItemsAdder
        if (itemKey.startsWith("itemsadder:")) {
            ItemStack customItem = itemsAdderIntegration.getCustomItem(itemKey.substring(11), amount);
            if (customItem != null) {
                return customItem;
            }
        }
        
        try {
            if (itemKey.contains(":")) {
                // Ítem de mod
                String[] parts = itemKey.split(":");
                
                // Intenta obtener el ítem personalizado de ItemsAdder si el prefijo es "itemsadder"
                if (parts[0].equalsIgnoreCase("itemsadder") && itemsAdderIntegration.isEnabled()) {
                    ItemStack customItem = itemsAdderIntegration.getCustomItem(parts[1], amount);
                    if (customItem != null) {
                        return customItem;
                    }
                }
                
                // Para ítems de Cobblemon Progress Items, intentar obtener el material directamente
                String namespace = parts[0];
                String key = parts[1];
                
                if (namespace.equals("cobblemon-progress-items")) {
                    // Intentar obtener el ítem directamente desde el mod
                    Material cobblemonMaterial = Material.matchMaterial("cobblemon-progress-items:" + key);
                    
                    // Si no funciona, intentar con diferentes formatos de namespace
                    if (cobblemonMaterial == null) {
                        cobblemonMaterial = Material.matchMaterial("cobblemon:" + key);
                    }
                    if (cobblemonMaterial == null) {
                        cobblemonMaterial = Material.matchMaterial("cobblemon_progress_items:" + key);
                    }
                    if (cobblemonMaterial == null) {
                        cobblemonMaterial = Material.matchMaterial("cobblemonprogressitems:" + key);
                    }
                    
                    // Si se encontró el material, crear el ítem
                    if (cobblemonMaterial != null) {
                        return new ItemStack(cobblemonMaterial, amount);
                    }
                    
                    // Si no funciona, intentar obtener a través de Bukkit
                    ItemStack tempItem = null;
                    try {
                        // Usar método alternativo para obtener el ítem del mod
                        String bukkitMethod = "org.bukkit.Bukkit";
                        Class<?> bukkitClass = Class.forName(bukkitMethod);
                        Object itemFactory = bukkitClass.getMethod("getItemFactory").invoke(null);
                        Class<?> materialClass = Material.class;
                        Object itemStack = itemFactory.getClass().getMethod("createItemStack", String.class)
                                .invoke(itemFactory, "cobblemon-progress-items:" + key);
                        if (itemStack instanceof ItemStack) {
                            tempItem = (ItemStack) itemStack;
                            tempItem.setAmount(amount);
                        }
                    } catch (Exception e) {
                        // Ignorar el error silenciosamente
                    }
                    
                    if (tempItem != null && tempItem.getType() != Material.AIR) {
                        return tempItem;
                    }
                }
                
                // Intento genérico para material de mod
                Material material = Material.matchMaterial(parts[0] + ":" + parts[1]);
                if (material != null) {
                    return new ItemStack(material, amount);
                } else {
                    // Crear un ítem genérico con el nombre correcto
                    ItemStack fallbackItem = new ItemStack(Material.PAPER, amount);
                    ItemMeta meta = fallbackItem.getItemMeta();
                    
                    // Establecer un nombre legible para las medallas
                    String displayName = parts[1];
                    if (parts[1].endsWith("_badge")) {
                        displayName = "Medalla " + 
                            parts[1].replace("_badge", "")
                                   .substring(0, 1).toUpperCase() + 
                            parts[1].replace("_badge", "")
                                   .substring(1);
                    }
                    
                    meta.setDisplayName(ChatColor.YELLOW + displayName);
                    
                    // Agregar lore para identificar el origen
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "ID: " + itemKey);
                    meta.setLore(lore);
                    
                    // Hacer que el ítem brille para diferenciarlo
                    try {
                        // Usar cualquier encantamiento disponible
                        Enchantment glow = Enchantment.values()[0]; // Usar el primer encantamiento disponible
                        meta.addEnchant(glow, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    } catch (Exception ex) {
                        // Ignorar si no se puede añadir el brillo
                    }
                    
                    fallbackItem.setItemMeta(meta);
                    return fallbackItem;
                }
            } else {
                // Ítem de vanilla
                Material material = Material.valueOf(itemKey);
                return new ItemStack(material, amount);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error al crear el ítem de visualización: " + itemKey);
            return null;
        }
    }
    
    /**
     * Aplica un fondo personalizado al inventario usando ítems de ItemsAdder
     */
    private void applyCustomBackground(Inventory inventory) {
        if (!itemsAdderIntegration.isEnabled() || !useItemsAdderGui) {
            return;
        }
        
        ItemStack backgroundItem = itemsAdderIntegration.getCustomMenuBackground(backgroundItemId);
        if (backgroundItem != null) {
            // Aplicar el fondo a todos los slots
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, backgroundItem.clone());
            }
        }
        
        // Aplicar slots vacíos (opcional)
        ItemStack emptySlotItem = itemsAdderIntegration.getCustomItem(emptySlotItemId, 1);
        if (emptySlotItem != null) {
            // Aplicar a los slots donde irían los ítems (excepto la primera fila)
            for (int i = 9; i < inventory.getSize(); i++) {
                inventory.setItem(i, emptySlotItem.clone());
            }
        }
    }
    
    /**
     * Abre el inventario de un jugador offline
     */
    public boolean openOfflinePlayerInventory(Player viewer, String offlinePlayerName) {
        // Buscar al jugador offline por nombre
        File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            return false;
        }
        
        // Primero buscar por UUID
        OfflinePlayer offlinePlayer = null;
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName() != null && op.getName().equalsIgnoreCase(offlinePlayerName)) {
                offlinePlayer = op;
                break;
            }
        }
        
        if (offlinePlayer == null) {
            return false;
        }
        
        // Cargar datos del jugador offline
        UUID offlinePlayerUUID = offlinePlayer.getUniqueId();
        File playerFile = new File(dataFolder, offlinePlayerUUID.toString() + ".yml");
        
        if (!playerFile.exists()) {
            return false;
        }
        
        // Cargar los datos
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        Map<String, Integer> items = new HashMap<>();
        
        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                items.put(key, config.getInt("items." + key));
            }
        }
        
        // Crear el inventario con título personalizado
        String title = ChatColor.translateAlternateColorCodes('&', 
                inventoryName + " de " + offlinePlayerName);
        
        Inventory inventory = Bukkit.createInventory(null, inventorySize, title);
        
        // Aplicar fondo personalizado de ItemsAdder si está habilitado
        if (useItemsAdderGui) {
            applyCustomBackground(inventory);
        }
        
        // Añadir cabeza del jugador en la posición central superior
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(offlinePlayer);
        meta.setDisplayName(ChatColor.GOLD + offlinePlayerName);
        head.setItemMeta(meta);
        
        // Colocar la cabeza en diferentes posiciones según el tamaño del inventario
        int headSlot = (inventorySize == 9) ? 0 : 4;
        inventory.setItem(headSlot, head);
        
        // Añadir ítems al inventario
        int slot = 9; // Comenzar después de la fila superior
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            if (slot >= inventorySize) break;
            
            String itemKey = entry.getKey();
            int amount = entry.getValue();
            
            ItemStack displayItem = createDisplayItem(itemKey, amount);
            if (displayItem != null) {
                inventory.setItem(slot, displayItem);
                slot++;
            }
        }
        
        // Abrir el inventario
        viewer.openInventory(inventory);
        viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                "&6Viendo la colección de medallas de &e" + offlinePlayerName + "&6..."));
        return true;
    }
    
    /**
     * Obtiene los nombres de jugadores offline que tienen datos guardados
     */
    public List<String> getOfflinePlayerNames(String partialName) {
        List<String> names = new ArrayList<>();
        File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        
        if (!dataFolder.exists()) {
            return names;
        }
        
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName() != null && 
                    player.getName().toLowerCase().startsWith(partialName.toLowerCase()) && 
                    !Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))) {
                File playerFile = new File(dataFolder, player.getUniqueId().toString() + ".yml");
                if (playerFile.exists()) {
                    names.add(player.getName());
                }
            }
        }
        
        return names;
    }
    
    /**
     * Obtiene la cantidad de un ítem específico que tiene un jugador
     * Utilizado para PlaceholderAPI
     */
    public int getItemAmount(UUID playerUUID, String itemKey) {
        Map<String, Integer> items = playerItems.getOrDefault(playerUUID, new HashMap<>());
        return items.getOrDefault(itemKey, 0);
    }
    
    /**
     * Verifica si un jugador tiene un ítem específico
     * Utilizado para PlaceholderAPI
     */
    public boolean hasItem(UUID playerUUID, String itemKey) {
        return getItemAmount(playerUUID, itemKey) > 0;
    }
    
    /**
     * Guarda los datos de un jugador
     */
    public void savePlayerData(UUID playerUUID) {
        try {
            File dataFolder = new File(plugin.getDataFolder(), "playerdata");
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
            YamlConfiguration config = new YamlConfiguration();
            
            Map<String, Integer> items = playerItems.getOrDefault(playerUUID, new HashMap<>());
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                config.set("items." + entry.getKey(), entry.getValue());
            }
            
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error al guardar datos del jugador: " + playerUUID);
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los datos de un jugador
     */
    public void loadPlayerData(UUID playerUUID) {
        File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        
        if (!playerFile.exists()) {
            return;
        }
        
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            Map<String, Integer> items = new HashMap<>();
            
            if (config.contains("items")) {
                for (String key : config.getConfigurationSection("items").getKeys(false)) {
                    items.put(key, config.getInt("items." + key));
                }
            }
            
            playerItems.put(playerUUID, items);
        } catch (Exception e) {
            plugin.getLogger().severe("Error al cargar datos del jugador: " + playerUUID);
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda los datos de todos los jugadores
     */
    public void saveAllPlayerData() {
        for (UUID playerUUID : playerItems.keySet()) {
            savePlayerData(playerUUID);
        }
    }
    
    /**
     * Carga los datos de todos los jugadores
     */
    private void loadAllPlayerData() {
        File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
            return;
        }
        
        File[] playerFiles = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (playerFiles == null) return;
        
        for (File file : playerFiles) {
            String fileName = file.getName();
            String uuidString = fileName.substring(0, fileName.length() - 4); // Remover .yml
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                loadPlayerData(playerUUID);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Archivo de datos inválido: " + fileName);
            }
        }
    }
    
    /**
     * Obtiene todos los ítems que se están rastreando
     */
    public List<String> getTrackingItems() {
        return trackingItems;
    }
    
    /**
     * Limpia todos los ítems del inventario virtual de un jugador
     * @param playerName Nombre del jugador
     * @return true si la operación fue exitosa
     */
    public boolean clearPlayerItems(String playerName) {
        // Buscar el jugador (online primero)
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        UUID playerUUID = null;
        
        if (onlinePlayer != null) {
            playerUUID = onlinePlayer.getUniqueId();
        } else {
            // Buscar entre jugadores offline
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer.hasPlayedBefore()) {
                playerUUID = offlinePlayer.getUniqueId();
            }
        }
        
        if (playerUUID == null) {
            return false; // No se encontró al jugador
        }
        
        // Limpiar el inventario virtual
        if (playerItems.containsKey(playerUUID)) {
            playerItems.get(playerUUID).clear();
            savePlayerData(playerUUID);
            return true;
        }
        
        return false;
    }
} 