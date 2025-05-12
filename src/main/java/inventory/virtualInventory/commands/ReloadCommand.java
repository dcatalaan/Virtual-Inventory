package inventory.virtualInventory.commands;

import inventory.virtualInventory.VirtualInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final VirtualInventory plugin;
    private final List<String> subCommands = Arrays.asList("reload", "help", "clear");
    private final ClearCommand clearCommand;
    
    public ReloadCommand(VirtualInventory plugin) {
        this.plugin = plugin;
        this.clearCommand = new ClearCommand(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        if (subCommand.equals("reload")) {
            // Verificar permiso
            if (!sender.hasPermission("virtualinventory.reload")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para ejecutar este comando.");
                sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
                return true;
            }
            
            // Recargar la configuración
            plugin.reloadConfig();
            
            // Recargar el gestor de inventario virtual
            plugin.reloadVirtualInventoryManager();
            
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&a¡La configuración de &6Inventario Virtual &ase ha recargado correctamente!"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&aThe &6Virtual Inventory &aconfiguration has been reloaded successfully!"));
        } 
        else if (subCommand.equals("clear")) {
            return clearCommand.execute(sender, args);
        }
        else if (subCommand.equals("help")) {
            sendHelp(sender);
        } 
        else {
            sender.sendMessage(ChatColor.RED + "Subcomando desconocido. Usa /mvs help para ver los comandos disponibles.");
            sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /mvs help to see available commands.");
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        // Español
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6=== &eComandos del Sistema de Medallas Virtuales &6==="));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mvs reload &7- &fRecarga la configuración del plugin"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mvs clear <jugador> &7- &fElimina todos los ítems del inventario virtual de un jugador"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mvs help &7- &fMuestra esta ayuda"));
        
        // Inglés
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6=== &eVirtual Badge System Commands &6==="));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/vms reload &7- &fReloads the plugin configuration"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/vms clear <player> &7- &fRemoves all items from a player's virtual inventory"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/vms help &7- &fShows this help"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(sc -> sc.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            String partialName = args[1].toLowerCase();
            List<String> playerNames = plugin.getServer().getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
            
            // También añadir jugadores offline que tengan datos guardados
            playerNames.addAll(plugin.getVirtualInventoryManager().getOfflinePlayerNames(partialName));
            
            return playerNames;
        }
        
        return new ArrayList<>();
    }
} 