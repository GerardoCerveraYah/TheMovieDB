package com.pruebadacodes.movies.interfaces;

/**
 * Interfaz para los métodos ejecutados en segundo plano
 * @param <T> Tipo de la respuesta
 */
public interface RxCallback<T> {
    void onError(Throwable throwable);
    void onComplete(T result);
}
