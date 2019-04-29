package org.onosproject.floodlightpof.protocol.factory;

import org.onosproject.floodlightpof.protocol.OFBucket;

/**
 * @author tsf
 * @date 18-7-3
 * @desp  Objects implementing this interface are expected to be instantiated with an
 *        instance of an OFBucketFactory.
 */

public interface OFBucketFactoryAware {
    /**
     * Sets the OFBucketFactory.
     * @param bucketFactory
     */
    public void setBucketFactory(OFBucketFactory bucketFactory);
}
