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

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.action.OFAction;
import org.onosproject.floodlightpof.protocol.factory.OFActionFactory;
import org.onosproject.floodlightpof.protocol.factory.OFActionFactoryAware;
import org.onosproject.floodlightpof.protocol.factory.OFBucketFactory;
import org.onosproject.floodlightpof.protocol.factory.OFBucketFactoryAware;
import org.onosproject.floodlightpof.util.HexString;
import org.onosproject.floodlightpof.util.U16;

/**
 * Represents an ofp_group_mod message.
 *
 * @desp modified by tsf. Implement all four kinds of ofp_group_mod message, i.e.
 *       all, indirect, select, fast failover. And make every ofp_group_mod contains
 *       up to six buckets. If buckets num less than six, then pad zeros.
 */
public class OFGroupMod extends OFMessage implements OFBucketFactoryAware, Cloneable {
    public static final int MINIMUM_LENGTH = OFMessage.MINIMUM_LENGTH + 16;  // 24B
    public static final int MAXIMAL_LENGTH = MINIMUM_LENGTH + OFGlobal.OFP_MAX_ACTION_NUMBER_PER_BUCKET
                                            * OFBucket.MAXIMAL_LENGTH;  // 24 + 6*304 = 1848 bytes

    public enum OFGroupModCmd {
        OFPGC_ADD,
        OFPGC_MODIFY,
        OFPGC_DELETE
    }

    public enum OFGroupType {
        OFPGT_ALL,
        OFPGT_SELECT,
        OFPGT_INDIRECT,
        OFPGT_FF
    }

    protected byte command;
    protected byte groupType;
    protected byte bucketNum;
    // padding 1 bytes
    protected int groupId;

    protected int counterId;
    protected short slotId;
    // padding 2 bytes

    protected List<OFBucket> bucketList;  // the size is up to six

    protected OFBucketFactory bucketFactory;

    public OFGroupMod() {
        super();
        this.type = OFType.GROUP_MOD;
//        this.length = U16.t(MINIMUM_LENGTH);
        this.length = U16.t(MAXIMAL_LENGTH);     // tsf: store the max len
    }

    @Override
    public int getLengthU() {
        return MAXIMAL_LENGTH;
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);

        command = data.readByte();
        groupType = data.readByte();
        bucketNum = data.readByte();
        data.readByte();
        groupId = data.readInt();

        counterId = data.readInt();
        slotId = data.readShort();
        data.readBytes(2);

        // read bucket_list, need to implement a OFBucketFactory? in fact, there is no reading for controller
        this.bucketList = this.bucketFactory.parseBuckets(data, OFGlobal.
                              OFP_MAX_BUCKET_NUMBER_PER_GROUP * OFBucket.MAXIMAL_LENGTH);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeByte(command);
        data.writeByte(groupType);
        data.writeByte(bucketNum);
        data.writeZero(1);
        data.writeInt(groupId);

        data.writeInt(counterId);
        data.writeShort(slotId);
        data.writeZero(2);

