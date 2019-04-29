package org.onosproject.floodlightpof.protocol.instruction;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.OFMatch20;
import org.onosproject.floodlightpof.util.HexString;

public class OFInstructionConditionalRelativeJump extends OFInstruction {
    public static final int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 8*2 + 2* OFMatch20.MINIMUM_LENGTH;

    protected byte field2Type;
    protected byte off1Dir;
    protected byte off2Dir;
    protected byte off3Dir;

    protected OFMatch20 cmpField1;

    protected int cmpField2Value;
    protected OFMatch20 cmpField2;

    protected int off1;
    protected int off2;
    protected int off3;


    public OFInstructionConditionalRelativeJump() {
        super.setType(OFInstructionType.CONDITIONAL_RELATIVE_JUMP);
        super.setLength((short) MINIMUM_LENGTH);
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        type = OFInstructionType.valueOf(data.readShort());
        length = data.readShort();
        field2Type = data.readByte();
        off1Dir = data.readByte();
        off2Dir = data.readByte();
        off3Dir = data.readByte();
        cmpField1 = new OFMatch20();
        cmpField1.readFrom(data);
        if (field2Type == 0) {
            cmpField2Value = data.readInt();
            data.readBytes(4);
            cmpField2 = null;
        } else if (field2Type == 1) {
            cmpField2Value = 0;
            cmpField2 = new OFMatch20();
            cmpField2.readFrom(data);
        } else {
            cmpField2Value = 0;
            cmpField2 = null;
            data.readBytes(OFMatch20.MINIMUM_LENGTH);
        }
        off1 = data.readInt();
        off2 = data.readInt();
        off3 = data.readInt();
        data.readBytes(4);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        data.writeShort(type.getTypeValue());
        data.writeShort(length);
        data.writeByte(field2Type);
        data.writeByte(off1Dir);
        data.writeByte(off2Dir);
        data.writeByte(off3Dir);
        if (cmpField1 != null) {
            cmpField1.writeTo(data);
        } else {
            data.writeZero(OFMatch20.MINIMUM_LENGTH);
        }

        if (field2Type == 0) {
            data.writeInt(cmpField2Value);
            data.writeZero(4);
        } else if (field2Type == 1 && cmpField2 != null) {
            cmpField2.writeTo(data);
        } else {
            data.writeZero(OFMatch20.MINIMUM_LENGTH);
        }
        data.writeInt(off1);
        data.writeInt(off2);
        data.writeInt(off3);
        data.writeZero(4);
    }

    @Override
    public String toBytesString() {
        String byteString = HexString.toHex(type.getTypeValue()) +
                HexString.toHex(length) +
                HexString.toHex(field2Type) +
                HexString.toHex(off1Dir) +
                HexString.toHex(off2Dir) +
                HexString.toHex(off3Dir);
        if (cmpField1 != null) {
            byteString += cmpField1.toBytesString();
        } else {
            byteString += HexString.byteZeroEnd(OFMatch20.MINIMUM_LENGTH);
        }

        if (field2Type == 0) {
            byteString += HexString.toHex(cmpField2Value) + HexString.byteZeroEnd(4);
        } else if (field2Type == 1 && cmpField2 != null) {
            byteString += cmpField2.toBytesString();
        } else {
            byteString += HexString.byteZeroEnd(OFMatch20.MINIMUM_LENGTH);
        }
        byteString += HexString.toHex(off1) +
                HexString.toHex(off2) +
                HexString.toHex(off3) +
                HexString.byteZeroEnd(4);
        return byteString;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += ";field2type=" + field2Type
                + ";off1dir=" + off1Dir
                + ";off2dir=" + off2Dir
                + ";off3dir=" + off3Dir
                + ";cmpfield1=" + cmpField1;

        if (field2Type == 0) {
            string += ";cmpfield2v=" + cmpField2Value;
        } else if (field2Type == 1) {
            string += ";cmpfield2r=" + cmpField2;
        } else {
            string += ";cmpfield2=0";
        }
        string += ";off1=" + off1 +
                ";off2=" + off2 +
                ";off3=" + off3;
        return string;
    }

    public byte getField2Type() {
        return field2Type;
    }

    public void setField2Type(byte field2Type) { this.field2Type = field2Type; }

    public byte getOff1Dir() { return off1Dir; }

    public void setOff1Dir(byte off1Dir) { this.off1Dir = off1Dir; }

    public byte getOff2Dir() { return off2Dir; }

    public void setOff2Dir(byte off2Dir) { this.off2Dir = off2Dir; }

    public byte getOff3Dir() { return off3Dir; }

    public void setOff3Dir(byte off3Dir) { this.off3Dir = off3Dir; }

    public OFMatch20 getCmpField1() {
        return cmpField1;
    }

    public void setCmpField1(OFMatch20 cmpField1) {
        this.cmpField1 = cmpField1;
    }

    public int getCmpField2Value() { return  cmpField2Value; }

    public void setCmpField2Value(int cmpField2Value) {
        this.cmpField2Value = cmpField2Value;
    }

    public OFMatch20 getCmpField2() {
        return cmpField2;
    }

    public void setCmpField2(OFMatch20 cmpField2) {
        this.cmpField2 = cmpField2;
    }

    public int getOff1() { return off1; }

    public void setOff1(int off1) {
        this.off1 = off1;
    }

    public int getOff2() { return off2; }

    public void setOff2(int off2) {
        this.off2 = off2;
    }
    public int getOff3() { return off3; }

    public void setOff3(int off3) {
        this.off3 = off3;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((cmpField1 == null) ? 0 : cmpField1.hashCode());
        result = prime * result
                + ((cmpField2 == null) ? 0 : cmpField2.hashCode());
        result = prime * result + field2Type;
        result = prime * result + off1Dir;
        result = prime * result + off2Dir;
        result = prime * result + off3Dir;
        result = prime * result + off1;
        result = prime * result + off2;
        result = prime * result + off3;
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
        if (!(obj instanceof OFInstructionConditionalRelativeJump)) {
            return false;
        }
        OFInstructionConditionalRelativeJump other = (OFInstructionConditionalRelativeJump) obj;
        if (field2Type != other.field2Type) {
            return false;
        }
        if (cmpField1 == null) {
            if (other.cmpField1 != null) {
                return false;
            }
        } else if (! cmpField1.equals(other.cmpField1)) {
            return false;
        }
        if (cmpField2 == null) {
            if (other.cmpField2 != null) {
                return false;
            }
        } else if (!cmpField2.equals(other.cmpField2)) {
            return false;
        }
        if (off1Dir != other.off1Dir) {
            return false;
        }
        if (off2Dir != other.off2Dir) {
            return false;
        }
        if (off3Dir != other.off3Dir) {
            return false;
        }
        if (off1 != other.off1) {
            return false;
        }
        if (off2 != other.off2) {
            return false;
        }
        if (off3 != other.off3) {
            return false;
        }
        return true;
    }

    @Override
    public OFInstructionBranch clone() throws CloneNotSupportedException {
        OFInstructionBranch ins = (OFInstructionBranch) super.clone();

        if (cmpField1 != null) {
            ins.setOperand1Field(cmpField1.clone());
        }

        if (cmpField2 != null) {
            ins.setOperand2Field(cmpField2.clone());
        }

        return ins;
    }
}
