package com.example.grawards.repository;

import com.example.grawards.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Long> {

    @Query(value = """
            SELECT * FROM movie
            """, nativeQuery = true)
    Page<Movie> findAllMovies(Pageable pageable);

    @Query(value = """
            SELECT * FROM movie m where m.winner = true and (m.producers is not null and length(m.producers) > 0) and m.release_year is not null
            """, nativeQuery = true)
    List<Movie> findAllMoviesReport();
}
