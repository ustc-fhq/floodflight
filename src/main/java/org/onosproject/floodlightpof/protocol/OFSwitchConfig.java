/**
*    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
*    University
*
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package org.onosproject.floodlightpof.protocol;


import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Base class representing ofp_switch_config based messages.
 */
public abstract class OFSwitchConfig extends OFMessage {
    public static int minimumLength = 16;  // 16B

    public enum OFConfigFlags {
        OFPC_FRAG_NORMAL,
        OFPC_FRAG_DROP,
        OFPC_FRAG_REASM,
        OFPC_FRAG_MASK
    }

    protected int deviceid;
    protected short flags;
    protected short missSendLength;  // OFP_DEFAULT_MISS_SEND_LEN = 128B

    public OFSwitchConfig() {
        super();
        super.setLengthU(minimumLength);
    }

    public int getDeviceid() {
        return deviceid;
    }


    public void setDeviceid(int deviceid) {
        this.deviceid = deviceid;
    }


    /**
     * @return the flags
     */
    public short getFlags() {
        return flags;
    }

    /**
     * @param flaGs the flags to set
     */
    public OFSwitchConfig setFlags(short flaGs) {
        this.flags = flaGs;
        return this;
    }

    /**
     * @return the missSendLength
     */
    public short getMissSendLength() {
        return missSendLength;
    }

    /**
     * @param missSendLenGth the missSendLength to set
     */
    public OFSwitchConfig setMissSendLength(short missSendLenGth) {
        this.missSendLength = missSendLenGth;
        return this;
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        this.deviceid = data.readInt();
        this.flags = data.readShort();
        this.missSendLength = data.readShort();
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeInt(this.deviceid);
        data.writeShort(this.flags);
        data.writeShort(this.missSendLength);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + deviceid;
        result = prime * result + flags;
        result = prime * result + missSendLength;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OFSwitchConfig)) {
            return false;
        }
        OFSwitchConfig other = (OFSwitchConfig) obj;
        if (deviceid != other.deviceid) {
            return false;
        }
        if (flags != other.flags) {
            return false;
        }
        if (missSendLength != other.missSendLength) {
            return false;
        }
        return true;
    }
}
