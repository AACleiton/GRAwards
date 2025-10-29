package com.example.grawards.controller;

import com.example.grawards.VO.MovieVO;
import com.example.grawards.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping(path = "/import")
    public void importMovies(@RequestPart MultipartFile movies) {
        movieService.importMovies(movies);
    }

    @GetMapping(path = "/all")
    public Page<MovieVO> getAllMovies(@RequestBody Pageable pageable) {
        return movieService.getAllMovies(pageable);
    }

    @GetMapping(path = "/report")
    public String getReport() {
        return movieService.getReport();
    }

}
