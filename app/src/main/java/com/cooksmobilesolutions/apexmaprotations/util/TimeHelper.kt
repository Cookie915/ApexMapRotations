package com.cooksmobilesolutions.apexmaprotations.util

//  Wrapper class for System.getCurrentTimeMillis()
//  That allows for Mockito stubbing, since we can't mock native methods
object TimeHelper {
    fun getNow() = System.currentTimeMillis()
}