package me.djbiokinetix.lobby;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
//import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;

//import es.minetsii.eggwars.API.EggWarsAPI;

public class Main extends JavaPlugin implements CommandExecutor/*, Listener*/ {
	
	public Main instancia;
	public static int TID;
	public static int TID_TEMPORIZADOR;
	public static int TID_PRINCIPAL;
	public static BukkitRunnable TID_COOLDOWN;
	public HashMap<Player, Integer> cooldownTime;
	public HashMap<Player, BukkitRunnable> cooldownTask;
	public HashMap<Player, Integer> cooldownTime2;
	public HashMap<Player, BukkitRunnable> cooldownTask2;
	public Messenger mensajero = Bukkit.getMessenger();
	public PluginManager pm = Bukkit.getPluginManager();
	
	@Override
	public void onEnable() {
		cooldownTime = new HashMap<Player, Integer>();
		cooldownTask = new HashMap<Player, BukkitRunnable>();
		cooldownTime2 = new HashMap<Player, Integer>();
		cooldownTask2 = new HashMap<Player, BukkitRunnable>();
		instancia = this;
		mensajero.registerOutgoingPluginChannel(this, "BungeeCord");
		getCommand("lobby").setExecutor(this);
		//pm.registerEvents(this, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			int segundosParaTeleport = 6;
			
			if (args.length == 0) {
				
				if (cooldownTime.containsKey(p)) {
					sender.sendMessage(establecerColor("&8[&6Socket&8] &4&lERROR: &7Espera &b" + cooldownTime.get(p) + " &7segundos!"));
					return true;
				}
				
				cooldownTime.put(p, 10);
				cooldownTask.put(p, TID_COOLDOWN=new BukkitRunnable(){
					public void run() {
						cooldownTime.put(p, cooldownTime.get(p)-1);
						if (cooldownTime.get(p) == 0) {
							cooldownTime.remove(p);
							cooldownTask.remove(p);
							cancel();
							sender.sendMessage(establecerColor("&8[&6Socket&8] &7Ya puedes enviar la peticion al &bLobby &7de nuevo!"));
						}
					}
				});
				
				cooldownTask.get(p).runTaskTimer(this, 20, 20);
				
				p.sendMessage(establecerColor("&8[&6Socket&8] &7Conectando con &bLobby&7..."));
				p.sendMessage(establecerColor("&8[&6Socket&8] &7Enviando peticion..."));
				
				TID_PRINCIPAL=Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					
					@Override
					public void run() {

						p.sendMessage("");
						p.sendMessage(establecerColor("&8═══════════════════════════════════"));
						p.sendMessage(establecerColor("&8[&6Socket&8] &7Servidor &bLobby &7acepto la peticion!"));
						p.sendMessage(establecerColor("&8[&6Socket&8] &7Respuesta en (&a10 &7ms)."));
						p.sendMessage(establecerColor("&8═══════════════════════════════════"));
						p.sendMessage("");
						
					}
					
				}, 10L);
				
				TID_TEMPORIZADOR=Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					@Override
					public void run() {
						p.sendMessage(establecerColor("&8[&6Socket&8] &7Teletransporte en: &b"+cooldownTime2.get(p)));
					}
				}, 20, 20);
				
				cooldownTime2.put(p, segundosParaTeleport);
				cooldownTask2.put(p, new BukkitRunnable() {
					public void run() {
						cooldownTime2.put(p, cooldownTime2.get(p) - 1);
						if (cooldownTime2.get(p) == 0) {
							cooldownTime2.remove(p);
							cooldownTask2.remove(p);
							cancel();
							Bukkit.getScheduler().cancelTask(TID_TEMPORIZADOR);
						}
					}
				});

				cooldownTask2.get(p).runTaskTimer(this, segundosParaTeleport, 20);
				
				TID=Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					@Override
					public void run() {
						bungeeLobby(p);
					}
				}, segundosParaTeleport*20L);
				
				return true;
			}
			
		} else {
			sender.sendMessage("[Code] Unicamente puedes ejecutar este comando desde el juego.");
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("resource")
	public void bungeeLobby(Player jugador) {
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream datos = new DataOutputStream(bytes);
		
		try {
			datos.writeUTF("Connect");
			datos.writeUTF("Lobby");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		jugador.sendPluginMessage(this, "BungeeCord", bytes.toByteArray());
		return;
	}
	
	public void log(String log) {
		getLogger().log(Level.INFO, log);
	}
	
	public void log(Level nivel, String log) {
		getLogger().log(nivel, log);
	}
	
	public Main instancia() {
		return instancia;
	}
	
	public String establecerColor(String color) {
		color=color.replace("{C1}", "»");
		color=color.replace("{C2}", "«");
		return ChatColor.translateAlternateColorCodes('&', color);
	}
	
	@Override
	public void onDisable() {}
	
}
