package de.pitbully.pitbullyplugin.commands.TabCompleters;

import de.pitbully.pitbullyplugin.utils.Locations;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tab completer for warp-related commands.
 * Provides auto-completion for warp names when using /warp and /delwarp commands.
 */
public class WarpTabCompleter implements TabCompleter {
    
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, 
                                    @NotNull Command command, 
                                    @NotNull String alias, 
                                    @NotNull String[] args) {
        
        if (!isWarpCommand(command.getName()) || args.length != 1) {
            return new ArrayList<>();
        }
        
        String input = args[0].toLowerCase();
        return Locations.getWarpHashMap()
                       .keySet()
                       .stream()
                       .filter(warpName -> warpName.toLowerCase().startsWith(input))
                       .collect(Collectors.toList());
    }
    
    /**
     * Checks if the command is a warp-related command that should have tab completion.
     * 
     * @param commandName The name of the command
     * @return true if it's a warp-related command, false otherwise
     */
    private boolean isWarpCommand(String commandName) {
        return commandName.equalsIgnoreCase("warp") || commandName.equalsIgnoreCase("delwarp");
    }
}
