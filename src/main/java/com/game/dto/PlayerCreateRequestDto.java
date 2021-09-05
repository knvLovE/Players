package com.game.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class PlayerCreateRequestDto extends PlayerCreateAbstractRequestDto {

    public PlayerCreateRequestDto() {

    }
}
