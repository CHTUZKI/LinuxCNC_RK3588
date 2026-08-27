# SSDC-ECX-H/J

Step-Servo System Hardware Manual

SSDC06-ECX-H/J SSDC10-ECX-H/J

![](images/36626a9115ece6610db41d34ddc85e036b2b000c2aab7ee39ead6c053fa05307.jpg)

![](images/56c324c06063e8a30969f1ba0f09a67334b10c16cc432444c3b612e2a2b40297.jpg)  
SHANGHAI AMP&MOONS’ AUTOMATION CO.,LTD.

1 Introduction....3
1.1 Features....3
1.2 Safety Instructions....4
2 Getting Started....5
2.1 Installing Software....6
2.2 Connecting the Power Supply....8
2.3 Choosing a Power Supply....9
2.3.1 Voltage....9
2.3.2 Current....10
2.4 Connecting the Motor....20
2.5 Connecting the EtherCAT....21
2.6 Setting the EtherCAT Node ID....21
3 Inputs and Outputs....22
3.1 Digital Inputs....23
3.2 Digital Outputs....25
3.3 Analog Inputs (SSDC-ECX-J)....27
4 Mounting the Drive....28
5 Warning and Fault Display....28
6 Reference Materials....30
6.1 Drive Mechanical Outlines(Units: mm)....30
6.2 Technical Specifications....31
6.3 Recommended Motors....32
7 Contacting MOONS'....33

## 1 Introduction

Thank you for selecting the MOONS’ SSDC series step-servo drive and motor. The SSDC-ECX series are high performance EtherCAT fieldbus control step-servo drive which also integrates with built-in motion controller. The SSDC-ECX drive can operate as a standard EtherCAT slave using CANopen over EtherCAT (CoE). The SSDC-ECX-H/J drive is the second generation of SSDC series, which has more higher performance and supports Vender specific profile over EtherCAT (VoE).

EtherCAT® is registered trademark and patented technology, licensed by Beckhof Automation GmbH, Germany.

## 1.1 Features

• Programmable, digital step-servo drive and motor package

• Push-in spring type connector, faster and reliable connection

CANopen over EtherCAT (CoE) with full support of CiA402. Based on the widely used 100BASE-TX cabling system and with a baud rate of 100Mbps full-duplex, EtherCAT enables high speed and highly reliable communication

• Vender specific profile over EtherCAT (VoE) is contribute to update the firmware over EtherCAT.

Supported modes: Profile Position, Profile Velocity, Profile Torque, Cyclic Synchronous Position, Cyclic Synchronous Velocity and Homing mode as well as MOONS’ own Q mode

## • Current output

SSDC06 output current：continuous 6A/phase（peak of sin），boost 7.5A(1.5s)SSDC10 output current：continuous 10A/phase（peak of sin），boost 15A(1.5s)

## • Wide range input voltage:

SSDC06：24\~70VDC

SSDC10：24\~70VDC

## • Encoder resolution：

20000 counts/rev (AM17/23/24/34SS-N motor)

4096 counts/rev (AM08/11/17/23/24/34RS motor)

## • Abundant I/O interface

SSDC06/10-ECX-H

3 optically isolated digital inputs,5-24VDC

1 optically isolated digital outputs,max30V/100mA

SSDC06/10-ECX-J

5 optically isolated digital inputs,5-24VDC

2 optically isolated digital outputs,max30V/100mA

1 analog inputs can be configured to 0-5V, 0-10V, ±5V or ±10V signal ranges

## • Communication

Dual-port RJ45 for EtherCAT communication

USB port for configuration

## 1.2 Safety Instructions

Only qualified personnel should transport, assemble, install, operate, or maintain this equipment. Properly qualified personnel are persons who are familiar with the transport, assembly, installation, operation, and maintenance of motors, and who meet the appropriate qualifications for their jobs.

To minimize the risk of potential safety problems, all applicable local and national codes regulating the installation and operation of equipment should be followed. These codes may vary from area to area and it is the responsibility of the operating personnel to determine which codes should be followed, and to verify that the equipment, installation, and operation are in compliance with the latest revision of these codes.

Equipment damage or serious injury to personnel can result from the failure to follow all applicable codes and standards. MOONS does not guarantee the products described in this publication are suitable for a particular application, nor do they assume any responsibility for product design, installation, or operation.

Read all available documentation before assembly and operation. Incorrect handling of the products referenced in this manual can result in injury and damage to persons and machinery.

All technical information concerning the installation requirements must be strictly adhered to.

It is vital to ensure that all system components are connected to earth ground. Electrical safety is impossible without a low-resistance earth connection.

This product contains electrostatically sensitive components that can be damaged by incorrect handling. Follow qualified anti-static procedures before touching the product.

During operation keep all covers and cabinet doors shut to avoid any hazards that could possibly cause severe damage to the product or personal health.

During operation, the product may have components that are live or have hot surfaces.

Never plug in or unplug the Integrated Motor while the system is live. The possibility of electric arcing can cause damage.

Be alert to the potential for personal injury. Follow recommended precautions and safe operating practices emphasized with alert symbols. Safety notices in this manual provide important information. Read and be familiar with these instructions before attempting installation, operation, or maintenance. The purpose of this section is to alert users to the possible safety hazards associated with this equipment and the precautions necessary to reduce the risk of personal injury and damage to equipment. Failure to observe these precautions could result in serious bodily injury, damage to the equipment, or operational dificulty.

## 2 Getting Started

The following items are needed:

• A 24-70VDC power supply, see the section below entitled“Choose a Power Supply”for helping to choose the right one.

