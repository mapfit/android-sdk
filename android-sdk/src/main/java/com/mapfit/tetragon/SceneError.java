package com.mapfit.tetragon;

import android.support.annotation.Keep;

import com.mapfit.android.MapController.Error;

@Keep
public class SceneError {

    private SceneUpdate sceneUpdate;
    private Error error;

    private SceneError(String sceneUpdatePath, String sceneUpdateValue, int error) {
        this.sceneUpdate = new SceneUpdate(sceneUpdatePath, sceneUpdateValue);
        this.error = Error.values()[error];
    }

    public SceneUpdate getSceneUpdate() {
        return this.sceneUpdate;
    }

    public Error getError() {
        return this.error;
    }
}