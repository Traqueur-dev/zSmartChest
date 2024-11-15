package fr.groupez.api.zcore;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.groupez.api.ZPlugin;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;

public class BossBarAnimation implements Runnable {

    private final BukkitAudiences audiences;
    private final Player player;
    private final BossBar bossBar;
    private final WrappedTask wrappedTask;
    private final long duration;
    private long remainingTicks;

    public BossBarAnimation(ZPlugin plugin, Player player, BossBar bossBar, long duration) {
        this.player = player;
        this.bossBar = bossBar;
        this.remainingTicks = duration;
        this.duration = duration;
        this.wrappedTask = plugin.getScheduler().runTimer(this, 0L, 1L);
        this.audiences = BukkitAudiences.create(plugin);
        this.audiences.player(player).showBossBar(bossBar);
    }

    @Override
    public void run() {
        double progress = (double) this.remainingTicks / this.duration;
        this.bossBar.progress((float) progress);

        if (remainingTicks <= 0) {
            audiences.player(player).hideBossBar(this.bossBar);
            this.wrappedTask.cancel();
            audiences.close();
        }

        this.remainingTicks -= 1;
    }
}