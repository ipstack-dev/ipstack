/*
 * Copyright 2018 NetSec Lab - University of Parma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package io.ipstack.net.ip6;


import java.util.Arrays;

import io.ipstack.net.ip4.IpPrefix;
import io.ipstack.net.packet.Address;
import io.ipstack.net.util.IpAddressUtils;


/** IPv6 network prefix.
 * <p>
 * Only prefix bits can be non-zeros.
 */
public class Ip6Prefix extends Ip6AddressPrefix implements IpPrefix {
	
	/** Serial version ID */
	private static final long serialVersionUID=1L;

	/** Address ANY "::/0" */
	public static final Ip6Prefix ANY=new Ip6Prefix("::/0");

	/** Multicast prefix  */
	public static final Ip6Prefix PREFIX_MULTICAST=new Ip6Prefix("ff00::/8");

	
	/** Creates a new prefix.
	 * @param addr_and_prefix the address and prefix */
	public Ip6Prefix(String addr_and_prefix) {
		super(addr_and_prefix);
		checkSuffix();
	}
	
	/** Creates a new prefix.
	 * @param addr IP address
	 * @param prefix_len prefix length */
	public Ip6Prefix(String addr, int prefix_len) {
		super(addr,prefix_len);
		checkSuffix();
	}
	
	/** Creates a new prefix.
	 * @param addr IP address
	 * @param prefix_len prefix length */
	/*public Ip6Prefix(byte[] addr, int prefix_len) {
		super(addr,prefix_len);
		checkSuffix();
	}*/
	
	/** Creates a new prefix.
	 * @param buf buffer containing the IP address
	 * @param off offset within the buffer
	 * @param prefix_len prefix length */
	public Ip6Prefix(byte[] buf, int off, int prefix_len) {
		super(buf,off,prefix_len);
		checkSuffix();
	}
	
	/** Creates a new prefix.
	 * @param ip_addr IP address
	 * @param prefix_len prefix length */
	public Ip6Prefix(Ip6Address ip_addr, int prefix_len) {
		super(ip_addr,prefix_len);
		checkSuffix();
	}
	
	/** Checks whether it is a valid prefix.
	 * It checks if the remaining part (i.e. the host-id suffix) is all zeros.
	 * <p>
	 * In case of invalid suffix, an "Invalid prefix address" RuntimeException is thrown.
	 * @throw RuntimeException */
	private void checkSuffix() {
		byte[] addr=getBytes();
		byte[] mask=IpAddressUtils.prefixLengthToIp6Mask(prefix_len);
		for (int i=0; i<16; i++) if ((addr[i]&(mask[i]^0xff)&0xff)!=0) throw new RuntimeException("Invalid prefix address: "+toString());
	}
	
	/** Gets the subnet's broadcast address.
	 * @return the address */
	/*public Ip6Address getNetworkBroadcastAddress() {
		byte[] bmask=getPrefixMask();
		byte[] baddr=Bytes.copy(addr);
		for (int i=0; i<baddr.length; i++) baddr[i]|=bmask[i]^0xff;
		return new Ip6Address(baddr);
	}*/
	
	/** Whether a given address matches this prefix.
	 * @param addr the address
	 * @return <i>true</i> if the given address belongs to this prefix; <i>false</i> otherwise */
	@Override
	public boolean contains(Address addr) {
		if (addr instanceof Ip6Prefix) {
			Ip6Prefix target_prefix=(Ip6Prefix)addr;
			if (target_prefix.getPrefixLength()<getPrefixLength()) return false;
			// else
			addr=target_prefix.getPrefixAddress();
		}
		if (!(addr instanceof Ip6Address)) return false;
		// else
		Ip6Address target_ipaddr=(Ip6Address)addr;		
		byte[] prefix_mask=IpAddressUtils.prefixLengthToIp6Mask(prefix_len);
		byte[] prefix_addr=getBytes();
		byte[] target_addr=target_ipaddr.getBytes();
		for (int i=0; i<target_addr.length; i++) if ((target_addr[i]&prefix_mask[i])!=prefix_addr[i]) return false;
		// else
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		Ip6Prefix prefix=null;
		if (o instanceof Ip6Prefix) prefix=(Ip6Prefix)o;
		else
			if (o instanceof String) prefix=new Ip6Prefix((String)o);
			else
				return false;
		return this.contains(prefix) && prefix.contains(this);
	}
	
	@Override
	public Ip6Address getPrefixAddress() {
		return new Ip6Address(getBytes());
	}

	@Override
	public String toString() {
		return toStringWithPrefixLength();
	}

	@Override
	public int hashCode() {
		byte[] bytes=new byte[17]; // net_addr[16B]|prefix_len[1B]
		getBytes(bytes,0);
		bytes[16]=(byte)prefix_len;
		return Arrays.hashCode(bytes);
	}
	
}
