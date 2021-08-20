package com.citrus.sandwitchdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**Hilt setup 04 - AndroidManifest.xml需加上 android:name=".MyApplication"*/
@HiltAndroidApp
class MyApplication:Application()