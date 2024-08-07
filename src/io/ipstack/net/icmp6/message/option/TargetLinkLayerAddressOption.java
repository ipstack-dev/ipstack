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

package io.ipstack.net.icmp6.message.option;


import io.ipstack.net.packet.Address;


/** Source Link-Layer option.
 * */
public class TargetLinkLayerAddressOption extends LinkLayerAddressOption {
	
	/** Creates a new ICMP6 option.
	 * @param o the ICMPv6 option */
	public TargetLinkLayerAddressOption(Icmp6Option o) {
		super(o);
		checkOptionType();
	}

	/** Creates a new ICMP6 option.
	 * @param addr the address */
	public TargetLinkLayerAddressOption(Address addr) {
		super(TYPE_Target_Link_Layer_Address,addr);
		checkOptionType();
	}

	/** Parses an ICMP6 option.
	 * @param buf buffer containing the option */
	public static TargetLinkLayerAddressOption parseOption(byte[] buf) {
		return parseOption(buf,0);
	}

	/** Parses an ICMP6 option.
	 * @param buf buffer containing the option
	 * @param off the offset within the buffer */
	public static TargetLinkLayerAddressOption parseOption(byte[] buf, int off) {
		return new TargetLinkLayerAddressOption(Icmp6Option.parseOption(buf,off));
	}
	
	/** Checks the correctness of the option type.
	 * @return <i>true</i> if it is correct */
	private void checkOptionType() {
		if (type!=TYPE_Target_Link_Layer_Address) throw new RuntimeException("ICMP6 option type ("+type+") is not a \"Source Link-Layer Address\" ("+TYPE_Target_Link_Layer_Address+")");
	}
		 
}
