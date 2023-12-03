package de.pitbully.pitbullyplugin.commands.TabCompleters;

import de.pitbully.pitbullyplugin.utils.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarpTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("warp") && args.length == 1) {
            for (String warpName : Locations.getWarpHashMap().keySet()) {
                if(warpName.toLowerCase().startsWith(args[0].toLowerCase())){
                    completions.add(warpName);
                }
            }
        }
        return completions;
    }
}
