package com.example.grawards.service;

import com.example.grawards.VO.MovieVO;
import com.example.grawards.domain.Movie;
import com.example.grawards.repository.MovieRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void importMovies(MultipartFile movies) {
        try {
            var reader = new BufferedReader(new InputStreamReader(movies.getInputStream()));

            var moviesPersist = new CsvToBeanBuilder<Movie>(reader)
                    .withType(Movie.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            movieRepository.saveAll(moviesPersist);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar os filmes", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<MovieVO> getAllMovies(Pageable pageable) {
        return this.movieRepository.findAllMovies(pageable);
    }

    @Transactional(readOnly = true)
    public String getReport() {
        return "";
    }
}
