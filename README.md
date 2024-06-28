# ipstack

_ipstack_ is pure Java implementation of the TCP/IP stack. It provides the API and corresponding implementation of the network layer protocols (IPv4 ICMP, IPv6, ICMPv6) togeter with some data-link protocols (Ethernet, ARP, PPP, SLIP, TUN), some configuration and routing protocols (DHCP, SPF), and the main trasport protocols (UDP and TCP).

In addition, some basic network elements are also provided like IPv4 and IPv6 nodes (host and router), Ethernet hub, Ethernet switch, NAT router.

The implementation of all these protocols and nodes leverages a well-defined design that can be easily reused for developing other protocols. This design includes generic classes and interfaces like Packet, Address, Node, NetInterface, Link, RoutingTable, etc.

In order to let _ipstack_ systems interact with external nodes,  different types of _connectors_ are also provided. These _connectors_ use native network and system interfaces supported by means of some _ipstack_ JNI libraries. The following three _connectors_ are available:
- _tuntap_ - it is the main method and provides a simple and transparent interface to the underlying OS networking by means of standard TUN or TAP interfaces that are layer-three and layer-two virtual interfaces that can be set-up for connecting an application to the OS networking.
- _rawsocket_ - that allows direct access to the OS standard POSIX sockets and to the host network interfaces.
- _netfilter_ - only for Linux-based OS, it allows the user to connect a NEMO element to the \textit{netfilter} interface of the Linux kernel; this, in turn, allows the user to intercept and process any packet passing through the underlying machine.
