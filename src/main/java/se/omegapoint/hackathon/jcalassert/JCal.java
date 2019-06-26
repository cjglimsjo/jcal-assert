package se.omegapoint.hackathon.jcalassert;

import se.omegapoint.hackathon.jcalassert.processor.Assert;

public class JCal {

    // Running 'mvn install' should result in 2 test failures unless the implementation is fixed.
    @Assert(that = true, returns = true)
    @Assert(that = false, returns = false)
    public boolean isTrue(boolean b) {
        return !b;
    }
}
