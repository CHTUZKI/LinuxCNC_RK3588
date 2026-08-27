SUMMARY = "CNC bring-up and RT validation tools"
DESCRIPTION = "cyclictest, stress-ng, and network utilities for LinuxCNC CNC image"

inherit packagegroup

RDEPENDS:${PN} = " \
    rt-tests \
    stress-ng \
    htop \
    neofetch \
    ripgrep \
    iperf3 \
    ethtool \
    tcpdump \
"
