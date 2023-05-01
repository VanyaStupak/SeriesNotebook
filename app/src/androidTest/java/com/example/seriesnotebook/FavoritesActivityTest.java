package com.example.seriesnotebook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class FavoritesActivityTest {
    @Rule
    public ActivityScenarioRule<FavoritesActivity> rule =
            new ActivityScenarioRule<>(FavoritesActivity.class);

    @Test
    public void checkOpening() {
        rule.getScenario();
        onView(withId(R.id.rv_movie_list)).check(matches(isDisplayed()));
    }

    @Test
    public void checkContent() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.rv_movie_list)).perform(click());
        onView(withId(R.id.button_add_to_favorites)).perform(click());
        ActivityScenario activityScenario2 = ActivityScenario.launch(FavoritesActivity.class);
        onView(withId(R.id.rv_movie_list)).check(matches(isDisplayed()));
    }
}