        if (bucketList == null) {
            data.writeZero(OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP * OFBucket.MAXIMAL_LENGTH);
        } else {
            OFBucket bucket;

            if (bucketNum > bucketList.size()) {
                throw new RuntimeException("bucketNum " + bucketNum + " > bucketList.size()" + bucketList.size());
            }

            int i;
            for (i = 0; i < bucketNum && i < OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP; i++) {
                bucket = bucketList.get(i);

                if (bucket == null) {
                    data.writeZero(OFBucket.MAXIMAL_LENGTH);
                } else {
                    bucket.writeTo(data);
                }
            }

            // @tsf: if buckets_num less than six, pad zeros here
            if (i < OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP) {
                data.writeZero((OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP - i) *
                                OFBucket.MAXIMAL_LENGTH);
            }
        }
    }

    public String toBytesString() {
        String string = super.toString();
        string += HexString.toHex(command) +
                    HexString.toHex(groupType) +
                    HexString.toHex(bucketNum) +
                    HexString.byteZeroEnd(1) +
                    HexString.toHex(groupId) +
                    HexString.toHex(counterId) +
                    HexString.toHex(slotId) +
                    HexString.byteZeroEnd(2);

        if (bucketList == null) {
            string += HexString.byteZeroEnd(OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP * OFBucket.MAXIMAL_LENGTH);
        } else {
            OFBucket bucket;

            if (bucketNum > bucketList.size()) {
                throw new RuntimeException("bucketNum " + bucketNum + " > bucketList.size()" + bucketList.size());
            }

            int i;
            for (i = 0; i < bucketNum && i < OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP; i++) {
                bucket = bucketList.get(i);
                if (bucket == null) {
                    string += HexString.byteZeroEnd(OFBucket.MAXIMAL_LENGTH);
                } else {
                    string += bucket.toBytesString();
                }
            }
            if (i < OFGlobal.OFP_MAX_BUCKET_NUMBER_PER_GROUP) {
                string += HexString.byteZeroEnd((OFGlobal
                        .OFP_MAX_BUCKET_NUMBER_PER_GROUP - i) * OFBucket.MAXIMAL_LENGTH);
            }
        }

        return string;
    }

    public String toString() {
        String string = super.toString();
        string += "; GroupMod:" +
                "command=" + command +
                ";type=" + groupType +
                ";bucketNum=" + bucketNum +
                ";groupId=" + groupId +
                ";counterId=" + counterId +
                ";slotId=" + slotId;

        if (bucketList == null) {
            string += "bucketList=null";
        } else {
            for (OFBucket bucket : bucketList) {
                if (bucket != null) {
                    string += ";bucket=" + bucket.toString();
                }
            }
        }

        return string;
    }

    public byte getCommand() {
        return command;
    }


    public void setCommand(byte command) {
        this.command = command;
    }

    public void setCommand(OFGroupModCmd command) {
        this.command = (byte) command.ordinal();
    }


    public byte getGroupType() {
        return groupType;
    }


    public void setGroupType(byte groupType) {
        this.groupType = groupType;
    }


    public byte getBucketNum() {
        return bucketNum;
    }


    public void setBucketNum(byte bucketNum) {
        this.bucketNum = bucketNum;
    }


    public int getGroupId() {
        return groupId;
    }


    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public int getCounterId() {
        return counterId;
    }


    public void setCounterId(int counterId) {
        this.counterId = counterId;
    }


    public List<OFBucket> getBucketList() {
        return bucketList;
    }


    public void setBucketList(List<OFBucket> bucketList) {
        this.bucketList = bucketList;
    }

    public short getSlotId() {
        return slotId;
    }

    public void setSlotId(short slotId) {
        this.slotId = slotId;
    }

    public void setBucketFactory(OFBucketFactory bucketFactory) {
        this.bucketFactory = bucketFactory;
    }

    public OFBucketFactory getBucketFactory() {
        return bucketFactory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bucketFactory == null) ? 0 : bucketFactory.hashCode());
        result = prime * result + ((bucketList == null) ? 0 : bucketList.hashCode());
        result = prime * result + bucketNum;
        result = prime * result + command;
        result = prime * result + counterId;
        result = prime * result + slotId;
        result = prime * result + groupId;
        result = prime * result + groupType;
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
        OFGroupMod other = (OFGroupMod) obj;
        if (bucketFactory == null) {
            if (other.bucketFactory != null) {
                return false;
            }
        } else if (!bucketFactory.equals(other.bucketFactory)) {
            return false;
        }
        if (bucketList == null) {
            if (other.bucketList != null) {
                return false;
            }
        } else if (!bucketList.equals(other.bucketList)) {
            return false;
        }
        if (bucketNum != other.bucketNum) {
            return false;
        }
        if (command != other.command) {
            return false;
        }
        if (counterId != other.counterId) {
            return false;
        }
        if (slotId != other.slotId) {
            return false;
        }
        if (groupId != other.groupId) {
            return false;
        }
        if (groupType != other.groupType) {
            return false;
        }
        return true;
    }

    @Override
    public OFGroupMod clone() throws CloneNotSupportedException {
        OFGroupMod groupMod = (OFGroupMod) super.clone();

        if (null != bucketList
                && 0 != bucketList.size()
                && 0 != bucketNum) {
            List<OFBucket> neoBucketList = new ArrayList<>();
            for (OFBucket ofBucket: this.bucketList) {
                neoBucketList.add(ofBucket.clone());
            }
            groupMod.setBucketList(neoBucketList);
        }

        return groupMod;
    }
}
