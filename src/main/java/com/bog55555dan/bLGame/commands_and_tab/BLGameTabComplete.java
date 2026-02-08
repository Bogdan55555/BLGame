package com.bog55555dan.bLGame.commands_and_tab;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BLGameTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
//        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
//        List<String> playerNames = onlinePlayers.stream()
//                .map(Player::getName)
//                .collect(Collectors.toList());

        if (args.length == 1)
            completions.addAll(Arrays.asList("reload", "purchase"));
        else if (args.length == 2 && args[0].equalsIgnoreCase("purchase"))
            completions.addAll(Arrays.asList("start", "stop"));

        String lastArg = args[args.length - 1].toLowerCase();
        completions.removeIf(completion -> !completion.toLowerCase().startsWith(lastArg));

        return completions;
    }
}
