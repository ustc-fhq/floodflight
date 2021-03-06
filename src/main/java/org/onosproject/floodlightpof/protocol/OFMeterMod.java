/**
 * Copyright (c) 2012, 2013, Huawei Technologies Co., Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.onosproject.floodlightpof.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.util.HexString;
import org.onosproject.floodlightpof.util.U16;

/**
 * Represents an ofp_meter_mod message.
 * OFMeterMod is used for limit the flow rate or shaping.
 *
 *
 */
public class OFMeterMod extends OFMessage {
    // public static int minimumLength = 16;
    public static int minimumLength = OFMessage.MINIMUM_LENGTH + 16;   // 24B

    public enum OFMeterModCmd {
        OFPMC_ADD,
        OFPMC_MODIFY,
        OFPMC_DELETE
    }

    protected OFMeterModCmd command;    // 1B
    protected short slotId; // tsf: POFSwitch-1.4.0.015
    protected int rate;     //kbps
    protected int meterId;

    public OFMeterMod() {
        super();
        this.type = OFType.METER_MOD;
        this.length = U16.t(minimumLength);
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        command = OFMeterModCmd.values()[ data.readByte() ];
        data.readBytes(1);
        slotId = data.readShort();
        rate = data.readInt();
        meterId = data.readInt();
        data.readBytes(4);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeByte((byte) command.ordinal());
        data.writeZero(1);
        data.writeShort(slotId);
        data.writeInt(rate);
        data.writeInt(meterId);
        data.writeZero(4);
    }

    public String toBytesString() {
        return super.toBytesString() +
                HexString.toHex((byte) command.ordinal()) +
                HexString.byteZeroEnd(1) +
                HexString.toHex(slotId) +
                HexString.toHex(rate) +
                HexString.toHex(meterId);
    }

    public String toString() {
        return super.toString() +
                "; MeterMod:" +
                "cmd=" + command +
                ";slot_id=" + slotId +
                ";rate=" + rate +
                ";mid=" + meterId;
    }

    public OFMeterModCmd getCommand() {
        return command;
    }

    public void setCommand(OFMeterModCmd command) {
        this.command = command;
    }

    public short getSlotId() {
        return slotId;
    }

    public void setSlotId(short slotId) {
        this.slotId = slotId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(short rate) {
        this.rate = rate;
    }

    public int getMeterId() {
        return meterId;
    }

    public void setMeterId(int meterId) {
        this.meterId = meterId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((command == null) ? 0 : command.hashCode());
        result = prime * result + slotId;
        result = prime * result + meterId;
        result = prime * result + rate;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        OFMeterMod other = (OFMeterMod) obj;
        if (command != other.command) {
            return false;
        }
        if (slotId != other.slotId) {
            return false;
        }
        if (meterId != other.meterId) {
            return false;
        }
        if (rate != other.rate) {
            return false;
        }
        return true;
    }
}
