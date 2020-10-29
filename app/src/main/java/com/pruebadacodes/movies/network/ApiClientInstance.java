package com.pruebadacodes.movies.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pruebadacodes.movies.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClientInstance {
    /**
     * Variable estática de apoyo para manejar el cliente
     * de consumo de la api como singleton
     */
    private static Retrofit sRetrofit;

    /**
     * Método para obtener la instancia del cliente de consumo de la api
     * @param context Contexto Android
     * @return Instancia del cliente de consumo de la api
     */
    public static Retrofit getRetrofitInstance(Context context){
        if(sRetrofit == null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Se establece el timeout en 30 segundos
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS).build();

            // Se crea una instancia de retrofit usando la configuración
            // para el manejo de RxJava y especificando que se usará
            // Gson para la serialización de los objetos
            sRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constants.API_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }

        return sRetrofit;
    }

    /**
     * Método para invalidar la variable de apoyo en caso de requerir
     * forzar la creación de la instancia en la siguiente llamada
     */
    public static void dispose() {
        sRetrofit = null;
    }
}