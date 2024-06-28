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

package test.rawsocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.zoolu.util.Flags;
import org.zoolu.util.log.DefaultLogger;
import org.zoolu.util.log.LoggerLevel;
import org.zoolu.util.log.WriterLogger;

import io.ipstack.net.base.Address;
import io.ipstack.net.ethernet.EthAddress;
import io.ipstack.net.ethernet.EthLayer;
import io.ipstack.net.icmp4.IcmpLayer;
import io.ipstack.net.icmp6.Icmp6Layer;
import io.ipstack.net.ip4.Ip4Address;
import io.ipstack.net.ip4.Ip4AddressPrefix;
import io.ipstack.net.ip4.Ip4EthInterface;
import io.ipstack.net.ip4.Ip4Layer;
import io.ipstack.net.ip6.Ip6Address;
import io.ipstack.net.ip6.Ip6AddressPrefix;
import io.ipstack.net.ip6.Ip6EthInterface;
import io.ipstack.net.ip6.Ip6Layer;
import io.ipstack.net.rawsocket.ethernet.RawEthInterface;


/** PING server.
 * It receives ICMP Echo Request messages and properly responds with
 * ICMP Echo Reply messages.
 * <p>
 * It uses {@link io.ipstack.net.rawsocket.ethernet.RawEthInterface} for capturing
 * and sending ICMP over IP over Ethernet packets.
  */
public abstract class PingServer {
	
		
	/** The main method. 
	 * @throws SocketException */
	public static void main(String[] args) throws SocketException {		

		Flags flags=new Flags(args);
		boolean help=flags.getBoolean("-h","prints this messsage");
		boolean debug=flags.getBoolean("-d","debug mode");
		flags.getBoolean("-4","ICMPv4 (default)");
		boolean ip_version_4=!flags.getBoolean("-6","ICMPv6");
		String eaddr=flags.getString("-e",null,"<eth-addr>","local Etherent address");
		String eth_name=flags.getString(null,null,"<interface>","network interface");
		String ipaddr_prefix=flags.getString(Flags.OPTIONAL_PARAM,null,"<ipaddr>/<prefix-len>","local IP address and prefix length");
		
		if (help || eth_name==null) {
			System.out.println(flags.toUsageString(PingServer.class.getSimpleName()));
			System.out.println(Flags.TAB1+"where <interface> is any of:");
			for (Enumeration<NetworkInterface> i=NetworkInterface.getNetworkInterfaces(); i.hasMoreElements(); ) {
				NetworkInterface ni=i.nextElement();
				System.out.println(Flags.TAB1+Flags.TAB2+ni.getName()+" - "+ni.getDisplayName());
			}
			System.exit(0);
		}
		if (debug) {
			DefaultLogger.setLogger(new WriterLogger(System.out,LoggerLevel.DEBUG));
			RawEthInterface.DEBUG=true;
			//ArpClient.DEBUG=true;
			//ArpServer.DEBUG=true;
			
			//Ip6EthInterface.DEBUG=true;
			//NeighborDiscoveryClient.DEBUG=true;
			//NeighborDiscoveryServer.DEBUG=true;			
		}

		EthAddress local_eth_addr=eaddr!=null? new EthAddress(eaddr) : null;
		Address local_ip_addr=null; // by default it will be the first IPv4 address bound to the network interface
		int prefix_len=24; // default prefix length
		if (ipaddr_prefix!=null) {
			int slash=ipaddr_prefix.indexOf('/');
			String addr_str=ipaddr_prefix.substring(0,slash);
			local_ip_addr=ip_version_4? new Ip4Address(addr_str) : new Ip6Address(addr_str);
			prefix_len=Integer.parseInt(ipaddr_prefix.substring(slash+1));  			
		}

		try {
			NetworkInterface network_interface=NetworkInterface.getByName(eth_name);
			//if (local_eth_addr==null) local_eth_addr=new EthAddress(network_interface.getHardwareAddress());
			InetAddress inet_addr=null;
			for (Enumeration<InetAddress> i=network_interface.getInetAddresses(); i.hasMoreElements(); ) {
				inet_addr=i.nextElement();
				//System.out.println("DEBUG: inet_addr: "+inet_addr);
				if (ip_version_4 && inet_addr instanceof java.net.Inet4Address) break;
				if (!ip_version_4 && inet_addr instanceof java.net.Inet6Address) break;
			}
			if (local_ip_addr==null) {
				local_ip_addr=ip_version_4? new Ip4Address(inet_addr.getAddress()) : new Ip6Address(inet_addr.getAddress());
			}
			RawEthInterface eth_ni=local_eth_addr!=null? new RawEthInterface(eth_name,local_eth_addr) : new RawEthInterface(eth_name);
							
			//System.out.println("DEBUG: local Eth addr: "+local_eth_addr.toString());
			//System.out.println("DEBUG: local IP addr: "+local_ip_addr.toString());

			if (ip_version_4) {
				Ip4EthInterface ip_interface=new Ip4EthInterface(new EthLayer(eth_ni),new Ip4AddressPrefix(local_ip_addr.getBytes(),0,prefix_len));
				IcmpLayer icmp_provider=new IcmpLayer(new Ip4Layer(ip_interface));
				
				try { new BufferedReader(new InputStreamReader(System.in)).readLine(); } catch (Exception e) {}
				icmp_provider.close();
				ip_interface.close();
			}
			else {
				Ip6EthInterface ip_interface=new Ip6EthInterface(eth_ni,new Ip6AddressPrefix(local_ip_addr.getBytes(),0,prefix_len));
				Icmp6Layer icmp_provider=new Icmp6Layer(new Ip6Layer(ip_interface));

				try { new BufferedReader(new InputStreamReader(System.in)).readLine(); } catch (Exception e) {}
				icmp_provider.close();
				ip_interface.close();
			}
			eth_ni.close();			
		}
		catch (Exception e) {
			e.printStackTrace();
		}	

	}

}
