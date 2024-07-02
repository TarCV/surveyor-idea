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

This file is based on parts of GNUstep that are covered under the following terms
(Copies of referenced files can be found in ipredicate/licenses/gnustepbase subdirectory in this
repository):

* caseconv.h
  Copyright (C) 2005 Free Software Foundation

  Copying and distribution of this file, with or without modification,
  are permitted in any medium without royalty provided the copyright
  notice and this notice are preserved.
*/
package com.github.tarcv.testingteam.surveyor.ipredicate;

import kotlin.UShort;

import static com.github.tarcv.testingteam.surveyor.ipredicate.ObjCHelpersKt.asUShortArray;
import static com.github.tarcv.testingteam.surveyor.ipredicate.ObjCHelpersKt.uShortArrayOfZeroes;

public class CaseMap {
    public static final UShort[] gs_casemap_empty_table = uShortArrayOfZeroes(256);

    public static final UShort[] gs_tolower_map_table_0 = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x61,        /* 41 */
            0x62,        /* 42 */
            0x63,        /* 43 */
            0x64,        /* 44 */
            0x65,        /* 45 */
            0x66,        /* 46 */
            0x67,        /* 47 */
            0x68,        /* 48 */
            0x69,        /* 49 */
            0x6a,        /* 4a */
            0x6b,        /* 4b */
            0x6c,        /* 4c */
            0x6d,        /* 4d */
            0x6e,        /* 4e */
            0x6f,        /* 4f */
            0x70,        /* 50 */
            0x71,        /* 51 */
            0x72,        /* 52 */
            0x73,        /* 53 */
            0x74,        /* 54 */
            0x75,        /* 55 */
            0x76,        /* 56 */
            0x77,        /* 57 */
            0x78,        /* 58 */
            0x79,        /* 59 */
            0x7a,        /* 5a */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0xe0,        /* c0 */
            0xe1,        /* c1 */
            0xe2,        /* c2 */
            0xe3,        /* c3 */
            0xe4,        /* c4 */
            0xe5,        /* c5 */
            0xe6,        /* c6 */
            0xe7,        /* c7 */
            0xe8,        /* c8 */
            0xe9,        /* c9 */
            0xea,        /* ca */
            0xeb,        /* cb */
            0xec,        /* cc */
            0xed,        /* cd */
            0xee,        /* ce */
            0xef,        /* cf */
            0xf0,        /* d0 */
            0xf1,        /* d1 */
            0xf2,        /* d2 */
            0xf3,        /* d3 */
            0xf4,        /* d4 */
            0xf5,        /* d5 */
            0xf6,        /* d6 */
            0x0,
            0xf8,        /* d8 */
            0xf9,        /* d9 */
            0xfa,        /* da */
            0xfb,        /* db */
            0xfc,        /* dc */
            0xfd,        /* dd */
            0xfe,        /* de */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_1 = asUShortArray(new int[]{
            0x101,        /* 0 */
            0x0,
            0x103,        /* 2 */
            0x0,
            0x105,        /* 4 */
            0x0,
            0x107,        /* 6 */
            0x0,
            0x109,        /* 8 */
            0x0,
            0x10b,        /* a */
            0x0,
            0x10d,        /* c */
            0x0,
            0x10f,        /* e */
            0x0,
            0x111,        /* 10 */
            0x0,
            0x113,        /* 12 */
            0x0,
            0x115,        /* 14 */
            0x0,
            0x117,        /* 16 */
            0x0,
            0x119,        /* 18 */
            0x0,
            0x11b,        /* 1a */
            0x0,
            0x11d,        /* 1c */
            0x0,
            0x11f,        /* 1e */
            0x0,
            0x121,        /* 20 */
            0x0,
            0x123,        /* 22 */
            0x0,
            0x125,        /* 24 */
            0x0,
            0x127,        /* 26 */
            0x0,
            0x129,        /* 28 */
            0x0,
            0x12b,        /* 2a */
            0x0,
            0x12d,        /* 2c */
            0x0,
            0x12f,        /* 2e */
            0x0,
            0x69,        /* 30 */
            0x0,
            0x133,        /* 32 */
            0x0,
            0x135,        /* 34 */
            0x0,
            0x137,        /* 36 */
            0x0,
            0x0,
            0x13a,        /* 39 */
            0x0,
            0x13c,        /* 3b */
            0x0,
            0x13e,        /* 3d */
            0x0,
            0x140,        /* 3f */
            0x0,
            0x142,        /* 41 */
            0x0,
            0x144,        /* 43 */
            0x0,
            0x146,        /* 45 */
            0x0,
            0x148,        /* 47 */
            0x0,
            0x0,
            0x14b,        /* 4a */
            0x0,
            0x14d,        /* 4c */
            0x0,
            0x14f,        /* 4e */
            0x0,
            0x151,        /* 50 */
            0x0,
            0x153,        /* 52 */
            0x0,
            0x155,        /* 54 */
            0x0,
            0x157,        /* 56 */
            0x0,
            0x159,        /* 58 */
            0x0,
            0x15b,        /* 5a */
            0x0,
            0x15d,        /* 5c */
            0x0,
            0x15f,        /* 5e */
            0x0,
            0x161,        /* 60 */
            0x0,
            0x163,        /* 62 */
            0x0,
            0x165,        /* 64 */
            0x0,
            0x167,        /* 66 */
            0x0,
            0x169,        /* 68 */
            0x0,
            0x16b,        /* 6a */
            0x0,
            0x16d,        /* 6c */
            0x0,
            0x16f,        /* 6e */
            0x0,
            0x171,        /* 70 */
            0x0,
            0x173,        /* 72 */
            0x0,
            0x175,        /* 74 */
            0x0,
            0x177,        /* 76 */
            0x0,
            0xff,        /* 78 */
            0x17a,        /* 79 */
            0x0,
            0x17c,        /* 7b */
            0x0,
            0x17e,        /* 7d */
            0x0,
            0x0,
            0x0,
            0x253,        /* 81 */
            0x183,        /* 82 */
            0x0,
            0x185,        /* 84 */
            0x0,
            0x254,        /* 86 */
            0x188,        /* 87 */
            0x0,
            0x256,        /* 89 */
            0x257,        /* 8a */
            0x18c,        /* 8b */
            0x0,
            0x0,
            0x1dd,        /* 8e */
            0x259,        /* 8f */
            0x25b,        /* 90 */
            0x192,        /* 91 */
            0x0,
            0x260,        /* 93 */
            0x263,        /* 94 */
            0x0,
            0x269,        /* 96 */
            0x268,        /* 97 */
            0x199,        /* 98 */
            0x0,
            0x0,
            0x0,
            0x26f,        /* 9c */
            0x272,        /* 9d */
            0x0,
            0x275,        /* 9f */
            0x1a1,        /* a0 */
            0x0,
            0x1a3,        /* a2 */
            0x0,
            0x1a5,        /* a4 */
            0x0,
            0x280,        /* a6 */
            0x1a8,        /* a7 */
            0x0,
            0x283,        /* a9 */
            0x0,
            0x0,
            0x1ad,        /* ac */
            0x0,
            0x288,        /* ae */
            0x1b0,        /* af */
            0x0,
            0x28a,        /* b1 */
            0x28b,        /* b2 */
            0x1b4,        /* b3 */
            0x0,
            0x1b6,        /* b5 */
            0x0,
            0x292,        /* b7 */
            0x1b9,        /* b8 */
            0x0,
            0x0,
            0x0,
            0x1bd,        /* bc */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1c6,        /* c4 */
            0x1c6,        /* c5 */
            0x0,
            0x1c9,        /* c7 */
            0x1c9,        /* c8 */
            0x0,
            0x1cc,        /* ca */
            0x1cc,        /* cb */
            0x0,
            0x1ce,        /* cd */
            0x0,
            0x1d0,        /* cf */
            0x0,
            0x1d2,        /* d1 */
            0x0,
            0x1d4,        /* d3 */
            0x0,
            0x1d6,        /* d5 */
            0x0,
            0x1d8,        /* d7 */
            0x0,
            0x1da,        /* d9 */
            0x0,
            0x1dc,        /* db */
            0x0,
            0x0,
            0x1df,        /* de */
            0x0,
            0x1e1,        /* e0 */
            0x0,
            0x1e3,        /* e2 */
            0x0,
            0x1e5,        /* e4 */
            0x0,
            0x1e7,        /* e6 */
            0x0,
            0x1e9,        /* e8 */
            0x0,
            0x1eb,        /* ea */
            0x0,
            0x1ed,        /* ec */
            0x0,
            0x1ef,        /* ee */
            0x0,
            0x0,
            0x1f3,        /* f1 */
            0x1f3,        /* f2 */
            0x0,
            0x1f5,        /* f4 */
            0x0,
            0x195,        /* f6 */
            0x1bf,        /* f7 */
            0x1f9,        /* f8 */
            0x0,
            0x1fb,        /* fa */
            0x0,
            0x1fd,        /* fc */
            0x0,
            0x1ff,        /* fe */
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_2 = asUShortArray(new int[]{
            0x201,        /* 0 */
            0x0,
            0x203,        /* 2 */
            0x0,
            0x205,        /* 4 */
            0x0,
            0x207,        /* 6 */
            0x0,
            0x209,        /* 8 */
            0x0,
            0x20b,        /* a */
            0x0,
            0x20d,        /* c */
            0x0,
            0x20f,        /* e */
            0x0,
            0x211,        /* 10 */
            0x0,
            0x213,        /* 12 */
            0x0,
            0x215,        /* 14 */
            0x0,
            0x217,        /* 16 */
            0x0,
            0x219,        /* 18 */
            0x0,
            0x21b,        /* 1a */
            0x0,
            0x21d,        /* 1c */
            0x0,
            0x21f,        /* 1e */
            0x0,
            0x0,
            0x0,
            0x223,        /* 22 */
            0x0,
            0x225,        /* 24 */
            0x0,
            0x227,        /* 26 */
            0x0,
            0x229,        /* 28 */
            0x0,
            0x22b,        /* 2a */
            0x0,
            0x22d,        /* 2c */
            0x0,
            0x22f,        /* 2e */
            0x0,
            0x231,        /* 30 */
            0x0,
            0x233,        /* 32 */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_3 = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x3ac,        /* 86 */
            0x0,
            0x3ad,        /* 88 */
            0x3ae,        /* 89 */
            0x3af,        /* 8a */
            0x0,
            0x3cc,        /* 8c */
            0x0,
            0x3cd,        /* 8e */
            0x3ce,        /* 8f */
            0x0,
            0x3b1,        /* 91 */
            0x3b2,        /* 92 */
            0x3b3,        /* 93 */
            0x3b4,        /* 94 */
            0x3b5,        /* 95 */
            0x3b6,        /* 96 */
            0x3b7,        /* 97 */
            0x3b8,        /* 98 */
            0x3b9,        /* 99 */
            0x3ba,        /* 9a */
            0x3bb,        /* 9b */
            0x3bc,        /* 9c */
            0x3bd,        /* 9d */
            0x3be,        /* 9e */
            0x3bf,        /* 9f */
            0x3c0,        /* a0 */
            0x3c1,        /* a1 */
            0x0,
            0x3c3,        /* a3 */
            0x3c4,        /* a4 */
            0x3c5,        /* a5 */
            0x3c6,        /* a6 */
            0x3c7,        /* a7 */
            0x3c8,        /* a8 */
            0x3c9,        /* a9 */
            0x3ca,        /* aa */
            0x3cb,        /* ab */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x3db,        /* da */
            0x0,
            0x3dd,        /* dc */
            0x0,
            0x3df,        /* de */
            0x0,
            0x3e1,        /* e0 */
            0x0,
            0x3e3,        /* e2 */
            0x0,
            0x3e5,        /* e4 */
            0x0,
            0x3e7,        /* e6 */
            0x0,
            0x3e9,        /* e8 */
            0x0,
            0x3eb,        /* ea */
            0x0,
            0x3ed,        /* ec */
            0x0,
            0x3ef,        /* ee */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_4 = asUShortArray(new int[]{
            0x450,        /* 0 */
            0x451,        /* 1 */
            0x452,        /* 2 */
            0x453,        /* 3 */
            0x454,        /* 4 */
            0x455,        /* 5 */
            0x456,        /* 6 */
            0x457,        /* 7 */
            0x458,        /* 8 */
            0x459,        /* 9 */
            0x45a,        /* a */
            0x45b,        /* b */
            0x45c,        /* c */
            0x45d,        /* d */
            0x45e,        /* e */
            0x45f,        /* f */
            0x430,        /* 10 */
            0x431,        /* 11 */
            0x432,        /* 12 */
            0x433,        /* 13 */
            0x434,        /* 14 */
            0x435,        /* 15 */
            0x436,        /* 16 */
            0x437,        /* 17 */
            0x438,        /* 18 */
            0x439,        /* 19 */
            0x43a,        /* 1a */
            0x43b,        /* 1b */
            0x43c,        /* 1c */
            0x43d,        /* 1d */
            0x43e,        /* 1e */
            0x43f,        /* 1f */
            0x440,        /* 20 */
            0x441,        /* 21 */
            0x442,        /* 22 */
            0x443,        /* 23 */
            0x444,        /* 24 */
            0x445,        /* 25 */
            0x446,        /* 26 */
            0x447,        /* 27 */
            0x448,        /* 28 */
            0x449,        /* 29 */
            0x44a,        /* 2a */
            0x44b,        /* 2b */
            0x44c,        /* 2c */
            0x44d,        /* 2d */
            0x44e,        /* 2e */
            0x44f,        /* 2f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x461,        /* 60 */
            0x0,
            0x463,        /* 62 */
            0x0,
            0x465,        /* 64 */
            0x0,
            0x467,        /* 66 */
            0x0,
            0x469,        /* 68 */
            0x0,
            0x46b,        /* 6a */
            0x0,
            0x46d,        /* 6c */
            0x0,
            0x46f,        /* 6e */
            0x0,
            0x471,        /* 70 */
            0x0,
            0x473,        /* 72 */
            0x0,
            0x475,        /* 74 */
            0x0,
            0x477,        /* 76 */
            0x0,
            0x479,        /* 78 */
            0x0,
            0x47b,        /* 7a */
            0x0,
            0x47d,        /* 7c */
            0x0,
            0x47f,        /* 7e */
            0x0,
            0x481,        /* 80 */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x48d,        /* 8c */
            0x0,
            0x48f,        /* 8e */
            0x0,
            0x491,        /* 90 */
            0x0,
            0x493,        /* 92 */
            0x0,
            0x495,        /* 94 */
            0x0,
            0x497,        /* 96 */
            0x0,
            0x499,        /* 98 */
            0x0,
            0x49b,        /* 9a */
            0x0,
            0x49d,        /* 9c */
            0x0,
            0x49f,        /* 9e */
            0x0,
            0x4a1,        /* a0 */
            0x0,
            0x4a3,        /* a2 */
            0x0,
            0x4a5,        /* a4 */
            0x0,
            0x4a7,        /* a6 */
            0x0,
            0x4a9,        /* a8 */
            0x0,
            0x4ab,        /* aa */
            0x0,
            0x4ad,        /* ac */
            0x0,
            0x4af,        /* ae */
            0x0,
            0x4b1,        /* b0 */
            0x0,
            0x4b3,        /* b2 */
            0x0,
            0x4b5,        /* b4 */
            0x0,
            0x4b7,        /* b6 */
            0x0,
            0x4b9,        /* b8 */
            0x0,
            0x4bb,        /* ba */
            0x0,
            0x4bd,        /* bc */
            0x0,
            0x4bf,        /* be */
            0x0,
            0x0,
            0x4c2,        /* c1 */
            0x0,
            0x4c4,        /* c3 */
            0x0,
            0x0,
            0x0,
            0x4c8,        /* c7 */
            0x0,
            0x0,
            0x0,
            0x4cc,        /* cb */
            0x0,
            0x0,
            0x0,
            0x0,
            0x4d1,        /* d0 */
            0x0,
            0x4d3,        /* d2 */
            0x0,
            0x4d5,        /* d4 */
            0x0,
            0x4d7,        /* d6 */
            0x0,
            0x4d9,        /* d8 */
            0x0,
            0x4db,        /* da */
            0x0,
            0x4dd,        /* dc */
            0x0,
            0x4df,        /* de */
            0x0,
            0x4e1,        /* e0 */
            0x0,
            0x4e3,        /* e2 */
            0x0,
            0x4e5,        /* e4 */
            0x0,
            0x4e7,        /* e6 */
            0x0,
            0x4e9,        /* e8 */
            0x0,
            0x4eb,        /* ea */
            0x0,
            0x4ed,        /* ec */
            0x0,
            0x4ef,        /* ee */
            0x0,
            0x4f1,        /* f0 */
            0x0,
            0x4f3,        /* f2 */
            0x0,
            0x4f5,        /* f4 */
            0x0,
            0x0,
            0x0,
            0x4f9,        /* f8 */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_5 = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x561,        /* 31 */
            0x562,        /* 32 */
            0x563,        /* 33 */
            0x564,        /* 34 */
            0x565,        /* 35 */
            0x566,        /* 36 */
            0x567,        /* 37 */
            0x568,        /* 38 */
            0x569,        /* 39 */
            0x56a,        /* 3a */
            0x56b,        /* 3b */
            0x56c,        /* 3c */
            0x56d,        /* 3d */
            0x56e,        /* 3e */
            0x56f,        /* 3f */
            0x570,        /* 40 */
            0x571,        /* 41 */
            0x572,        /* 42 */
            0x573,        /* 43 */
            0x574,        /* 44 */
            0x575,        /* 45 */
            0x576,        /* 46 */
            0x577,        /* 47 */
            0x578,        /* 48 */
            0x579,        /* 49 */
            0x57a,        /* 4a */
            0x57b,        /* 4b */
            0x57c,        /* 4c */
            0x57d,        /* 4d */
            0x57e,        /* 4e */
            0x57f,        /* 4f */
            0x580,        /* 50 */
            0x581,        /* 51 */
            0x582,        /* 52 */
            0x583,        /* 53 */
            0x584,        /* 54 */
            0x585,        /* 55 */
            0x586,        /* 56 */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_1e = asUShortArray(new int[]{
            0x1e01,        /* 0 */
            0x0,
            0x1e03,        /* 2 */
            0x0,
            0x1e05,        /* 4 */
            0x0,
            0x1e07,        /* 6 */
            0x0,
            0x1e09,        /* 8 */
            0x0,
            0x1e0b,        /* a */
            0x0,
            0x1e0d,        /* c */
            0x0,
            0x1e0f,        /* e */
            0x0,
            0x1e11,        /* 10 */
            0x0,
            0x1e13,        /* 12 */
            0x0,
            0x1e15,        /* 14 */
            0x0,
            0x1e17,        /* 16 */
            0x0,
            0x1e19,        /* 18 */
            0x0,
            0x1e1b,        /* 1a */
            0x0,
            0x1e1d,        /* 1c */
            0x0,
            0x1e1f,        /* 1e */
            0x0,
            0x1e21,        /* 20 */
            0x0,
            0x1e23,        /* 22 */
            0x0,
            0x1e25,        /* 24 */
            0x0,
            0x1e27,        /* 26 */
            0x0,
            0x1e29,        /* 28 */
            0x0,
            0x1e2b,        /* 2a */
            0x0,
            0x1e2d,        /* 2c */
            0x0,
            0x1e2f,        /* 2e */
            0x0,
            0x1e31,        /* 30 */
            0x0,
            0x1e33,        /* 32 */
            0x0,
            0x1e35,        /* 34 */
            0x0,
            0x1e37,        /* 36 */
            0x0,
            0x1e39,        /* 38 */
            0x0,
            0x1e3b,        /* 3a */
            0x0,
            0x1e3d,        /* 3c */
            0x0,
            0x1e3f,        /* 3e */
            0x0,
            0x1e41,        /* 40 */
            0x0,
            0x1e43,        /* 42 */
            0x0,
            0x1e45,        /* 44 */
            0x0,
            0x1e47,        /* 46 */
            0x0,
            0x1e49,        /* 48 */
            0x0,
            0x1e4b,        /* 4a */
            0x0,
            0x1e4d,        /* 4c */
            0x0,
            0x1e4f,        /* 4e */
            0x0,
            0x1e51,        /* 50 */
            0x0,
            0x1e53,        /* 52 */
            0x0,
            0x1e55,        /* 54 */
            0x0,
            0x1e57,        /* 56 */
            0x0,
            0x1e59,        /* 58 */
            0x0,
            0x1e5b,        /* 5a */
            0x0,
            0x1e5d,        /* 5c */
            0x0,
            0x1e5f,        /* 5e */
            0x0,
            0x1e61,        /* 60 */
            0x0,
            0x1e63,        /* 62 */
            0x0,
            0x1e65,        /* 64 */
            0x0,
            0x1e67,        /* 66 */
            0x0,
            0x1e69,        /* 68 */
            0x0,
            0x1e6b,        /* 6a */
            0x0,
            0x1e6d,        /* 6c */
            0x0,
            0x1e6f,        /* 6e */
            0x0,
            0x1e71,        /* 70 */
            0x0,
            0x1e73,        /* 72 */
            0x0,
            0x1e75,        /* 74 */
            0x0,
            0x1e77,        /* 76 */
            0x0,
            0x1e79,        /* 78 */
            0x0,
            0x1e7b,        /* 7a */
            0x0,
            0x1e7d,        /* 7c */
            0x0,
            0x1e7f,        /* 7e */
            0x0,
            0x1e81,        /* 80 */
            0x0,
            0x1e83,        /* 82 */
            0x0,
            0x1e85,        /* 84 */
            0x0,
            0x1e87,        /* 86 */
            0x0,
            0x1e89,        /* 88 */
            0x0,
            0x1e8b,        /* 8a */
            0x0,
            0x1e8d,        /* 8c */
            0x0,
            0x1e8f,        /* 8e */
            0x0,
            0x1e91,        /* 90 */
            0x0,
            0x1e93,        /* 92 */
            0x0,
            0x1e95,        /* 94 */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1ea1,        /* a0 */
            0x0,
            0x1ea3,        /* a2 */
            0x0,
            0x1ea5,        /* a4 */
            0x0,
            0x1ea7,        /* a6 */
            0x0,
            0x1ea9,        /* a8 */
            0x0,
            0x1eab,        /* aa */
            0x0,
            0x1ead,        /* ac */
            0x0,
            0x1eaf,        /* ae */
            0x0,
            0x1eb1,        /* b0 */
            0x0,
            0x1eb3,        /* b2 */
            0x0,
            0x1eb5,        /* b4 */
            0x0,
            0x1eb7,        /* b6 */
            0x0,
            0x1eb9,        /* b8 */
            0x0,
            0x1ebb,        /* ba */
            0x0,
            0x1ebd,        /* bc */
            0x0,
            0x1ebf,        /* be */
            0x0,
            0x1ec1,        /* c0 */
            0x0,
            0x1ec3,        /* c2 */
            0x0,
            0x1ec5,        /* c4 */
            0x0,
            0x1ec7,        /* c6 */
            0x0,
            0x1ec9,        /* c8 */
            0x0,
            0x1ecb,        /* ca */
            0x0,
            0x1ecd,        /* cc */
            0x0,
            0x1ecf,        /* ce */
            0x0,
            0x1ed1,        /* d0 */
            0x0,
            0x1ed3,        /* d2 */
            0x0,
            0x1ed5,        /* d4 */
            0x0,
            0x1ed7,        /* d6 */
            0x0,
            0x1ed9,        /* d8 */
            0x0,
            0x1edb,        /* da */
            0x0,
            0x1edd,        /* dc */
            0x0,
            0x1edf,        /* de */
            0x0,
            0x1ee1,        /* e0 */
            0x0,
            0x1ee3,        /* e2 */
            0x0,
            0x1ee5,        /* e4 */
            0x0,
            0x1ee7,        /* e6 */
            0x0,
            0x1ee9,        /* e8 */
            0x0,
            0x1eeb,        /* ea */
            0x0,
            0x1eed,        /* ec */
            0x0,
            0x1eef,        /* ee */
            0x0,
            0x1ef1,        /* f0 */
            0x0,
            0x1ef3,        /* f2 */
            0x0,
            0x1ef5,        /* f4 */
            0x0,
            0x1ef7,        /* f6 */
            0x0,
            0x1ef9,        /* f8 */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_1f = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f00,        /* 8 */
            0x1f01,        /* 9 */
            0x1f02,        /* a */
            0x1f03,        /* b */
            0x1f04,        /* c */
            0x1f05,        /* d */
            0x1f06,        /* e */
            0x1f07,        /* f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f10,        /* 18 */
            0x1f11,        /* 19 */
            0x1f12,        /* 1a */
            0x1f13,        /* 1b */
            0x1f14,        /* 1c */
            0x1f15,        /* 1d */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f20,        /* 28 */
            0x1f21,        /* 29 */
            0x1f22,        /* 2a */
            0x1f23,        /* 2b */
            0x1f24,        /* 2c */
            0x1f25,        /* 2d */
            0x1f26,        /* 2e */
            0x1f27,        /* 2f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f30,        /* 38 */
            0x1f31,        /* 39 */
            0x1f32,        /* 3a */
            0x1f33,        /* 3b */
            0x1f34,        /* 3c */
            0x1f35,        /* 3d */
            0x1f36,        /* 3e */
            0x1f37,        /* 3f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f40,        /* 48 */
            0x1f41,        /* 49 */
            0x1f42,        /* 4a */
            0x1f43,        /* 4b */
            0x1f44,        /* 4c */
            0x1f45,        /* 4d */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f51,        /* 59 */
            0x0,
            0x1f53,        /* 5b */
            0x0,
            0x1f55,        /* 5d */
            0x0,
            0x1f57,        /* 5f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f60,        /* 68 */
            0x1f61,        /* 69 */
            0x1f62,        /* 6a */
            0x1f63,        /* 6b */
            0x1f64,        /* 6c */
            0x1f65,        /* 6d */
            0x1f66,        /* 6e */
            0x1f67,        /* 6f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f80,        /* 88 */
            0x1f81,        /* 89 */
            0x1f82,        /* 8a */
            0x1f83,        /* 8b */
            0x1f84,        /* 8c */
            0x1f85,        /* 8d */
            0x1f86,        /* 8e */
            0x1f87,        /* 8f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f90,        /* 98 */
            0x1f91,        /* 99 */
            0x1f92,        /* 9a */
            0x1f93,        /* 9b */
            0x1f94,        /* 9c */
            0x1f95,        /* 9d */
            0x1f96,        /* 9e */
            0x1f97,        /* 9f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1fa0,        /* a8 */
            0x1fa1,        /* a9 */
            0x1fa2,        /* aa */
            0x1fa3,        /* ab */
            0x1fa4,        /* ac */
            0x1fa5,        /* ad */
            0x1fa6,        /* ae */
            0x1fa7,        /* af */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1fb0,        /* b8 */
            0x1fb1,        /* b9 */
            0x1f70,        /* ba */
            0x1f71,        /* bb */
            0x1fb3,        /* bc */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f72,        /* c8 */
            0x1f73,        /* c9 */
            0x1f74,        /* ca */
            0x1f75,        /* cb */
            0x1fc3,        /* cc */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1fd0,        /* d8 */
            0x1fd1,        /* d9 */
            0x1f76,        /* da */
            0x1f77,        /* db */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1fe0,        /* e8 */
            0x1fe1,        /* e9 */
            0x1f7a,        /* ea */
            0x1f7b,        /* eb */
            0x1fe5,        /* ec */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x1f78,        /* f8 */
            0x1f79,        /* f9 */
            0x1f7c,        /* fa */
            0x1f7d,        /* fb */
            0x1ff3,        /* fc */
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_21 = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x3c9,        /* 26 */
            0x0,
            0x0,
            0x0,
            0x6b,        /* 2a */
            0xe5,        /* 2b */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x2170,        /* 60 */
            0x2171,        /* 61 */
            0x2172,        /* 62 */
            0x2173,        /* 63 */
            0x2174,        /* 64 */
            0x2175,        /* 65 */
            0x2176,        /* 66 */
            0x2177,        /* 67 */
            0x2178,        /* 68 */
            0x2179,        /* 69 */
            0x217a,        /* 6a */
            0x217b,        /* 6b */
            0x217c,        /* 6c */
            0x217d,        /* 6d */
            0x217e,        /* 6e */
            0x217f,        /* 6f */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_24 = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x24d0,        /* b6 */
            0x24d1,        /* b7 */
            0x24d2,        /* b8 */
            0x24d3,        /* b9 */
            0x24d4,        /* ba */
            0x24d5,        /* bb */
            0x24d6,        /* bc */
            0x24d7,        /* bd */
            0x24d8,        /* be */
            0x24d9,        /* bf */
            0x24da,        /* c0 */
            0x24db,        /* c1 */
            0x24dc,        /* c2 */
            0x24dd,        /* c3 */
            0x24de,        /* c4 */
            0x24df,        /* c5 */
            0x24e0,        /* c6 */
            0x24e1,        /* c7 */
            0x24e2,        /* c8 */
            0x24e3,        /* c9 */
            0x24e4,        /* ca */
            0x24e5,        /* cb */
            0x24e6,        /* cc */
            0x24e7,        /* cd */
            0x24e8,        /* ce */
            0x24e9,        /* cf */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[] gs_tolower_map_table_ff = asUShortArray(new int[]{
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0xff41,        /* 21 */
            0xff42,        /* 22 */
            0xff43,        /* 23 */
            0xff44,        /* 24 */
            0xff45,        /* 25 */
            0xff46,        /* 26 */
            0xff47,        /* 27 */
            0xff48,        /* 28 */
            0xff49,        /* 29 */
            0xff4a,        /* 2a */
            0xff4b,        /* 2b */
            0xff4c,        /* 2c */
            0xff4d,        /* 2d */
            0xff4e,        /* 2e */
            0xff4f,        /* 2f */
            0xff50,        /* 30 */
            0xff51,        /* 31 */
            0xff52,        /* 32 */
            0xff53,        /* 33 */
            0xff54,        /* 34 */
            0xff55,        /* 35 */
            0xff56,        /* 36 */
            0xff57,        /* 37 */
            0xff58,        /* 38 */
            0xff59,        /* 39 */
            0xff5a,        /* 3a */
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
            0x0,
    });

    public static final UShort[][] gs_tolower_map = new UShort[][]{
            gs_tolower_map_table_0,
            gs_tolower_map_table_1,
            gs_tolower_map_table_2,
            gs_tolower_map_table_3,
            gs_tolower_map_table_4,
            gs_tolower_map_table_5,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_tolower_map_table_1e,
            gs_tolower_map_table_1f,
            gs_casemap_empty_table,
            gs_tolower_map_table_21,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_tolower_map_table_24,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_casemap_empty_table,
            gs_tolower_map_table_ff,
    };
}