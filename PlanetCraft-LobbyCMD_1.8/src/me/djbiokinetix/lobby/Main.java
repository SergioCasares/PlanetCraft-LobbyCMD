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
							/*if (isMC_8()||isMC_9()||isMC_10()) {
								p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1F, 10F);
							} else if (isMC_11()) {
								p.playSound(p.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1F, 10F);
							}*/
						}
					}
				});
				cooldownTask.get(p).runTaskTimer(this, 20, 20);
				
//--------------------------------------------------------------------------------------------------------
				
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
						//p.sendMessage(establecerColor("&8[&6Socket&8] &7Para cancelar usa &c/lobby cancelar&7!"));
						//p.sendMessage("");
						
						/*if (isMC_8()||isMC_9()||isMC_10()) {
							p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1F, 10F);
						} else if (isMC_11()) {
							p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 1F, 10F);
						}*/
						
					}
					
				}, 10L);
				
				TID_TEMPORIZADOR=Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					@Override
					public void run() {
						p.sendMessage(establecerColor("&8[&6Socket&8] &7Teletransporte en: &b"+cooldownTime2.get(p)));
						/*if (isMC_8()||isMC_9()||isMC_10()) {
							p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 1F, 1F);
						} else if (isMC_11()) {
							p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 1F, 1F);
						}*/
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
			
			/*if (args.length > 0) {
				if ((args[0].equalsIgnoreCase("cancelar"))||(args[0].equalsIgnoreCase("cancel"))||(args[0].equalsIgnoreCase("c"))) {

					Bukkit.getScheduler().cancelTask(TID_PRINCIPAL);
					Bukkit.getScheduler().cancelTask(TID_TEMPORIZADOR);
					Bukkit.getScheduler().cancelTask(TID);
					
					log("Cancelados: TID_PRINCIPAL y TID");
					
					if (isMC_8()||isMC_9()||isMC_10()) {
						p.playSound(p.getLocation(), Sound.valueOf("NOTE_BASS"), 1F, 1F);
					} else if (isMC_11()) {
						p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_BASS"), 1F, 1F);
					}
					
					p.sendMessage(establecerColor("&8[&6Socket&8] &7Hemos &ccancelado &7la peticion al &bLobby&7."));
					
				}
				
			}*/
			
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
		
		/*if (isMC_8()||isMC_9()||isMC_10()) {
			jugador.playSound(jugador.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1F, 1F);
		} else if (isMC_11()) {
			jugador.playSound(jugador.getLocation(), Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 1F, 1F);
		}*/
		
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
	
	/*public static boolean isMC_11() {
		return Bukkit.getBukkitVersion().contains("1.11");
	}
	
    public static boolean isMC_10() {
    	return Bukkit.getBukkitVersion().contains("1.10");
    }
	
	public static boolean isMC_9() {
		return Bukkit.getBukkitVersion().contains("1.9");
	}
	
	public static boolean isMC_8() {
		return Bukkit.getBukkitVersion().contains("1.8");
	}*/
	
	public String establecerColor(String color) {
		color=color.replace("{C1}", "»");
		color=color.replace("{C2}", "«");
		return ChatColor.translateAlternateColorCodes('&', color);
	}
	
	/*@EventHandler
	public void onCMD(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if ((e.getMessage().equalsIgnoreCase("/lobby"))||(e.getMessage().equalsIgnoreCase("/LobbyCMD:lobby"))) {
			if (EggWarsAPI.getEggWarsPlayer(p).isInArena()) {
				p.sendMessage(establecerColor("&8[&6Socket&8] &4&lERROR: &7No puedes ir al &bLobby &7si estas en una arena!"));
				if (isMC_8()||isMC_9()||isMC_10()) {
					p.playSound(p.getLocation(), Sound.valueOf("WITHER_HURT"), 1F, -20F);
				} else if (isMC_11()) {
					p.playSound(p.getLocation(), Sound.valueOf("ENTITY_WITHER_HURT"), 1F, -20F);
				}
				if (cooldownTime.containsKey(p)||cooldownTask.containsKey(p)) {
					cooldownTask.remove(p);
					cooldownTime.remove(p);
					Bukkit.getScheduler().cancelTasks((Plugin) TID_COOLDOWN);
				}
				e.setCancelled(true);
				return;
			}
		} else {
			return;
		}
	}*/
	
	@Override
	public void onDisable() {}
	
}
