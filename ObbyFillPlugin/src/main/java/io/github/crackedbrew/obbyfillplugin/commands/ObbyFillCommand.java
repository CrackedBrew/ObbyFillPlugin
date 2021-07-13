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


    String serverTag, transactionSuccessMessage, notEnoughSpaceMessage, notEnoughMoneyMessage;
    Economy econ;
    ItemStack OBSIDIAN_ITEM_STACK = new ItemStack(Material.OBSIDIAN, 64);
    double pricePerStack;
    FileConfiguration config;

    public ObbyFillCommand (Economy econ, FileConfiguration config) {

        //initializing variables from config and such
        this.econ = econ;
        this.config = config;
        this.serverTag = config.getString("server_tag");
        this.pricePerStack = config.getDouble("obsidian_stack_price");
        this.transactionSuccessMessage = config.getString("transaction_success_message");
        this.notEnoughSpaceMessage = config.getString("not_enough_space_message");
        this.notEnoughMoneyMessage = config.getString("not_enough_money_message");


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
                if (econ.getBalance(player) >= requiredMoneyAmount && item == null){

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

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', formatConfigText(config.getString("transaction_success_message"), moneySpent)));

            }//if
            else if (!hasEmptySpace){

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',formatConfigText(config.getString("not_enough_space_message"), moneySpent)));

            }//else if
            else {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', formatConfigText(config.getString("not_enough_money_message"), moneySpent)));

            }//else if the player doesn't have money

        }//if
        else{

                System.out.println("This command can only be executed by a player");

        }//else

        return true;
    }//onCommand

    //Methods
    //formatting the config strings so people can use pre-defined variables in the config messages
    public String formatConfigText(String stringParameter, double moneySpent) {

        //if the string parameter contains $$$ then replace the sequence of those symbols to their proper value form
        //which in this case is going to be moneySpent, in a formated way with econ
        if (stringParameter.contains("$$$")) {

            //$$$ meaning how much the player spent on obsidian
            stringParameter = stringParameter.replace("$$$", econ.format(moneySpent));

        }//if
        if (stringParameter.contains("$ps")) {

            //$ps meaning price per stack
            stringParameter = stringParameter.replace("$ps", econ.format(pricePerStack));

        }//if
        if (stringParameter.contains("serverTag")) {

            stringParameter = stringParameter.replace("serverTag", serverTag);

        }//if

        return stringParameter;

    }//formatConfigText Method




}//class
