package io.github.phearing.phearing.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.artitk.licensefragment.model.CustomUI
import com.artitk.licensefragment.model.License
import com.artitk.licensefragment.model.LicenseType
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        about_toolbar.setNavigationOnClickListener { onBackPressed() }

        val version = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        about_version_tv.text = "V $version"

        about_opl_tv.setOnClickListener {
            val fragment = RecyclerViewLicenseFragment.newInstance()
            fragment.addCustomLicense(arrayListOf(
                    License(this, "Pocketsphinx", R.raw.pocketsphinx, "1999-2016", "Carnegie Mellon University"),
                    License(this, "Dagger2", LicenseType.APACHE_LICENSE_20, "2012", "The Dagger Authors"),
                    License(this, "Retrofit2", LicenseType.APACHE_LICENSE_20, "2013", "Square, Inc."),
                    License(this, "Material Components for Android", LicenseType.APACHE_LICENSE_20, "", "Google"),
                    License(this, "rclayout", LicenseType.APACHE_LICENSE_20, "2017", "GcsSloop"),
                    License(this, "Loading", LicenseType.APACHE_LICENSE_20, "2015", "yankai-victor"),
                    License(this, "MarkdownView", LicenseType.APACHE_LICENSE_20, "2017-2018", "tiagohm")
            ))
            fragment.setCustomUI(CustomUI().setLicenseBackgroundColor(ContextCompat.getColor(this, R.color.PHearing_white)))

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.about_replace_layout, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}