• A compatible SS or RS motor, please see the section below entitled “Recommended Motor”

• A small flat blade screwdriver for tightening the connectors screw

• A PC running Microsoft Windows 7/ Windows 8/ Windows 10 (32bit or 64bit) and Microsoft.net framework 4.0

• A USB Mini-B cable (Sold separately)

• Install the Stepper Suite software

• A power cable(included)

• A CAT5 cable, used to do the daisy-chain connection. It is also used to configure the drive.

• Optional extended motor cable(Sold separately)

• Optional extended encoder cable(Sold separately)

## 2.1 Installing Software

Stepper Suite is the PC based software application used to configure, and perform servo tuning, drive testing and evaluation of the step-servo products. System servo control gains, drive functionality and I/O configuration are set with Stepper Suite. It also contains an oscilloscope function to help set the servo control gains.

• Download the Stepper Suite from the MOONS’ website and install it.

• Launch the software by clicking Start-----Programs ----MOONS’----Stepper Suite

Connect the drive to PC by USB Mini-B cable. Please see the section below entitled “Choosing the Right USB Port”.

• Connect the drive to the Power Supply.

• Connect the motor to the drive.

• Power up the drive.

• The software will recognize your drive, display the model and firmware version and be ready for action.

![](images/b1d472d308bdce499227d55e71c94a891a96093f540787ef0ec0d592fd851f00.jpg)

Encoder Connector

SSED-ECX-H

The connectors and other points of interest are illustrated below:

![](images/1d09ec02e861a1d26330b0386616c1308ffb05577ce4d1563bf8444b76f325ce.jpg)

SSED-ECX-J  
![](images/64a72ac0f9b0f8aff1027522152241d9d000b06d79e54aa1fc8a2fa314623f42.jpg)

## 2.2 Connecting the Power Supply

The SSDC series step-servo drive and motor are shipped with a power cable, 2 meters long. Connect the red wire to the positive of the power supply. Connect the black wire to the negative of the power supply. Plug the cable into the power connector of the drive.

(NOTE: Be careful not to reverse the wires. Reversing the connection may open the internal fuse on the drive and void the warranty.)

SSDC06: 24 – 70VDC

SSDC10: 24 – 70VDC

![](images/ad3886176ce0d7c49d70c93e1d4256700ef14eb746a8f990720db4ca4e308789.jpg)  
Power Connector

Wire Range：0.2mm2 – 1.5mm2 (24 – 16AWG)

Recommended stripping length：10mm

Connect the chassis to the earth ground through the grounding screws.

![](images/df7b94a34035ed7607744a8abea3772380dcb9aa326d02f6e252e7a2f811cc3a.jpg)

The section entitled “Choosing a Power Supply” will help you to select a right power supply.

## 2.3 Choosing a Power Supply

The main considerations when choosing a power supply are the voltage and current requirements for the application.

## 2.3.1 Voltage

The SSDC drive is designed to give optimum performance between 24 and 48 Volts DC. Choosing the voltage depends on the performance needed and motor/drive heating that is acceptable and/or does not cause a drive over-temperature. Higher voltages will give higher speed performance but will cause the RS driver to produce higher temperatures. Using power supplies with voltage outputs that are near the drive maximum may significantly reduce the operational duty-cycle.

## SSDC06/10

For the SSDC06/10 drive, the extended range of operation can be as low as 18 VDC minimum to as high as 75 VDC maximum. When operating below 18 VDC, the power supply input may require larger capacitance to prevent under-voltage and internal-supply alarms. Current spikes may make supply readings erratic. The supply input cannot go below 18 VDC for reliable operation. This will not fault the drive. Absolute maximum power supply input is 75 VDC at which point an overvoltage alarm and fault will occur. When using a power supply that is regulated and is near the drive maximum voltage of 75 VDC, a voltage clamp may be required to prevent over-voltage when regeneration occurs. When using an unregulated power supply, make sure the no-load voltage of the supply does not exceed the drive’s maximum input voltage of 75 VDC.

## 2.3.2 Current

The maximum supply currents required by the SSDC series step servo drive and motor are shown below in chats at diferent power supply voltage input. The SSDC drive power supply current is lower than the winding currents because it uses switching amplifiers to convert a high voltage and low current into low voltage and high current. The more power supply voltage exceeds the motor voltage, the less current will be required from the power supply.

It is important to note that the current draw is significantly diferent at higher speeds depending on the torque load to the motor. Estimating how much current is necessary may require a good analysis of the load to the motor.

![](images/5686a27716be5291398d08e191fab0c9fb72fc48f43bb7b7c634afe2e41cc5fb.jpg)

![](images/8bc54b459dc4d1b954ed224831a061b2137482ae55289e6fb80122436de65eca.jpg)

![](images/79380633278e5cfeb8025fa56335c58b419a3c1c96016c460552eb71052d6257.jpg)

![](images/5709a11ad1cd3f2cee080c5d6c60143ea53bb154ac960f8a03fa1d61cb3ad702.jpg)

![](images/c4428ceeb9f809a99efb28a3acf98ef60156a94c409c6e0f088d408da0f2283c.jpg)

![](images/d7efe03bf9672ed22c99d3e2538fa053ec4289dc8f60bd472117f0ccaf6eb77e.jpg)

![](images/f2d6c6b9db4b675e6e0f172c313d5887ca50c750ee4a5b57490588873907e3b2.jpg)

![](images/7978d28df02fe1443a83cb293599fdb4384990d2c6445c36a0bb345df6abb2b3.jpg)

