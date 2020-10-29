package com.pruebadacodes.movies.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pruebadacodes.movies.R;
import com.pruebadacodes.movies.databinding.FragmentMovieDetailBinding;
import com.pruebadacodes.movies.databinding.FragmentMoviesBinding;
import com.pruebadacodes.movies.interfaces.OnFragmentInteractionListener;
import com.pruebadacodes.movies.models.Configuration;
import com.pruebadacodes.movies.models.MovieDetails;
import com.pruebadacodes.movies.network.CallbackWrapper;
import com.pruebadacodes.movies.utils.Constants;
import com.pruebadacodes.movies.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Fragmento para mostrar los detalles de la película
 */
public class MovieDetailFragment extends Fragment {
    // Variable para el manejo de llamadas a la api
    protected CompositeDisposable mCompositeDisposable;
    // Variable para el manejo eficiente de la referencia a la actividad padre
    private WeakReference<FragmentActivity> mWeakActivity;
    // Binding de la vista
    private FragmentMovieDetailBinding mViewBinding;

    private OnFragmentInteractionListener mListener;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Método para generar una nueva instancia del fragmento
     *
     * @return Instancia del fragmento
     */
    public static MovieDetailFragment newInstance() {
        return new MovieDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        mWeakActivity = new WeakReference<FragmentActivity>(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, null, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey(Constants.ARG_MOVIE_ID)) {
            loadDetails(getArguments().getInt(Constants.ARG_MOVIE_ID));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        mViewBinding = null;
        mCompositeDisposable.dispose();
        mCompositeDisposable = null;
        super.onDestroy();
    }

    private void loadDetails(final int movieId) {
        mCompositeDisposable.add(Tools.getApiInstance(getContext())
                .getMovieDetail(
                        movieId,
                        Constants.API_KEY,
                        Locale.getDefault().toLanguageTag())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CallbackWrapper<Response<MovieDetails>>() {
                    @Override
                    protected void onSuccess(Response<MovieDetails> response) {
                        mViewBinding.loading.setVisibility(View.GONE);
                        mViewBinding.container.setVisibility(View.VISIBLE);
                        if (response.isSuccessful() && response.body() != null) {
                            MovieDetails detail = response.body();
                            mViewBinding.setMovie(detail);

                            if (!Tools.getSecureBaseUrl().isEmpty() &&
                                    detail.getBackdropPath() != null && !detail.getBackdropPath().isEmpty()) {
                                loadImage(Tools.getSecureBaseUrl() + detail.getBackdropPath());
                            } else if (!Tools.getBaseUrl().isEmpty() &&
                                    detail.getBackdropPath() != null && !detail.getBackdropPath().isEmpty()) {
                                loadImage(Tools.getBaseUrl() + detail.getBackdropPath());
                            }
                        } else {
                            mWeakActivity.get().onBackPressed();
                            mListener.onMessage(getString(R.string.unknown_error_message,
                                    response.message()),
                                    getString(R.string.retry),
                                    null);
                        }
                    }

                    @Override
                    protected void onUnknownError(String message) {
                        mWeakActivity.get().onBackPressed();
                        mListener.onMessage(getString(R.string.unknown_error_message, message),
                                getString(R.string.retry),
                                null);
                    }

                    @Override
                    protected void onTimeout() {
                        mWeakActivity.get().onBackPressed();
                        mListener.onMessage(getString(R.string.E002), getString(R.string.retry),
                                null);
                    }

                    @Override
                    protected void onNetworkError() {
                        mWeakActivity.get().onBackPressed();
                        mListener.onMessage(getString(R.string.E001), getString(R.string.retry),
                                null);
                    }
                }));
    }

    private void loadImage(String url) {
        Glide.with(mViewBinding.imagePoster.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_poster)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(mViewBinding.imagePoster);
    }
}
