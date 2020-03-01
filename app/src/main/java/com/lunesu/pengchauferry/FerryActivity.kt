package com.lunesu.pengchauferry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lunesu.pengchauferry.ui.ferry.FerryFragment
import com.lunesu.pengchauferry.ui.ferry.PagerFragment

class FerryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ferry_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FerryFragment.newInstance())
                .commitNow()
        }
    }

}
