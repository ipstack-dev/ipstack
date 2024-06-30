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

import java.util.ArrayList;

import org.zoolu.util.Bytes;
import org.zoolu.util.log.DefaultLogger;
import org.zoolu.util.log.LoggerLevel;

import io.ipstack.net.ethernet.EthPacket;
import io.ipstack.net.ip4.IpPacketConstants;
import io.ipstack.net.ip6.exthdr.DestinationOptionsHeader;
import io.ipstack.net.ip6.exthdr.ExtensionHeader;
import io.ipstack.net.ip6.exthdr.FragmentHeader;
import io.ipstack.net.ip6.exthdr.HopByHopOptionsHeader;
import io.ipstack.net.ip6.exthdr.RoutingHeader;
import io.ipstack.net.packet.DataPacket;


/** Internet Protocol version 6 (IPv6) packet (RFC 2460).
  */
public class Ip6Packet extends DataPacket<Ip6Address> implements IpPacketConstants {
	
	/** Debug mode */
	private static final boolean DEBUG=false;

	
	/** Version */
	private static final int VERSION=6;

	/** Default value for hop limit field */
	//private static final int DEFAULT_HOP_LIMIT=128;
	private static final int DEFAULT_HOP_LIMIT=255;
	
	/** Minimum MTU. Every link in the internet have an MTU of 1280 octets or greater */
	public static int MIN_MTU=1280;

	// Some standard IPv6 protocol numbers:
	
	/** No Next Header */
	public static final int NO_NEXT_HDR=59;


	// IPv6 packet additional attributes:

	/** Traffic class (8 bit) */
	int traffic_class=0;
	
	/** Flow label (20 bit) */
	int flow_label=0;
	
	/** Next header (8 bit) */
	//int next_header=0;

	/** Payload type (8 bit) */
	int payload_type=0;

	/** Hop limit (8 bit) */
	int hop_limit=DEFAULT_HOP_LIMIT;

	/** Extension headers */
	ArrayList<ExtensionHeader> ext_hdrs=null;
	
	

	/** Creates a new packet.
	 * @param src_addr source address
	 * @param dst_addr destination address
	 * @param payload_type the payload type
	 * @param data the packet payload */
	public Ip6Packet(Ip6Address src_addr, Ip6Address dst_addr, int payload_type, byte[] data) {
		super(src_addr,dst_addr,data);
		this.payload_type=payload_type;
	}

	
	/** Creates a new packet.
	 * @param src_addr source address
	 * @param dst_addr destination address
	 * @param payload_type the payload type
	 * @param data_buf the buffer containing the packet data
	 * @param data_off the offset within the buffer
	 * @param data_len the data length */
	public Ip6Packet(Ip6Address src_addr, Ip6Address dst_addr, int payload_type, byte[] data_buf, int data_off, int data_len) {
		super(src_addr,dst_addr,data_buf,data_off,data_len);
		this.payload_type=payload_type;
	}

	
	/** Gets IP protocol version.
	 * @return version */
	public int getVersion() {
		return VERSION;
	}

	/** Gets traffic class.
	 * @return the traffic class */
	public int getTrafficClass() {
		return traffic_class;
	}

	
	/** Sets traffic class.
	 * @param traffic_class the traffic class */
	public void setTrafficClass(int traffic_class) {
		this.traffic_class=traffic_class;
	}

	
	/** Gets flow labels.
	 * @return the flow label */
	public int getFlowLabel() {
		return flow_label;
	}


	/** Sets flow label.
	 * @param flow_label the flow label */
	public void setFlowLabel(int flow_label) {
		this.flow_label=flow_label;
	}


	/** Gets next header.
	 * @return the next header */
	/*public int getNextHeader() {
		return next_header;
	}*/


	/** Sets next header.
	 * @param next_header the next header */
	/*public void setNextHeader(int next_header) {
		this.next_header=next_header;
	}*/


	/** Gets payload type.
	 * @return the payload type */
	public int getPayloadType() {
		return payload_type;
	}


	/** Sets payload type.
	 * @param payload_type payload type */
	public void setPayloadType(int payload_type) {
		this.payload_type=payload_type;
	}


	/** Gets hop limit.
	 * @return the hop limit */
	public int getHopLimit() {
		return hop_limit;
	}


	/** Sets hop limit.
	 * @param hop_limit the hop limit */
	public void setHopLimit(int hop_limit) {
		this.hop_limit=hop_limit;
	}


