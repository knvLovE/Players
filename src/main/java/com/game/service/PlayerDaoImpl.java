package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.dto.PlayerCreateAbstractRequestDto;
import com.game.dto.PlayerCreateRequestDto;
import com.game.dto.PlayerRequestDto;
import com.game.dto.PlayerUpdateRequestDto;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerDaoImpl implements PlayerDao{

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

    @Override
    public Player getPlayerById(Long id) {
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id должен быть больше нуля");
        return playerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "player not found with id: " + id));
    }

    @Override
    public void deletePlayerById(Long id) {
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id должен быть больше нуля");
        try {
            playerRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }



    @Override
    public Player createPlayer(PlayerCreateRequestDto playerCreateRequestDto) {

        checkRequestBeforeSave(playerCreateRequestDto);
        Player player = new Player();

        player.setName(playerCreateRequestDto.getName());
        player.setTitle(playerCreateRequestDto.getTitle());
        player.setTitle(playerCreateRequestDto.getTitle());
        player.setRace(playerCreateRequestDto.getRace());
        player.setProfession(playerCreateRequestDto.getProfession());
        player.setExperience(playerCreateRequestDto.getExperience());
        Integer level =  calcLevel(playerCreateRequestDto.getExperience());
        player.setLevel(level);
        Integer untilNextLevel = calcNextLevel (playerCreateRequestDto.getExperience(), level);
        player.setUntilNextLevel(untilNextLevel);
        player.setBirthday(playerCreateRequestDto.getBirthday());
        player.setBanned(playerCreateRequestDto.getBanned());

        return playerRepository.saveAndFlush(player);
    }

    protected Integer calcLevel (Integer experience) {
        return (int) (Math.sqrt(experience * 200 + 2500) - 50) / 100;
    }

    protected Integer calcNextLevel (Integer experience, Integer level) {
        return 50 * (level + 1) * (level + 2) - experience;
    }

    @Override
    public Player update(PlayerUpdateRequestDto playerUpdate) {
        if (playerUpdate.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id должен быть больше нуля");


        checkRequestBeforeSave(playerUpdate);
        int count = playerRepository.updateById(
                    playerUpdate.getId(),
                    playerUpdate.getName(),
                    playerUpdate.getTitle(),
                    playerUpdate.getRace() != null ? playerUpdate.getRace().name() : null,
                    playerUpdate.getProfession() != null ? playerUpdate.getProfession().name() : null,
                    playerUpdate.getBirthday(),
                    playerUpdate.getBanned(),
                    playerUpdate.getExperience(),
                playerUpdate.getExperience() != null ? calcLevel(playerUpdate.getExperience()) : null,
                playerUpdate.getExperience() != null ? calcNextLevel(playerUpdate.getExperience(), calcLevel(playerUpdate.getExperience())) : null
        );
        if (count != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Player player = getPlayerById(playerUpdate.getId());
        // костылль
        if (playerUpdate.getBirthday() != null) {
            player.setBirthday(playerUpdate.getBirthday());
        }
        return player;
    }

    protected Page<Player> readPlayersInfoByFilter (PlayerRequestDto playerRequestDto) {

        checkRequestBeforeRead(playerRequestDto);
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

    static final int DEFAULT_PAGE_NUMBER = 0;
    static final int DEFAULT_PAGE_SIZE = 3;

    protected void checkRequestBeforeRead (PlayerRequestDto playerRequestDto) {

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

    static final int NAME_LENGTH_MAX = 12;
    static final int TITLE_LENGTH_MAX = 30;
    static final int EXPERIENCE_MAX = 10_000_000;
    static final int BIRTHDAY_YEAR_MIN = 2000;
    static final int DEFAULT_YEAR_MAX = 3000;

    protected void checkRequestBeforeSave(PlayerCreateAbstractRequestDto requestDto) {
        String errorMessage = null;

        if (requestDto instanceof PlayerCreateRequestDto &&
                (requestDto.getName() == null
                || requestDto.getTitle() == null
                || requestDto.getProfession() == null
                || requestDto.getRace() == null
                || requestDto.getBirthday() == null
                || requestDto.getExperience() == null
                )
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "должны быть заданы все параметры");
        }

        if (requestDto instanceof PlayerCreateRequestDto && requestDto.getBanned() == null)  {
            requestDto.setBanned(false);
        }

        if (requestDto.getName() != null && StringUtils.isEmpty(requestDto.getName())) errorMessage = "Имя должно быть не пустым";
        if (requestDto.getName() != null && requestDto.getName().length() > NAME_LENGTH_MAX) errorMessage = "длина имени должна быть меньше " + NAME_LENGTH_MAX;
        if (requestDto.getTitle() != null && requestDto.getTitle().length() > TITLE_LENGTH_MAX) errorMessage = "длина title должна быть меньше " + TITLE_LENGTH_MAX;
        if (requestDto.getExperience() != null && (requestDto.getExperience() < 0 || requestDto.getExperience() > EXPERIENCE_MAX)) errorMessage = "Experience выход за границы";
        if (requestDto.getBirthday() != null && (requestDto.getBirthday().getYear() + 1900 < BIRTHDAY_YEAR_MIN
                    || requestDto.getBirthday().getYear() + 1900 > DEFAULT_YEAR_MAX)) errorMessage = "birthday выход за границы";

        if (! StringUtils.isEmpty(errorMessage)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    };

    protected Pageable setSortAndFilters (PlayerRequestDto playerRequestDto) {

        Sort sort = Sort.by(playerRequestDto.getOrder().getFieldName());

        return  PageRequest.of(
                playerRequestDto.getPageNumber(),
                playerRequestDto.getPageSize(),
                sort);
    }


    protected PlayerRepository playerRepository;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