![](images/1fa9b080b6667c4ed90c3a0540e8adae21c83b09fae7e14010e2f13a3acbf376.jpg)

![](images/dd1e01b454d8dddfdf02e3175220cdc592ad2386361db408130a27bd234f2522.jpg)

![](images/a0b8690288a8ea752f2f4a1fab7d5530cbd7ce2dcf0736d32f22baaf6caa350b.jpg)

![](images/2e75d42269b6c7bac11e8573181249e0ea0ec6cb54c8960dc7b6268948e8673a.jpg)

![](images/c67fcf5f8f2bc53cda2dadefee248c6c8ac45b5ee1bdd8432d95aa7a5d7efca6.jpg)

![](images/56ceffbc9eebad631b0742d882db9b46d3a0eb06f75483880a49f559778c353c.jpg)

![](images/40404fa9d4ae5f5d30488716a1e5930c3003f39a0bb0ba545374dc968392a233.jpg)

![](images/8da2876a29d8c859b8b1fc6af719365802a488c2d39928c6cb6dffa6b09184a3.jpg)

![](images/cd6303fd20a62db112474825ceef02a0384f020f53b5701fa3e750a62a0f75c1.jpg)

![](images/7acb96f2c9f227278592d493a699d8363bef6772d22a0d1d9faf91a61a3f5d7f.jpg)

![](images/eee6415e1743653fcc736549df7f38f8e74913bd3719fd48a1074e59605273e4.jpg)

![](images/61be9a87c1353825a7cc9278f3f7481ff3abedacd9b30746528fc5ec3d0483d2.jpg)

![](images/274a91beed744db6c0e6e47f50dd83aaf0a06937e05c034ebb8208d8b4dc3ab2.jpg)

![](images/baaaf2667f80894c24136424ee3fcd80eeb8951f235d88981c6d7c863ce0d125.jpg)

![](images/e61cb22cd74ff888fe04194b68c2f57466b21bacefa81fc5ecbbe1d560d6abe1.jpg)

![](images/2f2c94a0b854991ac8eabb19d5f7368ed7439c33ec0277ff7786c18ca2699684.jpg)

![](images/7adf747ced311f432f90b8496277460ff4579a754e8294c38fa6ec7e1307e084.jpg)

![](images/20a66a621cccf56ed38b154dff8723c8843b778208aa98bf0d07278582dc7685.jpg)

![](images/e2c874ba9315cd69c0defaf292c0d70d96372bafd982636d1284af847a0e40fa.jpg)

![](images/281e2c78dba90a1fcf689e0069bcc17eda2cbf207ab0e117fa19b9cb387c19c8.jpg)

![](images/dbd0006625c550ff76de7abb0ce11bc30df12e2e4defed990439f12929e3dbb3.jpg)

## 2.4 Connecting the Motor

The SS/RS motors have two diferent cables. One is the motor power cable, the other one is the encoder feedback cable. Plug the motor power cable into the motor connector on the drive and plug the encoder feedback cable into the encoder feedback connector on the drive.

(NOTE: Do not damage or drag the cables on the motor.)

![](images/ca5d525454083b4aa544b0a9e1d8f9908f798ed629b101e6b143e4c4d7d85143.jpg)  
Motor connector on the driver

![](images/b9c00ef7b4c286d8fafbf80962b561c61895a7bc2e5ff3614f39dd825029e0ea.jpg)  
Encoder connector on the driver

Please check the information of mating connectors, extended motor cable and extended encoder cable in below section ”Optional Accessories (Sold separately)”

## 2.5 Connecting the EtherCAT

Dual RJ-45 connectors accept standard Ethernet cables and are categorized as 100BASE-TX(100 Mb/sec) ports. CAT5 or CAT5e (or higher) cables should be used. The IN port connects to a master, or to the OUT port of an upstream node. The OUT port connects to a downstream node.

If the drive is the last node on a network, only the IN port is used. No terminator is required on the OUT port.

## EtherCAT Status Indicator LEDs

The LEDs are used for indicating status of the EtherCAT. There are two Link/Activity LEDs (one for each RJ-45 Ethernet connector) and two status LEDs (RUN and ERR).

![](images/a63cb050d660fc5c39e65d12a5c050d77eac569c05e2b76bed3971fe55771066.jpg)

<table><tr><td>LED</td><td>Color</td><td>Status</td><td>Description</td></tr><tr><td rowspan="3">Link/Activity</td><td rowspan="3">Green</td><td>OFF</td><td>no Ethernet connection</td></tr><tr><td>ON</td><td>Ethernet is connected</td></tr><tr><td>Flickering</td><td>activity on line</td></tr><tr><td rowspan="4">RUN</td><td rowspan="4">Green</td><td>OFF</td><td>initialization state</td></tr><tr><td>Blinking</td><td>pre-operational state</td></tr><tr><td>Single Flash</td><td>safe-operational state</td></tr><tr><td>ON</td><td>operational state</td></tr><tr><td rowspan="4">ERR</td><td rowspan="4">Red</td><td>OFF</td><td>no error</td></tr><tr><td>Blinking</td><td>general error</td></tr><tr><td>Single Flash</td><td>sync error</td></tr><tr><td>Double Flash</td><td>watch dog error</td></tr></table>

## Notes:

• Flickering: Rapid flashing with a period of approximately 50ms (10Hz)

• Blinking: Flashing with equal on and of periods of 200ms (2.5Hz)

• Single Flash: Repeating on for 200ms and of for 1s

• Double Flash: Two flashes with a period of 200ms followed by of for 1s

