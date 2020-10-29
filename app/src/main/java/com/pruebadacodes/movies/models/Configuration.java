package com.pruebadacodes.movies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Clase con información relacionada a la configuración
 */
public class Configuration {
    @SerializedName("images")
    private ImageConfiguration imagesConfiguration;

    @SerializedName("change_keys")
    private List<String> changeKeys;

    public ImageConfiguration getImagesConfiguration() {
        return imagesConfiguration;
    }

    public void setImagesConfiguration(ImageConfiguration imagesConfiguration) {
        this.imagesConfiguration = imagesConfiguration;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }
}
