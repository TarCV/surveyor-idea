/**
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

This file is based on parts of GNUstep that are covered under the following terms
(Copies of referenced files can be found in ipredicate/licenses/gnustepbase subdirectory in this
repository):

* NSString.m
Implementation of GNUSTEP string class
Copyright (C) 1995-2012 Free Software Foundation, Inc.

Written by:  Andrew Kachites McCallum <mccallum@gnu.ai.mit.edu>
Date: January 1995

Unicode implementation by Stevo Crvenkovski <stevo@btinternet.com>
Date: February 1997

Optimisations by Richard Frith-Macdonald <richard@brainstorm.co.uk>
Date: October 1998 - 2000

This file is part of the GNUstep Base Library.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
Boston, MA 02110 USA.

<title>NSString class reference</title>
$Date$ $Revision$

* GSeq.h
Implementation of composite character sequence functions for GNUSTEP
Copyright (C) 1999 Free Software Foundation, Inc.

Written by:  Richard Frith-Macdonald <richard@brainstorm.co.uk>
Date: May 1999
Based on code by:  Stevo Crvenkovski <stevo@btinternet.com>
Date: March 1997

This file is part of the GNUstep Base Library.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
Boston, MA 02110 USA.

* Unicode.m
Support functions for Unicode implementation
Function to determine default c string encoding for
GNUstep based on GNUSTEP_STRING_ENCODING environment variable.

Copyright (C) 1997 Free Software Foundation, Inc.

Written by: Stevo Crvenkovski < stevo@btinternet.com >
Date: March 1997
Merged with GetDefEncoding.m and iconv by: Fred Kiefer <fredkiefer@gmx.de>
Date: September 2000
Rewrite by: Richard Frith-Macdonald <rfm@gnu.org>

This file is part of the GNUstep Base Library.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
Boston, MA 02111 USA.
 */
package com.github.tarcv.testingteam.surveyor.ipredicate

import com.github.tarcv.testingteam.surveyor.ipredicate.CaseMap.gs_tolower_map
import com.github.tarcv.testingteam.surveyor.ipredicate.Cop.uni_cop_table
import com.github.tarcv.testingteam.surveyor.ipredicate.Dec.uni_dec_table

fun gsCompare(self: NSString, string: NSString, mask: Set<StringCompareOption>, compareRange: Range<StringUTF16ViewIndex>): ComparisonResult {
    // See https://github.com/gnustep/libs-base/blob/cc38f2f4a1ce2d3f0a5f478ead595d2b011ecf41/Source/NSString.m#L663
    // As locale == NULL in the predicate code, (NO == [locale isKindOfClass: [NSLocale class]]) always succeeds
    // As locale == NULL in the predicate code, (nil == locale) always succeeds
    // As NSNumericSearch is not used in the predicate code, (mask & NSNumericSearch) always fails
    // Thus the collator is always NULL and strCompNsNs is used
    return strCompNsNs(self, string, mask, compareRange)
}

private fun uni_isnonsp(u: UniChar): Boolean
{
    /* Treating upper surrogates as non-spacing is a convenient solution
     * to a number of issues with UTF-16
     */
    if ((u >= 0xdc00u) && (u <= 0xdfffu))
        return true

    return NSCharacterSet.nonBaseCharacters.characterIsMember(u)
}

private fun NSString.rangeOfComposedCharacterSequenceAtIndex(anIndex: UInt): Range<StringUTF16ViewIndex> {
    val length: UInt = this.length.toUInt()
    var ch: UniChar

    require(anIndex < length) { "Invalid location." }

    var lastStart: UInt = anIndex
    for (start in lastStart downTo 1u)
    {
        lastStart = start
        ch = this.characterAtIndex(start.toInt())
        if (!uni_isnonsp(ch))
            break
    }
    var lastEnd: UInt = lastStart + 1u
    for (end in lastEnd until length)
    {
        lastEnd = end
        ch = this.characterAtIndex(end.toInt())
        if (!uni_isnonsp(ch))
            break
    }

    return NSMakeRange(StringUTF16ViewIndex(lastStart.toInt()), (lastEnd-lastStart).toInt())
}

