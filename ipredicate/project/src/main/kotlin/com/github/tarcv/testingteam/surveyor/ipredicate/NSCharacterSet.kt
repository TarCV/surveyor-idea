/*
 *  Copyright (C) 2024 TarCV
 *
 *  This file is part of UI Surveyor.
 *  UI Surveyor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.tarcv.testingteam.surveyor.ipredicate

import com.github.tarcv.testingteam.surveyor.ipredicate.NSString.Companion.toNSString

interface NSCharacterSet {
    fun characterIsMember(ch: UniChar): Boolean = characterIsMember(Char(ch))

    fun characterIsMember(ch: Char): Boolean

    fun copy() = this

    val invertedSet: NSCharacterSet
        get() = object : NSCharacterSet {
            override fun characterIsMember(ch: Char): Boolean {
                return !this@NSCharacterSet.characterIsMember(ch)
            }
        }

    companion object {
        val alphanumerics: NSCharacterSet
            get() {
                return object : NSCharacterSet {
                    override fun characterIsMember(ch: Char): Boolean {
                        return CharCategory.values()
                            .filter {
                                it.code.startsWith("L")
                                        || it.code.startsWith("M")
                                        || it.code.startsWith("N")
                            }
                            .any { it.contains(ch) }
                    }
                }
            }

        val nonBaseCharacters: NSCharacterSet
            get() {
                return object : NSCharacterSet {
                    override fun characterIsMember(ch: Char): Boolean {
                        return CharCategory.values()
                            .filter { it.code.startsWith("M") }
                            .any { it.contains(ch) }
                    }
                }
            }

        val decimalDigitCharacterSet: NSCharacterSet = characterSetWithCharactersInString("1234567890".toNSString())

        val illegalCharacterSet: NSCharacterSet
            get() {
                return object : NSCharacterSet {
                    override fun characterIsMember(ch: Char): Boolean {
                        if (CharCategory.UNASSIGNED.contains(ch)) {
                            return true
                        }
                        if (ch.code == 0xfffe || ch.code == 0xffff) {
                            return true
                        }
                        if (ch.code in 0xFDD0..0xFDEF) {
                            return true
                        }

                        return false
                    }
                }
            }

        val letterCharacterSet: NSCharacterSet
            get() {
                return object : NSCharacterSet {
                    override fun characterIsMember(ch: Char): Boolean {
                        return CharCategory.values()
                            .filter {
                                it.code.startsWith("L") || it.code.startsWith("M")
                            }
                            .any { it.contains(ch) }
                    }
                }
            }

        val whitespacesAndNewlines: NSCharacterSet
            get() {
                return object : NSCharacterSet {
                    override fun characterIsMember(ch: Char): Boolean {
                        val isInAnyZ = CharCategory.values()
                            .filter { it.code.startsWith("Z") }
                            .any { it.contains(ch) }
                        return isInAnyZ
                                || ch in '\u000A'.rangeTo('\u000D')
                                || ch == '\u0085'
                    }
                }
            }
        
        fun characterSetWithCharactersInString(characters: NSString) = NSCharacterSet(characters)
        fun characterSetWithCharactersInString(characters: String) = NSCharacterSet(characters.toNSString())
    }
}


fun NSCharacterSet(characters: NSString = NSString.emptyString): NSCharacterSet {
    return object : NSCharacterSet {
        private val chars = characters.toString()
        override fun characterIsMember(ch: Char): Boolean {
            return chars.contains(ch)
        }
    }
}

class NSMutableCharacterSet : NSCharacterSet {
    private val wrappedSets = mutableListOf<NSCharacterSet>()

    fun addCharactersInString(string: String) {
        formUnionWithCharacterSet(NSCharacterSet.characterSetWithCharactersInString(string))
    }

    fun formUnionWithCharacterSet(set: NSCharacterSet) {
        wrappedSets.add(set)
    }

    override fun characterIsMember(ch: Char): Boolean = wrappedSets.any { 
        it.characterIsMember(ch)
    }
}