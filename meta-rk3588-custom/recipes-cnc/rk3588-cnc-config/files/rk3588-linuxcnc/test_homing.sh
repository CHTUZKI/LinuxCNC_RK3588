#!/bin/bash
# test_homing.sh — 独立测试 SSDC06-ECX-H 碰撞回零 (method -4)
# 不需要 LinuxCNC GUI，只用 realtime + lcec 维持 EtherCAT 周期通信。
#
# 按 SSDC EtherCAT User Manual §3.6：
#   Homing start = controlword bit4 → 0x001F（不是 bit12 / 0x100F）
#   成功条件     = statusword bit12 (attained) + bit10 (target reached)

set -e

# root 下 rtapi_app 需要非零 fallback UID
export RTAPI_UID="${RTAPI_UID:-1000}"
export RTAPI_FIFO_PATH="${RTAPI_FIFO_PATH:-/tmp/.rtapi_fifo}"
export PYTHONPATH="/usr/lib/python3/dist-packages${PYTHONPATH:+:${PYTHONPATH}}"
export HAL_RTMOD_DIR="${HAL_RTMOD_DIR:-/usr/lib/linuxcnc/modules}"

CFG_DIR="$(cd "$(dirname "$0")" && pwd)"
ECAT_XML="${CFG_DIR}/ethercat-conf.xml"
REALTIME="${REALTIME:-/usr/lib/linuxcnc/realtime}"

# 导程 10 mm/rev + 4096 counts/rev → 409.6 counts/mm
# 碰撞回零速度：20 mm/s = 8192 counts/s
SEARCH_SPEED=8192
ZERO_SPEED=4096
ACCEL=81920           # 200 mm/s^2 * 409.6
# 连续电流 0x2200=500 (5.00A) → 软碰撞约 10% = 0.5A
HARDSTOP_CURRENT=50
# 碰硬限位后反向退 5 mm（0x2036=1 + 0x607C），该点作为零点
HOME_OFFSET=2048      # 5 mm * 409.6

cleanup() {
    echo "=== 清理：停止 realtime / HAL ==="
    halcmd stop 2>/dev/null || true
    halcmd unload all 2>/dev/null || true
    "$REALTIME" stop 2>/dev/null || true
}
trap cleanup EXIT

echo "=== 1. 启动 realtime + lcec ==="
halrun -U 2>/dev/null || true
"$REALTIME" start

halcmd loadusr -W lcec_conf "${ECAT_XML}"
halcmd loadrt lcec
halcmd loadrt threads name1=servo-thread period1=1000000
# 新版 lcec 建议显式 activate；失败则依赖 start 时 inline activation
halcmd initf lcec.activate servo-thread 2>/dev/null || true
halcmd addf lcec.read-all servo-thread
halcmd addf lcec.write-all servo-thread
halcmd start

echo "=== 等待 EtherCAT 进入 OP ==="
OP_OK=0
for i in $(seq 1 30); do
    sleep 1
    state=$(ethercat slaves 2>/dev/null | awk '{print $3; exit}')
    echo "  [$i] slave state=$state"
    if [ "$state" = "OP" ]; then
        OP_OK=1
        break
    fi
done
ethercat slaves
halcmd show pin lcec.master0.x-axis 2>&1 | head -30

if [ "$OP_OK" != "1" ]; then
    echo "ERROR: 从站未进入 OP，无法测试"
    exit 1
fi

echo "=== 2. 当前 statusword / position ==="
halcmd getp lcec.master0.x-axis.srv-cia-statusword
halcmd getp lcec.master0.x-axis.srv-actual-position

echo "=== 3. 写回零参数 (SDO) ==="
ethercat download -p 0 -t int8   -- 0x6098 0 -4
ethercat download -p 0 -t uint32 -- 0x6099 1 ${SEARCH_SPEED}
ethercat download -p 0 -t uint32 -- 0x6099 2 ${ZERO_SPEED}
ethercat download -p 0 -t uint32 -- 0x609a 0 ${ACCEL}
ethercat download -p 0 -t int32  -- 0x607c 0 ${HOME_OFFSET}
# 1 = 找到硬限位后按 0x607C 再移动，新位置设为零点
ethercat download -p 0 -t uint16 -- 0x2036 0 1 || \
    echo "WARNING: 0x2036 Move Homeoffset write failed"
ethercat download -p 0 -t uint16 -- 0x2202 0 ${HARDSTOP_CURRENT} || \
    echo "WARNING: 0x2202 hardstop current write failed"

