package de.pitbully.pitbullyplugin.commands.TabCompleters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tab completer for the /back command.
 * Provides auto-completion for back command types: death and teleport.
 * 
 * <p>This tab completer enhances user experience by:
 * <ul>
 * <li>Suggesting "death" and "teleport" options when using /back</li>
 * <li>Filtering suggestions based on current input</li>
 * <li>Respecting player permissions before showing suggestions</li>
 * <li>Providing sorted suggestions</li>
 * </ul>
 * 
 * @author Pitbully01
 * @since 1.5.3
 */
public class BackTabCompleter implements TabCompleter {
    
    private static final List<String> BACK_OPTIONS = Arrays.asList("death", "teleport");
    
    /**
     * Provides tab completion for the /back command.
     * 
     * <p>This method returns a list of back options ("death", "teleport") that match
     * the player's current input, but only if the player has permission to use them.
     * 
     * @param commandSender The command sender requesting tab completion
     * @param command The command being tab-completed
     * @param alias The alias of the command which was used
     * @param args The arguments passed to the command
     * @return A list of matching back options, or empty list if no matches or no permission
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, 
                                    @NotNull Command command, 
                                    @NotNull String alias, 
                                    @NotNull String[] args) {
        
        // Only provide completions for the first argument
        if (args.length != 1) {
            return new ArrayList<>();
        }
        
        // Check basic permission for /back command
        if (!commandSender.hasPermission("pitbullyplugin.back")) {
            return new ArrayList<>();
        }
        
        String input = args[0].toLowerCase();
        List<String> validOptions = new ArrayList<>();
        
        // Check permissions for each option and add to valid options if allowed
        for (String option : BACK_OPTIONS) {
            String permissionNode = "pitbullyplugin.back." + option;
            
            // If player has permission for this specific back option, include it
            if (commandSender.hasPermission(permissionNode)) {
                validOptions.add(option);
            }
        }
        
        // Filter options based on input and return sorted list
        return validOptions.stream()
                          .filter(option -> option.startsWith(input))
                          .sorted()
                          .collect(Collectors.toList());
    }
}
