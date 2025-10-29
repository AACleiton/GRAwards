package com.example.grawards.repository;

import com.example.grawards.VO.MovieVO;
import com.example.grawards.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query(value = """
            SELECT * FROM movies
            """, nativeQuery = true)
    Page<MovieVO> findAllMovies(Pageable pageable);
}
