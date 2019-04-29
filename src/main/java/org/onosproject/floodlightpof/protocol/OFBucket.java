package org.onosproject.floodlightpof.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.action.OFAction;
import org.onosproject.floodlightpof.protocol.factory.OFActionFactory;
import org.onosproject.floodlightpof.util.HexString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsf
 * @date 18-7-2
 * @desp each group have six buckets, and every bucket contains six actions in actionList.
 */
public class OFBucket implements Cloneable {

    public static final int MINIMUM_LENGTH = 16;
    public static final int MAXIMAL_LENGTH = MINIMUM_LENGTH + OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET
                                             * OFAction.MAXIMAL_LENGTH;  // 16 + 6 * 48 = 304 bytes

    /**
     * struct ofp_bucket
     */
    protected short action_num;
    protected short weight;
    protected short watch_slot_id;   // only required for fast failover groups
    protected byte watch_port;       // only required for fast failover groups
    // 1 byte padding
    protected int watch_group;
    // 4 byte padding
    protected List<OFAction> actionList;

    protected OFActionFactory actionFactory;

    public OFBucket() {
        this.action_num = 0;
        this.weight = (short) 0xffff;    // only required for select groups
        this.watch_slot_id = 0;
        this.watch_port = (byte) 0xff;   // OFPP_NONE
        this.watch_group = 0xffffffff;   // OFPG_ANY
        this.actionList = new ArrayList<>();
    }

    public OFBucket(short action_num, short weight, short watch_slot_id,
                    byte watch_port, int watch_group, List<OFAction> actionList) {
        this.action_num = action_num;
        this.weight = weight;
        this.watch_slot_id = watch_slot_id;
        this.watch_port = watch_port;
        this.watch_group = watch_group;
        this.actionList = actionList;
    }

    public int getLengthU() {
        return MAXIMAL_LENGTH;
    }

    public String toString() {
        String string = "OFBucket: action_num=" + action_num +
                        ";weight=" + weight +
                        ";watch_slot_id=" + watch_slot_id +
                        ";watch_port=" + watch_port +
                        ";watch_group=" + watch_group;

        if (actionList == null) {
            string += ";action_list=null";
        } else {
            for (OFAction action : actionList) {
                if (action != null) {
                    string += ";actions=" + action.toString();
                }
            }
        }

        return string;
    }

    public short getAction_num() {
        return this.action_num;
    }

    public void setAction_num(short action_num) {
        this.action_num = action_num;
    }

    public short getWeight() {
        return weight;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }

    public short getWatch_slot_id() {
        return watch_slot_id;
    }

    public void setWatch_slot_id(short watch_slot_id) {
        this.watch_slot_id = watch_slot_id;
    }

    public byte getWatch_port() {
        return watch_port;
    }

    public void setWatch_port(byte watch_port) {
        this.watch_port = watch_port;
    }

    public int getWatch_group() {
        return watch_group;
    }

    public void setWatch_group(int watch_group) {
        this.watch_group = watch_group;
    }

    public List<OFAction> getActionList() {
        return actionList;
    }

    public void setActionList(List<OFAction> actionList) {
        this.actionList = actionList;
    }

    public OFActionFactory getActionFactory() {
        return actionFactory;
    }

    public void setActionFactory(OFActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();

        result = prime * result + ((actionFactory == null) ? 0 : actionFactory.hashCode());
        result = prime * result + ((actionList == null) ? 0 : actionList.hashCode());
        result = prime * result + action_num;
        result = prime * result + weight;
        result = prime * result + watch_slot_id;
        result = prime * result + watch_group;

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

        OFBucket other = (OFBucket) obj;
        if (actionFactory == null) {
            if (other.actionFactory != null) {
                return false;
            }
        } else if (!actionFactory.equals(other.actionFactory)) {
            return false;
        }

        if (actionList == null) {
            if (other.actionList != null) {
                return false;
            }
        } else if (!actionList.equals(other.actionList)) {
            return false;
        }

        if (action_num != other.action_num) {
            return false;
        }

        if (weight != other.weight) {
            return false;
        }

        if (watch_slot_id != other.watch_slot_id) {
            return false;
        }

        if (watch_port != other.watch_port) {
            return false;
        }

        if (watch_group != other.watch_group) {
            return false;
        }

        return true;
    }

    public void readFrom(ChannelBuffer data) {
        action_num = data.readShort();
        weight = data.readShort();
        watch_slot_id = data.readShort();
        watch_port = data.readByte();

        data.readBytes(1);
        watch_group = data.readInt();
        data.readBytes(4);

        this.actionList = this.actionFactory.parseActions(data, OFGlobal.
                OFP_MAX_ACTION_NUMBER_PER_BUCKET * OFAction.MAXIMAL_LENGTH);
    }

    public void writeTo(ChannelBuffer data) {
        data.writeShort(action_num);
        data.writeShort(weight);
        data.writeShort(watch_slot_id);
        data.writeByte(watch_port);

        data.writeZero(1);
        data.writeInt(watch_group);
        data.writeZero(4);

        if (actionList == null) {
            data.writeZero(OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET * OFAction.MAXIMAL_LENGTH);
        } else {
            OFAction action;

            if (action_num > actionList.size()) {
                throw new RuntimeException("actionNum " + action_num + " > actionList.size() " + actionList.size());
            }

            int i;
            for (i = 0; i < action_num && i < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET; i++) {
                action = actionList.get(i);
                if (action == null) {
                    data.writeZero(OFAction.MAXIMAL_LENGTH);
                } else {
                    action.writeTo(data);   // @tsf: write action header, caller needs to pad zeros
                    if (action.getLength() < OFAction.MAXIMAL_LENGTH) {
                        data.writeZero(OFAction.MAXIMAL_LENGTH - action.getLength());
                    }
                }
            }

            // @tsf: pad zeros if actions_num less than six, so caller doesn't need to pad zeros
            if (i < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET) {
                data.writeZero((OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET - i) * OFAction.MAXIMAL_LENGTH);
            }
        }

    }

    public String toBytesString() {
        String string = HexString.toHex(action_num) +
                        HexString.toHex(weight) +
                        HexString.toHex(watch_slot_id) +
                        HexString.toHex(watch_port) +
                        HexString.byteZeroEnd(1) +
                        HexString.toHex(watch_group) +
                        HexString.byteZeroEnd(4);

        if (actionList == null) {
            string += HexString.byteZeroEnd(OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET * OFAction.MAXIMAL_LENGTH);
        } else {
            OFAction action;

            if (action_num > actionList.size()) {
                throw new RuntimeException("actionNum " + action_num + " > actionList.size() " + actionList.size());
            }

            int i;
            for (i = 0; i < action_num && i < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET; i++) {
                action = actionList.get(i);

                if (action == null) {
                    string += HexString.byteZeroEnd(OFAction.MAXIMAL_LENGTH);
                } else {
                    string += action.toBytesString();
                    if (action.getLength() < OFAction.MAXIMAL_LENGTH) {
                        string += HexString.byteZeroEnd(OFAction.MAXIMAL_LENGTH - action.getLength());
                    }
                }
            }

            if (i < OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET) {
                string += HexString.byteZeroEnd((OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET - i) *
                        OFAction.MAXIMAL_LENGTH);
            }
        }
        return string;
    }

    @Override
    public OFBucket clone() throws CloneNotSupportedException {
        // deep clone
        OFBucket bucket = (OFBucket) super.clone();

        int i = 0;
        for (OFAction action : this.actionList) {
            bucket.actionList.set(i++, action.clone());
        }

        return bucket;
    }
}
