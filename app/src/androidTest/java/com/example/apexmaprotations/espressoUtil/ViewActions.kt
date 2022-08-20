package com.example.apexmaprotations.espressoUtil

import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe

public fun swipeRightCustom(): GeneralSwipeAction {
    return GeneralSwipeAction(
        Swipe.SLOW,
        GeneralLocation.CENTER_LEFT,
        GeneralLocation.CENTER_RIGHT,
        Press.FINGER
    )
}