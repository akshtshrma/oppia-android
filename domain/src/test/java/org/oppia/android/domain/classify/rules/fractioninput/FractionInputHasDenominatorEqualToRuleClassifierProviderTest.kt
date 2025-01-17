package org.oppia.android.domain.classify.rules.fractioninput

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.BindsInstance
import dagger.Component
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oppia.android.domain.classify.ClassificationContext
import org.oppia.android.testing.InteractionObjectTestBuilder
import org.oppia.android.domain.classify.RuleClassifier
import org.oppia.android.testing.assertThrows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import javax.inject.Inject
import javax.inject.Singleton

/** Tests for [FractionInputHasDenominatorEqualToRuleClassifierProvider]. */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(manifest = Config.NONE)
class FractionInputHasDenominatorEqualToRuleClassifierProviderTest {

  private val WHOLE_NUMBER_VALUE_TEST_123 =
    InteractionObjectTestBuilder.createWholeNumber(
      isNegative = false,
      value = 123
    )
  private val FRACTION_VALUE_TEST_1_OVER_2 =
    InteractionObjectTestBuilder.createFraction(
      isNegative = false,
      numerator = 1,
      denominator = 2
    )
  private val FRACTION_VALUE_TEST_2_OVER_4 =
    InteractionObjectTestBuilder.createFraction(
      isNegative = false,
      numerator = 2,
      denominator = 4
    )
  private val NON_NEGATIVE_VALUE_TEST_1 =
    InteractionObjectTestBuilder.createNonNegativeInt(
      value = 1
    )
  private val NON_NEGATIVE_VALUE_TEST_2 =
    InteractionObjectTestBuilder.createNonNegativeInt(
      value = 2
    )

  @Inject
  internal lateinit var fractionInputHasDenominatorEqualToRuleClassifierProvider:
    FractionInputHasDenominatorEqualToRuleClassifierProvider

  private val denominatorIsEqualClassifierProvider: RuleClassifier by lazy {
    fractionInputHasDenominatorEqualToRuleClassifierProvider.createRuleClassifier()
  }

  @Before
  fun setUp() {
    setUpTestApplicationComponent()
  }

  @Test
  fun testDenominatorEquals_wholeNumber123Answer_withInt1Input_bothValuesMatch() {
    val inputs = mapOf("x" to NON_NEGATIVE_VALUE_TEST_1)

    val matches =
      denominatorIsEqualClassifierProvider.matches(
        answer = WHOLE_NUMBER_VALUE_TEST_123,
        inputs = inputs,
        classificationContext = ClassificationContext()
      )

    // This should match because whole numbers have a denominator of 1 by default
    assertThat(matches).isTrue()
  }

  @Test
  fun testDenominatorEquals_wholeNumber123Answer_withInt2Input_bothValuesDoNotMatch() {
    val inputs = mapOf("x" to NON_NEGATIVE_VALUE_TEST_2)

    val matches =
      denominatorIsEqualClassifierProvider.matches(
        answer = WHOLE_NUMBER_VALUE_TEST_123,
        inputs = inputs,
        classificationContext = ClassificationContext()
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testDenominatorEquals_fraction2Over4Answer_withInt1Input_bothValuesDoNotMatch() {
    val inputs = mapOf("x" to NON_NEGATIVE_VALUE_TEST_1)

    val matches =
      denominatorIsEqualClassifierProvider.matches(
        answer = FRACTION_VALUE_TEST_2_OVER_4,
        inputs = inputs,
        classificationContext = ClassificationContext()
      )

    assertThat(matches).isFalse()
  }

  @Test
  fun testDenominatorEquals_fraction1Over2Answer_withInt2Input_bothValuesMatch() {
    val inputs = mapOf("x" to NON_NEGATIVE_VALUE_TEST_2)

    val matches =
      denominatorIsEqualClassifierProvider.matches(
        answer = FRACTION_VALUE_TEST_1_OVER_2,
        inputs = inputs,
        classificationContext = ClassificationContext()
      )

    assertThat(matches).isTrue()
  }

  @Test
  fun testDenominatorEquals_nonNegativeInput_inputWithIncorrectType_throwsException() {
    val inputs = mapOf("x" to FRACTION_VALUE_TEST_2_OVER_4)

    val exception = assertThrows<IllegalStateException>() {
      denominatorIsEqualClassifierProvider.matches(
        answer = FRACTION_VALUE_TEST_2_OVER_4,
        inputs = inputs,
        classificationContext = ClassificationContext()
      )
    }

    assertThat(exception)
      .hasMessageThat()
      .contains(
        "Expected input value to be of type NON_NEGATIVE_INT not FRACTION"
      )
  }

  @Test
  fun testDenominatorEquals_missingInputF_throwsException() {
    val inputs = mapOf("y" to NON_NEGATIVE_VALUE_TEST_1)

    val exception = assertThrows<IllegalStateException>() {
      denominatorIsEqualClassifierProvider.matches(
        answer = FRACTION_VALUE_TEST_2_OVER_4,
        inputs = inputs,
        classificationContext = ClassificationContext()
      )
    }

    assertThat(exception)
      .hasMessageThat()
      .contains("Expected classifier inputs to contain parameter with name 'x' but had: [y]")
  }

  private fun setUpTestApplicationComponent() {
    DaggerFractionInputHasDenominatorEqualToRuleClassifierProviderTest_TestApplicationComponent
      .builder()
      .setApplication(ApplicationProvider.getApplicationContext()).build().inject(this)
  }

  // TODO(#89): Move this to a common test application component.
  @Singleton
  @Component(modules = [])
  interface TestApplicationComponent {
    @Component.Builder
    interface Builder {
      @BindsInstance
      fun setApplication(application: Application): Builder

      fun build(): TestApplicationComponent
    }

    fun inject(test: FractionInputHasDenominatorEqualToRuleClassifierProviderTest)
  }
}
