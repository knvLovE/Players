package com.game.dto;

import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.Date;

public class PlayerUpdateRequestDto extends PlayerCreateAbstractRequestDto {

    Long id;

    public PlayerUpdateRequestDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
