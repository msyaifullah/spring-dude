package com.yyggee.eggs.service.ds1.player;

import com.yyggee.eggs.model.ds1.player.Player;
import com.yyggee.eggs.repositories.ds1.player.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Iterable<Player> list() {
        return playerRepository.findAll();
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public void save(List<Player> players) {
        players.forEach(this::save);
    }

}
