package de.leonkoth.blockparty.listener;

import de.leonkoth.blockparty.BlockParty;
import de.leonkoth.blockparty.arena.Arena;
import de.leonkoth.blockparty.arena.ArenaState;
import de.leonkoth.blockparty.arena.GameState;
import de.leonkoth.blockparty.event.PlayerWinEvent;
import de.leonkoth.blockparty.player.PlayerInfo;
import de.leonkoth.blockparty.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

import static de.leonkoth.blockparty.locale.BlockPartyLocale.*;

public class PlayerWinListener implements Listener {

    private BlockParty blockParty;
    public PlayerWinListener(BlockParty blockParty) {
        this.blockParty = blockParty;

        Bukkit.getPluginManager().registerEvents(this, blockParty.getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerWin(PlayerWinEvent event) {

        Arena arena = event.getArena();
        //Player player = event.getPlayer();
        List<PlayerInfo> playerInfos = event.getPlayerInfo();

        arena.getPhaseHandler().cancelGamePhase();
        //if (arena.getSongManager().getVotedSong() != null) {
        arena.getSongManager().stop(this.blockParty);
        arena.setGameState(GameState.WAIT);
        //}

        arena.setArenaState(ArenaState.ENDING);

        arena.getFloor().clearInventories();
        arena.getFloor().setEndFloor();

        for (PlayerInfo playerInfo : playerInfos) {
            playerInfo.setPlayerState(PlayerState.WINNER);
            Player player = playerInfo.asPlayer();
            if (player != null) {

                arena.broadcast(PREFIX, WINNER_ANNOUNCE_ALL, false, playerInfo, "%PLAYER%", player.getName());
                WINNER_ANNOUNCE_SELF.message(PREFIX, player);
                player.teleport(arena.getGameSpawn());
            }

            playerInfo.addPoints(15);
            playerInfo.addWins(1);
            this.blockParty.getPlayerInfoManager().savePlayerInfo(playerInfo);
        }


        arena.getPhaseHandler().startWinningPhase(null);
    }

}
