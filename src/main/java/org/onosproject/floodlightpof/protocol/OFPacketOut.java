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

import java.util.Arrays;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.action.OFAction;
import org.onosproject.floodlightpof.protocol.factory.OFActionFactory;
import org.onosproject.floodlightpof.protocol.factory.OFActionFactoryAware;
import org.onosproject.floodlightpof.util.U16;

/**
 * Represents an ofp_packet_out message.
 *
 */
public class OFPacketOut extends OFMessage implements OFActionFactoryAware {
    public static int minimumLength = 16;    // 16B
    public static int maximumLength = 2360;  // 2360B
    public static int bufferIdNone = 0xffffffff;

    protected OFActionFactory actionFactory;
    protected int bufferId;
    protected int inPort;
    protected byte actionsLength;  /* tsf: actionNum */
    protected List<OFAction> actions;
    protected byte[] packetData;
    private static final int OFPMAXACTION_PERINSTRUCTION = 6;
    private static final int OFPPACKETIN_MAXLENGTH = 2048;
    private static final byte[] PADDING = new byte[128];
    public OFPacketOut() {
        super();
        this.type = OFType.PACKET_OUT;
        // this.length = U16.t(minimumLength);
        this.length = U16.t(maximumLength);
    }

    /**
     * Get buffer_id.
     * @return bufferId
     */
    public int getBufferId() {
        return this.bufferId;
    }

    /**
     * Set buffer_id.
     * @param buffErId
     */
    public OFPacketOut setBufferId(int buffErId) {
        this.bufferId = buffErId;
        return this;
    }

    /**
     * Returns the packet data.
     * @return packetData
     */
    public byte[] getPacketData() {
        return this.packetData;
    }

    /**
     * Sets the packet data.
     * @param pacKetData
     */
    public OFPacketOut setPacketData(byte[] pacKetData) {
        this.packetData = pacKetData;
        return this;
    }

    /**
     * Get in_port.
     * @return inPort
     */
    public int getInPort() {
        return this.inPort;
    }

    /**
     * Set in_port.
     * @param inPoRt
     */
    public OFPacketOut setInPort(int inPoRt) {
        this.inPort = inPoRt;
        return this;
    }

    /**
     * Set in_port. Convenience method using OFPort enum.
     * @param inPoRt
     */
    public OFPacketOut setInPort(OFPort inPoRt) {
        this.inPort = inPoRt.getValue();
        return this;
    }

    /**
     * Get actions_len.
     * @return actionsLength
     */
    public byte getActionsLength() {
        return this.actionsLength;
    }

    /**
     * Get actions_len, unsigned.
     * @return actionsLength
     */
    public int getActionsLengthU() {
        return U16.f(this.actionsLength);
    }

    /**
     * Set actions_len.
     * @param actionsLenGth
     */
    public OFPacketOut setActionsLength(short actionsLenGth) {
        this.actionsLength = (byte) actionsLenGth;
        return this;
    }

    /**
     * Returns the actions contained in this message.
     * @return a list of ordered OFAction objects
     */
    public List<OFAction> getActions() {
        return this.actions;
    }

    /**
     * Sets the list of actions on this message.
     * @param actIons a list of ordered OFAction objects
     */
    public OFPacketOut setActions(List<OFAction> actIons) {
        this.actions = actIons;
        return this;
    }

    @Override
    public void setActionFactory(OFActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    /* tsf: Controller will not process PacketOut message. */
    @Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        this.bufferId = data.readInt();
        this.inPort = data.readShort();
        this.actionsLength = data.readByte();
        if (this.actionFactory == null) {
            throw new RuntimeException("ActionFactory not set");
        }
        this.actions = this.actionFactory.parseActions(data, getActionsLengthU());
        this.packetData = new byte[getLengthU() - minimumLength - getActionsLengthU()];
        data.readBytes(this.packetData);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeInt(this.bufferId);
        data.writeInt(this.inPort);
        data.writeByte(this.actionsLength);
        // data.writeBytes(PADDING, 0, 3);
        data.writeZero(3);
        data.writeInt(this.packetData.length);
        if (this.actionsLength == 0) {
            data.writeZero(OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION * OFAction.MAXIMAL_LENGTH);  // 6 * 48
        } else {
            for (OFAction action : actions) {
                action.writeTo(data);
                if (action.getLength() < OFAction.MAXIMAL_LENGTH) {
                    data.writeZero(OFAction.MAXIMAL_LENGTH - action.getLength());
                }
            }
            if (this.actions.size() < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION) {
                data.writeZero((OFGlobal.OFP_MAX_ACTION_NUMBER_PER_INSTRUCTION - this.actions.size()) *
                                OFAction.MAXIMAL_LENGTH);
            }

            if (this.packetData != null) {
                if (this.packetData.length < OFGlobal.OFP_PACKET_IN_MAX_LENGTH) {  /* check length */
                    data.writeBytes(this.packetData);
                }
            }
            int blank = OFGlobal.OFP_PACKET_IN_MAX_LENGTH - this.packetData.length;
            if (blank > 1024) {
                data.writeZero(1024);
                data.writeZero(blank - 1024);
            } else {
                data.writeZero(blank);
            }
        }

    }

    @Override
    public int hashCode() {
        final int prime = 293;
        int result = super.hashCode();
        result = prime * result + ((actions == null) ? 0 : actions.hashCode());
        result = prime * result + actionsLength;
        result = prime * result + bufferId;
        result = prime * result + inPort;
        result = prime * result + Arrays.hashCode(packetData);
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
        if (!(obj instanceof OFPacketOut)) {
            return false;
        }
        OFPacketOut other = (OFPacketOut) obj;
        if (actions == null) {
            if (other.actions != null) {
                return false;
            }
        } else if (!actions.equals(other.actions)) {
            return false;
        }
        if (actionsLength != other.actionsLength) {
            return false;
        }
        if (bufferId != other.bufferId) {
            return false;
        }
        if (inPort != other.inPort) {
            return false;
        }
        if (!Arrays.equals(packetData, other.packetData)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OFPacketOut [actionFactory=" + actionFactory + ", actions="
                + actions + ", actionsLength=" + actionsLength + ", bufferId=0x"
                + Integer.toHexString(bufferId) + ", inPort=" + inPort + ", packetData="
                + Arrays.toString(packetData) + "]";
    }
}
