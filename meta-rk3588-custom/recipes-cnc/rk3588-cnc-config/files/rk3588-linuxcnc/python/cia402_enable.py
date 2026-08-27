#!/usr/bin/env python3
# cia402_enable.py — HAL userspace component to enable a CiA402 drive.
#
# Implements the CiA402 state machine transition:
#   Switch on disabled -> Ready to switch on -> Switched on -> Operation enabled
#
# Usage in HAL:
#   loadusr -W cia402_enable names=x-enable
#   net x-cia-cmd  cia402_enable.x-enable.cia-controlword  => lcec.master0.x-axis.srv-cia-controlword
#   net x-cia-stat lcec.master0.x-axis.srv-cia-statusword  => cia402_enable.x-enable.cia-statusword
#
# The component reads the statusword, determines the required controlword
# to advance the state machine, and writes it.  Once "Operation enabled"
# is reached, it holds controlword = 0x000F.
#
# If a fault (bit 3) is detected, it sends 0x0080 (fault reset) first.

import hal
import time
import sys

def status_to_control(statusword):
    """Return the controlword needed to advance toward Operation Enabled."""
    # Fault reset
    if statusword & (1 << 3):
        return 0x0080  # Fault reset

    # "Switch on disabled" (bit 6) -> send Shutdown (0x06) to go to "Ready to switch on"
    if statusword & (1 << 6):
        return 0x0006

    # "Ready to switch on" (bit 0) but not "Switched on" (bit 1)
    if (statusword & (1 << 0)) and not (statusword & (1 << 1)):
        return 0x0007  # Switch on

    # "Switched on" (bit 1) but not "Operation enabled" (bit 2)
    if (statusword & (1 << 1)) and not (statusword & (1 << 2)):
        return 0x000F  # Enable operation

    # "Operation enabled" — hold
    if statusword & (1 << 2):
        return 0x000F

    # Default: send shutdown to try to advance
    return 0x0006


def main():
    names = sys.argv[2:] if len(sys.argv) > 2 else []
    if sys.argv[1:2] == ['names=']:
        names = sys.argv[2:]

    # Parse names= argument
    for arg in sys.argv[1:]:
        if arg.startswith('names='):
            names_str = arg[len('names='):]
            names = [n.strip() for n in names_str.split(',') if n.strip()]
            break

    if not names:
        names = ['cia402_enable']

    h = hal.component("cia402_enable")
    instances = []

    for name in names:
        prefix = f"cia402_enable.{name}"
        h.newpin(f"{name}.cia-controlword", hal.HAL_U32, hal.HAL_OUT)
        h.newpin(f"{name}.cia-statusword", hal.HAL_U32, hal.HAL_IN)
        h.newpin(f"{name}.enabled", hal.HAL_BIT, hal.HAL_OUT)
        h.newpin(f"{name}.fault", hal.HAL_BIT, hal.HAL_OUT)
        instances.append(name)

    h.ready()

    try:
        while True:
            for name in instances:
                sw = int(h[f"{name}.cia-statusword"])
                cw = status_to_control(sw)
                h[f"{name}.cia-controlword"] = cw
                h[f"{name}.enabled"] = bool(sw & (1 << 2))  # Operation enabled
                h[f"{name}.fault"] = bool(sw & (1 << 3))    # Fault
            time.sleep(0.001)  # 1 ms cycle
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
