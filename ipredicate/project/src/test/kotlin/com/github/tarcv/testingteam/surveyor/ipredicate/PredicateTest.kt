/*
 Copyright (C) 2024 TarCV

 This file is part of UI Surveyor.
 UI Surveyor is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.

 This file is based on test programs of GNUstep that are covered under the following terms (Copies of referenced
 files can be found in ipredicate/licenses/gnustepbase subdirectory in this repository):
   GNUstep tools, test programs, and other files are covered under the GNU Public License. This means if you
   make changes to these programs, you cannot charge a fee, other than distribution fees, for others to use the
   program. You should read the COPYING file for more information.
*/
package com.github.tarcv.testingteam.surveyor.ipredicate

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun assertTrue(actual: Boolean, message: NSString) =
  assertTrue(actual, message.toString())

private fun <T> assertEquals(expected: T, actual: T, message: NSString) {
  assertEquals(expected, actual, message.toString())
}

@Suppress("JoinDeclarationAndAssignment")
class PredicateTest {
  @Test
  fun testKVC() {
    assertEquals("A Title".toNSString(), (dict["title".toNSString()] as NSString), "com.github.tarcv.testingteam.surveyor.ipredicate.valueForKeyPath: with string".toNSString())
    assertEquals("A Title".toNSString(), (dict.valueForKeyPath("title".toNSString()) as NSString), "com.github.tarcv.testingteam.surveyor.ipredicate.valueForKeyPath: with string".toNSString())
    assertEquals("John".toNSString(), (dict.valueForKeyPath("Record1.Name".toNSString()) as NSString), "com.github.tarcv.testingteam.surveyor.ipredicate.valueForKeyPath: with string".toNSString())
    assertEquals(30, dict.valueForKeyPath("Record2.Age".toNSString()) as Int, "com.github.tarcv.testingteam.surveyor.ipredicate.valueForKeyPath: with int".toNSString())
  }

  @Test
  fun testContains() {
    var p: NSPredicate

    p = NSPredicate.predicateWithFormat("%@ CONTAINS %@".toNSString(), "AABBBAA".toNSString(), "BBB".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%@ CONTAINS %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%@ IN %@".toNSString(), "BBB".toNSString(), "AABBBAA".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%@ IN %%@".toNSString())
  }

