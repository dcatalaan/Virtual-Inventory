package inventory.virtualInventory.commands;

import inventory.virtualInventory.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClearCommand implements Listener {
    
    private final VirtualInventory plugin;
    private final Map<UUID, PendingConfirmation> pendingConfirmations = new HashMap<>();
    
    public ClearCommand(VirtualInventory plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Ejecuta el comando de limpiar el inventario virtual
     * @param sender Quien ejecuta el comando
     * @param args Argumentos (nombre del jugador a limpiar)
     * @return true si se procesó correctamente
     */
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Verificar permiso
        if (!player.hasPermission("virtualinventory.clear")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando.");
            player.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            return true;
        }
        
        // Verificar si se especificó un jugador
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /mvs clear <nombre>");
            player.sendMessage(ChatColor.RED + "Usage: /vms clear <name>");
            return true;
        }
        
        String targetName = args[1];
        
        // Comprobar si el jugador existe
        boolean playerExists = Bukkit.getPlayer(targetName) != null;
        
        if (!playerExists) {
            // Verificar jugadores offline
            boolean offlinePlayerExists = false;
            for (String offlineName : plugin.getVirtualInventoryManager().getOfflinePlayerNames("")) {
                if (offlineName.equalsIgnoreCase(targetName)) {
                    offlinePlayerExists = true;
                    break;
                }
            }
            
            if (!offlinePlayerExists) {
                player.sendMessage(ChatColor.RED + "No se encontró al jugador " + targetName);
                player.sendMessage(ChatColor.RED + "Player " + targetName + " not found");
                return true;
            }
        }
        
        // Guardar la confirmación pendiente
        pendingConfirmations.put(player.getUniqueId(), new PendingConfirmation(targetName, System.currentTimeMillis()));
        
        // Solicitar confirmación en español
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c⚠ &e¡Estás a punto de eliminar todos los ítems de la colección de &6" + targetName + "&e!"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&ePara confirmar, escribe el nombre del jugador en el chat: &6" + targetName));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7La confirmación expirará en 30 segundos. Escribe &ccancelar&7 para anular la operación."));
        
        // Solicitar confirmación en inglés
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c⚠ &eYou are about to delete all items from &6" + targetName + "&e's collection!"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&eTo confirm, type the player's name in chat: &6" + targetName));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7The confirmation will expire in 30 seconds. Type &ccancel&7 to abort."));
        
        return true;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Verificar si el jugador tiene una confirmación pendiente
        if (!pendingConfirmations.containsKey(playerUUID)) {
            return;
        }
        
        event.setCancelled(true); // Cancelar el mensaje en el chat
        
        PendingConfirmation confirmation = pendingConfirmations.get(playerUUID);
        pendingConfirmations.remove(playerUUID); // Eliminar siempre la confirmación
        
        // Verificar si ha expirado (30 segundos)
        if (System.currentTimeMillis() - confirmation.timestamp > 30000) {
            player.sendMessage(ChatColor.RED + "La confirmación ha expirado.");
            player.sendMessage(ChatColor.RED + "The confirmation has expired.");
            return;
        }
        
        String message = event.getMessage();
        
        // Verificar si quiere cancelar
        if (message.equalsIgnoreCase("cancelar") || message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&aLa operación ha sido cancelada."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&aThe operation has been cancelled."));
            return;
        }
        
        // Verificar si el mensaje coincide con el nombre del jugador
        if (!message.equalsIgnoreCase(confirmation.playerName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cEl nombre no coincide. Operación cancelada."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cThe name doesn't match. Operation cancelled."));
            return;
        }
        
        // Ejecutar la limpieza (hacerlo en el hilo principal)
        Bukkit.getScheduler().runTask(plugin, () -> {
            boolean success = plugin.getVirtualInventoryManager().clearPlayerItems(confirmation.playerName);
            
            if (success) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a¡Se han eliminado todos los ítems de la colección de &e" + confirmation.playerName + "&a!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&aAll items have been removed from &e" + confirmation.playerName + "&a's collection!"));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&cHa ocurrido un error al limpiar la colección de &e" + confirmation.playerName + "&c."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&cAn error occurred while clearing &e" + confirmation.playerName + "&c's collection."));
            }
        });
    }
    
    /**
     * Clase interna para almacenar la información de confirmación pendiente
     */
    private static class PendingConfirmation {
        final String playerName;
        final long timestamp;
        
        PendingConfirmation(String playerName, long timestamp) {
            this.playerName = playerName;
            this.timestamp = timestamp;
        }
    }
} 