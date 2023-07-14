package de.hdmstuttgart.thelaendofadventure.ui

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import de.hdmstuttgart.the_laend_of_adventure.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@Suppress("TooManyFunctions")
class UserNameTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION"
        )

    @Test
    @Suppress("LongMethod")
    fun startTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(5801)

        val appCompatEditText = onView(
            allOf(
                withId(R.id.name_text_input),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_user_creation),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.name_text_input),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_user_creation),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(click())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.name_text_input),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_user_creation),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("Reihner"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.name_text_input),
                withText("Reihner"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_user_creation),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(pressImeActionButton())

        val materialTextView = onView(
            allOf(
                withId(R.id.user_creation_page_confirm_button),
                withText("ERSTELLEN"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_user_creation),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val shapeableImageView = onView(
            allOf(
                withId(R.id.mainPage_profileButton),
                withContentDescription("Profilebutton"),
                childAtPosition(
                    allOf(
                        withId(R.id.main_page_profile_button_container),
                        childAtPosition(
                            withId(R.id.frameLayout),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        shapeableImageView.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(5000)

        val editText = onView(
            allOf(
                withId(R.id.user_page_name_field),
                withText("Reihner"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        editText.check(matches(withText("Reihner")))

        val textView = onView(
            allOf(
                withId(R.id.user_page_experience_numeric),
                withText("0 / 100"),
                withParent(
                    allOf(
                        withId(R.id.user_page_level_bar),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("0 / 100")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>,
        position: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent) &&
                    view == parent.getChildAt(position)
            }
        }
    }
}
