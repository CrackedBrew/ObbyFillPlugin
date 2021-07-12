package io.github.crackedbrew.obbyfillplugin.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ObbyFillCommand implements CommandExecutor {

    String serverTag;
    Economy econ;
    ItemStack OBSIDIAN_ITEM_STACK = new ItemStack(Material.OBSIDIAN, 64);
    double pricePerStack;
    FileConfiguration config;

    public ObbyFillCommand (Economy econ, FileConfiguration config) {

        this.econ = econ;
        this.config = config;
        this.serverTag = config.getString("server_tag");
        this.pricePerStack = config.getDouble("obsidian_stack_price");

    }//constructor

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        double requiredMoneyAmount = pricePerStack;
        double moneySpent = 0;
        boolean hasEmptySpace = false;

        if (commandSender instanceof Player) {

            Player player = (Player) commandSender;
            Inventory inventory = player.getInventory();
            ItemStack[] playersInventoryItems = player.getInventory().getContents();

            for (ItemStack item :
                    playersInventoryItems) {

                //if the item is nothing, then add obsidian to the slot
                if (econ.getBalance(player) > requiredMoneyAmount && item == null){

                    inventory.addItem(OBSIDIAN_ITEM_STACK);

                    //add a price per stack, so the loop can check each time if the player can afford the next interation
                    requiredMoneyAmount += pricePerStack;

                    moneySpent = requiredMoneyAmount;

                }//if
                else if (item == null) {

                    hasEmptySpace = true;

                }//else if it's null, then the player does have empty space, but not enough money

            }//for

            //if a player has enough money for at least 1 stack of obsidian, then tell them how much they've spent, else tell
            //them they don't have enough money
            if (moneySpent > 0) {

                //do this because when the foreach statement runs, the if statement goes 1 more time to make sure
                //the player doesn't get an extra stack of obsidian, so you have to minus 1 iteration of the if loop
                moneySpent -= pricePerStack;

                econ.withdrawPlayer(player, moneySpent);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', serverTag + " " + econ.format(moneySpent) +  " was spent on obsidian"));

            }//if
            else if (!hasEmptySpace){

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',serverTag + " You don't have any free space in your inventory"));

            }//else if
            else {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',serverTag + " You don't have enough money to use this command"));

            }//else if the player doesn't have money

        }//if
        else{

                System.out.println("This command can only be executed by a player");

        }//else

        return true;
    }
}
