package com.pruebadacodes.movies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Clase con información detallada de la película
 */
public class MovieDetails {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("overview")
    private String overview;

    @SerializedName("backdrop_path")
    private String backdropPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getGenresList() {
        String genreList = "";
        if (genres != null && genres.size() > 0) {
            for (Genre genre : genres) {
                genreList = genreList.concat(genre.getName() + " ");
            }
        }

        return genreList;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}