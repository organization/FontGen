package ifteam.affogatoman.fontgen

import android.app.Activity
import android.os.Bundle

class LicenseActivity : Activity() {
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.license)
    }

    init {
        val activity: Activity = this
    }
}