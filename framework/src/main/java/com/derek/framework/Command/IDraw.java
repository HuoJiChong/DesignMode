package com.derek.framework.Command;

import android.graphics.Canvas;

public interface IDraw {
    void draw(Canvas canvas);
    void undo();
}
