package com.indexyear.jd.dispatch.data.crisis;

import com.indexyear.jd.dispatch.models.Crisis;

public interface ICrisisEventListener {
    void onCrisisCreated(Crisis newCrisis);
    void onCrisisRemoved(Crisis removedCrisis);
    void onCrisisUpdated(Crisis updatedCrisis);
}
