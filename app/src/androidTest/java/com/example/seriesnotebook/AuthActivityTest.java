package com.example.seriesnotebook;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;

@RunWith(AndroidJUnit4.class)
public class AuthActivityTest {
    @Rule
    public ActivityScenarioRule<AuthActivity> rule =
            new ActivityScenarioRule<>(AuthActivity.class);


    Instrumentation.ActivityMonitor monitor1 = getInstrumentation()
            .addMonitor(MainActivity.class.getName(), null, false);

    Instrumentation.ActivityMonitor monitor2 = getInstrumentation()
            .addMonitor(RegisterActivity.class.getName(), null, false);

    @Test
    public void CheckActivity() {
        rule.getScenario();
        onView(withId(R.id.auth))
                .check(matches(isDisplayed()));
    }


    @Test
    public void CheckMainAfterAuth() {
        rule.getScenario();
        onView(withId(R.id.edit_text_sign_in_email))
                .perform(click(), typeText("stupakvana2@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_sign_in_password))
                .perform(click(), typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.button_sign_in))
                .perform(click());

        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor1, 5000);
        assertNotNull(secondActivity);
    }


}