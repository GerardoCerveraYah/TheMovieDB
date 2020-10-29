package com.pruebadacodes.movies.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.pruebadacodes.movies.models.Configuration;
import com.pruebadacodes.movies.network.ApiClientInstance;
import com.pruebadacodes.movies.network.MoviesApi;

public class Tools {
    private static String messageDialogName = "messageDialog";

    // Variable para almacenar la url base de los posters
    private static String baseUrl = "";
    private static String secureBaseUrl = "";

    // Variable para almacenar temporalmente la configuración de la api
    private static Configuration configuration;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(Configuration configuration) {
        Tools.configuration = configuration;

        if (Tools.configuration != null &&
                Tools.configuration.getImagesConfiguration() != null) {

            if (!Tools.configuration.getImagesConfiguration().getSecureBaseUrl().isEmpty()) {
                secureBaseUrl = Tools.configuration.getImagesConfiguration().getSecureBaseUrl();
            }

            if (!Tools.configuration.getImagesConfiguration().getBaseUrl().isEmpty()) {
                baseUrl = Tools.configuration.getImagesConfiguration().getBaseUrl();
            }

            if (Tools.configuration.getImagesConfiguration().getPosterSizes() != null) {
                if (!baseUrl.isEmpty()) {
                    baseUrl = baseUrl.concat(Tools.configuration
                            .getImagesConfiguration()
                            .getPosterSizes().get(Tools.configuration
                                    .getImagesConfiguration()
                                    .getPosterSizes().size() - 1));
                }

                if (!secureBaseUrl.isEmpty()) {
                    secureBaseUrl = secureBaseUrl.concat(Tools.configuration
                            .getImagesConfiguration()
                            .getPosterSizes().get(Tools.configuration
                                    .getImagesConfiguration()
                                    .getPosterSizes().size() - 1));
                }
            } else {
                if (!baseUrl.isEmpty()) {
                    baseUrl = baseUrl.concat("original");
                }

                if (!secureBaseUrl.isEmpty()) {
                    secureBaseUrl = secureBaseUrl.concat("original");
                }
            }
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getSecureBaseUrl() {
        return secureBaseUrl;
    }

    /**
     * Método para obtener la instancia del cliente de la api
     * @param context Contexto android
     * @return Instancia del cliente de la api
     */
    public static MoviesApi getApiInstance(Context context) {
        return ApiClientInstance.getRetrofitInstance(context)
                .create(MoviesApi.class);
    }
}