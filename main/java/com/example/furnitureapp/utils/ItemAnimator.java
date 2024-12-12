package com.example.furnitureapp.utils;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.RecyclerView;
import com.example.furnitureapp.R;

public class ItemAnimator {
    public static void animateRecyclerView(Context context, RecyclerView recyclerView) {
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.scheduleLayoutAnimation();
    }
} 