## 2.6 Setting the EtherCAT Node ID

When the drive’s ID is configured to be assigned by master controller in Stepper Suite software, the master controller can set the EtherCAT node Alias ID to address 0004h of SII (Slave Information Interface) EEPROM. The drive can get this ID value from SII EEPROM address 0004h after power up.

## 3 Inputs and Outputs

SSDC-ECX-H inputs and outputs include：

• 3 Optically isolated digital inputs, 5 - 24VDC logic

• 1 Optically isolated, Open Collector, 30V/100 mA max

![](images/42a0aa663c9d8fbb2430b4c734d6dce51bcaefcd1139d41c3e64eca72205ddb4.jpg)  
I/O Connector Diagram

SSDC-ECX-J inputs and outputs include：

• 5 Optically isolated digital inputs, 5 - 24VDC logic

• 2 Optically isolated, Open Collector, 30V/100 mA max,

• 1 analog inputs can be configured to $0 { - } 5 \mathsf { V } , 0 { - } 1 0 \mathsf { V } , \pm 5 \mathsf { V } \circ \mathsf { r } \pm 1 0 \mathsf { V }$ signal ranges

• User 5V

![](images/b01a0528575b27a1d7e6f4968b498483d898599d6ccd8f9ae51c47847396663d.jpg)  
I/O Connector Diagram

<table><tr><td>Signal</td><td>Pin No.</td><td>Function</td></tr><tr><td>X3</td><td>1</td><td>Available function:CW limit sensor inputHoming sensor inputGeneral purpose</td></tr><tr><td>X4</td><td>2</td><td>Available function:CCW limit sensor inputHoming sensor inputGeneral purpose</td></tr><tr><td>X7</td><td>3</td><td>Available function:Touch probe inputHoming sensor inputGeneral purpose</td></tr><tr><td>XCOM</td><td>4</td><td>The common voltage of X3/X4/X7</td></tr><tr><td colspan="3">0-ECX-J</td></tr><tr><td>Signal</td><td>Pin No.</td><td>Function</td></tr><tr><td>X3</td><td>4</td><td>Available function:CW limit sensor inputHoming sensor inputGeneral purpose</td></tr><tr><td>X4</td><td>5</td><td>Available function:CCW limit sensor inputHoming sensor inputGeneral purpose</td></tr><tr><td>X5</td><td>6</td><td>General purpose</td></tr><tr><td>X7</td><td>7</td><td>Available function:Touch probe inputHoming sensor inputGeneral purpose</td></tr><tr><td>X8</td><td>8</td><td>Available function:Emergency stopGeneral purpose</td></tr><tr><td>XCOM</td><td>9</td><td>The common voltage of X3/X4/X5/X7/X8</td></tr></table>

## 3.1 Digital Inputs

SSDC-ECX-H//J series drive has several digital optically isolated inputs, the function of every input can be configurated by Stepper Suite software.

## SSDC06/10-ECX-H

## SSDC06/10-ECX-J

X3、X4、X5、X7、X8: Optically isolated, 5-24VDC, Minimum pulse width = 100μs, Maximum pulse frequency = 5KHz.

Because the input is an optically isolated circuit, a 5-24V power supply is needed. For example, you can use the power supply of the PLC when you are using a PLC control system, but if you want to connect a relay or mechanical switch to the input, you must need a power supply.

XCOM is an electronics term for a single-ended signal connection to a common voltage. If you are using a sourcing(PNP) input signals, you need to connect XCOM to the ground(power supply -), if you are using a sinking(NPN) input signals, the XCOM need to connect to the power supply +.

The diagrams below show how to connect the X3, X4 , X5, X7 and X8 to various commonly used devices.

![](images/d49111ebfe76d5388c96041fdf20749f99c9641891ec68c9a413fcb65d733e6f.jpg)  
Connecting a switch or relay to an input

![](images/30a27b28ff98fd09c6fdbd2cfd33b31b48e086b5f2449deb363d8986a7ed3e77.jpg)  
Connecting a NPN type output to an input

![](images/f3b8d1a2c4ee0015461d126e29b88b3e262a136c61ef72d1c59f177ee25f3d38.jpg)  
Connecting a PNP type output to an input

## 3.2 Digital Outputs

SSDC-ECX-H//J series drive has several digital optically isolated outputs, the function of every output can be configurated by Stepper Suite software.

## SSDC06/10-ECX-H

<table><tr><td>Signal</td><td>Pin No.</td><td>Function</td></tr><tr><td rowspan="2">Y2</td><td>5</td><td rowspan="2">Available function:Release brake outputStatic in position outputDynamic in position outputGeneral purpose output</td></tr><tr><td>6</td></tr></table>

## SSDC06/10-ECX-J

<table><tr><td>Signal</td><td>Pin No.</td><td>Function</td></tr><tr><td>Y1</td><td>10</td><td>Available function:Alarm outputStatic in position outputDynamic in position output</td></tr><tr><td>Y2</td><td>11</td><td>Available function:Release brake outputStatic in position outputDynamic in position output</td></tr><tr><td>YCOM</td><td>12</td><td>The common voltage of Y1/Y2</td></tr></table>

Y1、Y2: Optically isolated, 5-24VDC, Maximum pulse frequency = 10KHz. The chats below show how to connect to the output:

(NOTE: Do not connect the outputs to more than 30VDC power supply.

And the current of each output terminal must not exceed 100mA.)

![](images/fad2c929998392c2864c1e651b5de901dc8491279dd4ce5028882e5775bf35ec.jpg)  
Connecting a sourcing output to load

