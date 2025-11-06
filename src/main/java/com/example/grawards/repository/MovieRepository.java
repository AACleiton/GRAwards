package com.example.grawards.repository;

import com.example.grawards.VO.ReportQueryVO;
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
            select producers as producers, min(release_year) as minYear, max(release_year) as maxYear from movie
            where winner is true and release_year is not null and producers is not null and length(producers) > 0
            group by producers
            having min(release_year) < max(release_year)
            """, nativeQuery = true)
    List<ReportQueryVO> findAllMoviesReport();
}
