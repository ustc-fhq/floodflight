package org.onosproject.floodlightpof.protocol.instruction;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.OFMatch;
import org.onosproject.floodlightpof.protocol.OFMatch20;
import org.onosproject.floodlightpof.util.HexString;

public class OFInstructionCompare extends OFInstruction {
    public static final int MINIMUM_LENGTH = OFInstruction.MINIMUM_LENGTH + 3 * OFMatch20.MINIMUM_LENGTH;

    protected byte operand2Type;

    protected OFMatch20 cmpResField;

    protected OFMatch20 operand1Field;

    protected int operand2Value;
    protected OFMatch20 operand2Field;


    public OFInstructionCompare() {
        super.setType(OFInstructionType.COMPARE);
        super.setLength((short) MINIMUM_LENGTH);
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        type = OFInstructionType.valueOf(data.readShort());
        length = data.readShort();
        operand2Type = data.readByte();
        data.readBytes(3);
        cmpResField = new OFMatch20();
        cmpResField.readFrom(data);
        operand1Field = new OFMatch20();
        operand1Field.readFrom(data);
        if (operand2Type == 0) {
            operand2Value = data.readInt();
            data.readBytes(4);
            operand2Field = null;
        } else if (operand2Type == 1) {
            operand2Value = 0;
            operand2Field = new OFMatch20();
            operand2Field.readFrom(data);
        } else {
            operand2Value = 0;
            operand2Field = null;
            data.readBytes(OFMatch20.MINIMUM_LENGTH);
        }
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        data.writeShort(type.getTypeValue());
        data.writeShort(length);
        data.writeByte(operand2Type);
        data.writeZero(3);
        if (cmpResField != null) {
            cmpResField.writeTo(data);
        } else {
            data.writeZero(OFMatch20.MINIMUM_LENGTH);
        }
        if (operand1Field != null) {
            operand1Field.writeTo(data);
        } else {
            data.writeZero(OFMatch20.MINIMUM_LENGTH);
        }

        if (operand2Type == 0) {
            data.writeInt(operand2Value);
            data.writeZero(4);
        } else if (operand2Type == 1 && operand2Field != null) {
            operand2Field.writeTo(data);
        } else {
            data.writeZero(OFMatch20.MINIMUM_LENGTH);
        }
    }

    @Override
    public String toBytesString() {
        String byteString = HexString.toHex(type.getTypeValue()) +
                HexString.toHex(length) +
                HexString.toHex(operand2Type) +
                HexString.byteZeroEnd(3);
        if (cmpResField != null) {
            byteString += cmpResField.toBytesString();
        } else {
            byteString += HexString.byteZeroEnd(OFMatch20.MINIMUM_LENGTH);
        }
        if (operand1Field != null) {
            byteString += operand1Field.toBytesString();
        } else {
            byteString += HexString.byteZeroEnd(OFMatch20.MINIMUM_LENGTH);
        }

        if (operand2Type == 0) {
            byteString += HexString.toHex(operand2Value) + HexString.byteZeroEnd(4);
        } else if (operand2Type == 1 && operand2Field != null) {
            byteString += operand2Field.toBytesString();
        } else {
            byteString += HexString.byteZeroEnd(OFMatch20.MINIMUM_LENGTH);
        }

        return byteString;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string += ";op2t=" + operand2Type
                +"crf=" + cmpResField
                + ";op1f=" + operand1Field;

        if (operand2Type == 0) {
            string += ";op2v=" + operand2Value;
        } else if (operand2Type == 1) {
            string += ";op2f=" + operand2Field;
        } else {
            string += ";op2=0";
        }

        return string;
    }


    public byte getOperand2Type() {
        return operand2Type;
    }

    public void setOperand2Type(byte operand2Type) {
        this.operand2Type = operand2Type;
    }

    public OFMatch20 getCmpResField(){ return cmpResField; }

    public void setCmpResField(OFMatch20 cmpResField) { this.cmpResField = cmpResField;}

    public OFMatch20 getOperand1Field() {
        return operand1Field;
    }

    public void setOperand1Field(OFMatch20 operand1Field) {
        this.operand1Field = operand1Field;
    }

    public int getOperand2Value() {
        return operand2Value;
    }

    public void setOperand2Value(int operand2Value) {
        this.operand2Value = operand2Value;
    }

    public OFMatch20 getOperand2Field() {
        return operand2Field;
    }

    public void setOperand2Field(OFMatch20 operand2Field) {
        this.operand2Field = operand2Field;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((operand1Field == null) ? 0 : operand1Field.hashCode());
        result = prime * result
                + ((operand2Field == null) ? 0 : operand2Field.hashCode());
        result = prime * result
                + ((cmpResField == null) ? 0 : cmpResField.hashCode());
        result = prime * result + operand2Type;
        result = prime * result + operand2Value;
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
        if (!(obj instanceof OFInstructionCompare)) {
            return false;
        }
        OFInstructionCompare other = (OFInstructionCompare) obj;
        if (cmpResField == null) {
            if (other.cmpResField != null) {
                return false;
            }
        } else if (!operand1Field.equals(other.operand1Field)) {
            return false;
        }
        if (operand1Field == null) {
            if (other.operand1Field != null) {
                return false;
            }
        } else if (!operand1Field.equals(other.operand1Field)) {
            return false;
        }
        if (operand2Field == null) {
            if (other.operand2Field != null) {
                return false;
            }
        } else if (!operand2Field.equals(other.operand2Field)) {
            return false;
        }
        if (operand2Type != other.operand2Type) {
            return false;
        }
        if (operand2Value != other.operand2Value) {
            return false;
        }
        return true;
    }

    @Override
    public OFInstructionCompare clone() throws CloneNotSupportedException {
        OFInstructionCompare ins = (OFInstructionCompare) super.clone();
        if (cmpResField != null) {
            ins.setOperand1Field(cmpResField.clone());
        }
        if (operand1Field != null) {
            ins.setOperand1Field(operand1Field.clone());
        }

        if (operand2Field != null) {
            ins.setOperand2Field(operand2Field.clone());
        }

        return ins;
    }
}