![](images/6c4e38c591888882f6b7ab60caa68725fd0cda55be8a133eb7956d347afcdc35.jpg)  
Connecting a sinking output to PLC's input

![](images/cd94b02519b4c0419e00504cd3894c8d85f2bd82045f72d766f6cb76d19db651.jpg)  
Connecting a sourcing output to PLC's input

![](images/bd6f10d2a66e387ada27348271c4ba26485dd0c7ed00cbf1bc31f78cb0fb5e38.jpg)  
Driving a relay

## 3.3 Analog Inputs （SSDC-ECX-J）

SSDC-ECX-J series drive has one analog signal inputs which can accept signal range of 0-5V, 0-10V,±5V and ±10V.

Use the Stepper Suite to configure the input range, ofset, deadband and noisy filter frequency.

SSDC-ECX-J series provides a +5V/100mA limit power supply that can be used to power external devices such as potentiometer. It is not the most accurate supply for reference, for more precise readings use an external supply that can provide the desired accuracy.

![](images/f4fbbb034a467cdd095daf9a10ae8a872aa1661a8c14571ba0b8eb0f17dbe057.jpg)  
Connecting a potentiometer to an analog input

## 4 Mounting the Drive

Use the M3 or M4 screw to mount the SSDC series drive .The drive should be securely fastened to a smooth, flat metal surface will help conduct heat away from the chassis. If this is not possible, forced airflow from a fan maybe required to prevent the drive from overheating.

![](images/adbbb5f9a18b41ec30eac1272ea8a69be8cb71b67a2fe6098bd2d5536b909738.jpg)

Never use the drive in a place where there is no air flow or the surrounding air is more than $4 0 \%$

Never put the drive where it can get wet or where metal or other electrically conductive particle particles can get on the circuitry.

• Always provide air flow around the drive. When mounting multiple SSDC drives near each other, maintain at least 2cm of space between drives.

## 5 Warning and Fault Display

The SSDC - ECX-H/J series step-servo package have two sets of 7-segment digital LED to display the EtherCAT address, alarm codes and status of the drive.

![](images/d50fc73b2eced7a6693b4587ca0c96221a5b2181d80e17e5773bc419080c00b4.jpg)

## EtherCAT Address Display

The two LEDs will display the EtherCAT address (Physical address or EtherCAT ID) during the drive working in normal status.

NOTE: Physical address is assigned by master controller according to the physical topology link. EtherCAT ID is configured by the Stepper Suite software.

① When the EtherCAT ID is set to 0, the master controller assigns each drive physical address, and the LED indicates the physical address. When the power was just turned on, the address had not been assigned, the LED displayed “0 0”. After a few seconds, the master controller assigned physical address to each drive, and the LED displayed the relevant value.

② When the EtherCAT ID is not set to 0, the master controller reads the EtheCAT ID from the drive, and then assign it again, the LED indicates this EtherCAT ID. If the master controller doesn’t assign the readed address, the LED indicates the actual assigned address.

NOTE: The LED diaplys the low two digitals address value in decimal.

## Alarm Codes

When the drive has alarm, the LED flashes with the period of 0.5s to display the current alarm information .

LED1 shows the word “E”or “E.”, LED2 shows specific error or warning code. The specific alarm description is shown in the below table.

<table><tr><td>LED2</td><td>Description</td><td>LED2</td><td>Description</td></tr><tr><td><img src="images/0cd5ba34d2bcac6d93877d069893a61bf14037caa173c45302e48b209da1cbe8.jpg"/></td><td>Position error</td><td><img src="images/6320f1e582bb70cc65487e38fc6b4271b8b5d6b88a6a431c87f1a525c7624edf.jpg"/></td><td>Current foldback</td></tr><tr><td><img src="images/126ffe0615e81a0fbf8716feb43370b80f1585fdf2c601603dd32ac7d9bbd9f5.jpg"/></td><td>CCW limit</td><td><img src="images/724d0688b5c7445720a387adeeff5186f6fa842ab6dd5f6f71b10baf85a8279a.jpg"/></td><td>Open winding</td></tr><tr><td></td><td>CW limit</td><td><img src="images/bfda7dda5321f3c9058664aa0cd3ad5e43e3ed1001cce44127f1db040516b2d3.jpg"/></td><td>Bad encoder</td></tr><tr><td><img src="images/bcdbaf49d4ed7fac0e1a1aaa625e5e37d9b83245cbcc8d9b206086c6cf573f86.jpg"/></td><td>CCW &amp; CW limit</td><td><img src="images/cc34739b1a88a6947f0479ce1b6be206b722c35b550e0d56d1d298878925c515.jpg"/></td><td>Save failed</td></tr><tr><td></td><td>Drive over temperature</td><td><img src="images/804be1cf0d93d91252dd5e0f8962a46ab06e9bc36c8d83b8a47ccf38babdc660.jpg"/></td><td>Communication error</td></tr><tr><td><img src="images/1ebe732f54418c27a781371bbb5b7093936d560dc0fb7a3716237f86d4772193.jpg"/></td><td>Power supply over voltage</td><td><img src="images/f46e5252798bec9b6c349c3be51fe382810cc71f8fe3c50e165ca351d5158f91.jpg"/></td><td>Blank Q segment</td></tr><tr><td><img src="images/016d587413bf8e45f1a91b4a88149780760a37324899aa699218e13a7f4612d3.jpg"/></td><td>Power supply under voltage</td><td><img src="images/0915769fa1bfa211943b92beb802b8167cb7039820d28312831b3d0712d76bda.jpg"/></td><td>NV error</td></tr><tr><td><img src="images/7f51c2819993175d33c495d894ccb3345767b838d74ee04d1a8b0dfbcc3ea929.jpg"/></td><td>Internal voltage out of range</td><td><img src="images/4cd8ee0d0dd9be1c7dd36677c395e0c31fa9c1b21a16834e2e7c6c5cf0ec06fb.jpg"/></td><td>Move while disabled</td></tr><tr><td></td><td>Over current</td><td></td><td></td></tr></table>