	/** Gets extension headers.
	 * @return the list of extension headers */
	/*public ExtensionHeader[] getExtHdrs() {
		if (ext_hdrs==null) return null;
		// else
		return ext_hdrs.toArray(new ExtensionHeader[]{});
	}*/

	
	/** Gets extension header at position i-th.
	 * @param i the index of the extension header
	 * @return the i-th extension header */
	public ExtensionHeader getExtHdrAt(int i) {
		return ext_hdrs.get(i);
	}

	
	/** Adds an extension header.
	 * @param eh the extension header */
	public void addExtHdr(ExtensionHeader eh) {
		if (ext_hdrs==null) ext_hdrs=new ArrayList<ExtensionHeader>();
		ext_hdrs.add(eh);
	}

	
	/** Inserts an extension header at a given position.
	 * @param i the position of the extension header
	 * @param eh the extension header */
	public void insertExtHdrAt(int i, ExtensionHeader eh) {
		if (ext_hdrs==null) ext_hdrs=new ArrayList<ExtensionHeader>();
		ext_hdrs.add(i,eh);
	}

	
	/** Whether there is a given extension header.
	 * @param type the type of header
	 * @return <i>true</i> if present; <i>false</i> otherwise */
	public boolean hasExtHdr(int type) {
		if (ext_hdrs==null) return false;
		// else
		for (ExtensionHeader eh : ext_hdrs) {
			if (eh.getHeaderType()==type) return true;
		}
		return false;
	}

	
	/** Gets a given extension header.
	 * @param type the type of header
	 * @return the extension header, if present; <i>null</i> otherwise */
	public ExtensionHeader getExtHdr(int type) {
		if (ext_hdrs==null) return null;
		// else
		for (ExtensionHeader eh : ext_hdrs) {
			if (eh.getHeaderType()==type) return eh;
		}
		return null;
	}

	
	/** Removes a given extension header.
	 * @param type the type of header */
	public void removeExtHdr(int type) {
		if (ext_hdrs==null) return;
		// else
		for (int i=0; i<ext_hdrs.size(); i++) {
			ExtensionHeader eh=ext_hdrs.get(i);
			if (eh.getHeaderType()==type) {
				ext_hdrs.remove(i);
				
			}
		}
	}

	
	//** Sets traffic class.
	// * @param ext_hdrs the list of extension headers */
	/*public void setExtHdrs(ExtensionHeader[] ext_hdrs) {
		if (DEBUG) DefaultLogger.log(LoggerLevel.DEBUG,"Ip6Packet: setExtHdrs(): payload_type: "+payload_type);
		if (DEBUG) for (ExtensionHeader eh : ext_hdrs) DefaultLogger.log(LoggerLevel.DEBUG,"Ip6Packet: setExtHdrs(): eh="+eh.getHeaderType()+", eh_next="+eh.getNextHdr());
		this.ext_hdrs=ext_hdrs;
	}*/


