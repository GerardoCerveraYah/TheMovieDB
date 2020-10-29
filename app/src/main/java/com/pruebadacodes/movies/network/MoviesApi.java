package com.pruebadacodes.movies.network;

import com.pruebadacodes.movies.models.*;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaz de definición con los métodos a consumir en la api
 */
public interface MoviesApi {

    /**
     * Obtiene una lista de las películas disponibles en los cines
     * @param apiKey Llave de la api
     * @param languageCode Lenguaje en formato ISO 639-1 Ej. es-MX
     * @param page Página a consultar
     * @return Lista con información general de las películas
     */
    @GET("movie/now_playing")
    Observable<Response<PagedResponse<Movie>>> getMovies(
            @Query("api_key") String apiKey,
            @Query("language") String languageCode,
            @Query("page") int page);

    /**
     * Obtiene la información detallada para una película en particular
     * @param apiKey Llave de la api
     * @param languageCode Lenguaje en formato ISO 639-1 Ej. es-MX
     * @param movieId Identificador de la película
     * @return Objeto con información detallada de la película
     */
    @GET("movie/{movieId}")
    Observable<Response<MovieDetails>> getMovieDetail(
            @Path("movieId") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String languageCode);

    /**
     * Obtiene la configuración del servicio
     * @param apiKey Llave de la api
     * @return Objeto con configuración del servicio
     */
    @GET("configuration")
    Observable<Response<Configuration>> getConfiguration(
            @Query("api_key") String apiKey);
}
