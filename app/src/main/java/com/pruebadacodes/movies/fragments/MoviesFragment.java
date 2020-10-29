package com.pruebadacodes.movies.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pruebadacodes.movies.R;
import com.pruebadacodes.movies.adapters.MovieAdapter;
import com.pruebadacodes.movies.adapters.OnItemClickListener;
import com.pruebadacodes.movies.databinding.FragmentMoviesBinding;
import com.pruebadacodes.movies.interfaces.OnFragmentInteractionListener;
import com.pruebadacodes.movies.models.Configuration;
import com.pruebadacodes.movies.models.Movie;
import com.pruebadacodes.movies.models.PagedResponse;
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
 * Fragmento para desplegar el listado de películas
 */
public class MoviesFragment extends Fragment implements OnItemClickListener<Movie> {
    // Variable para el manejo de llamadas a la api
    protected CompositeDisposable mCompositeDisposable;
    // Variable para el manejo eficiente de la referencia a la actividad padre
    private WeakReference<FragmentActivity> mWeakActivity;
    // Binding de la vista
    private FragmentMoviesBinding mViewBinding;
    // Adaptador de las películas
    private MovieAdapter mMoviesAdapter;
    // Variables de apoyo para scroll infinito
    private int currentPage = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private OnFragmentInteractionListener mListener;

    public MoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Método para generar una nueva instancia del fragmento
     *
     * @return Instancia del fragmento
     */
    public static MoviesFragment newInstance() {
        return new MoviesFragment();
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
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, null, false);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Se asigna la acción a ejecutar al hacer el gesto Pull-to-refresh
        mViewBinding.moviesRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        currentPage = 1;
                        loadMovies();
                    }
                }
        );

        setPullToRefreshColorScheme();

        setupMoviesRecycler();
        setupScrollListener();

        // Carga de la configuración de la api
        loadConfiguration();
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

    /**
     * Método para la descarga de la configuración de la api
     */
    private void loadConfiguration() {
        currentPage = 1;
        mViewBinding.moviesRefresh.setRefreshing(true);
        mCompositeDisposable.add(Tools.getApiInstance(getContext())
                .getConfiguration(Constants.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CallbackWrapper<Response<Configuration>>() {
                    @Override
                    protected void onSuccess(Response<Configuration> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Tools.setConfiguration(response.body());
                        }
                        loadMovies();
                    }

                    @Override
                    protected void onUnknownError(String message) {
                        loadMovies();
                    }

                    @Override
                    protected void onTimeout() {
                        loadMovies();
                    }

                    @Override
                    protected void onNetworkError() {
                        mViewBinding.moviesRefresh.setRefreshing(false);
                        mListener.onMessage(getString(R.string.E001), getString(R.string.retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadConfiguration();
                                    }
                                });
                    }
                }));
    }

    /**
     * Método para la descarga de la lista de películas
     */
    private void loadMovies() {
        mCompositeDisposable.add(Tools.getApiInstance(getContext())
                .getMovies(
                        Constants.API_KEY,
                        Locale.getDefault().toLanguageTag(),
                        currentPage
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CallbackWrapper<Response<PagedResponse<Movie>>>() {
                    @Override
                    protected void onSuccess(Response<PagedResponse<Movie>> response) {
                        initRefresh();
                        if (response.isSuccessful() && response.body() != null) {
                            isLastPage = response.body().getPage()
                                    == response.body().getTotalPages();
                            if (currentPage == 1) {
                                mMoviesAdapter.setMovieList(response.body().getResults());
                            } else {
                                mMoviesAdapter.addMovies(response.body().getResults());
                            }
                        } else {
                            mListener.onMessage(getString(R.string.unknown_error_message,
                                    response.message()),
                                    getString(R.string.retry),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            loadMovies();
                                        }
                                    });
                        }
                    }

                    @Override
                    protected void onUnknownError(String message) {
                        initRefresh();
                        mListener.onMessage(getString(R.string.unknown_error_message, message),
                                getString(R.string.retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadMovies();
                                    }
                                });
                    }

                    @Override
                    protected void onTimeout() {
                        initRefresh();
                        mListener.onMessage(getString(R.string.E002), getString(R.string.retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadMovies();
                                    }
                                });
                    }

                    @Override
                    protected void onNetworkError() {
                        initRefresh();
                        mListener.onMessage(getString(R.string.E001), getString(R.string.retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadMovies();
                                    }
                                });
                    }
                }));
    }

    /**
     * Método para asignar el esquema de colores del Pull-to-refresh
     */
    private void setPullToRefreshColorScheme() {
        int colorPrimary = ContextCompat.getColor(mWeakActivity.get(), R.color.colorPrimary);
        int colorAccent = ContextCompat.getColor(mWeakActivity.get(), R.color.colorAccent);
        mViewBinding.moviesRefresh.setColorSchemeColors(colorPrimary, colorAccent);
    }

    /**
     * Método para inicialización del recyclerview
     */
    private void setupMoviesRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(mWeakActivity.get(), 2,
                RecyclerView.VERTICAL, false);
        // En el caso de que el item sea null (Loading), deberá ocupar 2 columnas en vez de 1
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mMoviesAdapter.getMovieList().get(position) != null ? 1 : 2;
            }
        });
        mViewBinding.recyclerMovies.setLayoutManager(layoutManager);

        mMoviesAdapter = new MovieAdapter(null, this);
        mViewBinding.recyclerMovies.setAdapter(mMoviesAdapter);
    }

    /**
     * Método para inicializar el listener del scroll del recyclerview
     * para implementación de scroll infinito
     */
    private void setupScrollListener() {
        mViewBinding.recyclerMovies.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView,
                                                     int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        GridLayoutManager layoutManager =
                                (GridLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading && !isLastPage) {
                            if (layoutManager != null &&
                                    layoutManager.findLastCompletelyVisibleItemPosition()
                                            == mMoviesAdapter.getMovieList().size() - 1) {
                                currentPage++;
                                mMoviesAdapter.addMovie(null);
                                isLoading = true;
                                loadMovies();
                            }
                        }
                    }
                }
        );
    }

    /**
     * Método para inicializar los controles de refresh (Pull-to-refresh/scroll infinito)
     */
    private void initRefresh() {
        mViewBinding.moviesRefresh.setRefreshing(false);
        isLoading = false;
        if (currentPage > 1) {
            mMoviesAdapter.removeMovie(
                    mMoviesAdapter.getMovieList().size() - 1);
        }
    }

    @Override
    public void onItemClick(Movie item) {
        mListener.onNavigationChanged(true, getString(R.string.title_fragment_detail));
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ARG_MOVIE_ID, item.getId());
        Navigation.findNavController(mViewBinding.getRoot()).navigate(R.id.fragmentListToDetail, bundle);
    }
}