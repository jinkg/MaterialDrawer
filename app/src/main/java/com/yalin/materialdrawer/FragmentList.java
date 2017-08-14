package com.yalin.materialdrawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.yalin.materialdrawer.utils.TransitionHelperList;
import com.yalin.materialdrawer.weight.MaterialMenuDrawable;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class FragmentList extends Fragment implements MenuAnimation {
    public static final int TRANSFORM_DURATION = 900;
    private static final String INTRO_ANIMATE_KEY = "intro_animate";

    private View root;
    private ImageView menu;

    private RecyclerView recycler;
    private MaterialMenuDrawable menuIcon;

    public static FragmentList getInstance(boolean introAnimate) {
        FragmentList fragmentList = new FragmentList();
        Bundle args = new Bundle();
        args.putBoolean(INTRO_ANIMATE_KEY, introAnimate);
        fragmentList.setArguments(args);
        return fragmentList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_list, container, false);
        recycler = (RecyclerView) root.findViewById(R.id.ma_recyclerView);

        setupMenuButton();

        introAnimate();

        ((MainActivity) getActivity()).setCurrentMenuIndex(1);

        return root;
    }

    private void introAnimate() {
        boolean introAnimate = getArguments().getBoolean(INTRO_ANIMATE_KEY, false);
        if (introAnimate) {
            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    TransitionHelperList.startIntroAnim(root, showShadowListener);
                    showShadow();
                }
            });
        }
    }

    private void setupMenuButton() {
        menu = (ImageView) root.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((MainActivity) getActivity()).isMenuVisible) {
                    ((MainActivity) getActivity()).showMenu();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });
        menuIcon = new MaterialMenuDrawable(getActivity(), Color.WHITE,
                MaterialMenuDrawable.Stroke.THIN, TRANSFORM_DURATION);
        menu.setImageDrawable(menuIcon);
    }

    private void hideShadow() {
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.GONE);
    }

    private void showShadow() {
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.VISIBLE);

        ObjectAnimator.ofFloat(actionbarShadow, View.ALPHA, 0, 0.8f).setDuration(400).start();
    }


    @Override
    public void animateTOMenu() {
        TransitionHelperList.animateToMenuState(root, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        menuIcon.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        showShadow();
    }

    @Override
    public void revertFromMenu() {
        TransitionHelperList.startRevertFromMenu(root, showShadowListener);
        menuIcon.animateIconState(MaterialMenuDrawable.IconState.BURGER);
    }

    @Override
    public void exitFromMenu() {
        TransitionHelperList.animateMenuOut(root);
    }


    AnimatorListenerAdapter showShadowListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            hideShadow();
        }
    };

}
