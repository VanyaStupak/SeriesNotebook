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
public class RegisterActivityTest {
    @Rule
    public ActivityScenarioRule<RegisterActivity> rule =
            new ActivityScenarioRule<>(RegisterActivity.class);


    @Test
    public void CheckActivity() throws Throwable {
        rule.getScenario();
        onView(withId(R.id.register)).check(matches(isDisplayed()));
    }

    Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(MainActivity.class.getName(), null, false);

    @Test
    public void CheckMainAfterRegister() throws Throwable {
        rule.getScenario();
        onView(withId(R.id.edit_text_register_email))
                .perform(click(), typeText("stupakivann@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_register_password)).perform(click(), typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.button_create_account)).perform(click());

        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(secondActivity);
    }


}