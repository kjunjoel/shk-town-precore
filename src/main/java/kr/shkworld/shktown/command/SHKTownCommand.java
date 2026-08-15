package kr.shkworld.shktown.command;

import kr.shkworld.shktown.SHKTown;
import kr.shkworld.shktown.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SHKTownCommand implements CommandExecutor, TabCompleter {
    private final SHKTown plugin;

    public SHKTownCommand(SHKTown plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("shktown.admin")) {
                MessageUtil.sendNoPermission(sender);
                return true;
            }

            plugin.reload();
            MessageUtil.sendReloadSuccess(sender);
            return true;
        }

        sender.sendMessage(Component.text(MessageUtil.getPrefix() + "/shktown reload - 설정 파일을 재불러옵니다."));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }
}