fun uni_tolower(ch: UniChar): UniChar {
    // TODO: Switch to icu4j for this:
    val result: UniChar = gs_tolower_map[(ch / 256u).toInt()][(ch % 256u).toInt()]
    return if (result.toInt() != 0) result else ch
}

const val MAXDEC = 18
fun GSeq_lowercase(seq: GSeqStruct)
{
    val s = seq.chars
    val	len: UInt = seq.count
    for (i in 0u until len) {
        s[i.toInt()] = uni_tolower(s[i.toInt()])
    }
}

class GSeqStruct(
    val chars: MutableList<UniChar>,
    var count: UInt,
    var normalized: Boolean
)

fun GSeq_normalize(seq: GSeqStruct)
{
    var count: UInt = seq.count

    if (count > 0u)
    {
        val source = seq.chars
        val targetSize = (count* MAXDEC.toUInt() +1u).toInt()
        val target = buildList<UniChar>(targetSize) {
            repeat(targetSize) {
                add(0u)
            }
        }.toMutableList()
        var base: UInt = 0u

        /*
         * Pre-scan ... anything with a code under 0x00C0 is not a decomposable
         * character, so we don't need to expand it.
         * If there are no decomposable characters or composed sequences, the
         * sequence is already normalised and we don't need to make any changes.
         */
        while (base < count)
        {
            if (source[base.toInt()] >= 0x00C0u)
            {
                break
            }
            base++
        }
        // source[count.toInt()] = (0u);
        if (base < count)
        {

            /*
             * Now expand decomposable characters into the long format.
             * Use the 'base' value to avoid re-checking characters which have
             * already been expanded.
             */
            while (base < count)
            {
                var spoint = source.subList(base.toInt())
                var tpoint = target.subList(base.toInt())
                var newbase: UInt = 0u

                do
                {
                    var dpoint: MutableList<UniChar>? = uni_is_decomp(spoint[0])?.toMutableList()

                    if (dpoint == null)
                    {
                        tpoint[0] = spoint[0]
                        tpoint = tpoint.subList(1)
                    }
                    else
                    {
                        while (dpoint!![0].toUInt() != 0u)
                        {
                            tpoint[0] = dpoint[0]
                            tpoint = tpoint.subList(1)
                            dpoint = dpoint.subList(1)
                        }
                        if (newbase <= 0u)
                        {
                            newbase = spoint.findIndexFromBase(source) + 1u
                        }
                    }
                    spoint = spoint.subList(1)
                }
                while (spoint.size > 0 && spoint[0].toUInt() != 0u)

                count = tpoint.findIndexFromBase(target)
                source.subList(base.toInt(), (base + (count - base)).toInt()).apply {
                    forEachIndexed { index, _ ->
                        this[index] = target[base + index]
                    }
                }
                source[count.toInt()] = (0u)
                base = if (newbase > 0u) {
                    newbase
                } else {
                    count
                }
            }
            seq.count = count

            /*
             * Now standardise ordering of all composed character sequences.
             */
            if (count > 1u)
            {
                var notdone: Boolean = true

                while (notdone)
                {
                    var first = seq.chars
                    var second = first.subList(1)

                    notdone = false
                    for (i in 1u until count)
                    {
                        if (GSPrivateUniCop(second[0]).toUInt() != 0u)
                        {
                            if (GSPrivateUniCop(first[0])
                                > GSPrivateUniCop(second[0]))
                            {
                                val	tmp = first[0]

                                first[0] = second[0]
                                second[0] = tmp
                                notdone = true
                            }
                            else if (GSPrivateUniCop(first[0])
                                == GSPrivateUniCop(second[0]))
                            {
                                if (first[0] > second[0])
                                {
                                    val	tmp = first[0]

                                    first[0] = second[0]
                                    second[0] = tmp
                                    notdone = true
                                }
                                else if (first[0] == second[0])
                                {
                                    val end = seq.chars.subList(count.toInt())

                                    while (first[0] == second[0] && second.findIndexFromBase(seq.chars) < end.findIndexFromBase(seq.chars))
                                    {
                                        second = second.subList(1)
                                        count--
                                    }
                                    first = first.subList(1)
                                    while (second.findIndexFromBase(seq.chars) < end.findIndexFromBase(seq.chars))
                                    {
                                        first[0] = second[0]
                                        first = first.subList(1)
                                    }
                                    notdone = true
                                    break
                                }
                            }
                        }
                        first = first.subList(1)
                        second = second.subList(1)
                    }
                }
                seq.count = count
            }
        }
        seq.normalized = true
    }
}

