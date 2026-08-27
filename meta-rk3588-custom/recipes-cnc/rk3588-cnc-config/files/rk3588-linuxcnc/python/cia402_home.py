#!/usr/bin/env python3
# cia402_home.py — HAL userspace executor for drive-side hard-stop homing.
#
# Triggered by ssdc_homecomp (joint.N.ext-home-start → home-request).
# Performs CiA402 Homing on MOONS'/AMP SSDC06-ECX-H (method -3/-4).
# Asserts home-done / home-fault for the RT homemod to finish/abort.
#
# Per SSDC EtherCAT User Manual §3.6:
#   Controlword bit4  = Homing operation start  (0x0010)  → start = 0x001F
#   Statusword  bit10 = Target reached
#   Statusword  bit12 = Homing attained
#   Statusword  bit13 = Homing error
#   0x2202            = Hardstop current limit

import hal
import time
import subprocess
import sys


class Cia402Home:
    IDLE = 0
    WRITE_PARAMS = 1
    SWITCH_TO_HOMING = 2
    ENABLE_AND_START = 3
    WAIT_HOMING = 4
    READ_POSITION = 5
    SWITCH_TO_CSP = 6
    DONE = 7
    FAULT = 8

    CW_SHUTDOWN = 0x0006
    CW_SWITCH_ON = 0x0007
    CW_ENABLE = 0x000F
    CW_HOME_START = 0x001F
    CW_FAULT_RESET = 0x0080

    SW_BIT_READY_TO_SWITCH_ON = 0
    SW_BIT_SWITCHED_ON = 1
    SW_BIT_OP_ENABLED = 2
    SW_BIT_FAULT = 3
    SW_BIT_SWITCH_ON_DISABLED = 6
    SW_BIT_TARGET_REACHED = 10
    SW_BIT_HOMING_ATTAINED = 12
    SW_BIT_HOMING_ERROR = 13

    def __init__(self, h, comp_name="cia402_home"):
        self.h = h
        self.comp_name = comp_name
        self.state = self.IDLE
        self.state_timer = 0
        self.slave_pos = 0
        self.home_position_counts = 0
        self.home_position_mm = 0.0
        self.input_scale = 409.6
        self.homing_method = -4
        self.search_speed = 0
        self.search_zero_speed = 0
        self.homing_accel = 0
        self.home_offset = 0
        self.move_home_offset = 1
        self.hardstop_current = 0

    def _enable_controlword(self, statusword):
        if statusword & (1 << self.SW_BIT_FAULT):
            return self.CW_FAULT_RESET
        if statusword & (1 << self.SW_BIT_SWITCH_ON_DISABLED):
            return self.CW_SHUTDOWN
        if (statusword & (1 << self.SW_BIT_READY_TO_SWITCH_ON)) and not (
            statusword & (1 << self.SW_BIT_SWITCHED_ON)
        ):
            return self.CW_SWITCH_ON
        if (statusword & (1 << self.SW_BIT_SWITCHED_ON)) and not (
            statusword & (1 << self.SW_BIT_OP_ENABLED)
        ):
            return self.CW_ENABLE
        if statusword & (1 << self.SW_BIT_OP_ENABLED):
            return self.CW_ENABLE
        return self.CW_SHUTDOWN

    def ethercat_download(self, index, subindex, value, dtype):
        cmd = [
            "ethercat", "download",
            "-p", str(self.slave_pos),
            "-t", dtype,
            "--",
            hex(index), str(subindex), str(value),
        ]
        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=5)
            if result.returncode != 0:
                print(
                    f"[cia402_home] SDO download 0x{index:04x}:{subindex}={value} "
                    f"failed: {result.stderr.strip()}",
                    file=sys.stderr,
                )
                return False
            return True
        except Exception as e:
            print(f"[cia402_home] SDO download exception: {e}", file=sys.stderr)
            return False

    def ethercat_upload(self, index, subindex, dtype):
        cmd = [
            "ethercat", "upload",
            "-p", str(self.slave_pos),
            "-t", dtype,
            "--",
            hex(index), str(subindex),
        ]
        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=5)
            if result.returncode != 0:
                return (False, 0)
            parts = result.stdout.strip().split()
            if len(parts) >= 2:
                return (True, int(parts[1], 0))
            return (False, 0)
        except Exception:
            return (False, 0)

    def _refresh_params_from_pins(self):
        h = self.h
        self.slave_pos = int(h["slave-position"])
        self.input_scale = float(h["input-scale"])
        self.homing_method = int(h["homing-method"])
        speed_mm = float(h["search-speed-mm-s"])
        if speed_mm <= 0:
            speed_mm = 5.0
        if speed_mm > 50.0:
            print(
                f"[cia402_home] WARNING: search-speed-mm-s={speed_mm} too high for "
                f"hard-stop; clamping to 50 mm/s",
                file=sys.stderr,
            )
            speed_mm = 50.0
        self.search_speed = max(1, int(speed_mm * self.input_scale))
        self.search_zero_speed = max(1, int(self.search_speed * 0.5))
        self.homing_accel = max(1, int(self.input_scale * 200))
        offset_mm = float(h["home-offset-mm"])
        self.home_offset = int(round(offset_mm * self.input_scale))
        self.move_home_offset = 1 if self.home_offset != 0 else 0
        pin_hs = int(h["hardstop-current"])
        if pin_hs > 0:
            self.hardstop_current = pin_hs
        else:
            ok, cont = self.ethercat_upload(0x2200, 0, "uint16")
            if ok and cont > 0:
                self.hardstop_current = max(30, cont // 5)
            else:
                self.hardstop_current = 100

    def run(self):
        h = self.h
        prev_home_req = False

        while True:
            home_req = bool(h["home-request"])
            statusword = int(h["cia-statusword"]) & 0xFFFF
            actual_pos = int(h["actual-position"])
            self.input_scale = float(h["input-scale"]) or 409.6

            # Abort if homemod drops the request mid-cycle
            if self.state not in (self.IDLE, self.FAULT, self.DONE) and not home_req:
                print("[cia402_home] home-request cleared, aborting", file=sys.stderr)
                self.state = self.FAULT
                self.state_timer = 0

            if self.state == self.IDLE:
                h["cia-controlword"] = self._enable_controlword(statusword)
                h["srv-opmode"] = 8
                h["home-done"] = False
                h["home-fault"] = False
                h["homing-active"] = False

                if home_req and not prev_home_req:
                    self._refresh_params_from_pins()
                    print(
                        f"[cia402_home] Homing start: method={self.homing_method}, "
                        f"speed={self.search_speed} counts/s "
                        f"({self.search_speed / self.input_scale:.2f} mm/s), "
                        f"offset={self.home_offset} counts "
                        f"({self.home_offset / self.input_scale:.2f} mm, "
                        f"0x2036={self.move_home_offset}), "
                        f"hardstop_I={self.hardstop_current}",
                        file=sys.stderr,
                    )
                    self.state = self.WRITE_PARAMS
                    self.state_timer = 0
                    h["homing-active"] = True

            elif self.state == self.WRITE_PARAMS:
                h["cia-controlword"] = self.CW_ENABLE
                h["srv-opmode"] = 8
                h["homing-active"] = True
                self.state_timer += 1

                ok = True
                if self.state_timer == 1:
                    ok = self.ethercat_download(0x6098, 0, self.homing_method, "int8")
                elif self.state_timer == 2:
                    ok = self.ethercat_download(0x6099, 1, self.search_speed, "uint32")
                elif self.state_timer == 3:
                    ok = self.ethercat_download(
                        0x6099, 2, self.search_zero_speed, "uint32"
                    )
                elif self.state_timer == 4:
                    ok = self.ethercat_download(0x609a, 0, self.homing_accel, "uint32")
                elif self.state_timer == 5:
                    ok = self.ethercat_download(0x607c, 0, self.home_offset, "int32")
                elif self.state_timer == 6:
                    ok = self.ethercat_download(
                        0x2036, 0, self.move_home_offset, "uint16"
                    )
                elif self.state_timer == 7:
                    ok = self.ethercat_download(
                        0x2202, 0, self.hardstop_current, "uint16"
                    )
                elif self.state_timer >= 12:
                    print(
                        "[cia402_home] Params written, switching to Homing mode",
                        file=sys.stderr,
                    )
                    self.state = self.SWITCH_TO_HOMING
                    self.state_timer = 0

                if not ok:
                    print("[cia402_home] SDO write failed", file=sys.stderr)
                    self.state = self.FAULT
                    self.state_timer = 0

            elif self.state == self.SWITCH_TO_HOMING:
                self.state_timer += 1
                h["homing-active"] = True
                if self.state_timer <= 50:
                    h["cia-controlword"] = self.CW_SHUTDOWN
                    h["srv-opmode"] = 8
                elif self.state_timer <= 100:
                    h["cia-controlword"] = self.CW_SHUTDOWN
                    h["srv-opmode"] = 6
                else:
                    ready = bool(statusword & (1 << self.SW_BIT_READY_TO_SWITCH_ON))
                    sod = bool(statusword & (1 << self.SW_BIT_SWITCH_ON_DISABLED))
                    if ready and not sod:
                        print(
                            f"[cia402_home] Ready for Switch on, "
                            f"statusword=0x{statusword:04x}",
                            file=sys.stderr,
                        )
                        self.state = self.ENABLE_AND_START
                        self.state_timer = 0
                    elif self.state_timer > 2000:
                        print(
                            f"[cia402_home] Timeout waiting Ready to switch on "
                            f"(sw=0x{statusword:04x})",
                            file=sys.stderr,
                        )
                        self.state = self.FAULT
                        self.state_timer = 0
                    else:
                        h["cia-controlword"] = self.CW_SHUTDOWN
                        h["srv-opmode"] = 6

            elif self.state == self.ENABLE_AND_START:
                h["srv-opmode"] = 6
                h["homing-active"] = True
                self.state_timer += 1

                if self.state_timer <= 50:
                    h["cia-controlword"] = self.CW_SWITCH_ON
                elif self.state_timer <= 150:
                    h["cia-controlword"] = self.CW_ENABLE
                    if not (statusword & (1 << self.SW_BIT_OP_ENABLED)):
                        if self.state_timer > 140:
                            self.state_timer = 100
                elif self.state_timer == 151:
                    print(
                        f"[cia402_home] Homing start edge 0x001F, "
                        f"statusword=0x{statusword:04x}",
                        file=sys.stderr,
                    )
                    h["cia-controlword"] = self.CW_HOME_START
                else:
                    h["cia-controlword"] = self.CW_HOME_START
                    if self.state_timer >= 170:
                        self.state = self.WAIT_HOMING
                        self.state_timer = 0

                if statusword & (1 << self.SW_BIT_FAULT):
                    print(
                        f"[cia402_home] Fault while enabling: sw=0x{statusword:04x}",
                        file=sys.stderr,
                    )
                    self.state = self.FAULT
                    self.state_timer = 0

            elif self.state == self.WAIT_HOMING:
                h["cia-controlword"] = self.CW_HOME_START
                h["srv-opmode"] = 6
                h["homing-active"] = True
                self.state_timer += 1

                attained = bool(statusword & (1 << self.SW_BIT_HOMING_ATTAINED))
                target = bool(statusword & (1 << self.SW_BIT_TARGET_REACHED))
                herr = bool(statusword & (1 << self.SW_BIT_HOMING_ERROR))
                fault = bool(statusword & (1 << self.SW_BIT_FAULT))

                if herr or fault:
                    print(
                        f"[cia402_home] Homing FAILED! statusword=0x{statusword:04x}",
                        file=sys.stderr,
                    )
                    self.state = self.FAULT
                    self.state_timer = 0
                elif attained and target:
                    print(
                        f"[cia402_home] Homing completed (attained+target), "
                        f"statusword=0x{statusword:04x}",
                        file=sys.stderr,
                    )
                    self.state = self.READ_POSITION
                    self.state_timer = 0
                elif attained and self.move_home_offset == 0 and self.state_timer > 500:
                    print(
                        f"[cia402_home] Homing attained (bit12), accepting "
                        f"statusword=0x{statusword:04x}",
                        file=sys.stderr,
                    )
                    self.state = self.READ_POSITION
                    self.state_timer = 0
                elif attained and self.move_home_offset != 0 and not target:
                    if self.state_timer % 1000 == 0:
                        print(
                            f"[cia402_home] hard-stop found, backing off... "
                            f"sw=0x{statusword:04x} pos={actual_pos}",
                            file=sys.stderr,
                        )
                elif self.state_timer > 60000:
                    print("[cia402_home] Homing TIMEOUT!", file=sys.stderr)
                    self.state = self.FAULT
                    self.state_timer = 0
                elif self.state_timer % 1000 == 0:
                    print(
                        f"[cia402_home] waiting... sw=0x{statusword:04x} "
                        f"pos={actual_pos} t={self.state_timer}ms",
                        file=sys.stderr,
                    )

            elif self.state == self.READ_POSITION:
                h["cia-controlword"] = self.CW_ENABLE
                h["srv-opmode"] = 6
                h["homing-active"] = True
                self.state_timer += 1
                if self.state_timer >= 20:
                    self.home_position_counts = actual_pos
                    self.home_position_mm = self.home_position_counts / self.input_scale
                    h["home-position"] = self.home_position_mm
                    print(
                        f"[cia402_home] Home position: {self.home_position_counts} "
                        f"counts = {self.home_position_mm:.4f} mm",
                        file=sys.stderr,
                    )
                    self.state = self.SWITCH_TO_CSP
                    self.state_timer = 0

            elif self.state == self.SWITCH_TO_CSP:
                h["cia-controlword"] = self.CW_ENABLE
                h["srv-opmode"] = 8
                h["homing-active"] = True
                self.state_timer += 1
                if self.state_timer >= 50:
                    print(
                        "[cia402_home] Back to CSP, asserting home-done",
                        file=sys.stderr,
                    )
                    self.state = self.DONE
                    self.state_timer = 0

            elif self.state == self.DONE:
                h["cia-controlword"] = self._enable_controlword(statusword)
                h["srv-opmode"] = 8
                h["home-done"] = True
                h["home-fault"] = False
                h["homing-active"] = True
                if not home_req:
                    print("[cia402_home] homemod finished, idle", file=sys.stderr)
                    self.state = self.IDLE
                    self.state_timer = 0
                    h["home-done"] = False
                    h["homing-active"] = False

            elif self.state == self.FAULT:
                h["cia-controlword"] = self.CW_FAULT_RESET
                h["srv-opmode"] = 8
                h["home-done"] = False
                h["home-fault"] = True
                h["homing-active"] = False
                self.state_timer += 1
                if self.state_timer > 200 and not home_req:
                    self.state = self.IDLE
                    self.state_timer = 0
                    h["home-fault"] = False

            prev_home_req = home_req
            time.sleep(0.001)


def main():
    comp_name = "cia402_home"
    h = hal.component(comp_name)

    h.newpin("home-request", hal.HAL_BIT, hal.HAL_IN)
    h.newpin("home-done", hal.HAL_BIT, hal.HAL_OUT)
    h.newpin("home-fault", hal.HAL_BIT, hal.HAL_OUT)
    h.newpin("homing-active", hal.HAL_BIT, hal.HAL_OUT)
    h.newpin("home-position", hal.HAL_FLOAT, hal.HAL_OUT)
    h.newpin("cia-controlword", hal.HAL_U32, hal.HAL_OUT)
    h.newpin("cia-statusword", hal.HAL_U32, hal.HAL_IN)
    h.newpin("srv-opmode", hal.HAL_S32, hal.HAL_OUT)
    h.newpin("actual-position", hal.HAL_S32, hal.HAL_IN)
    h.newpin("input-scale", hal.HAL_FLOAT, hal.HAL_IN)
    h.newpin("slave-position", hal.HAL_S32, hal.HAL_IN)
    h.newpin("homing-method", hal.HAL_S32, hal.HAL_IN)
    h.newpin("search-speed-mm-s", hal.HAL_FLOAT, hal.HAL_IN)
    h.newpin("hardstop-current", hal.HAL_U32, hal.HAL_IN)
    h.newpin("home-offset-mm", hal.HAL_FLOAT, hal.HAL_IN)

    h["home-done"] = False
    h["home-fault"] = False
    h["homing-active"] = False
    h["home-position"] = 0.0
    h["cia-controlword"] = 0x000F
    h["srv-opmode"] = 8
    h["input-scale"] = 409.6
    h["slave-position"] = 0
    h["homing-method"] = -4
    h["search-speed-mm-s"] = 20.0
    h["hardstop-current"] = 50
    h["home-offset-mm"] = 5.0

    h.ready()

    time.sleep(0.5)
    home = Cia402Home(h, comp_name)
    print(
        f"[cia402_home] ready (ssdc_homecomp path): method={int(h['homing-method'])}, "
        f"speed={float(h['search-speed-mm-s'])} mm/s, "
        f"offset={float(h['home-offset-mm'])} mm, "
        f"hardstop_I={int(h['hardstop-current'])}",
        file=sys.stderr,
    )

    try:
        home.run()
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
