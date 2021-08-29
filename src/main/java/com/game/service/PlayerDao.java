package com.game.service;

import com.game.dto.PlayerRequestDto;
import com.game.entity.Player;

import java.util.List;

public interface PlayerDao {
    List<Player> getPlayersByFilter(PlayerRequestDto playerRequestDto);
    Long getPlayersCountByFilter(PlayerRequestDto playerRequestDto);
}