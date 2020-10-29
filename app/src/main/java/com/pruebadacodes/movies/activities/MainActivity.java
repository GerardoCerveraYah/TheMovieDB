package com.pruebadacodes.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.pruebadacodes.movies.R;
import com.pruebadacodes.movies.databinding.ActivityMainBinding;
import com.pruebadacodes.movies.fragments.MoviesFragment;
import com.pruebadacodes.movies.interfaces.OnFragmentInteractionListener;
import com.pruebadacodes.movies.models.Movie;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private ActivityMainBinding mViewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
    }

    @Override
    public void onMessage(String message, String actionText, View.OnClickListener action) {
        if(action!=null){
            Snackbar.make(mViewBinding.parentCoordinator, message, Snackbar.LENGTH_LONG)
                    .setAction(actionText, action).show();
        } else{
            Snackbar.make(mViewBinding.parentCoordinator, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNavigationChanged(boolean showBack, String title) {
        if(showBack){
            mViewBinding.appBar.topAppBar.setNavigationIcon(R.drawable.ic_back);
            mViewBinding.appBar.topAppBar.setNavigationOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    }
            );
        } else{
            mViewBinding.appBar.topAppBar.setNavigationIcon(null);
            mViewBinding.appBar.topAppBar.setNavigationOnClickListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        onNavigationChanged(false, getString(R.string.title_fragment_movies));
        super.onBackPressed();
    }
}