fun GSeq_compare(s0: GSeqStruct, s1: GSeqStruct): ComparisonResult
{
    val	end: UInt
    val c0 = s0.chars
    val c1 = s1.chars

    var len0: UInt = s0.count
    var len1: UInt = s1.count
    if (len0 == len1)
    {
        var finalI: UInt = 0u
        for (i in 0u until len1)
        {
            finalI = i
            if (c0[i.toInt()] != c1[i.toInt()])
            {
                break
            }
        }
        if (finalI == len0)
        {
            return ComparisonResult.orderedSame
        }
    }
    if (!s0.normalized) {
        GSeq_normalize(s0)
    }
    if (!s1.normalized) {
        GSeq_normalize(s1)
    }
    len0 = s0.count
    len1 = s1.count
    end = if (len0 < len1) {
        len0
    } else {
        len1
    }
    for (i in 0u until end)
    {
        if (c0[i.toInt()] < c1[i.toInt()])
            return ComparisonResult.orderedAscending
        if (c0[i.toInt()] > c1[i.toInt()])
            return ComparisonResult.orderedDescending
    }
    if (len0 < len1)
        return ComparisonResult.orderedAscending
    if (len0 > len1)
        return ComparisonResult.orderedDescending
    return ComparisonResult.orderedSame
}

// TODO: Switch to icu4j for this
fun uni_is_decomp(u: UniChar): Array<UniChar>? {
    if (u < uni_dec_table[0].code)
    {
        return null		// Special case for latin1
    }
    else
    {
        var code: UniChar
        var count: UInt = 0u
        var first: UInt = 0u
        var last: UInt = uni_dec_table.size.toUInt() - 1u

        while (first <= last)
        {
            if (first != last)
            {
                count = (first + last) / 2u
                code = uni_dec_table[count.toInt()].code
                if (code < u)
                {
                    first = count+1u
                }
                else if (code > u)
                {
                    last = count-1u
                }
                else
                {
                    return uni_dec_table[count.toInt()].decomp
                }
            }
            else  /* first == last */
            {
                if (u == uni_dec_table[first.toInt()].code)
                {
                    return uni_dec_table[first.toInt()].decomp
                }
                return null
            }
        }
        return null
    }
}