NOTE: Items in bold italic represent drive faults, which automatically disable the motor.

## Enabled Status and Execution Q Program Status

The decimal point of LED1 is used to display the execution state of the Q program, and this decimal point flashes with the period of 250ms to indicate that the Q program is being executed.

The decimal point of LED2 is used to display the drive enabling situation, this decimal point is of which indicates the drive is disable, otherwise the drive is enable.

![](images/114670f0d95f55b89e4abf84a00093edbb5c6d35f1f5ee909922f3ada6eb1061.jpg)

## 6 Reference Materials

## 6.1 Drive Mechanical Outlines(Units: mm)

SSDC06/10-ECX-H

![](images/c2a6318e527bd050a3531f700763baa9fac948dedd60775707bb5bce6d80ddcd.jpg)

![](images/64855cffee4eb6a68c1958050a7e948d9973f0ecd42682b8199a48f8a2f24686.jpg)  
SSDC06/10-ECX-J

6.2 Technical Specifications

<table><tr><td colspan="3">Power Amplifier</td></tr><tr><td colspan="2">Amplifier Type</td><td>Dual H-Bridge, 4 Quadrant</td></tr><tr><td colspan="2">Current Control</td><td>4 state PWM at 16 KHz</td></tr><tr><td rowspan="2" colspan="2">Output Current</td><td>SSDC06:Continuous Current 6A max, Boost Current 7.5A max (1.5s), current limitation auto set-up by attached motor</td></tr><tr><td>SSDC10:Continuous Current 10A max, Boost Current 15A max (1.5s), current limitation auto set-up by attached motor</td></tr><tr><td rowspan="2" colspan="2">Power Supply</td><td>SSDC06:External nominal 24 - 70 volt DC power supply required, Absolute maximum input voltage range 18 - 75 VDC</td></tr><tr><td>SSDC10:External nominal 24 - 70 volt DC power supply required, Absolute maximum input voltage range 18 - 75 VDC</td></tr><tr><td colspan="2">Protection</td><td>Over-voltage, under-voltage, over-temp, motor/winding shorts (phase-to-phase, phase-to-ground)</td></tr><tr><td colspan="3">Controller</td></tr><tr><td rowspan="2" colspan="2">Electronic Gearing &amp; Encoder Resolution</td><td>20000 counts/rev( for AM17/23/24/34SS-N motors)</td></tr><tr><td>4096 counts/rev( for AM08/11/17/23/24/34RS motors)</td></tr><tr><td colspan="2">Speed Range</td><td>Up to 3000rpm</td></tr><tr><td colspan="2">Filters</td><td>Digital input noise filter, Analog input noise filter, Smoothing filter, PID filter, Notch filter</td></tr><tr><td colspan="2">Non-Volatile Storage</td><td>Configurations are saved in FLASH memory on-board the DSP</td></tr><tr><td colspan="2">Protocol</td><td>CoE conform CiA402, VoE(Supports update the firmware over EtherCAT)</td></tr><tr><td colspan="2">Modes of Operation</td><td>Profile Position, Profile Velocity, Profile Torque, Cyclic Synchronous Position, Cyclic Synchronous Velocity and Homing mode, Q programmer</td></tr><tr><td rowspan="2" colspan="2">Sync</td><td>SM Event: PP, PV, PT, Homing, Q program</td></tr><tr><td>SYNC Event: CSP, CSV, Homing, Q program</td></tr><tr><td rowspan="2">Digital Inputs</td><td>SSDC06/10-ECX-H</td><td>3 digital inputsX3, X4, X7: Optically isolated, single-ended, 5-24VDC; Minimum pulse width = 100 μs,Maximum pulse frequency = 5KHz</td></tr><tr><td>SSDC06/10-ECX-J</td><td>5 digital inputsX3, X4, X5, X7, X8: Optically isolated, single-ended, 5-24VDC; Minimum pulse width = 100 μs,Maximum pulse frequency = 5KHz</td></tr><tr><td rowspan="2">Digital Outputs</td><td>SSDC06/10-ECX-H</td><td>1 digital outputY2: Optically isolated, Open Collector, 30V/100 mA max, Maximum pulse frequency = 10KHz</td></tr><tr><td>SSDC06/10-ECX-J</td><td>2 digital outputsY1, Y2: Optically isolated, Open Collector, 30V/100 mA max, Maximum pulse frequency = 10KHz</td></tr><tr><td rowspan="3" colspan="2">Analog Inputs</td><td>1 analog input (Only for SSDC06/10-ECX-J)</td></tr><tr><td>Analog resolution: 12bit</td></tr><tr><td>Each input can accept a signal range of 0 to 5 VDC, ±5 VDC, 0 to 10 VDC or ±10 VDC</td></tr><tr><td colspan="2">+5V Output</td><td>4.8~5V, 100 mA max (Only for SSDC06/10-ECX-J)</td></tr><tr><td rowspan="2" colspan="2">Communication</td><td>USB for configuration</td></tr><tr><td>EtherCAT (Dual-port RJ45)</td></tr><tr><td colspan="3">Physical</td></tr><tr><td colspan="2">Ambient Temperature</td><td>0 to 40°C (32 to 104°F) when mounted to a suitable heatsink</td></tr><tr><td colspan="2">Ambient Humidity</td><td>90% Max., non-condensing</td></tr></table>

