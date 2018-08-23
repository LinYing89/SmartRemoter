package com.bairock.iot.smartremoter.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import com.bairock.iot.smartremoter.R

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            bindPreferenceSummaryToValue(findPreference(Config.keyTouchRing))
        }

        //        override fun onOptionsItemSelected(item: MenuItem): Boolean {
//            val id = item.itemId
//            if (id == android.R.id.home) {
//                Log.e("GeneralFragment", "back")
//                //activity.onBackPressed()
//                //startActivity(Intent(activity, SettingsActivity::class.java))
//                //return true
//            }
//            return super.onOptionsItemSelected(item)
//        }
    }

    companion object {

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(stringValue)
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else {
                preference.summary = stringValue
            }
            when(preference.key){
                Config.keyTouchRing -> {Config.ctrlRing = value as Boolean}
            }
            true
        }

        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            val value : Any = when (preference) {
                is SwitchPreference -> PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getBoolean(preference.key, true)
                is ListPreference -> PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "0")
                else -> PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
            }

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,value)

        }
    }
}
