package com.yalin.materialdrawer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yalin.materialdrawer.interpolators.ExpoIn;
import com.yalin.materialdrawer.interpolators.QuintOut;
import com.yalin.materialdrawer.utils.Preferences;
import com.yalin.materialdrawer.weight.CircularSplashView;

public class MainActivity extends AppCompatActivity {

    boolean isMenuVisible = false;
    private ViewGroup menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferences.makeAppFullscreen(this, Color.TRANSPARENT);

        currentFragment = FragmentHome.getInstance(false);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commit();
        setupMenu(0);
    }

    int currentMenuIndex = 0;
    private Fragment currentFragment;

    public void setCurrentMenuIndex(int currentMenuIndex) {
        this.currentMenuIndex = currentMenuIndex;
    }

    public void showMenu() {
        isMenuVisible = true;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(menu,
                View.TRANSLATION_Y, menu.getHeight(), 0);
        translationY.setDuration(1000);
        translationY.setInterpolator(new QuintOut());
        translationY.setStartDelay(150);
        translationY.start();
        selectMenuItem(currentMenuIndex,
                ((TextView) menu.getChildAt(currentMenuIndex)
                        .findViewById(R.id.item_text)).getCurrentTextColor());
        ((MenuAnimation) currentFragment).animateTOMenu();
    }

    public void hideMenu() {
        isMenuVisible = false;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(menu,
                View.TRANSLATION_Y, menu.getHeight());
        translationY.setDuration(750);
        translationY.setInterpolator(new ExpoIn());
        translationY.start();
    }

    private void setupMenu(int position) {
        menu = (ViewGroup) findViewById(R.id.menu_container);

        int splash = R.drawable.ic_splash;
        int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);
        int buttonMenu = R.drawable.menu_btn;

        addMenuItem(menu, getString(R.string.fragment_name_home),
                splash, colorAccent, buttonMenu, 0);
        addMenuItem(menu, getString(R.string.fragment_name_wallpaper),
                splash, colorAccent, buttonMenu, 1);

        selectMenuItem(position, colorAccent);
        menu.setTranslationY(20000);
    }

    private void addMenuItem(ViewGroup menu, String text,
                             int drawableResource, int splashColor,
                             int menu_btn, int menuIndex) {
        ViewGroup item = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.menu_item, menu, false);
        ((TextView) item.findViewById(R.id.item_text)).setText(text);
        CircularSplashView ic = (CircularSplashView) item.findViewById(R.id.circle);
        ic.setSplash(BitmapFactory.decodeResource(getResources(), drawableResource));
        ic.setSplashColor(splashColor);
        item.setOnClickListener(getMenuItemCLick(menuIndex, splashColor));
        menu.addView(item, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setBackground(ContextCompat.getDrawable(this, menu_btn));

    }

    private void selectMenuItem(int menuIndex, int color) {
        for (int i = 0; i < menu.getChildCount(); i++) {
            View menuItem = menu.getChildAt(i);
            if (i == menuIndex) {
                select(menuItem, color);
            } else {
                unSelect(menuItem);
            }
        }
        currentMenuIndex = menuIndex;
    }

    private void unSelect(View menuItem) {
        final View circle = menuItem.findViewById(R.id.circle);
        circle.animate().scaleX(0).scaleY(0).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {
                circle.setVisibility(View.INVISIBLE);
            }
        }).start();
        fadeColoTo(Color.BLACK, (TextView) menuItem.findViewById(R.id.item_text));
    }

    private void select(View menuItem, int color) {
        final CircularSplashView circle = (CircularSplashView) menuItem.findViewById(R.id.circle);
        circle.setScaleX(1f);
        circle.setScaleY(1f);
        circle.setVisibility(View.VISIBLE);
        circle.introAnimate();
        fadeColoTo(color, (TextView) menuItem.findViewById(R.id.item_text));
    }

    private void fadeColoTo(int newColor, TextView view) {
        ObjectAnimator color = ObjectAnimator.ofObject(view, "TextColor",
                new ArgbEvaluator(), view.getCurrentTextColor(), newColor);
        color.setDuration(200);
        color.start();
    }

    private View.OnClickListener getMenuItemCLick(final int menuIndex, final int color) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuIndex == currentMenuIndex) {
                    onBackPressed();
                } else if (menuIndex == 0 && !(currentFragment instanceof FragmentHome)) {
                    ((MenuAnimation) currentFragment).exitFromMenu();
                    FragmentHome fragmentHome = FragmentHome.getInstance(true);
                    goToFragment(fragmentHome);
                    hideMenu();
                    selectMenuItem(menuIndex, color);
                } else if (menuIndex == 1 && !(currentFragment instanceof FragmentList)) {
                    ((MenuAnimation) currentFragment).exitFromMenu();
                    FragmentList fragmentList = FragmentList.getInstance(true);
                    goToFragment(fragmentList);
                    hideMenu();
                    selectMenuItem(menuIndex, color);
                }
            }
        };
    }

    public void goToFragment(final Fragment newFragment) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container, newFragment).commit();
        final Fragment removeFragment = currentFragment;
        currentFragment = newFragment;
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().remove(removeFragment).commit();
            }
        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!isMenuVisible) {
                showMenu();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (isMenuVisible) {
            hideMenu();
            ((MenuAnimation) currentFragment).revertFromMenu();
        } else {
            finish();
        }
    }
}