6.3 Recommended Motors

<table><tr><td rowspan="3">Model</td><td rowspan="3">Drive P/N</td><td rowspan="2">Torque</td><td rowspan="2">Rotor Inertia</td><td rowspan="2">Encoder Resolution</td><td rowspan="2">Maximum Speed</td><td rowspan="2">Mass</td><td rowspan="2">Frame Size</td><td colspan="5">Permissible Overhung Load(N)</td><td rowspan="3">Permissible Thrust Load</td></tr><tr><td colspan="5">Distance(L) from Shaft End(mm)</td></tr><tr><td>Nm</td><td> $gcm^2$ </td><td>counts/rev</td><td>RPM</td><td>g</td><td>mm</td><td>0</td><td>5</td><td>10</td><td>15</td><td>20</td></tr><tr><td>AM08RS1DMA</td><td rowspan="6">SSDC03</td><td>0.03</td><td>1.6</td><td rowspan="3">4096</td><td rowspan="28">3600</td><td>50</td><td rowspan="3">20</td><td rowspan="3">12</td><td rowspan="3">15</td><td rowspan="3">20</td><td rowspan="3">-</td><td rowspan="3">-</td><td rowspan="28">Less than the motor mass</td></tr><tr><td>AM08RS2DMA</td><td>0.042</td><td>2.9</td><td>70</td></tr><tr><td>AM08RS3DMA</td><td>0.05</td><td>4.2</td><td>90</td></tr><tr><td>AM11RS1DMA</td><td>0.065</td><td>9</td><td rowspan="7">4096</td><td>118</td><td rowspan="3">28</td><td rowspan="3">20</td><td rowspan="3">25</td><td rowspan="3">34</td><td rowspan="3">52</td><td rowspan="3">-</td></tr><tr><td>AM11RS2DMA</td><td>0.08</td><td>12</td><td>168</td></tr><tr><td>AM11RS3DMA</td><td>0.125</td><td>18</td><td>218</td></tr><tr><td>AM17RS1DM□</td><td rowspan="8">SSDC03 or SSDC10</td><td>0.26</td><td>38</td><td>390</td><td rowspan="8">42</td><td rowspan="8">35</td><td rowspan="8">44</td><td rowspan="8">58</td><td rowspan="8">85</td><td rowspan="8">-</td></tr><tr><td>AM17RS2DM□</td><td>0.42</td><td>57</td><td>440</td></tr><tr><td>AM17RS3DM□</td><td>0.52</td><td>82</td><td>520</td></tr><tr><td>AM17RS4DM□</td><td>0.7</td><td>123</td><td>760</td></tr><tr><td>AM17SS1DG□-N</td><td>0.26</td><td>38</td><td rowspan="4">20000</td><td>390</td></tr><tr><td>AM17SS2DG□-N</td><td>0.42</td><td>57</td><td>440</td></tr><tr><td>AM17SS3DG□-N</td><td>0.52</td><td>82</td><td>520</td></tr><tr><td>AM17SS4DG□-N</td><td>0.7</td><td>123</td><td>760</td></tr><tr><td>AM23RS2DM□</td><td rowspan="8">SSDC06 or SSDC10</td><td>0.95</td><td>260</td><td rowspan="3">4096</td><td>850</td><td rowspan="6">56</td><td rowspan="6">63</td><td rowspan="6">75</td><td rowspan="6">95</td><td rowspan="6">130</td><td rowspan="6">190</td></tr><tr><td>AM23RS3DM□</td><td>1.5</td><td>460</td><td>1250</td></tr><tr><td>AM23RS4DMA</td><td>2.4</td><td>365</td><td>1090</td></tr><tr><td>AM23SS2DG□-N</td><td>0.95</td><td>260</td><td rowspan="3">20000</td><td>850</td></tr><tr><td>AM23SS3DG□-N</td><td>1.5</td><td>460</td><td>1250</td></tr><tr><td>AM23SS4DGA-N</td><td>2.4</td><td>365</td><td>1090</td></tr><tr><td>AM24RS3DM□</td><td>2.5</td><td>900</td><td>4096</td><td>1650</td><td rowspan="2">60</td><td rowspan="2">90</td><td rowspan="2">100</td><td rowspan="2">130</td><td rowspan="2">180</td><td rowspan="2">270</td></tr><tr><td>AM24SS3DG□-N</td><td>2.5</td><td>900</td><td>20000</td><td>1650</td></tr><tr><td>AM34RS1DMA</td><td rowspan="6">SSDC10</td><td>2.7</td><td>915</td><td rowspan="3">4096</td><td>2000</td><td rowspan="6">86</td><td rowspan="6">260</td><td rowspan="6">290</td><td rowspan="6">340</td><td rowspan="6">390</td><td rowspan="6">480</td></tr><tr><td>AM34RS3DMA</td><td>5.2</td><td>1480</td><td>3100</td></tr><tr><td>AM34RS5DMA</td><td>7.0</td><td>2200</td><td>4200</td></tr><tr><td>AM34SS1DGA-N</td><td>2.7</td><td>915</td><td rowspan="3">20000</td><td>2000</td></tr><tr><td>AM34SS3DGA-N</td><td>5.2</td><td>1480</td><td>3100</td></tr><tr><td>AM34SS5DGA-N</td><td>7.0</td><td>2200</td><td>4200</td></tr></table>

