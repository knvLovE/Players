package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;

@Service
public interface PlayerRepository extends JpaRepository<Player, Long> {


    List<Player> findByNameContaining (String name, Pageable pageable);
    List<Player> findByTitle (String title, Pageable pageable);
    List<Player> findByRace (Race race, Pageable pageable);
    List<Player> findByProfession (Profession profession, Pageable pageable);
    List<Player> findByBirthdayBetween (Date before, Date after, Pageable pageable);
    List<Player> findByBanned (Boolean banned, Pageable pageable);
    List<Player> findByExperienceBetween (Integer minExperience, Integer maxExperience, Pageable pageable);
    List<Player> findByLevelBetween (Integer minLevel, Integer maxLevel, Pageable pageable);


    @Query ("select u from Player u where" +
            "    (:name IS NULL OR u.name LIKE %:name%)" +
            "AND (:title IS NULL OR u.title LIKE %:title%)" +
            "AND (:race IS NULL OR u.race = :race)" +
            "AND (:profession IS NULL OR u.profession = :profession)" +
            "AND (:before IS NULL OR u.birthday >= :before)" +
            "AND (:after IS NULL OR u.birthday <= :after)" +
            "AND (:banned IS NULL OR u.banned = :banned)" +
            "AND (:minExperience IS NULL OR u.experience >= :minExperience)" +
            "AND (:maxExperience IS NULL OR u.experience <= :maxExperience)" +
            "AND (:minLevel IS NULL OR u.level >= :minLevel)" +
            "AND (:maxLevel IS NULL OR u.level <= :maxLevel)")
    Page<Player> findByFilters  (
            @Param("name") String name,
            @Param("title") String title,
            @Param("race")  Race race,
            @Param("profession") Profession profession,
            @Param("before") Long before,
            @Param("after") Long after,
            @Param("banned") Boolean banned,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("minLevel") Integer minLevel,
            @Param("maxLevel") Integer maxLevel,
            @Param("pageable") Pageable pageable
    );
}
