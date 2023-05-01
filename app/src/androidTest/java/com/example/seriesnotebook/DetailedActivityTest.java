package com.example.seriesnotebook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class DetailedActivityTest {
    @Rule
    public ActivityScenarioRule<DetailedActivity> rule =
            new ActivityScenarioRule<>(DetailedActivity.class);

    @Test
    public void checkOpening() {
        rule.getScenario();
        onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkContent() {
        rule.getScenario();
        onView(withId(R.id.tv_detail_movie_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.button_add_to_favorites)).check(matches(isDisplayed()));
        onView(withId(R.id.button_add_to_watchlist)).check(matches(isDisplayed()));

    }


    @Test
    public void checkDescription() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.rv_movie_list)).perform(click());
        onView(withId(R.id.button_show_description)).perform(click());
        onView(withId(R.id.tv_detail_movie_description)).check(matches(isDisplayed()));

    }

}