fun strCompNsNs(
    ss: NSString,
    os: NSString,
    mask: Set<StringCompareOption>,
    aRange: Range<StringUTF16ViewIndex>
): ComparisonResult {
    val s: NSString = ss as NSString
    val o: NSString = os as NSString

    val oLength: UInt = o.length.toUInt()            /* Length of other.	*/
    if (aRange.isEmpty()) {
        if (oLength.toInt() == 0) {
            return ComparisonResult.orderedSame
        }
        return ComparisonResult.orderedAscending
    } else if (oLength.toInt() == 0) {
        return ComparisonResult.orderedDescending
    }

    if (mask.contains(StringCompareOption.literal)) {
        val sLen: UInt = aRange.length.toUInt()
        val oLen: UInt = oLength

        val sBuf: Array<UniChar> = s.getCharacters(aRange)
        val oBuf: Array<UniChar> = o.getCharacters(NSMakeRange(StringUTF16ViewIndex(0), oLen.toInt()))

        val end: UInt = if (oLen < sLen) {
            oLen
        } else {
            sLen
        }

        if (mask.contains(StringCompareOption.caseInsensitive)) {
            for (i in 0.toUInt() until end)
            {
                val c1: UniChar = uni_tolower(sBuf[i.toInt()] as UniChar)
                val c2: UniChar = uni_tolower(oBuf[i.toInt()] as UniChar)

                if (c1 < c2)
                    return ComparisonResult.orderedAscending
                if (c1 > c2)
                    return ComparisonResult.orderedDescending
            }
        } else {
            for (i in 0.toUInt() until end)
            {
                if ((sBuf[i.toInt()] as UniChar) < (oBuf[i.toInt()] as UniChar)) {
                    return ComparisonResult.orderedAscending
                }
                if ((sBuf[i.toInt()] as UniChar) > (oBuf[i.toInt()] as UniChar)) {
                    return ComparisonResult.orderedDescending
                }
            }
        }
        return if (sLen > oLen)
            ComparisonResult.orderedDescending
        else if (sLen < oLen)
            ComparisonResult.orderedAscending
        else
            ComparisonResult.orderedSame
    } else {
        val start: UInt = aRange.location.index.toUInt()
        val end: UInt = (start + aRange.length).toUInt()
        val sLength: UInt = s.length.toUInt()
        var sCount: UInt = start
        var oCount: UInt = 0u
        var result: ComparisonResult

        while (sCount < end) {
            if (oCount >= oLength) {
                return ComparisonResult.orderedDescending
            } else if (sCount >= sLength) {
                return ComparisonResult.orderedAscending
            } else {
                val sRange = s.rangeOfComposedCharacterSequenceAtIndex(sCount)
                val oRange = o.rangeOfComposedCharacterSequenceAtIndex(oCount)
                val sBuf = s.getCharacters(sRange).toMutableList()
                    .addZeroesUntilSize((sRange.length * MAXDEC).toUInt())
                val sSeq = GSeqStruct(sBuf, sRange.length.toUInt(), false)
                val oBuf = o.getCharacters(oRange).toMutableList()
                    .addZeroesUntilSize((oRange.length * MAXDEC).toUInt())
                val oSeq = GSeqStruct(oBuf, oRange.length.toUInt(), false)

                result = GSeq_compare(sSeq, oSeq)

                if (result != ComparisonResult.orderedSame) {
                    if (mask.contains(StringCompareOption.caseInsensitive)) {
                        GSeq_lowercase(oSeq)
                        GSeq_lowercase(sSeq)
                        result = GSeq_compare(sSeq, oSeq)
                        if (result != ComparisonResult.orderedSame) {
                            return result
                        }
                    } else {
                        return result
                    }
                }

                sCount += sRange.length.toUInt()
                oCount += oRange.length.toUInt()
            }
        }
        if (oCount < oLength)
            return ComparisonResult.orderedAscending
        return ComparisonResult.orderedSame
    }
}

// TODO: Switch to icu4j for this
fun GSPrivateUniCop(u: UniChar): UniChar
{
    if (u < uni_cop_table[0].code)
    {
        return 0u	// Special case for latin1
    }
    else
    {
        var code: UniChar
        var count: UInt = 0u
        var first: UInt = 0u
        var last: UInt = uni_cop_table.size.toUInt() - 1u

        while (first <= last)
        {
            if (first != last)
            {
                count = ((first + last) / 2u)
                code = uni_cop_table[count.toInt()].code
                if (code < u)
                {
                    first = count+1u
                }
                else if (code > u)
                {
                    last = count-1u
                }
                else
                {
                    return uni_cop_table[count.toInt()].cop
                }
            }
            else  /* first == last */
            {
                if (u == uni_cop_table[first.toInt()].code)
                {
                    return uni_cop_table[first.toInt()].cop
                }
                return 0u
            }
        }
        return 0u
    }
}