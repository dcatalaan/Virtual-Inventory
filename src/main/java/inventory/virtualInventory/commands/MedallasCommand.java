package inventory.virtualInventory.commands;

import inventory.virtualInventory.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MedallasCommand implements CommandExecutor, TabCompleter {

    private final VirtualInventory plugin;
    
    public MedallasCommand(VirtualInventory plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Si no hay argumentos, abrir el propio inventario del jugador
        if (args.length == 0) {
            plugin.getVirtualInventoryManager().openVirtualInventory(player);
            return true;
        }
        
        // Si hay un argumento, intentar abrir el inventario de otro jugador
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            // Intentar cargar datos del jugador offline
            if (plugin.getVirtualInventoryManager().openOfflinePlayerInventory(player, targetName)) {
                return true;
            }
            
            player.sendMessage(ChatColor.RED + "No se encontró al jugador " + targetName);
            return true;
        }
        
        // Abrir inventario del jugador objetivo
        plugin.getVirtualInventoryManager().openVirtualInventory(player, targetPlayer);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
            
            // También añadir jugadores offline que tengan datos guardados
            playerNames.addAll(plugin.getVirtualInventoryManager().getOfflinePlayerNames(partialName));
            
            return playerNames;
        }
        
        return new ArrayList<>();
    }
} 