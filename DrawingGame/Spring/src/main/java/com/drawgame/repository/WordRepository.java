package com.drawgame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.drawgame.model.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {

}
