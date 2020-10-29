package com.pruebadacodes.movies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pruebadacodes.movies.R;
import com.pruebadacodes.movies.databinding.ItemLoadMoreBinding;
import com.pruebadacodes.movies.databinding.ItemMovieBinding;
import com.pruebadacodes.movies.models.Movie;
import com.pruebadacodes.movies.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Lista de películas del adaptador
    private List<Movie> mMovieList;

    // Tipos de vista, se usan como apoyo en la implementación de
    // scroll infinito
    private final int VIEW_TYPE_MOVIE = 0;
    private final int VIEW_TYPE_LOADING = 1;

    // Variable para formato de fecha
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");

    private OnItemClickListener<Movie> mOnItemClickListener;

    public MovieAdapter(List<Movie> movieList, OnItemClickListener<Movie> onItemClickListener) {
        formatDate(movieList);
        this.mMovieList = movieList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MOVIE) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemMovieBinding itemBinding = ItemMovieBinding.inflate(layoutInflater, parent, false);
            return new MovieHolder(itemBinding);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemLoadMoreBinding itemBinding = ItemLoadMoreBinding.inflate(layoutInflater, parent, false);
            return new LoadingViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieHolder) {
            Movie movie = mMovieList.get(position);
            ((MovieHolder) holder).bind(movie);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList != null ? mMovieList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mMovieList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_MOVIE;
    }

    /**
     * Método para añadir nuevas películas a la lista
     *
     * @param newMovies Lista de películas a añadir
     */
    public void addMovies(List<Movie> newMovies) {
        if (newMovies != null) {
            if (mMovieList == null) {
                mMovieList = new ArrayList<>();
            }
            int startPosition = mMovieList.size();
            formatDate(newMovies);
            mMovieList.addAll(newMovies);
            notifyItemRangeInserted(startPosition, newMovies.size());
        }
    }

    /**
     * Añade una película a la lista
     *
     * @param newMovie Película a añadir
     */
    public void addMovie(Movie newMovie) {
        if (mMovieList == null) {
            mMovieList = new ArrayList<>();
        }

        mMovieList.add(newMovie);
        notifyItemInserted(mMovieList.size() - 1);
    }

    /**
     * Elimina una película de la lista
     *
     * @param position Posición de la película en la lista
     */
    public void removeMovie(int position) {
        if (mMovieList != null) {
            mMovieList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Método para obtener la lista actual del adaptador
     *
     * @return Lista de películas
     */
    public List<Movie> getMovieList() {
        return mMovieList;
    }

    /**
     * Método para inicializar la lista de películas
     *
     * @param movieList Lista inicial de películas
     */
    public void setMovieList(List<Movie> movieList) {
        formatDate(movieList);
        this.mMovieList = movieList;
        notifyDataSetChanged();
    }

    /**
     * Método para formateo de la fecha
     *
     * @param list Lista de películas
     */
    private void formatDate(List<Movie> list) {
        if (list != null) {
            Date date;
            for (Movie movie :
                    list) {
                try {
                    if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
                        date = format1.parse(movie.getReleaseDate());
                        if (date != null) {
                            movie.setReleaseDate(format2.format(date));
                        }
                    }
                } catch (ParseException ex) {
                    // Se mantiene el formato de fecha original
                }

            }
        }
    }

    class MovieHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        private ItemMovieBinding mViewBinding;

        public MovieHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.mViewBinding = binding;
            this.mViewBinding.cardMovie.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            mViewBinding.setMovie(movie);
            if (!Tools.getSecureBaseUrl().isEmpty() &&
                    movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                loadImage(Tools.getSecureBaseUrl() + movie.getPosterPath());
            } else if (!Tools.getBaseUrl().isEmpty() &&
                    movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                loadImage(Tools.getBaseUrl() + movie.getPosterPath());
            }
            mViewBinding.executePendingBindings();
        }

        private void loadImage(String url) {
            Glide.with(mViewBinding.imagePoster.getContext())
                    .load(url)
                    .centerInside()
                    .placeholder(R.drawable.ic_poster)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(mViewBinding.imagePoster);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(mMovieList.get(getAdapterPosition()));
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ItemLoadMoreBinding mViewBinding;

        public LoadingViewHolder(ItemLoadMoreBinding binding) {
            super(binding.getRoot());
            this.mViewBinding = binding;
        }
    }
}