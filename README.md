# ![](https://raw.githubusercontent.com/virgilholmes/projectile-thing/refs/heads/main/static/logo.png "Projectile-Thing Logo")

Code for a "turret" robot I made for my AP CSA class.


## How to run

#### Make sure that you have the prereqs for Python

```
pip install -r requirements.txt
```


#### Run the frontend

```
 flask --app serv run
 ```
 #### Run the backend
```
mvn package
java --enable-native-access=ALL-UNNAMED -cp 'target/classes:target/dependency/*' main
```

The backend requires Java 25 or newer and the motor wiring described below.
Each POST to `/shoot` runs both motors for 200 milliseconds, then sets both
outputs LOW. Change `motors.runFor(200)` in `main.java` to adjust the duration.

## Run the wheels with Java

`motor.java` is a standalone program for a Raspberry Pi 5 and an L298N
module. Use Java 25 or newer and Maven on the Pi.

With all power off, connect:

| Raspberry Pi / connection | L298N |
| --- | --- |
| GPIO2 (physical pin 3) | IN1 |
| GPIO3 (physical pin 5) | IN3 |
| Pi GND (physical pin 6) | GND |
| GND | IN2 and IN4 |

Fit the **ENA and ENB** enable jumpers. Connect one motor to OUT1/OUT2 and
the other to OUT3/OUT4. Supply the driver's motor-power terminal from a
power supply suitable for your motors, and connect its negative to driver
GND. Configure the module's separate **5V-EN/regulator jumper** according to
its instructions and supply voltage; it is different from ENA/ENB.
Do not connect the driver's 5 V terminal to a Pi GPIO.

GPIO2/GPIO3 are also the I2C pins: disable I2C if it is enabled or otherwise
using these pins. They have pull-ups, so the wheels may start before the
program runs. Keep the motor supply off until ready. For controlled startup,
use GPIO-controlled enable inputs with hardware pull-downs instead of the
ENA/ENB jumpers.

Build and run on the Pi:

```bash
mvn package
java --enable-native-access=ALL-UNNAMED -cp 'target/classes:target/dependency/*' motor
```

Both outputs stay HIGH until Ctrl+C. Normal shutdown sets them LOW.
Forced termination or a crash may leave the outputs active; disconnect motor
power to stop in that situation. This program runs both channels in one
direction at full speed; wheel direction depends on the motor wiring.

Pi4J setup: https://www.pi4j.com/getting-started/minimal-example-application/

