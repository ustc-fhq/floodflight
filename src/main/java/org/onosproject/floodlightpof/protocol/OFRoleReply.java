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
import org.onosproject.floodlightpof.util.U16;

/**
 * TODO Not used in POF yet.
 *
 */
public class OFRoleReply extends OFMessage {
    public static int minimumlength = OFMessage.MINIMUM_LENGTH + 8;  // tsf: 64 bits alignment
    protected OFControllerRole ofControllerRole;
    //TODO implement
    public OFRoleReply() {
        super();
        this.type = OFType.ROLE_REPLY;
        this.length = (short)minimumlength;
        this.ofControllerRole = null;
    }
    public void readFrom(ChannelBuffer data) {
        super.readFrom(data);
        ofControllerRole = OFControllerRole.values()[data.readByte()];
        data.readBytes(7);
    }
    /**
     * @return the ofControllerRole
     */
    public OFControllerRole getOfControllerRole() {
        return ofControllerRole;
    }
    /**
     * @param ofControllerRole
     * the ofControllerRole to set
     */
    public void setOfControllerRole(OFControllerRole ofControllerRole) {
        this.ofControllerRole = ofControllerRole;
    }

    @Override
    public void writeTo(ChannelBuffer data) {
        super.writeTo(data);
        if (ofControllerRole != null) {
            data.writeByte( (byte)ofControllerRole.ordinal());
        }
        data.writeZero(7);
    }
    @Override
    public String toString() {
        String string = super.toString();
        string += ";OFControllerRole=" + this.ofControllerRole;
        return string;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result += result*prime + ((ofControllerRole == null) ? 0 : ofControllerRole.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object object) {
        if (this==object){
            return true;
        }
        if (!super.equals(object)) {
            return false;
        }
        if (!(object instanceof OFRoleRequest)) {
            return false;
        }
        OFRoleRequest other = (OFRoleRequest) object;
        if (ofControllerRole == null) {
            if (other.ofControllerRole != null) {
                return false;
            }
        } else if (!ofControllerRole
                .equals(other.ofControllerRole)) {
            return false;
        }
        return true;
    }

}
