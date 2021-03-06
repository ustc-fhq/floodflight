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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.floodlightpof.protocol.serializers.OFFeaturesReplyJsonSerializer;
import org.onosproject.floodlightpof.util.HexString;
import org.onosproject.floodlightpof.util.ParseString;
import org.onosproject.floodlightpof.util.U16;

/**
 * Modified by Song Jian (jack.songjian@huawei.com), Huawei Technologies Co., Ltd.
 *      Modified items of enum OFCapabilities
 *          remove OFPC_STP
 *          remove OFPC_RESERVED
 *          remove OFPC_ARP_MATCH_IP
 *          add     OPFC_GROUP_STATS
 *          add     OPFC_PROT_BLOCKED
 *
 *      Modified the class members
 *          change long datapathID to int deviceId
 *          delete buffers, tables, actions, List ports
 *          add  portNum, tableNum, experimenterName, deviceForwardingEngineName, deviceLookupEngineName
 *
 *      Modified the get/set methods and readFrom/writeTo methods based on updated class members
 *
 */

/**
 * Represents a features reply message.
 *
 */
@JsonSerialize(using = OFFeaturesReplyJsonSerializer.class)
public class OFFeaturesReply extends OFMessage {
    public static int minimumLength = 16 + 3 * OFGlobal.OFP_NAME_MAX_LENGTH + OFMessage.MINIMUM_LENGTH; // 216B
    //public static int minimumLength = 216;   // added by tsf

    /**
     * Corresponds to bits on the capabilities field.
     */
    public enum OFCapabilities {
        OFPC_FLOW_STATS(1 << 0),
        OFPC_TABLE_STATS(1 << 1),
        OFPC_PORT_STATS(1 << 2),
        OFPC_GROUP_STATS(1 << 3),
        //OFPC_RESERVED       (1 << 4),
        OFPC_IP_REASM(1 << 5),
        OFPC_QUEUE_STATS(1 << 6),
        //OFPC_ARP_MATCH_IP   (1 << 7);
        OFPC_PORT_BLOCKED(1 << 8);

        protected int value;

        private OFCapabilities(int value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }
    }

    protected int   deviceId;
    protected short slotid;
    protected short portNum;

    protected short tableNum;

    protected int capabilities;

    protected String    experimenterName;           //32B?? tsf: 64B
    protected String    deviceForwardEngineName;    // 64B
    protected String    deviceLookupEngineName;     // 64B

    public OFFeaturesReply() {
        super();
        this.type = OFType.FEATURES_REPLY;
        this.length = U16.t(minimumLength);
    }

    /**
     * @return the capabilities
     */
    public int getCapabilities() {
        return capabilities;
    }

    /**
     * @param capabilities the capabilities to set
     */
    public void setCapabilities(int capabilities) {
        this.capabilities = capabilities;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public short getSlotid() {
        return slotid;
    }

    public void setSlotid(short slotid) {
        this.slotid = slotid;
    }

    public short getPortNum() {
        return portNum;
    }

    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }

    public short getTableNum() {
        return tableNum;
    }

    public void setTableNum(short tableNum) {
        this.tableNum = tableNum;
    }

    public String getExperimenterName() {
        return experimenterName;
    }

    public void setExperimenterName(String experimenterName) {
        this.experimenterName = experimenterName;
    }

    public String getDeviceForwardEngineName() {
        return deviceForwardEngineName;
    }

    public void setDeviceForwardEngineName(String deviceForwardEngineName) {
        this.deviceForwardEngineName = deviceForwardEngineName;
    }

    public String getDeviceLookupEngineName() {
        return deviceLookupEngineName;
    }

    public void setDeviceLookupEngineName(String deviceLookupEngineName) {
        this.deviceLookupEngineName = deviceLookupEngineName;
    }

    @Override
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        this.deviceId = data.readInt();
        this.slotid = data.readShort();
        this.portNum = data.readShort();

        this.tableNum = data.readShort();
        data.readShort();
        this.capabilities = data.readInt();

        this.experimenterName = ParseString.nameByteToString(data);
        this.deviceForwardEngineName = ParseString.nameByteToString(data);
        this.deviceLookupEngineName = ParseString.nameByteToString(data);
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        data.writeInt(this.deviceId);
        data.writeShort(this.slotid);
        data.writeShort(this.portNum);

        data.writeShort(this.tableNum);
        data.writeZero(2);
        data.writeInt(this.capabilities);

        data.writeBytes(ParseString.nameStringToBytes(experimenterName));
        data.writeBytes(ParseString.nameStringToBytes(deviceForwardEngineName));
        data.writeBytes(ParseString.nameStringToBytes(deviceLookupEngineName));
    }

    public String toBytesString() {
        String bytesString = super.toBytesString();

        bytesString += HexString.toHex(deviceId);

        bytesString += HexString.toHex(slotid);
        bytesString += HexString.toHex(portNum);
        bytesString += " ";

        bytesString += HexString.toHex(tableNum);
        bytesString += HexString.byteZero(2);
        bytesString += HexString.toHex(capabilities);

        bytesString += HexString.nameToHex(experimenterName);
        bytesString += HexString.nameToHex(deviceForwardEngineName);
        bytesString += HexString.nameToHex(deviceLookupEngineName);


        return bytesString;
    }

    public String toString() {
        String string = super.toString();

        string += "; FeatureReply:" +
                    "did=" + deviceId +
                    ";sid=" + slotid +
                    ";pnum=" + portNum +
                    ";tnum=" + tableNum +
                    ";cap=" + capabilities +
                    ";eid=" + experimenterName +
                    ";fwid=" + deviceForwardEngineName +
                    ";lkid=" + deviceLookupEngineName;

        return string;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + capabilities;
        result = prime * result
                + ((deviceForwardEngineName == null) ? 0
                        : deviceForwardEngineName.hashCode());
        result = prime * result + deviceId;
        result = prime * result
                + ((deviceLookupEngineName == null) ? 0
                        : deviceLookupEngineName.hashCode());
        result = prime * result
                + ((experimenterName == null) ? 0 : experimenterName.hashCode());
        result = prime * result + portNum;
        result = prime * result + slotid;
        result = prime * result + tableNum;
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
        if (!(obj instanceof OFFeaturesReply)) {
            return false;
        }
        OFFeaturesReply other = (OFFeaturesReply) obj;
        if (capabilities != other.capabilities) {
            return false;
        }
        if (deviceForwardEngineName == null) {
            if (other.deviceForwardEngineName != null) {
                return false;
            }
        } else if (!deviceForwardEngineName
                .equals(other.deviceForwardEngineName)) {
            return false;
        }
        if (deviceId != other.deviceId) {
            return false;
        }
        if (deviceLookupEngineName == null) {
            if (other.deviceLookupEngineName != null) {
                return false;
            }
        } else if (!deviceLookupEngineName.equals(other.deviceLookupEngineName)) {
            return false;
        }
        if (experimenterName == null) {
            if (other.experimenterName != null) {
                return false;
            }
        } else if (!experimenterName.equals(other.experimenterName)) {
            return false;
        }
        if (portNum != other.portNum) {
            return false;
        }
        if (slotid != other.slotid) {
            return false;
        }
        if (tableNum != other.tableNum) {
            return false;
        }
        return true;
    }
}