□：A or B, refer to motor part numbering system

![](images/1bba85347bad0351e0e8102ace1e00437d30f6adea2d737589015590bbfd6a70.jpg)

## 7 Contacting MOONS

# Customer Service Center +86-400-820-9661

![](images/68ac7ed850b652e73a5ea6ad3a936e4cdf6e03a6ff9008342b1eb8ee241428e3.jpg)

## MOONS’ Headquarter

Building 7, Lane 88, Minbei Road, Minhang District, Shanghai， 201107，P.R.China

## MOONS’ Taicang

No. 18 Yingang Rd, Fuqiao Town, Taicang City Jiangsu Province, 215434, P.R. China

## Domestic Office

## Beijing

Room 1206, Jing Liang Mansion, No.16 Middle Road of East,3rd Ring, Chaoyang District, Beijing 100022, P.R. China

## Qingdao

Room1913,Scientific and Technological Innovation Builing,Floor19, No.171, ShanDong Road,Shibei District,QingDao, Shangdong Province, 266033, P.R. China

## Xi an’

Room 1006, Tower D, Wangzuo International City, No.1 Tangyan Road, Xi an, Shanxi ’ Province, 710065, P.R. China

## Wuhan

Room 3001, World Trade Tower, No.686 Jiefang Avenue, Jianghan District, Wuhan, Hubei Province, 430022, P.R. China

## Hefei

Room 1521, Building B, CBC Tuoji Plaza, Jinggang Road, Shushan District, Hefei, Anhui Province, 230088, P.R. China

## Nanjing

Room 1101-1102, Building 2, New Town Development Center, No.126 Tianyuan Road , Moling Street, Jiangning District, Jiangsu Province, China, 211106, P.R. China

## Suzhou

Room 1103-1105, North Building 4, Huizu Plaza, 758 Nanhuan East Rd, Gusu District, Suzhou,Jiangsu Province, 215007, P.R. China

## Ningbo

Room 309, Tower B, Taifu Plaza, 565 Jiangjia Road,Jiangdong District, Ningbo, Zhejiang Province, 315040, P.R. China

## Chengdu

Room 3907, Maoye Plaza, No.19, Dongyu Street, Jinjiang Distrit, Chengdu Sichuan Province, 610066, P.R. China

## Chongqing

Room 2108, South yuanzhu Buliding 20, No.18 Fuquan Rd., Jiangbei District, Chongqing, 400000, P.R. China

## Guangzhou

Room 4006, Tower B, China Shine Plaza, 9 Linhe Xi Road, Tianhe District, Guangzhou, Guangdong Province, 510610, P.R. China

## Dongguan

Room 1106-1207, Building 5, Linrunzhigu, No.1 RD 5th Rd, Songshan Lake, Dongguan, Guangdong Province, 523000, P.R. China

## Shenzhen

Room 3901, Building A, Zhongguan Times Square,No 4168 Liuxian Avenue, Nanshan District, Shenzhen, Guangdong Province, 518000, P.R. China

## North America

## USA

MOONS' INDUSTRIES (AMERICA), INC. (Chicago) 1113 North Prospect Avenue, Itasca, IL 60143, USA

MOONS’ INDUSTRIES (AMERICA), INC. (Boston) 36 Cordage Park Circle, Suite 310 Plymouth, MA 02360, USA

APPLIED MOTION PRODUCTS, INC. (Morgan Hill) 18645 Madrone Parkway. Morgan Hill, CA 95037, USA

LIN ENGINEERING, Inc. (Morgan Hill) 16245 Vineyard Blvd., Morgan Hill, CA 95037, USA

## Europe

## Germany

AMP & MOONS’ AUTOMATION (GERMANY) GMBH Kaiserhofstr. 15 60313 Frankfurt am Main Germany

## Italy

MOONS’ INDUSTRIES (EUROPE) HEAD QUARTER S.R.L. Via Torri Bianche n.1 20871 Vimercate(MB) Italy

## Switzerland

TECHNOSOFT SA Avenue des Alpes 20 CH 2000 Neuchâtel Switzerland

## U.K

MOONS' INDUSTRIES (UK), LIMITED Rooms 4&5, 1st Floor, Greenbank, London Road, Reading, UK. RG1 5AQ

## Asia

## Singapore

MOONS' INDUSTRIES (SOUTH-EAST ASIA) PTE. LTD. 33 Ubi Avenue 3 #08-23 Vertex Singapore 408868

## Japan

MOONS' INDUSTRIES JAPAN CO., LTD. Room 602, 6F, Shin Yokohama Koushin Building, 2-12-1, Shin-Yokohama, Kohoku-ku, Yokohama, Kanagawa, Japan 222-0033

## India

MOONS' INTELLIGENT MOTION SYSTEM INDIA PVT. LTD. Room. 908, 9th Floor, Amar Business Park, Tal. Haveli, Baner, Pune, India 411045

## Vietnam

MOONS' INDUSTRIES (VIETNAM) COMPANY LIMITED. Factory C1&D1, Lot IN3-11 \* A, VSIP Hai Phong Industrial Park, Lap Le Ward, Thuy Nguyen City, Hai Phong City, Vietnam.

![](images/86313abe868f3971fcf7c4cac83f897f993072e4507e12681474d583aedbe63a.jpg)

https://www.moonsindustries.com/ E-mail:ama-info@moons.com.cn MOONS'

moving in better ways

• All the specifications, technical parameters of the products provided in this catalog are for reference only, subject to change without notice. For the latest details, please contact our sales department.