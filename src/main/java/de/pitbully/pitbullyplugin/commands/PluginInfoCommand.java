package de.pitbully.pitbullyplugin.commands;

import de.pitbully.pitbullyplugin.utils.PluginInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /pitbullyinfo command.
 * Displays detailed plugin information including version, build details, and more.
 * This command is useful for debugging and support purposes.
 * 
 * @author Pitbully01
 * @since 1.5.3
 */
public class PluginInfoCommand implements CommandExecutor {

    /**
     * Executes the pitbullyinfo command.
     * 
     * @param sender The command sender
     * @param command The command that was executed
     * @param label The alias of the command which was used
     * @param args The arguments passed to the command
     * @return true if the command was handled successfully
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("pitbullyplugin.info")) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }
        
        // Display plugin information
        sender.sendMessage("§6=== " + PluginInfo.getName() + " Information ===");
        sender.sendMessage("§eVersion: §f" + PluginInfo.getVersion());
        sender.sendMessage("§eDescription: §f" + PluginInfo.getDescription());
        
        String url = PluginInfo.getUrl();
        if (url != null) {
            sender.sendMessage("§eURL: §f" + url);
        }
        
        String groupId = PluginInfo.getGroupId();
        String artifactId = PluginInfo.getArtifactId();
        if (groupId != null && artifactId != null) {
            sender.sendMessage("§eArtifact: §f" + groupId + ":" + artifactId);
        }
        
        String buildTimestamp = PluginInfo.getBuildTimestamp();
        if (buildTimestamp != null) {
            sender.sendMessage("§eBuild Time: §f" + buildTimestamp);
        }
        
        String javaVersion = PluginInfo.getBuildJavaVersion();
        if (javaVersion != null) {
            sender.sendMessage("§eBuild Java: §f" + javaVersion);
        }
        
        sender.sendMessage("§6=== Full Version Info ===");
        sender.sendMessage("§f" + PluginInfo.getFullVersionInfo());
        
        return true;
    }
}
