package com.bluepixel.mood.ui.main;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bluepixel.mood.R;
import com.bluepixel.mood.automation.ScheduleManager;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.preferences.AppPreferences;
import com.bluepixel.mood.databinding.ActivityMainBinding;
import com.bluepixel.mood.mode.ModeEngine;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigationInsets();
        AppDatabase.getInstance(getApplicationContext());
        setupNavigation(savedInstanceState);

        ModeEngine.getInstance(this).restoreAfterBoot();
        new ScheduleManager(this).scheduleAllAsync();
    }

    private void setupBottomNavigationInsets() {
        View bottomNavigationCard = binding.cardBottomNavigation;

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationCard, (view, insets) -> {
            Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets systemGestures = insets.getInsets(WindowInsetsCompat.Type.systemGestures());

            int bottomInset = Math.max(navigationBars.bottom, systemGestures.bottom);
            int horizontalInset = Math.max(navigationBars.left, navigationBars.right);

            int appMargin = dpToPx(14);

            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            params.bottomMargin = appMargin + bottomInset;
            params.leftMargin = appMargin + navigationBars.left;
            params.rightMargin = appMargin + navigationBars.right;

            view.setLayoutParams(params);

            return insets;
        });

        ViewCompat.requestApplyInsets(bottomNavigationCard);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setupNavigation(@Nullable Bundle savedInstanceState) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainNavHost);

        if (navHostFragment == null) {
            throw new IllegalStateException("Main NavHostFragment was not found.");
        }

        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            boolean showBottomNavigation =
                    destinationId == R.id.homeFragment
                            || destinationId == R.id.modesFragment
                            || destinationId == R.id.historyFragment
                            || destinationId == R.id.settingsFragment;

            binding.cardBottomNavigation.setVisibility(
                    showBottomNavigation ? View.VISIBLE : View.GONE
            );
        });

        if (savedInstanceState == null) {
            openInitialDestination();
        }
    }

    private void openInitialDestination() {
        boolean onboardingCompleted =
                AppPreferences.getInstance(this).isOnboardingCompleted();

        if (!onboardingCompleted) {
            return;
        }

        NavOptions options = new NavOptions.Builder()
                .setPopUpTo(R.id.onboardingFragment, true)
                .setLaunchSingleTop(true)
                .build();

        navController.navigate(R.id.homeFragment, null, options);
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
