package org.onosproject.floodlightpof.protocol.instruction;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.OFGlobal;
import org.onosproject.floodlightpof.util.HexString;
import java.util.Arrays;

public class OFInstructionGetMetadate extends OFInstruction{
    public static final int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 8;

    protected short metadataOffset;

    protected byte[] value;

    public OFInstructionGetMetadate() {
        super.setType(OFInstructionType.WRITE_METADATA_FROM_PACKET);
        super.setLength((short) MINIMUM_LENGTH);
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        type = OFInstructionType.valueOf(data.readShort());
        metadataOffset = data.readShort();
        length = data.readShort();
        data.readBytes(2);
        value = new byte[OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE];
        data.readBytes(value);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        data.writeShort(type.getTypeValue());
        data.writeShort(metadataOffset);
        data.writeShort(length);
        data.writeZero(2);
        if (value == null) {
            data.writeZero(OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE);
        } else {
            if (value.length > OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE) {
                data.writeBytes(value, OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE - value
                        .length, OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE);
            } else {
                data.writeBytes(value);
                data.writeZero(OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE - value.length);
            }
        }
    }

    @Override
    public String toBytesString() {
        String string = HexString.toHex(type.getTypeValue()) +
                HexString.toHex(metadataOffset) +
                HexString.toHex(length) +
                HexString.byteZeroEnd(2) +
                HexString.toHex(value);
        if (value == null) {
            string += HexString.byteZeroEnd(OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE);
        } else {
            if (value.length > OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE) {
                string += HexString.toHex(value, 0, OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE);
                string += HexString.zeroEnd(0);
            } else {
                //string += HexString.ZeroEnd(0);
                string += HexString.toHex(value);
                //string += HexString.ZeroEnd(0);
                string += HexString.byteZeroEnd(OFGlobal.OFP_MAX_FIELD_LENGTH_IN_BYTE - value.length);
            }
        }

        return string;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += ";meta_offset=" + metadataOffset
                + ";value=" + HexString.toHex(value) ;

        return string;
    }

    public short getMetadataOffset() {
        return metadataOffset;
    }

    public void setMetadataOffset(short metadataOffset) {
        this.metadataOffset = metadataOffset;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + metadataOffset;
        result = prime * result + Arrays.hashCode(value);
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
        if (!(obj instanceof OFInstructionGetMetadate)) {
            return false;
        }
        OFInstructionGetMetadate other = (OFInstructionGetMetadate) obj;
        if (metadataOffset != other.metadataOffset) {
            return false;
        }
        if (!Arrays.equals(value, other.value)) {
            return false;
        }
        return true;
    }
}