echo "=== 4. 读回参数确认 ==="
echo -n "homing method: "; ethercat upload -p 0 -t int8   -- 0x6098 0
echo -n "search speed:  "; ethercat upload -p 0 -t uint32 -- 0x6099 1
echo -n "zero speed:    "; ethercat upload -p 0 -t uint32 -- 0x6099 2
echo -n "accel:         "; ethercat upload -p 0 -t uint32 -- 0x609a 0
echo -n "home offset:   "; ethercat upload -p 0 -t int32  -- 0x607c 0
echo -n "offset mode:   "; ethercat upload -p 0 -t uint16 -- 0x2036 0 || true
echo -n "hardstop I:    "; ethercat upload -p 0 -t uint16 -- 0x2202 0 || true
echo -n "continuous I:  "; ethercat upload -p 0 -t uint16 -- 0x2200 0 || true

echo "=== 5. CiA402: Shutdown → Homing mode ==="
halcmd setp lcec.master0.x-axis.srv-cia-controlword 0x0006
sleep 1
halcmd setp lcec.master0.x-axis.srv-opmode 6
sleep 1
echo -n "statusword: "; halcmd getp lcec.master0.x-axis.srv-cia-statusword
echo -n "opmode-display: "; halcmd getp lcec.master0.x-axis.srv-opmode-display

echo "=== 6. Switch on (0x0007) → Enable (0x000F, bit4=0) ==="
halcmd setp lcec.master0.x-axis.srv-cia-controlword 0x0007
sleep 1
halcmd setp lcec.master0.x-axis.srv-cia-controlword 0x000F
sleep 1
echo -n "statusword: "; halcmd getp lcec.master0.x-axis.srv-cia-statusword
echo -n "opmode-display: "; halcmd getp lcec.master0.x-axis.srv-opmode-display

sw=$(halcmd -s getp lcec.master0.x-axis.srv-cia-statusword 2>/dev/null | awk '{print $1}')
if [ -n "$sw" ] && [ $(( (sw >> 2) & 1 )) -ne 1 ]; then
    echo "WARNING: 驱动未 Operation Enabled (sw=0x$(printf '%04x' "$sw"))，继续尝试启动回零"
fi

echo "=== 7. 启动回零: controlword=0x001F (bit4 Homing operation start) ==="
echo "流程: 负向低速寻找 → 软碰撞 → 反向退 ${HOME_OFFSET} counts (5mm) → 该点为零"
halcmd setp lcec.master0.x-axis.srv-cia-controlword 0x001F

echo "=== 8. 监控回零 (最多 90s) ==="
SUCCESS=0
for i in $(seq 1 90); do
    sleep 1
    sw=$(halcmd -s getp lcec.master0.x-axis.srv-cia-statusword 2>/dev/null | awk '{print $1}')
    pos=$(halcmd -s getp lcec.master0.x-axis.srv-actual-position 2>/dev/null | awk '{print $1}')
    opmd=$(halcmd -s getp lcec.master0.x-axis.srv-opmode-display 2>/dev/null | awk '{print $1}')
    if [ -z "$sw" ]; then
        continue
    fi
    bit12=$(( (sw >> 12) & 1 ))
    bit13=$(( (sw >> 13) & 1 ))
    bit10=$(( (sw >> 10) & 1 ))
    bit3=$(( (sw >> 3) & 1 ))
    printf "[%2ds] sw=0x%04x pos=%s opmode=%s | attained=%d target=%d error=%d fault=%d\n" \
        "$i" "$sw" "$pos" "$opmd" "$bit12" "$bit10" "$bit13" "$bit3"
    if [ "$bit13" = "1" ] || [ "$bit3" = "1" ]; then
        echo ">>> 回零失败 / 驱动故障"
        exit 1
    fi
    if [ "$bit12" = "1" ] && [ "$bit10" = "1" ]; then
        echo ">>> 回零成功 (attained + target reached，含反向退让)"
        SUCCESS=1
        break
    fi
    # 无退让时才允许仅 bit12；有 HOME_OFFSET 时必须等 bit10
    if [ "$HOME_OFFSET" = "0" ] && [ "$bit12" = "1" ] && [ "$i" -ge 3 ]; then
        echo ">>> 回零成功 (attained；无退让)"
        SUCCESS=1
        break
    fi
    if [ "$bit12" = "1" ] && [ "$bit10" = "0" ]; then
        # 已碰硬限位，正在按 0x607C 反向退
        :
    fi
done

if [ "$SUCCESS" != "1" ]; then
    echo ">>> 回零超时未完成"
    exit 1
fi

echo "=== 9. 切回 CSP ==="
halcmd setp lcec.master0.x-axis.srv-cia-controlword 0x000F
sleep 0.5
halcmd setp lcec.master0.x-axis.srv-opmode 8
sleep 1
echo -n "statusword: "; halcmd getp lcec.master0.x-axis.srv-cia-statusword
echo -n "actual position: "; halcmd getp lcec.master0.x-axis.srv-actual-position
echo -n "opmode-display: "; halcmd getp lcec.master0.x-axis.srv-opmode-display
echo "=== 测试完成 ==="
