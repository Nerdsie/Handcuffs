package me.NerdsWBNerds.Handcuffs;

import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Handcuffs extends JavaPlugin implements CommandExecutor{
	public int cuffID = Material.STRING.getId(), keyID = Material.SHEARS.getId();
	public int cuffAmount = 7;
	public boolean cuffTake = true, burnCuffs = true, canPickup = false, nerfDamage = true, canChangeInv = false, reqOP = false, keyTake = false, usePerms = false;
	
	public PermissionManager pex;
	public HListener Listener = new HListener(this);
	public Server server;
	public Logger log;
	
	public ArrayList<Player> cuffed = new ArrayList<Player>();
	
	public void onEnable(){
		server = this.getServer();
		log = this.getLogger();

		server.getPluginManager().registerEvents(Listener, this);
		loadConfig();

		if(usePerms){
			try{
				pex = PermissionsEx.getPermissionManager();
			}catch(Exception e){ usePerms = false; }
		}	
	}
	
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		/*if(cmd.getName().equalsIgnoreCase("hc") && ((sender instanceof Player && ((Player)sender).isOp()) || sender instanceof ConsoleCommandSender)){
			if(args[0].equalsIgnoreCase("reload")){
				this.loadConfig();
				return true;
			}
		}
		
		Maybe a reload command, wan't working earlier.
		
		return false;*/
		
		if(cmd.getName().equalsIgnoreCase("hc") && sender instanceof Player){
			Player player = (Player) sender;

			if(args[0].equalsIgnoreCase("carry")){
				if(((reqOP && player.isOp()) || !reqOP) && (!usePerms || pex.getUser(player).has("hc.cmd.carry"))){
						Player target = server.getPlayer(args[1]);
						
						if(target==null || !target.isOnline()){
							Listener.tell(player, ChatColor.RED + "Player not found.");
							return true;
						}
						
						if(Listener.cuffed(target) && !Listener.cuffed(player) && player.getLocation().distance(target.getLocation()) <= 5){
							player.setPassenger(target);
							
							if(player.getPassenger()==null)
								Listener.tell(player, ChatColor.GREEN + "You have put down " + ChatColor.AQUA + target.getName());
							else
								Listener.tell(player, ChatColor.GREEN + "You have picked up " + ChatColor.AQUA + target.getName());
							
							return true;
						}
				}else{
					Listener.tell(player, RED + "You don't have permission to do this.");
					return true;
				}
			}

			if(args[0].equalsIgnoreCase("cuff")){
				if(((reqOP && player.isOp()) || !reqOP) && (!usePerms || pex.getUser(player).has("hc.cmd.cuff"))){
						Player target = server.getPlayer(args[1]);
						
						if(target==null || !target.isOnline()){
							Listener.tell(player, ChatColor.RED + "Player not found.");
							return true;
						}
						
						if(!Listener.cuffed(target)){
							Listener.cuff(target);
							Listener.tell(player, ChatColor.GREEN + "You have cuffed " + ChatColor.AQUA + target.getName());
							return true;
						}else{
							Listener.tell(player, RED + "This player is already cuffed.");
							return true;
						}
				}else{
					Listener.tell(player, RED + "You don't have permission to do this.");
					return true;
				}
			}

			if(args[0].equalsIgnoreCase("free")){
				if(((reqOP && player.isOp()) || !reqOP) && (!usePerms || pex.getUser(player).has("hc.cmd.free"))){
						Player target = server.getPlayer(args[1]);
						
						if(target==null || !target.isOnline()){
							Listener.tell(player, ChatColor.RED + "Player not found.");
							return true;
						}
						
						if(Listener.cuffed(target)){
							Listener.free(target);
							Listener.tell(player, ChatColor.GREEN + "You have set " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " free.");
							return true;
						}else{
							Listener.tell(player, RED + "This player is already free.");
						}
				}else{
					Listener.tell(player, RED + "You don't have permission to do this.");
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void loadConfig(){
		this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
		
		String path = "cuffID";
		if(this.getConfig().contains(path)){
			
			try{
				cuffID = getConfig().getInt(path);
			}catch(Exception e){
				cuffID = Material.STRING.getId();
			}
		}
		
		path = "cuffAmount";
		if(this.getConfig().contains(path)){
			try{
				cuffAmount = getConfig().getInt(path);
			}catch(Exception e){
				cuffAmount = 7;
			}
		}
		
		path = "cuffTake";
		if(this.getConfig().contains(path)){
			try{
				cuffTake=this.getConfig().getBoolean(path);
			}catch(Exception e){
				cuffTake=true;
			}
		}

		path = "nerfDamage";
		if(this.getConfig().contains(path)){
			try{
				nerfDamage=this.getConfig().getBoolean(path);
			}catch(Exception e){
				nerfDamage=true;
			}
		}
		
		path = "burnCuffs";
		if(this.getConfig().contains(path)){
			try{
				burnCuffs=this.getConfig().getBoolean(path);
			}catch(Exception e){
				burnCuffs=true;
			}
		}
		
		path = "canPickup";
		if(this.getConfig().contains(path)){
			try{
				canPickup=this.getConfig().getBoolean(path);
			}catch(Exception e){
				canPickup=false;
			}
		}
		
		path = "canChangeInv";
		if(this.getConfig().contains(path)){
			try{
				canChangeInv=this.getConfig().getBoolean(path);
			}catch(Exception e){
				canChangeInv=false;
			}
		}
		
		path = "reqOP";
		if(this.getConfig().contains(path)){
			try{
				reqOP=this.getConfig().getBoolean(path);
			}catch(Exception e){
				reqOP=false;
			}
		}
		
		path = "keyID";
		if(this.getConfig().contains(path)){
			try{
				keyID=this.getConfig().getInt(path);
			}catch(Exception e){
				keyID=Material.SHEARS.getId();
			}
		}
		
		path = "keyTake";
		if(this.getConfig().contains(path)){
			try{
				keyTake=this.getConfig().getBoolean(path);
			}catch(Exception e){
				keyTake=false;
			}
		}
		
		path = "usePerms";
		if(this.getConfig().contains(path)){
			try{
				usePerms=this.getConfig().getBoolean(path);
			}catch(Exception e){
				usePerms=false;
			}
		}
		
		this.saveConfig();
	}
}
