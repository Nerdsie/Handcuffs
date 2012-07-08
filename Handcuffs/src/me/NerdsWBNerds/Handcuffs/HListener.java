package me.NerdsWBNerds.Handcuffs;

import static org.bukkit.ChatColor.*;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class HListener implements Listener{
	public Handcuffs plugin;
	public HListener(Handcuffs p){
		plugin = p;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player player = e.getPlayer();
		
		if(!cuffed(player))
			return;
		
		e.setCancelled(true);
		tell(player, GREEN + "You cannot do this while cuffed.");
	}

	@EventHandler
	public void onRun(PlayerMoveEvent e){
		Player player = e.getPlayer();
		
		if(!cuffed(player))
			return;
		
		if(player.isSprinting()){
			tell(player, GREEN + "You cannot do this while cuffed.");
			player.setSprinting(false);
		}
	}

	@EventHandler
	public void onHurt(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			
			if(e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.LAVA && plugin.burnCuffs){
				if(new Random().nextInt(20) == 0){
					if(cuffed(player)){
						tell(player, GREEN + "Fire has burnt through your cuffs, you are free.");
						free(player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			Player player = (Player) e.getDamager();
			
			if(cuffed(player) && plugin.nerfDamage){
				e.setDamage(e.getDamage()/2);
			}
		}
	}
	
	@EventHandler
	public void onPickUp(PlayerPickupItemEvent e){
		Player player = e.getPlayer();
		
		if(cuffed(player) && !plugin.canPickup)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onChangeInv(InventoryOpenEvent e){
		Player player = (Player) e.getPlayer();
		
		if(cuffed(player) && !plugin.canChangeInv)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEntityEvent e){
		Player player = e.getPlayer();
		
		if(cuffed(player))
			return;
		
		if(e.getRightClicked() instanceof Player){
			Player target = (Player) e.getRightClicked();
			
			if(!player.isOp() && plugin.reqOP)
				return;
			
			if(inHand(player, Material.getMaterial(plugin.cuffID))){
				if(cuffed(target)){
					tell(player, RED + "This player is already cuffed.");
				}else{
					if(plugin.usePerms && !player.hasPermission("hc.cuff")){
						tell(player, RED + "You can't have permission to do this.");
						return;
					}
					
					if(target.hasPermission("hc.immune")){
						tell(player, ChatColor.RED + "You cannot do that to this player.");
					}
					
					if(inHandAmount(player) >= plugin.cuffAmount){
						tell(target, AQUA + player.getName() + GREEN + " has cuffed you.");
						tell(player, GREEN + "You have cuffed " + AQUA + target.getName());
						cuff(target);
						
						if(plugin.cuffTake){
							if(player.getItemInHand().getAmount()==plugin.cuffAmount)
								player.getItemInHand().setType(Material.AIR);
							else
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - plugin.cuffAmount);
						}
					}else{
						tell(player, RED + "You must have 7 string in-hand to handcuff someone.");
					}
				}
			}
			
			if(inHand(player, Material.getMaterial(plugin.keyID))){
				if(!cuffed(target)){
					tell(player, RED + "This player is already free.");
					
					if(plugin.keyTake){
						if(player.getItemInHand().getAmount()==1)
							player.getItemInHand().setType(Material.AIR);
						else
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
					}
				}else{
					if(plugin.usePerms && !player.hasPermission("hc.free")){
						tell(player, RED + "You can't have permission to do this.");
						return;
					}
					
					tell(target, AQUA + player.getName() + GREEN + " has set you free you.");
					tell(player, GREEN + "You have set " + AQUA + target.getName() + GREEN + " free.");
					free(target);
				}
			}
		}
	}
	
	public boolean cuffed(Player player){
		if(plugin.cuffed.contains(player))
			return true;
		
		return false;
	}
	
	public void cuff(Player player){
		if(!cuffed(player)){
			plugin.cuffed.add(player);
		}
	}
	
	public void free(Player player){
		if(cuffed(player)){
			plugin.cuffed.remove(player);
		}
	}
	
	public boolean inHand(Player player, Material m){
		if(player.getItemInHand().getType() == m)
			return true;
		
		return false;
	}
	
	public int inHandAmount(Player p){
		return p.getItemInHand().getAmount();
	}
	
	public void tell(Player p, String m){
		p.sendMessage(GOLD + "[Handcuffs] " + m);
	}
}
