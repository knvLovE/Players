package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.dto.PlayerRequestDto;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.util.List;

@Service
public class PlayerDaoImpl implements PlayerDao{

    static final int DEFAULT_PAGE_NUMBER = 0;
    static final int DEFAULT_PAGE_SIZE = 3;

    @Override
    public List<Player> getPlayersByFilter(PlayerRequestDto playerRequestDto) {
        Page page = readPlayersInfoByFilter(playerRequestDto);
        return page.getContent();

    }

    @Override
    public Long getPlayersCountByFilter(PlayerRequestDto playerRequestDto) {

        Page page = readPlayersInfoByFilter(playerRequestDto);
        return page.getTotalElements();
    }


    Page<Player> readPlayersInfoByFilter (PlayerRequestDto playerRequestDto) {

        checkRequest(playerRequestDto);
        Pageable pageableParams = setSortAndFilters(playerRequestDto);

        Page<Player> playersPage = playerRepository.findByFilters (
                playerRequestDto.getName(),
                playerRequestDto.getTitle(),
                playerRequestDto.getRace(),
                playerRequestDto.getProfession(),
                playerRequestDto.getBefore(),
                playerRequestDto.getAfter(),
                playerRequestDto.getBanned(),
                playerRequestDto.getMinExperience(),
                playerRequestDto.getMaxExperience(),
                playerRequestDto.getMinLevel(),
                playerRequestDto.getMaxLevel(),
                pageableParams
        );
        return playersPage;
    }

    void checkRequest (PlayerRequestDto playerRequestDto) {

        if (playerRequestDto.getPageNumber() == null) {
            playerRequestDto.setPageNumber(DEFAULT_PAGE_NUMBER);
        }
        if (playerRequestDto.getPageSize() == null) {
            playerRequestDto.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (playerRequestDto.getOrder() == null) {
            playerRequestDto.setOrder(PlayerOrder.ID);
        }
    }

    Pageable setSortAndFilters (PlayerRequestDto playerRequestDto) {

        Sort sort = Sort.by(playerRequestDto.getOrder().getFieldName());

        return  PageRequest.of(
                playerRequestDto.getPageNumber(),
                playerRequestDto.getPageSize(),
                sort);
    }


    PlayerRepository playerRepository;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
