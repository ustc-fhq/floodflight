package org.onosproject.floodlightpof.protocol.factory;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.OFBucket;

import java.util.List;

/**
 * @author tsf
 * @date 18-7-3
 * @desp likely to be OFActionFactory or OFInstructionFactory
 */
public interface OFBucketFactory {

    /**
     * bucket is same, no type, so return a new OFBucket object directly.
     */
    public OFBucket getBucket();

    /**
     * default limit is zero
     */
    public List<OFBucket> parseBuckets(ChannelBuffer data, int length);

    /**
     * parse OFBuckets in List<OFBucket>
     */
    public List<OFBucket> parseBuckets(ChannelBuffer data, int length, int limit);
}