	@Override
	public int getPacketLength() {
		int ext_len=0;
		if (ext_hdrs!=null) for (ExtensionHeader e : ext_hdrs) { ext_len+=e.getLength(); }
		return 40+ext_len+data_len;
	}

	
	@Override
	public int getBytes(byte[] buf, int off) {
		// basic header
		int index=off;
		buf[index++]=(byte)((VERSION<<4)|((traffic_class&0xf0)>>4));
		buf[index++]=(byte)(((traffic_class&0x0f)<<4)|((flow_label&0xf0000)>>16));
		buf[index++]=(byte)((flow_label&0xff00)>>8);
		buf[index++]=(byte)(flow_label&0xff);
		int payload_len=getPacketLength()-40;
		buf[index++]=(byte)((payload_len&0xff00)>>8);
		buf[index++]=(byte)(payload_len&0xff);
		int next_header=(ext_hdrs!=null && ext_hdrs.size()>0)? ext_hdrs.get(0).getHeaderType() : payload_type;
		buf[index++]=(byte)(next_header);
		buf[index++]=(byte)(hop_limit);
		//Ip6Address.stringToBytes(getSourceAddress(),buf,index);
		System.arraycopy(((Ip6Address)src_addr).getBytes(),0,buf,index,16);
		index+=16;
		//Ip6Address.stringToBytes(getDestAddress(),buf,index);
		System.arraycopy(((Ip6Address)dst_addr).getBytes(),0,buf,index,16);
		index+=16;		
		// extension headers
		if (ext_hdrs!=null && ext_hdrs.size()>0) {
			ext_hdrs.get(ext_hdrs.size()-1).setNextHdr(payload_type);
			for (ExtensionHeader eh : ext_hdrs) {
				int len=eh.getBytes(buf,index);
				if (DEBUG) DefaultLogger.log(LoggerLevel.DEBUG,"Ip6Packet: getBytes(): ExtHdr "+eh.getHeaderType()+": "+Bytes.toHex(buf,index,len));
				index+=len;
			}
		}
		// payload
		if (data_len>0) System.arraycopy(data_buf,data_off,buf,index,data_len);
		return index-off+data_len;
	}

	
	/** Parses the given raw data (array of bytes) for an IPv6 packet.
	 * @param buf the buffer containing the IP packet
	 * @return the IP packet */
	public static Ip6Packet parseIp6Packet(byte[] buf) {
		return parseIp6Packet(buf,0,buf.length);
	}

	
	/** Parses the given raw data (array of bytes) for an IPv6 packet.
	 * @param buf the buffer containing the IP packet
	 * @param off the offset within the buffer
	 * @param maxlen maximum number of bytes that can be processed
	 * @return the IP packet */
	public static Ip6Packet parseIp6Packet(byte[] buf, int off, int maxlen) {
		// basic header
		int version=(buf[off]&0xf0)>>4;
		if (version!=6) throw new RuntimeException("Wrong IPv6 version field ("+version+")");
		// else
		int traffic_class=(buf[off]&0x0f)<<4 | ((buf[off+1]&0xf0)>>4);
		int flow_label=(buf[off+1]&0x0f)<<16 | ((buf[off+2]&0xff)<<8) | (buf[off+3]&0xff);
		int payload_len=((buf[off+4]&0xff)<<8) + (buf[off+5]&0xff);
		int next_header=buf[off+6]&0xff;
		int hop_limit=buf[off+7]&0xff;
		//String src_addr=Ip6Address.bytesToString(buf,off+8);
		//String dst_addr=Ip6Address.bytesToString(buf,off+24);
		Ip6Address src_addr=new Ip6Address(buf,off+8);
		Ip6Address dst_addr=new Ip6Address(buf,off+24);
		// extension headers
		ArrayList<ExtensionHeader> ext_hdrs=new ArrayList<ExtensionHeader>();
		int ext_len=0;
		if (DEBUG) DefaultLogger.log(LoggerLevel.DEBUG,"Ip6Packet: parseIp6Packet(): next_header: "+next_header);
		// extension headers
		while (true) {
			ExtensionHeader eh=null;
			switch (next_header) {
				case ExtensionHeader.ROUTING_HDR : {
					eh=RoutingHeader.parseRoutingHeader(buf,off+40,maxlen-40);
					break;
				}
				case ExtensionHeader.DST_OPTIONS_HDR : {
					eh=DestinationOptionsHeader.parseDestinationOptionsHeader(buf,off+40,maxlen-40);
					break;
				}
				case ExtensionHeader.HOP_OPTIONS_HDR : {
					eh=HopByHopOptionsHeader.parseHopByHopOptionsHeader(buf,off+40,maxlen-40);
					break;
				}
				case ExtensionHeader.FRAGMENT_HDR : {
					eh=FragmentHeader.parseFragmentHeader(buf,off+40,maxlen-40);
					break;
				}
				case ExtensionHeader.AUTH_HDR :
				case ExtensionHeader.ESP_HDR : 
				case ExtensionHeader.MOBILITY_HDR : 
				case ExtensionHeader.HIP_HDR : 
				case ExtensionHeader.SHIM6_HDR : 
				case ExtensionHeader.TEST253_HDR : 
				case ExtensionHeader.TEST254_HDR : 									
					throw new RuntimeException("Extension header type "+next_header+" not supported.");
			}
			if (eh!=null) {
				ext_hdrs.add(eh);
				ext_len+=eh.getLength();
				next_header=eh.getNextHdr();
			}
			else break;
		}
		// payload
		byte[] data_buf=buf;
		int data_off=off+40+ext_len;
		int data_len=payload_len-ext_len;
		//int payload_type=(ext_hdrs!=null && ext_hdrs.size()>0)? ext_hdrs.get(ext_hdrs.size()-1).getNextHdr() : next_header;
		int payload_type=next_header;
		if (DEBUG) DefaultLogger.log(LoggerLevel.DEBUG,"Ip6Packet: parseIp6Packet(): payload_type: "+payload_type);
		// create packet
		Ip6Packet pkt=new Ip6Packet(src_addr,dst_addr,payload_type,data_buf,data_off,data_len);
		pkt.setTrafficClass(traffic_class);
		pkt.setFlowLabel(flow_label);
		pkt.setHopLimit(hop_limit);
		pkt.ext_hdrs=ext_hdrs;
		return pkt;
	}

		
	/** Parses an Ethernet packet for an IP packet.
	 * @param eth_pkt Ethernet packet containing the IP packet
	 * @return the IP packet */
	public static Ip6Packet parseIp6Packet(EthPacket eth_pkt) {
		return parseIp6Packet(eth_pkt.getPayloadBuffer(),eth_pkt.getPayloadOffset(),eth_pkt.getPayloadLength());
	}

	
	@Override
	public String toString() {
		//return "src="+src_addr+", dst="+dst_addr+", hop_limit="+hop_limit+", proto="+payload_type+", datalen="+getPayloadLength();
		return "IP "+src_addr+" > "+dst_addr+" hop_limit="+hop_limit+" proto="+payload_type+" datalen="+getPayloadLength();
	}
	
}
