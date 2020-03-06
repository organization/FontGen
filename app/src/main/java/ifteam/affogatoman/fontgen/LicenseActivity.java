package ifteam.affogatoman.fontgen;

import android.app.Activity;
import android.os.Bundle;
import defpackage.LogCatBroadcaster;

public class LicenseActivity extends Activity {
    @Override
    public void onCreate(Bundle bundle) {
        Bundle bundle2 = bundle;
        LogCatBroadcaster.start(this);
        super.onCreate(bundle2);
        setContentView(R.layout.license);
    }

    public LicenseActivity() {
        Activity activity = this;
    }
}
