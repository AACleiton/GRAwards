package com.example.grawards.VO;

import com.example.grawards.domain.Movie;

public class MovieVO {
    private Long id;
    private int year;
    private String title;
    private String studios;
    private String producers;
    private Boolean winner;

    public MovieVO() {
    }

    public MovieVO(Movie movie) {
        this.id = movie.getId();
        this.year = movie.getYear();
        this.title = movie.getTitle();
        this.studios = movie.getStudios();
        this.producers = movie.getProducers();
        this.winner = movie.getWinner();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudios() {
        return studios;
    }

    public void setStudios(String studios) {
        this.studios = studios;
    }

    public String getProducers() {
        return producers;
    }

    public void setProducers(String producers) {
        this.producers = producers;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }
}
