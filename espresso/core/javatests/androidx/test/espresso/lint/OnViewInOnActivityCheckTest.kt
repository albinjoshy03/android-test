/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.test.espresso.lint

import com.android.tools.lint.detector.api.JavaContext
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiSubstitutor
import com.intellij.psi.PsiType
import com.intellij.psi.util.MethodSignature
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.getParentOfType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** Unit tests of OnViewInOnActivityExternalCheck. */
@RunWith(JUnit4::class)
class OnViewInOnActivityCheckTest {
  private val mockJavaContext = mock<JavaContext>()
  private val handler = OnViewInOnActivityCheck()
  private val mockOnViewCall = mock<UCallExpression>()
  private val mockOnViewMethod = mock<PsiMethod>()
  private val mockOnActivityCall = mock<UCallExpression>()
  private val mockOnActivityMethod = mock<PsiMethod>()

  @Before
  fun setUp() {
    // Constructs a valid input for visitMethodCall.
    // Should reach UElement.searchCall().
    val psiType =
      mock<PsiType> {
        on { getCanonicalText(false) } doReturn "org.hamcrest.Matcher<android.view.View>"
      }
    val psiClass =
      mock<PsiClass> { on { qualifiedName } doReturn "androidx.test.espresso.Espresso" }
    val signatureObject =
      mock<MethodSignature> {
        on { parameterTypes } doReturn arrayOf(psiType)
        on { name } doReturn "onView"
      }
    whenever(mockOnViewMethod.containingClass).doReturn(psiClass)
    whenever(mockOnViewMethod.getSignature(PsiSubstitutor.EMPTY)).doReturn(signatureObject)
    whenever(mockOnViewCall.getParentOfType<UCallExpression>()).doReturn(mockOnActivityCall)
    whenever(mockOnActivityMethod.getSignature(PsiSubstitutor.EMPTY)).doReturn(signatureObject)
    whenever(mockOnActivityCall.resolve()).doReturn(mockOnActivityMethod)
  }

  @Test
  fun visitCallExpression_onActivityFound() {
    // Should reach mockOnActivityMethod.getMethodSignature.
    handler.visitMethodCall(mockJavaContext, mockOnViewCall, mockOnViewMethod)

    // PsiMethod.getMethodSignature() is a final method that cannot be verified. Checks the
    // reachability of PsiMethod.containingClass inside PsiMethod.getMethodSignature().
    verify(mockOnActivityMethod).containingClass
  }

  @Test
  fun visitCallExpression_onActivityResolveFailed() {
    // If onActivity call is not found, it should not reach mockOnActivityMethod.getMethodSignature.
    whenever(mockOnActivityCall.resolve()).doReturn(null)
    handler.visitMethodCall(mockJavaContext, mockOnViewCall, mockOnViewMethod)

    verify(mockOnActivityCall).resolve()
    verify(mockOnActivityMethod, never()).containingClass
  }

  @Test
  fun visitCallExpression_onActivityNotFound() {
    // If onActivity call is not found, it should not reach mockOnActivityCall.resolve().
    whenever(mockOnViewCall.getParentOfType<UCallExpression>()).doReturn(null)
    handler.visitMethodCall(mockJavaContext, mockOnViewCall, mockOnViewMethod)

    verify(mockOnViewCall).getParentOfType<UCallExpression>()
    verify(mockOnViewCall, never()).resolve()
  }

  @Test
  fun visitCallExpression_onViewNotFound() {
    // If onView is not found, it should not reach
    // mockOnViewCall.getParentOfType<UCallExpression>().
    whenever(mockOnViewMethod.containingClass).doReturn(null)
    handler.visitMethodCall(mockJavaContext, mockOnViewCall, mockOnViewMethod)

    verify(mockOnViewMethod).containingClass
    verify(mockOnViewCall, never()).getParentOfType<UCallExpression>()
  }
}