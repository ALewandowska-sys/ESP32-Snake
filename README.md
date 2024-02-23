# Application to handle SNAKE-robot

## Views
![image](https://github.com/ALewandowska-sys/ESP32-Snake/assets/82601472/0ac8a728-159e-4fce-8c79-1cccd3c26275)\
Application have 2 views:
- main, where is information about robot's WiFi
- stream, where user can control robot and see camera stream. This view is available when user is connect to access point

## Nice functions
1. Application is set to use HTTP protocol, so it isn't a default configuration.
   I set it using file `network_security_config.xml` and annotation inside AndroidManifest file: `android:networkSecurityConfig="@xml/network_security_config"`
3. Joystick is handle to map value to left and right engines.
   I make it inside function `calculatePowerForEngines(angle: Int, percentOfPower: Float)`
