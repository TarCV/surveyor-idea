package com.github.tarcv.testingteam.surveyor

import com.github.tarcv.testingteam.surveyor.Notice.Companion.loadNoticeFromResource

object IPredicateNotices {
    val gnuStepNotice = Notice.loadNoticeFromResource(
    "GNUStep - GNU GPL License",
        "incorporates parts of GNUStep which are covered by" +
                " the following license:",
    "gnustepbase/COPYING"
    )
    val wdaNotice = Notice.Companion.loadNoticeFromResource(
    "WebDriverAgent - BSD License",
        "incorporates parts of WebDriverAgent which are covered by" +
                " the following copyright and permission notices:",
    "wda/LICENSE"
    )
}