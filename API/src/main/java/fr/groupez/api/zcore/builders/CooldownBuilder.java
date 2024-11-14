package fr.groupez.api.zcore.builders;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import fr.groupez.api.ZPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownBuilder {

	public static Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();
	/**
	 * 
	 * @param
	 * @return
	 */
	public static Map<UUID, Long> getCooldownMap(String key) {
		return cooldowns.getOrDefault(key, null);
	}

	/**
	 * 
	 */
	public static void clear() {
		cooldowns.clear();
	}

	/**
	 * 
	 * @param key
	 */
	public static void createCooldown(String key) {
		cooldowns.putIfAbsent(key, new HashMap<>());
	}

	/**
	 * 
	 * @param key
	 */
	public static void removeCooldown(String key, UUID uuid) {

		createCooldown(key);

		getCooldownMap(key).remove(uuid);
	}

	/**
	 * 
	 * @param key
	 * @param player
	 */
	public static void removeCooldown(String key, Player player) {
		removeCooldown(key, player.getUniqueId());
	}

	/**
	 * 
	 * @param key
	 * @param seconds
	 */
	public static void addCooldown(String key, UUID uuid, int seconds) {

		createCooldown(key);

		long next = System.currentTimeMillis() + seconds * 1000L;
		getCooldownMap(key).put(uuid, Long.valueOf(next));
	}
	
	/**
	 * 
	 * @param key
	 * @param player
	 * @param seconds
	 */
	public static void addCooldown(String key, Player player, int seconds) {
		addCooldown(key, player.getUniqueId(), seconds);
	}

	/**
	 * 
	 * @param key
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isCooldown(String key, UUID uuid) {

		createCooldown(key);
		Map<UUID, Long> map = cooldowns.get(key);

		return (map.containsKey(uuid)) && (System.currentTimeMillis() <= ((Long) map.get(uuid)).longValue());
	}

	/**
	 * 
	 * @param key
	 * @param player
	 * @return boolean
	 */
	public static boolean isCooldown(String key, Player player) {
		return isCooldown(key, player.getUniqueId());
	}

	/**
	 * 
	 * @param key
	 * @param uuid
	 * @return long
	 */
	public static long getCooldown(String key, UUID uuid) {

		createCooldown(key);
		Map<UUID, Long> map = cooldowns.get(key);

		return ((Long) map.getOrDefault(uuid, 0l)).longValue() - System.currentTimeMillis();
	}

	/**
	 * 
	 * @param key
	 * @param player
	 * @return long
	 */
	public static long getCooldownPlayer(String key, Player player) {
		return getCooldown(key, player.getUniqueId());
	}

	/**
	 * 
	 * @param key
	 * @param player
	 * @return
	 */
	public static String getCooldownAsString(String key, UUID player) {
		return TimerBuilder.getStringTime(getCooldown(key, player) / 1000);
	}
}
