# ipstack

This is a pure Java implementation of the TCP/IP stack. It provides the API and corresponding implementation of the network layer protocols (IPv4 ICMP, IPv6, ICMPv6) togeter with some data-link protocols (Ethernet, ARP, PPP, SLIP, TUN), some configuration and routing protocols (DHCP, SPF), and the main trasport protocols (UDP and TCP).

In addition, some basic network elements are also provided like IPv4 and IPv6 nodes (host and router), Ethernet hub, Ethernet switch, NAT router.

The implementation of all these protocols and nodes leverages a well-defined design that can be easily reused for developing other protocols. This design includes generic classes and interfaces like `Packet`, `Address`, `Node`, `NetInterface`, `Link`, `RoutingTable`, etc.

In order to let ipstack systems interact with external nodes, different types of _connectors_ are also provided. These connectors use native network and system interfaces and are implemented using Java JNI. The following three connectors (and corresponding JNI libraries) are provided:
- `tuntap` - it is the main method and provides a simple and transparent interface to the underlying OS networking by means of standard TUN or TAP interfaces that are layer-three and layer-two virtual interfaces that can be set-up for connecting an application to the OS networking.
- `rawsocket` - that allows direct access to the OS standard POSIX sockets and to the host network interfaces.
- `netfilter` - only for Linux-based OS, it allows the user to connect an ipstack element at user space to the Linux netfilter (kernel level); this, in turn, allows the user to intercept and process any packet passing through the underlying machine.

On top of TCP and UDP there is also an alternative implementation of the standard Java socket API (`java.net`), based on ipstack. 
This alternative implementation of the Java socket API let the user either: i) to port any existing application to run on top of the ipstack networking without requiring code changes, or ii) to easily implement new networked application benefiting from a well-known programming socket API.

Having a pure Java implementation of the TCP/IP stack can be used in different use cases. Some notable ones are:

- Implementation of network components at user space, benefiting of an object-oriented Java API, without depending on particular platform dependedent APIs; examples of such components are: Virtual Network Functions, packet filter firewalls, and VPNs.

- Virtualization of Java applications by running the applications on top of a networking layer running at user space and independent from the networking layer of the actual platform where the application is running on; the application can be interact with external application by exploiting one of the ipstack connectors on the hosting machine or on a remote machine; in the latter case proper ipstack virtual nodes (switches or routers) can be used to create a iptack virtual network overlay spanning two or more physical machines.

- Network emulation; an example is [NEMO]((https://netsec.unipr.it)). a description of NEMO can be foud is this [paper](https://www.sciencedirect.com/science/article/pii/S2352711019300135).
