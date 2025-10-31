package com.example.grawards.controller;

import com.example.grawards.DTO.PageableDTO;
import com.example.grawards.VO.MovieVO;
import com.example.grawards.VO.ReportVO;
import com.example.grawards.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping(path = "/import")
    public void importMovies(@RequestPart(name = "movies") MultipartFile movies) {
        movieService.importMovies(movies);
    }

    @GetMapping(path = "/all")
    public Page<MovieVO> getAllMovies(@RequestBody PageableDTO pageable) {
        return movieService.getAllMovies(pageable.toPageable());
    }

    @GetMapping(path = "/report")
    public ReportVO getReport() {
        return movieService.getReport();
    }

}
