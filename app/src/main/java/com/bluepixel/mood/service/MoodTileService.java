package com.bluepixel.mood.service;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.bluepixel.mood.core.AppExecutors;
import com.bluepixel.mood.data.database.AppDatabase;
import com.bluepixel.mood.data.database.entity.ModeEntity;
import com.bluepixel.mood.mode.ActiveMode;
import com.bluepixel.mood.mode.ModeEngine;
import com.bluepixel.mood.model.ActivationSource;
import com.bluepixel.mood.model.ModeEndReason;
import com.bluepixel.mood.model.ModeVisualType;

public class MoodTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        refreshTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        ModeEngine engine = ModeEngine.getInstance(this);

        if (engine.getActiveMode() != null) {
            engine.deactivate(ModeEndReason.USER_STOPPED);
            refreshTile();
            return;
        }

        AppExecutors.io().execute(() -> {
            ModeEntity mode = AppDatabase
                    .getInstance(this)
                    .modeDao()
                    .getFirstFavoriteBlocking();

            if (mode == null) {
                mode = AppDatabase
                        .getInstance(this)
                        .modeDao()
                        .getFirstByVisualTypeBlocking(
                                ModeVisualType.SLEEP
                        );
            }

            if (mode != null) {
                ModeEntity finalMode = mode;
                ModeEngine.getInstance(this).activate(
                        finalMode,
                        ActivationSource.TILE,
                        new ModeEngine.Callback() {
                            @Override
                            public void onSuccess(
                                    ActiveMode activeMode
                            ) {
                                refreshTile();
                            }

                            @Override
                            public void onError(
                                    String message,
                                    Throwable throwable
                            ) {
                                refreshTile();
                            }
                        }
                );
            }
        });
    }

    private void refreshTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        ActiveMode activeMode =
                ModeEngine.getInstance(this).getActiveMode();

        if (activeMode == null) {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel("مود سریع");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                tile.setSubtitle("لمس برای اجرا");
            }
        } else {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(activeMode.modeName);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                tile.setSubtitle("لمس برای پایان");
            }
        }

        tile.updateTile();
    }
}