  @Test
  fun testString() {
    var p: NSPredicate

    p = NSPredicate.predicateWithFormat("%K == %@".toNSString(), "Record1.Name".toNSString(), "John".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%K == %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K MATCHES[c] %@".toNSString(), "Record1.Name".toNSString(), "john".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%K MATCHES[c] %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K BEGINSWITH %@".toNSString(), "Record1.Name".toNSString(), "Jo".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%K BEGINSWITH %%@".toNSString())

    p = NSPredicate.predicateWithFormat("(%K == %@) AND (%K == %@)".toNSString(), "Record1.Name".toNSString(), "John".toNSString(), "Record2.Name".toNSString(), "Mary".toNSString())
    assertTrue(p.evaluateWithObject(dict), "(%%K == %%@);AND (%%K == %%@)".toNSString())

    val strings = mutableListOf("a".toNSString(), "aa".toNSString(), "aaa".toNSString(), "aaaa".toNSString())
    val expect = listOf("aaa".toNSString(), "aaaa".toNSString())
    p = NSPredicate.predicateWithFormat("self beginswith 'aaa'".toNSString())
    strings.filterUsingPredicate(p)
    assertEquals(expect, strings, "filter using BEGINSWITH".toNSString())
  }

  @Test
  fun testInteger() {
    var p: NSPredicate

    p = NSPredicate.predicateWithFormat("%K == %d".toNSString(), "Record1.Age".toNSString(), 34)
    assertTrue(p.evaluateWithObject(dict), "%%K == %%d".toNSString())

    p = NSPredicate.predicateWithFormat("%K = %@".toNSString(), "Record1.Age".toNSString(), 34)
    assertTrue(p.evaluateWithObject(dict), "%%K = %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K == %@".toNSString(), "Record1.Age".toNSString(), 34)
    assertTrue(p.evaluateWithObject(dict), "%%K == %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K < %d".toNSString(), "Record1.Age".toNSString(), 40)
    assertTrue(p.evaluateWithObject(dict), "%%K < %%d".toNSString())

    p = NSPredicate.predicateWithFormat("%K < %@".toNSString(), "Record1.Age".toNSString(), 40)
    assertTrue(p.evaluateWithObject(dict), "%%K < %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K <= %@".toNSString(), "Record1.Age".toNSString(), 40)
    assertTrue(p.evaluateWithObject(dict), "%%K <= %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K <= %@".toNSString(), "Record1.Age".toNSString(), 34)
    assertTrue(p.evaluateWithObject(dict), "%%K <= %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K > %@".toNSString(), "Record1.Age".toNSString(), 20)
    assertTrue(p.evaluateWithObject(dict), "%%K > %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K >= %@".toNSString(), "Record1.Age".toNSString(), 34)
    assertTrue(p.evaluateWithObject(dict), "%%K >= %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K >= %@".toNSString(), "Record1.Age".toNSString(), 20)
    assertTrue(p.evaluateWithObject(dict), "%%K >= %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K != %@".toNSString(), "Record1.Age".toNSString(), 20)
    assertTrue(p.evaluateWithObject(dict), "%%K = %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K <> %@".toNSString(), "Record1.Age".toNSString(), 20)
    assertTrue(p.evaluateWithObject(dict), "%%K <> %%@".toNSString())

    p = NSPredicate.predicateWithFormat("%K BETWEEN %@".toNSString(), "Record1.Age".toNSString(), mutableListOf(20, 40))
    assertTrue(p.evaluateWithObject(dict), "%%K BETWEEN %%@".toNSString())

    p = NSPredicate.predicateWithFormat("(%K == %d) OR (%K == %d)".toNSString(), "Record1.Age".toNSString(), 34, "Record2.Age".toNSString(), 34)
    assertTrue(p.evaluateWithObject(dict), "(%%K == %%d);OR (%%K == %%d)".toNSString())
  }

  @Test
  fun testFloat() {
    var p: NSPredicate

    p = NSPredicate.predicateWithFormat("%K < %f".toNSString(), "Record1.Age".toNSString(), 40.5)
    assertTrue(p.evaluateWithObject(dict), "%%K < %%f".toNSString())

    p = NSPredicate.predicateWithFormat("%f > %K".toNSString(), 40.5, "Record1.Age".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%f > %%K".toNSString())
  }

  @Test
  fun testAttregate() {
    var p: NSPredicate
    p = NSPredicate.predicateWithFormat("%@ IN %K".toNSString(), "Kid1".toNSString(), "Record1.Children".toNSString())
    assertTrue(p.evaluateWithObject(dict), "%%@ IN %%K".toNSString())
    p = NSPredicate.predicateWithFormat("Any %K == %@".toNSString(), "Record2.Children".toNSString(), "Girl1".toNSString())
    assertTrue(p.evaluateWithObject(dict), "Any %%K == %%@".toNSString())
  }


  @Test
  fun testBlock() {
    NSLog("TODO: Lambda predicates".toNSString())
  }

  private val dict: MutableMap<NSString, Any> = run {
    val dict: MutableMap<String, Any> = mutableMapOf()

    dict["title"] = "A Title".toNSString()

    dict["Record1"] = mutableMapOf(
      "Name" to "John".toNSString(),
      "Age" to 34,
      "Children" to mutableListOf("Kid1".toNSString(), "Kid2".toNSString())
    )

    dict["Record2"] = mutableMapOf(
      "Name" to "Mary".toNSString(),
      "Age" to 30,
      "Children" to mutableListOf("Kid1".toNSString(), "Girl1".toNSString())
    )

    dict
      .mapKeys { (k, _) -> k.toNSString() }
      .mapValues { (_, v) ->
        if (v is Map<*, *>) {
          v.mapKeys { (k, _) -> k?.toNSString() }
        } else {
          v
        }
      }
      .toMutableMap()
  }

  @Test
  fun main() {
    var filtered: List<Any>
    var p: NSPredicate

    val pitches: List<Any> = listOf("Do".toNSString(), "Re".toNSString(), "Mi".toNSString(), "Fa".toNSString(), "So".toNSString(), "La".toNSString())
    val expect: List<Any> = listOf("Do".toNSString())

    filtered = pitches.filteredArrayUsingPredicate(NSPredicate.predicateWithFormat("SELF == 'Do'".toNSString()))
    assertEquals((expect), filtered, "filter with SELF".toNSString())

    filtered = pitches.filteredArrayUsingPredicate(NSPredicate.predicateWithFormat("description == 'Do'".toNSString()))
    assertEquals((expect), filtered, "filter with description".toNSString())

    filtered = pitches.filteredArrayUsingPredicate(NSPredicate.predicateWithFormat("SELF == '%@'".toNSString(), "Do".toNSString()))
    assertEquals((emptyList()), filtered, "filter with format".toNSString())

    assertEquals(
      NSExpression.expressionForEvaluatedObject(), NSExpression.expressionForEvaluatedObject(),
      "expressionForEvaluatedObject is unique"
    )

    p = NSPredicate.predicateWithFormat("SELF == 'aaa'".toNSString())
    assertTrue(p.evaluateWithObject("aaa".toNSString()), "SELF equality works".toNSString())


    val d = mapOf("foo" to 2) // TODO: why this should work with "2" ?
    p = NSPredicate.predicateWithFormat("SELF.foo <= 2".toNSString())
    assertTrue(p.evaluateWithObject(d), "SELF.foo <= 2".toNSString())

    p = NSPredicate.predicateWithFormat("%K like %@+\$b+\$c".toNSString(), "\$single".toNSString(), "b\"".toNSString())
    assertEquals(
      "\$single LIKE (\"b\\\"\" + \$b) + \$c".toNSString(), p.predicateFormat(),
      "predicate created with format has the format is preserved"
    )


    p = p.predicateWithSubstitutionVariables(
      mutableMapOf<NSString, Any>(
        "single".toNSString() to "val_for_single_string".toNSString(), // why %K does not make a variable
        "b".toNSString() to "val_for_\$b".toNSString(),
        "c".toNSString() to "val_for_\$c".toNSString()
      )
    )
    assertEquals(
      "\$single LIKE (\"b\\\"\" + \"val_for_\$b\") + \"val_for_\$c\"".toNSString(), p.predicateFormat(),
      "Predicate created by substitution has the expected format".toNSString()
    )

    val a = listOf(
      mapOf<String, Any>("a" to 1),
      mapOf<String, Any>("a" to 2)
    )
    p = NSPredicate.predicateWithFormat("sum(a) == 3".toNSString())
    assertTrue(p.evaluateWithObject(a), "aggregate sum works".toNSString())
  }
}
