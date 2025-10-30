package com.example.grawards.service;

import com.example.grawards.VO.MovieVO;
import com.example.grawards.VO.ReportElementVO;
import com.example.grawards.VO.ReportVO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                    .withSeparator(';')
                    .build()
                    .parse();

            movieRepository.saveAll(moviesPersist);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar os filmes", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<MovieVO> getAllMovies(Pageable pageable) {
        return this.movieRepository.findAllMovies(pageable).map(MovieVO::new);
    }

    @Transactional(readOnly = true)
    public ReportVO getReport() {
        var movies = this.movieRepository.findAllMoviesReport();

        var mins = this.getMins(movies);
        var maxs = this.getMaxs(movies);

        return new ReportVO(mins, maxs);
    }

    private List<ReportElementVO> getMins(List<Movie> movies) {
        var elements = this.getReportElements(movies);

        var minInterval = elements.stream().min(Comparator.comparingInt(ReportElementVO::getInterval)).orElse(null);

        return elements.stream().filter(element -> element.getInterval().equals(minInterval.getInterval())).toList();
    }

    private List<ReportElementVO> getMaxs(List<Movie> movies) {
        var elements = this.getReportElements(movies);

        var minInterval = elements.stream().max(Comparator.comparingInt(ReportElementVO::getInterval)).orElse(null);

        return elements.stream().filter(element -> element.getInterval().equals(minInterval.getInterval())).toList();
    }

    private List<ReportElementVO> getReportElements(List<Movie> movies) {
        return movies.stream()
                .filter(Movie::getWinner)
                .collect(Collectors.groupingBy(Movie::getProducers))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(entry -> {
                    var producers = entry.getKey();
                    var moviesEntry = entry.getValue();
                    var minYear = moviesEntry.stream().min(Comparator.comparingInt(Movie::getYear)).map(Movie::getYear);
                    var maxYear = moviesEntry.stream().max(Comparator.comparingInt(Movie::getYear)).map(Movie::getYear);

                    if(minYear.isEmpty() || maxYear.isEmpty()){
                        return null;
                    }

                    var minYearValid = minYear.get();
                    var maxYearValid = maxYear.get();

                    return new ReportElementVO(producers, maxYearValid - minYearValid, minYearValid, maxYearValid);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
