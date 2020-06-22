package fi.ketjusovellus.ketjupilotti.main

import android.os.Bundle
import fi.ketjusovellus.ketjupilotti.BaseActivity
import fi.ketjusovellus.ketjupilotti.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
