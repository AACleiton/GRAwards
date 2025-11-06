package com.example.grawards.service;

import com.example.grawards.DTO.MovieDTO;
import com.example.grawards.VO.MovieVO;
import com.example.grawards.VO.ReportElementVO;
import com.example.grawards.VO.ReportQueryVO;
import com.example.grawards.VO.ReportVO;
import com.example.grawards.domain.Movie;
import com.example.grawards.repository.MovieRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void init() {
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream("data/Movielist.csv");

            if (inputStream == null) {
                throw new RuntimeException("Arquivo Movielist.csv nao encontrado!");
            }

            var reader = new InputStreamReader(inputStream);

            var moviesDTO = new CsvToBeanBuilder<MovieDTO>(reader)
                    .withType(MovieDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .build()
                    .parse();

            ArrayList<Movie> moviesPersist = new ArrayList<>();

            for (MovieDTO movieDTO : moviesDTO) {
                var producers = this.getAllProducers(movieDTO.getProducers());

                for (String producer : producers) {
                    moviesPersist.add(new Movie(movieDTO, producer));
                }
            }

            movieRepository.saveAll(moviesPersist);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possivel carregar os filmes!");
        }
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
        var report = this.movieRepository.findAllMoviesReport();

        Integer currentMinInterval = null;
        Integer currentMaxInterval = null;
        var mins = new ArrayList<ReportElementVO>();
        var maxs = new ArrayList<ReportElementVO>();

        for (ReportQueryVO reportQueryVO : report) {
            var interval = reportQueryVO.getMaxYear() - reportQueryVO.getMinYear();

            currentMinInterval = isNull(currentMinInterval) ? interval : currentMinInterval;
            currentMaxInterval = isNull(currentMaxInterval) ? interval : currentMaxInterval;

            currentMinInterval = getNewCurrentValueAndUpdateElements(currentMinInterval, mins, reportQueryVO, interval, interval < currentMinInterval);

            currentMaxInterval = getNewCurrentValueAndUpdateElements(currentMaxInterval, maxs, reportQueryVO, interval, interval > currentMaxInterval);
        }

        return new ReportVO(mins, maxs);
    }

    private Integer getNewCurrentValueAndUpdateElements(Integer currentInterval, ArrayList<ReportElementVO> elementVOS, ReportQueryVO reportQueryVO, int interval, boolean updateCurrentInterval) {
        if (interval == currentInterval) {
            elementVOS.add(new ReportElementVO(reportQueryVO.getProducers(), interval, reportQueryVO.getMinYear(), reportQueryVO.getMaxYear()));
        }

        if (updateCurrentInterval) {
            elementVOS.clear();
            elementVOS.add(new ReportElementVO(reportQueryVO.getProducers(), interval, reportQueryVO.getMinYear(), reportQueryVO.getMaxYear()));
            currentInterval = interval;
        }
        return currentInterval;
    }

    private List<String> getAllProducers(String producers) {
        var producersFormatted = producers.replaceAll(" and ", ", ");
        var producersList = new ArrayList<String>();

        if (producersFormatted.contains(",")) {

            this.getProducersRegex(producersFormatted, producersList, "([^,]+)(?=,)");
            this.getProducersRegex(producersFormatted, producersList, "(?<=,)[^,]*$");

        } else {
            producersList.add(producers);
        }

        return producersList;
    }

    private void getProducersRegex(String producersFormatted, List<String> producersList, String regex) {
        var regexCompiled = Pattern.compile(regex);
        var results = regexCompiled.matcher(producersFormatted);

        while (results.find()) {
            var result = results.group();

            if (nonNull(result) && !result.trim().isBlank()) {
                producersList.add(result.trim());
            }
        }
    }
}
