package ifteam.affogatoman.fontgen

import android.app.Activity
import android.os.Bundle
import defpackage.LogCatBroadcaster

class LicenseActivity : Activity() {
    public override fun onCreate(bundle: Bundle?) {
        LogCatBroadcaster.start(this)
        super.onCreate(bundle)
        setContentView(R.layout.license)
    }

    init {
        val activity: Activity = this
    }
}