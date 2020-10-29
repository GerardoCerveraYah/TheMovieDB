package com.pruebadacodes.movies.network;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Clase para el manejo de diferentes tipos de excepciones al consumir una api
 * @param <T>
 */
public abstract class CallbackWrapper <T extends Response>
        extends DisposableObserver<T> {

    public CallbackWrapper() {
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    /**
     * Método que se invocará al presentarse un error para identificar
     * el tipo de excepción
     * @param t Excepción generada
     */
    @Override
    public void onError(Throwable t) {
        if (t instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) t).response().errorBody();
            onUnknownError(getErrorMessage(responseBody));
        } else if (t instanceof SocketTimeoutException) {
            onTimeout();
        } else if (t instanceof IOException) {
            onNetworkError();
        } else {
            onUnknownError(t.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }

    protected abstract void onSuccess(T t);

    protected abstract void onUnknownError(String message);

    protected abstract void onTimeout();

    protected abstract void onNetworkError();

    /**
     * Método para obtener el texto de error de la excepción
     * @param responseBody Variable con el objeto de respuesta del servicio
     * @return Texto con el mensaje de error
     */
    public static String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}