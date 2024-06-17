package com.clockworkorange.haohsing.utils

import android.content.Intent
import android.net.Uri

object IntentUtils {

    fun createPhoneCallIntent(phone: String): Intent {
        return Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
    }

    fun createMapIntent(address: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address")).apply {
            setPackage("com.google.android.apps.maps")
        }
    }

    fun createViewUrlIntent(url: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

}