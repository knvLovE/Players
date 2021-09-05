package com.game.controller;


import com.game.dto.PlayerCreateRequestDto;
import com.game.dto.PlayerRequestDto;
import com.game.dto.PlayerUpdateRequestDto;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class UserRestController {

    // получить игроков
    @GetMapping("/players")
    public List<Player> getPlayersList(
            @RequestParam (required = false) String name,
            @RequestParam (required = false) String title,
            @RequestParam (required = false) Race race,
            @RequestParam (required = false) Profession profession,
            @RequestParam (required = false) Long after,
            @RequestParam (required = false) Long before,
            @RequestParam (required = false) Boolean banned,
            @RequestParam (required = false) Integer minExperience,
            @RequestParam (required = false) Integer maxExperience,
            @RequestParam (required = false) Integer minLevel,
            @RequestParam (required = false) Integer maxLevel,
            @RequestParam (required = false) PlayerOrder order,
            @RequestParam (required = false) Integer pageNumber,
            @RequestParam (required = false) Integer pageSize
    ) {
        PlayerRequestDto playerRequestDto = new PlayerRequestDto(
                name, title, race, profession, convertData(after), convertData(before), banned,
                minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);

        return playerDao.getPlayersByFilter(playerRequestDto);
    }

    // Получить общее количество игроков
    @GetMapping("/players/count")
    public Long getPlayersCount(
            @RequestParam (required = false) String name,
            @RequestParam (required = false) String title,
            @RequestParam (required = false) Race race,
            @RequestParam (required = false) Profession profession,
            @RequestParam (required = false) Long after,
            @RequestParam (required = false) Long before,
            @RequestParam (required = false) Boolean banned,
            @RequestParam (required = false) Integer minExperience,
            @RequestParam (required = false) Integer maxExperience,
            @RequestParam (required = false) Integer minLevel,
            @RequestParam (required = false) Integer maxLevel,
            @RequestParam (required = false) PlayerOrder order,
            @RequestParam (required = false) Integer pageNumber,
            @RequestParam (required = false) Integer pageSize
    ) {
        PlayerRequestDto playerRequestDto = new PlayerRequestDto(
                name, title, race, profession, convertData(after), convertData(before), banned,
                minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);

        return playerDao.getPlayersCountByFilter(playerRequestDto);
    }

    protected Date convertData (Long longData) {
        return longData != null ? new Date(longData) : null;
    }

    // Получить игрока по id
    @GetMapping("/players/{id}")
    public Player getPlayer (
            @PathVariable(required = true, name = "id") Long id) {
        return playerDao.getPlayerById(id);
    }

    // создать игрока
    @PostMapping("/players")
    public Player createPlayer (
            @RequestBody PlayerCreateRequestDto playerCreateRequestDto) {
        return playerDao.createPlayer(playerCreateRequestDto);
    }

    // удалить игрока
    @DeleteMapping("/players/{id}")
    public void deletePlayer (
            @PathVariable(required = true, name = "id") Long id) {
         playerDao.deletePlayerById(id);
    }

    // обновить игрока
    @PostMapping("/players/{id}")
    public Player updatePlayer (
            @PathVariable (name = "id") Long id,
            @RequestBody PlayerUpdateRequestDto playerUpdate) {
        playerUpdate.setId(id);
        return playerDao.update(playerUpdate);
    }

    PlayerDao playerDao;

    @Autowired
    public void setPlayerDao(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }
}
