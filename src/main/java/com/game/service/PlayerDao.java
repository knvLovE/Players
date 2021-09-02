package com.game.service;

import com.game.dto.PlayerCreateRequestDto;
import com.game.dto.PlayerRequestDto;
import com.game.dto.PlayerUpdateRequestDto;
import com.game.entity.Player;

import java.util.List;

public interface PlayerDao {
    List<Player> getPlayersByFilter(PlayerRequestDto playerRequestDto);
    Long getPlayersCountByFilter(PlayerRequestDto playerRequestDto);
    Player getPlayerById(Long id);
    void deletePlayerById(Long id);
    Player createPlayer(PlayerCreateRequestDto playerCreateRequestDto);
    Player update(PlayerUpdateRequestDto playerUpdate);
}
