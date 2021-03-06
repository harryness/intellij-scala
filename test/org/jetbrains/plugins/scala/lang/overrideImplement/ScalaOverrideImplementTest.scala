package org.jetbrains.plugins.scala.lang.overrideImplement

import org.jetbrains.plugins.scala.util.TestUtils
import org.jetbrains.plugins.scala.overrideImplement.ScalaOIUtil
import org.jetbrains.plugins.scala.base.ScalaLightPlatformCodeInsightTestCaseAdapter

/**
 * @author Alefas
 * @since 14.05.12
 */
class ScalaOverrideImplementTest extends ScalaLightPlatformCodeInsightTestCaseAdapter {
  protected def rootFilePath(): String = TestUtils.getTestDataPath + "/override/"

  def runTest(methodName: String, fileText: String, expectedText: String, isImplement: Boolean,
              needsInferType: Boolean = true) {
    configureFromFileTextAdapter("dummy.scala", fileText)
    ScalaOIUtil.invokeOverrideImplement(getProjectAdapter, getEditorAdapter, getFileAdapter, isImplement, methodName, needsInferType)
    checkResultByText(expectedText)
  }

  def testFoo() {
    val fileText =
      """
        |package test
        |
        |class Foo extends b {
        |  <caret>
        |}
        |abstract class b {
        |  def foo(x: b): b
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Foo extends b {
        |  def foo(x: b): b = <selection>???</selection>
        |}
        |abstract class b {
        |  def foo(x: b): b
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testEmptyLinePos() {
    val fileText =
      """
        |package test
        |class Empty extends b {
        |  def foo(): Int = 3
        |
        |
        |  <caret>
        |
        |
        |}
        |abstract class b {
        |  def too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |class Empty extends b {
        |  def foo(): Int = 3
        |
        |  def too: b = <selection>???</selection>
        |}
        |abstract class b {
        |  def too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "too"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testNewLineBetweenMethods() {
    val fileText =
      """
        |package test
        |
        |class MethodsNewLine extends b {
        |  def foo(): Int = 3<caret>
        |}
        |abstract class b {
        |  def too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class MethodsNewLine extends b {
        |  def foo(): Int = 3
        |
        |  def too: b = <selection>???</selection>
        |}
        |abstract class b {
        |  def too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "too"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testNewLineUpper() {
    val fileText =
      """
        |package test
        |
        |class UpperNewLine extends b {
        |  <caret>
        |  def foo(): Int = 3
        |}
        |abstract class b {
        |  def too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class UpperNewLine extends b {
        |
        |  def too: b = <selection>???</selection>
        |
        |  def foo(): Int = 3
        |}
        |abstract class b {
        |  def too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "too"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testOverrideFunction() {
    val fileText =
      """
        |package test
        |
        |class A {
        |  def foo(): A = null
        |}
        |class FunctionOverride extends A {
        |  val t = foo()
        |
        |
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class A {
        |  def foo(): A = null
        |}
        |class FunctionOverride extends A {
        |  val t = foo()
        |
        |  override def foo(): A = <selection>super.foo()</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testOverrideTypeAlias() {
    val fileText =
      """
        |package Y
        |class Aa {
        |  type K = Int
        |}
        |class TypeAlias extends Aa {
        |  val t = foo()
        |  <caret>
        |  def y(): Int = 3
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package Y
        |class Aa {
        |  type K = Int
        |}
        |class TypeAlias extends Aa {
        |  val t = foo()
        |
        |  override type K = <selection>Int</selection>
        |
        |  def y(): Int = 3
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "K"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testOverrideValue() {
    val fileText =
      """
        |package test
        |
        |class A {
        |  val foo: A = new A
        |}
        |class OverrideValue extends A {
        |  val t = foo()
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class A {
        |  val foo: A = new A
        |}
        |class OverrideValue extends A {
        |  val t = foo()
        |  override val foo: A = <selection>_</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testOverrideVar() {
    val fileText =
      """
        |package test
        |
        |class A {
        |  var foo: A = new A
        |}
        |class VarOverride extends A {
        |  val t = foo()
        |  <caret>
        |  def y(): Int = 3
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class A {
        |  var foo: A = new A
        |}
        |class VarOverride extends A {
        |  val t = foo()
        |
        |  override var foo: A = <selection>_</selection>
        |
        |  def y(): Int = 3
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testImplementFromSelfType() {
    val fileText =
      """
        |package test
        |
        |trait A {
        |  def foo: Int
        |}
        |trait B {
        |  self: A =>
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |trait A {
        |  def foo: Int
        |}
        |trait B {
        |  self: A =>
        |  def foo: Int = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testOverrideFromSelfType() {
    val fileText =
      """
        |package test
        |
        |trait A {
        |  def foo: Int = 1
        |}
        |trait B {
        |  self: A =>
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |trait A {
        |  def foo: Int = 1
        |}
        |trait B {
        |  self: A =>
        |  override def foo: Int = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testTypeAlias() {
    val fileText =
      """
        |class ImplementTypeAlias extends b {
        |  <caret>
        |}
        |abstract class b {
        |  type L
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class ImplementTypeAlias extends b {
        |  type L = <selection>this.type</selection>
        |}
        |abstract class b {
        |  type L
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "L"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testVal() {
    val fileText =
      """
        |package test
        |
        |class Val extends b {
        |  <caret>
        |}
        |abstract class b {
        |  val too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Val extends b {
        |  val too: b = <selection>_</selection>
        |}
        |abstract class b {
        |  val too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "too"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testVar() {
    val fileText =
      """
        |package test
        |
        |class Var extends b {
        |  <caret>
        |}
        |abstract class b {
        |  var too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Var extends b {
        |  var too: b = <selection>_</selection>
        |}
        |abstract class b {
        |  var too: b
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "too"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testList() {
    val fileText =
      """
        |class ExtendsList extends java.util.List {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |import java.util
        |
        |class ExtendsList extends java.util.List {
        |  def removeAll(c: util.Collection[_]): Boolean = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "removeAll"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testClassTypeParam() {
    val fileText =
      """
        |class A[T] {
        |  def foo: T = new T
        |}
        |
        |class ClassTypeParam extends A[Int] {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A[T] {
        |  def foo: T = new T
        |}
        |
        |class ClassTypeParam extends A[Int] {
        |  override def foo: Int = <selection>super.foo</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testHardSubstituting() {
    val fileText =
      """
        |class A[T] {
        |  def foo(x: (T) => T, y: (T, Int) => T): Double = 1.0
        |}
        |
        |class Substituting extends A[Float] {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A[T] {
        |  def foo(x: (T) => T, y: (T, Int) => T): Double = 1.0
        |}
        |
        |class Substituting extends A[Float] {
        |  override def foo(x: (Float) => Float, y: (Float, Int) => Float): Double = <selection>super.foo(x, y)</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSimpleTypeParam() {
    val fileText =
      """
        |abstract class A {
        |  def foo[T](x: T): T
        |}
        |class SimpleTypeParam extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |abstract class A {
        |  def foo[T](x: T): T
        |}
        |class SimpleTypeParam extends A {
        |  def foo[T](x: T): T = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL1997() {
    val fileText =
      """
        |package test
        |
        |trait Foo {
        |  def foo(a: Any*): Any
        |}
        |
        |trait Sub extends Foo {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |trait Foo {
        |  def foo(a: Any*): Any
        |}
        |
        |trait Sub extends Foo {
        |  def foo(a: Any*): Any = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL1999() {
    val fileText =
      """
        |package test
        |
        |trait Parent {
        |  def m(p: T forSome {type T <: Number})
        |}
        |
        |class Child extends Parent {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |trait Parent {
        |  def m(p: T forSome {type T <: Number})
        |}
        |
        |class Child extends Parent {
        |  def m(p: (T) forSome {type T <: Number}): Unit = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "m"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL2540() {
    val fileText =
      """
        |class A {
        |  def foo(x_ : Int) = 1
        |}
        |
        |class B extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A {
        |  def foo(x_ : Int) = 1
        |}
        |
        |class B extends A {
        |  override def foo(x_ : Int): Int = <selection>super.foo(x_)</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL2010() {
    val fileText =
      """
        |package test
        |
        |class Parent {
        |  def doSmth(smth: => String) {}
        |}
        |
        |class Child extends Parent {
        | <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Parent {
        |  def doSmth(smth: => String) {}
        |}
        |
        |class Child extends Parent {
        |  override def doSmth(smth: => String): Unit = <selection>super.doSmth(smth)</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "doSmth"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL2052A() {
    val fileText =
      """
        |class A {
        |  type ID[X] = X
        |  def foo(in: ID[String]): ID[Int] = null
        |}
        |
        |class B extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A {
        |  type ID[X] = X
        |  def foo(in: ID[String]): ID[Int] = null
        |}
        |
        |class B extends A {
        |  override def foo(in: B#ID[String]): B#ID[Int] = <selection>super.foo(in)</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL2052B() {
    val fileText =
      """
        |class A {
        |  type ID[X] = X
        |  val foo: ID[Int] = null
        |}
        |
        |class B extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A {
        |  type ID[X] = X
        |  val foo: ID[Int] = null
        |}
        |
        |class B extends A {
        |  override val foo: B#ID[Int] = <selection>_</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL2052C() {
    val fileText =
      """
        |class A {
        |  type F = (Int => String)
        |  def foo(f: F): Any = null
        |}
        |
        |object B extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A {
        |  type F = (Int => String)
        |  def foo(f: F): Any = null
        |}
        |
        |object B extends A {
        |  override def foo(f: B.F): Any = <selection>super.foo(f)</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL3808() {
    val fileText =
      """
        |trait TC[_]
        |
        |class A {
        |  def foo[M[X], N[X[_]]: TC]: String = ""
        |}
        |
        |object B extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |trait TC[_]
        |
        |class A {
        |  def foo[M[X], N[X[_]]: TC]: String = ""
        |}
        |
        |object B extends A {
        |  override def foo[M[X], N[X[_]] : TC]: String = <selection>super.foo</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testSCL3305() {
    val fileText =
      """
        |package test
        |
        |object A {
        |  object Nested {
        |    class Nested2
        |  }
        |}
        |
        |abstract class B {
        |  def foo(v: A.Nested.Nested2)
        |}
        |
        |class C extends B {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |import test.A.Nested.Nested2
        |
        |object A {
        |  object Nested {
        |    class Nested2
        |  }
        |}
        |
        |abstract class B {
        |  def foo(v: A.Nested.Nested2)
        |}
        |
        |class C extends B {
        |  def foo(v: Nested2): Unit = <selection>???</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testUnitReturn() {
    val fileText =
      """
        |package test
        |
        |class Foo extends b {
        |  <caret>
        |}
        |abstract class b {
        |  def foo(x: b): Unit
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Foo extends b {
        |  def foo(x: b): Unit = <selection>???</selection>
        |}
        |abstract class b {
        |  def foo(x: b): Unit
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = true
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testUnitInferredReturn() {
    val fileText =
      """
        |package test
        |
        |class Foo extends b {
        |  <caret>
        |}
        |abstract class b {
        |  def foo(x: b) = ()
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Foo extends b {
        |  override def foo(x: b): Unit = <selection>super.foo(x)</selection>
        |}
        |abstract class b {
        |  def foo(x: b) = ()
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testInferredReturn() {
    val fileText =
      """
        |package test
        |
        |class Foo extends b {
        |  <caret>
        |}
        |abstract class b {
        |  def foo(x: b) = 1
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |package test
        |
        |class Foo extends b {
        |  override def foo(x: b): Int = <selection>super.foo(x)</selection>
        |}
        |abstract class b {
        |  def foo(x: b) = 1
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = true
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }

  def testNoExplicitReturn() {
    val fileText =
      """
        |class A {
        |  def foo(x : Int): Int = 1
        |}
        |
        |class B extends A {
        |  <caret>
        |}
      """.replace("\r", "").stripMargin.trim
    val expectedText =
      """
        |class A {
        |  def foo(x : Int): Int = 1
        |}
        |
        |class B extends A {
        |  override def foo(x: Int) = <selection>super.foo(x)</selection>
        |}
      """.replace("\r", "").stripMargin.trim
    val methodName: String = "foo"
    val isImplement = false
    val needsInferType = false
    runTest(methodName, fileText, expectedText, isImplement, needsInferType)
  }
}
