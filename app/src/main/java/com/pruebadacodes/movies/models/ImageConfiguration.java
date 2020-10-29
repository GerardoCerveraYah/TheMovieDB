package com.pruebadacodes.movies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Clase con información relacionada a la configuración de imágenes
 */
public class ImageConfiguration {
    @SerializedName("base_url")
    private String baseUrl;

    @SerializedName("secure_base_url")
    private String secureBaseUrl;

    @SerializedName("backdrop_sizes")
    private List<String> backdropSizes;

    @SerializedName("poster_sizes")
    private List<String> posterSizes;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSecureBaseUrl() {
        return secureBaseUrl;
    }

    public void setSecureBaseUrl(String secureBaseUrl) {
        this.secureBaseUrl = secureBaseUrl;
    }

    public List<String> getBackdropSizes() {
        return backdropSizes;
    }

    public void setBackdropSizes(List<String> backdropSizes) {
        this.backdropSizes = backdropSizes;
    }

    public List<String> getPosterSizes() {
        return posterSizes;
    }

    public void setPosterSizes(List<String> posterSizes) {
        this.posterSizes = posterSizes;
    }
}