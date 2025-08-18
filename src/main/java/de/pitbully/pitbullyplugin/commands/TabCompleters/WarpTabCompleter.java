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
 * 
 * <p>This tab completer enhances user experience by:
 * <ul>
 * <li>Suggesting existing warp names as the user types</li>
 * <li>Filtering suggestions based on current input</li>
 * <li>Respecting player permissions before showing suggestions</li>
 * <li>Providing sorted, alphabetical suggestions</li>
 * </ul>
 * 
 * @author Pitbully01
 * @since 1.4.4
 */
public class WarpTabCompleter implements TabCompleter {
    
    /**
     * Provides tab completion for warp-related commands.
     * 
     * <p>This method returns a list of warp names that match the player's
     * current input, but only if the player has permission to use the command.
     * 
     * @param commandSender The command sender requesting tab completion
     * @param command The command being tab-completed
     * @param alias The alias of the command which was used
     * @param args The arguments passed to the command
     * @return A list of matching warp names, or empty list if no matches or no permission
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, 
                                    @NotNull Command command, 
                                    @NotNull String alias, 
                                    @NotNull String[] args) {
        
        // Check if this is a warp-related command and we're completing the first argument
        if (!isWarpCommand(command.getName()) || args.length != 1) {
            return new ArrayList<>();
        }
        
        // Check permissions before providing completions
        if (!commandSender.hasPermission("pitbullyplugin." + command.getName().toLowerCase())) {
            return new ArrayList<>();
        }
        
        String input = args[0].toLowerCase();
        return Locations.getWarpHashMap()
                       .keySet()
                       .stream()
                       .filter(warpName -> warpName.toLowerCase().startsWith(input))
                       .sorted() // Sort alphabetically for better UX
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
