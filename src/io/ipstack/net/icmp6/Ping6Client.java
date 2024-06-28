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

package io.ipstack.net.icmp6;


import java.io.PrintStream;

import org.zoolu.util.Clock;

import io.ipstack.net.icmp6.Icmp6Message;
import io.ipstack.net.icmp6.message.Icmp6EchoReplyMessage;
import io.ipstack.net.icmp6.message.Icmp6EchoRequestMessage;
import io.ipstack.net.ip6.Ip6Address;
import io.ipstack.net.ip6.Ip6Layer;
import io.ipstack.net.ip6.Ip6Packet;


/** PING client.
 * It sends ICMP Echo Request messages to a remote node and captures possible
 * ICMP Echo Reply messages.
 */
public class Ping6Client {
	
	/** Counter of received replies */
	int reply_count=0;

	/** Last received packet time */
	long last_time=-1;

	
	/** Creates and run a ping session.
	 * @param ip_layer IP layer
	 * @param target_ip_addr IP address of the target node
	 * @param count the number of ICMP Echo requests to be sent
	 * @param out output where ping results are printed */
	public Ping6Client(Ip6Layer ip_layer, final Ip6Address target_ip_addr, int count, final PrintStream out) {
		this(ip_layer,0,"01234567890123456789".getBytes(),target_ip_addr,count,1000,out);		
	}

	/** Creates and run a ping session.
	 * @param ip_layer IP layer
	 * @param echo_id identifier in the ICMP Echo request
	 * @param echo_data payload data in the ICMP Echo request
	 * @param target_ip_addr IP address of the target node
	 * @param count the number of ICMP Echo requests to be sent
	 * @param ping_time ping period time
	 * @param out output where ping results are printed */
	public Ping6Client(Ip6Layer ip_layer, final int echo_id, final byte[] echo_data, final Ip6Address target_ip_addr, int count, final long ping_time, final PrintStream out) {
		if (out!=null) out.println("PING6 "+target_ip_addr+" "+echo_data.length+" bytes of data:");
		final long start_time=Clock.getDefaultClock().currentTimeMillis();
		Icmp6LayerListener this_icmp_listener=new Icmp6LayerListener() {
			@Override
			public void onReceivedIcmpMessage(Icmp6Layer icmp_layer, Ip6Packet ip_pkt) {
				Icmp6Message icmp_msg=new Icmp6Message(ip_pkt);
				//DefaultLogger.log(LoggerLevel.DEBUG,"PingClinet: ICMP message ("+icmp_msg.getType()+") received from "+icmp_msg.getSourceAddress()+" (target="+target_ip_addr+")");
				if (icmp_msg.getSourceAddress().equals(target_ip_addr) && icmp_msg.getType()==Icmp6Message.TYPE_Echo_Reply) {
					Icmp6EchoReplyMessage icmp_echo_reply=new Icmp6EchoReplyMessage(icmp_msg);
					//DefaultLogger.log(LoggerLevel.DEBUG,"PingClinet: ICMP Echo Reply message: id: "+icmp_echo_reply.getIdentifier()+" ("+echo_id+")");
					if (icmp_echo_reply.getIdentifier()==echo_id) {
						int sqn=icmp_echo_reply.getSequenceNumber();
						last_time=Clock.getDefaultClock().currentTimeMillis()-start_time;
						long time=last_time-ping_time*sqn;
						if (out!=null) out.println(""+icmp_echo_reply.getEchoData().length+" bytes from "+icmp_msg.getSourceAddress()+": icmp_sqn="+icmp_echo_reply.getSequenceNumber()+" ttl="+ip_pkt.getHopLimit()+" time="+time+" ms");
						reply_count++;
					}
				}					
			}		
		};
		Icmp6Layer icmp_layer=ip_layer.getIcmp6Layer();
		icmp_layer.addListener(this_icmp_listener);
	
		for (int sqn=0; sqn<count; sqn++) {
			Ip6Address src_ip_addr=icmp_layer.getSourceAddress(target_ip_addr);
			if (src_ip_addr!=null) {
				Icmp6EchoRequestMessage icmp_echo_request=new Icmp6EchoRequestMessage(src_ip_addr,(Ip6Address)target_ip_addr,echo_id,sqn,echo_data);
				icmp_layer.send(icmp_echo_request);
			}
			else {
				if (out!=null) out.println("No route to destination "+target_ip_addr);
			}
			Clock.getDefaultClock().sleep(start_time+(sqn+1)*ping_time-Clock.getDefaultClock().currentTimeMillis());
		}
		// sleep extra time before ending
		Clock.getDefaultClock().sleep(2*ping_time);		
		icmp_layer.removeListener(this_icmp_listener);
		
		if (out!=null) out.println("\n--- "+target_ip_addr+" ping statistics ---");
		if (last_time<0) last_time=Clock.getDefaultClock().currentTimeMillis()-start_time;
		if (out!=null) out.println(""+count+" packets transmitted, "+reply_count+" received, "+((count-reply_count)*100/(double)count)+"% packet loss, total time "+last_time+"ms");

	}

}
