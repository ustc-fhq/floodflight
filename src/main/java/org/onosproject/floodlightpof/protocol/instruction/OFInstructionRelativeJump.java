package org.onosproject.floodlightpof.protocol.instruction;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.OFMatch20;
import org.onosproject.floodlightpof.util.HexString;

public class OFInstructionRelativeJump extends OFInstruction {
    public static final int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 8;

    protected byte direction;

    protected int offset;

    public OFInstructionRelativeJump() {
        super.setType(OFInstructionType.RELATIVE_JUMP);
        super.setLength((short) MINIMUM_LENGTH);
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        type = OFInstructionType.valueOf(data.readShort());
        length = data.readShort();
        direction = data.readByte();
        data.readBytes(3);
        offset = data.readInt();
        data.readBytes(4);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        data.writeShort(type.getTypeValue());
        data.writeShort(length);
        data.writeByte(direction);
        data.writeZero(3);
        data.writeInt(offset);
        data.writeZero(4);
    }

    @Override
    public String toBytesString() {
        String byteString = HexString.toHex(type.getTypeValue()) +
                HexString.toHex(length) +
                HexString.toHex(direction) +
                HexString.byteZeroEnd(3) +
                HexString.toHex(offset) +
                HexString.byteZeroEnd(4);
        return byteString;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += ";dir=" + direction
                + ";offset=" + offset;

        return string;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + direction;
        result = prime * result + offset;
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
        if (!(obj instanceof OFInstructionRelativeJump)) {
            return false;
        }
        OFInstructionRelativeJump other = (OFInstructionRelativeJump) obj;
        if (direction != other.direction) {
            return false;
        }
        if (offset != other.offset) {
            return false;
        }
        return true;
    }
}
