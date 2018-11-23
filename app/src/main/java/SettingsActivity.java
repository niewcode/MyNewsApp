import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.newsapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreference extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener{

        //Update UI (pref summary) when settings activity is launched in onCreate()
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //Find preference desired, then bind the current preference value to be displayed
            Preference minEvents=findPreference(getString(R.string.settings_pages_key));
            bindPreferenceSummaryToValue(minEvents);

            Preference orderBy=findPreference(getString(R.string.settings_receive_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value){
            //The code in this method takes care of updating the displayed preference
            //summary after it has been changed
            String stringValue=value.toString();
            if(preference instanceof ListPreference){
                ListPreference listPreference=(ListPreference)preference;
                int prefIndex=listPreference.findIndexOfValue((stringValue));
                if (prefIndex>=0){
                    CharSequence[] labels=listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }else{
                preference.setSummary(stringValue);
            }
            return true;
        }
        //Helper Method, takes in a preference as param, and uses onPreferenceChangeListener to set
        //the current EventPreferenceFragment instance to listen for passed in pref changes.
        private void bindPreferenceSummaryToValue(Preference preference){
            preference.setOnPreferenceChangeListener(this);
            //Read curr value of prefs stored in SharedPreferences on the device & display it in
            //the preference summary to show user current pref value.
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences
                    (preference.getContext());
            String preferenceString=preferences.getString(preference.getKey(),"" );
            onPreferenceChange(preference,preferenceString);
        }

    }
}

