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
    private final List<String> subCommands = Arrays.asList("reload", "help");
    
    public ReloadCommand(VirtualInventory plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        if (subCommand.equals("reload")) {
            // Recargar la configuración
            plugin.reloadConfig();
            
            // Recargar el gestor de inventario virtual
            plugin.reloadVirtualInventoryManager();
            
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    "&a¡La configuración de &6Inventario Virtual &ase ha recargado correctamente!"));
        } 
        else if (subCommand.equals("help")) {
            sendHelp(sender);
        } 
        else {
            sender.sendMessage(ChatColor.RED + "Subcomando desconocido. Usa /mvs help para ver los comandos disponibles.");
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6=== &eComandos del Sistema de Medallas Virtuales &6==="));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mvs reload &7- &fRecarga la configuración del plugin"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/mvs help &7- &fMuestra esta ayuda"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(sc -> sc.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
} 