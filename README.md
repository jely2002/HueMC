# HueMC
A minecraft plugin to control Philips Hue lights in Minecraft.
The plugin works by creating a so called **input**. An input is a redstone lamp that of course can be powered by in game redstone.
There are as of now 2 different types of inputs: switch and dimmer.

#### **Important: This plugin will only work if the minecraft server is located inside the same network as the Hue Bridge.**

## Switch input
This input acts as a normal light switch, if it receives redstone power it will turn on the linked Hue light(s).
This applies the other way around as well. 

## Dimmer input
This input acts as a dimmer. Depending on the strength of the redstone signal it will adjust the brightness of the Hue light(s).
A redstone signal strength of 0 means a brightness of 0, where a strength of 15 means a brightness of 100.

## Colors
There are a variety of colors that can be added to an input. The color of an input can be changed by placing stained glass underneath it.
When there is no glass underneath the light will be white, this behavior can be changed in the config. So when there is no block underneath it won't change any colors.

## What can I link an input with?
An input can be linked with a whole zone or room. As well as just one light in a zone or room.
The linking process is quite easy, the plugin auto-detects your zones, rooms and the lights that are in them.

## Commands
| Command       | Description     
| ------------- |:-------------:|
|/huemc setup    | Sets up a connection to your bridge |
|/huemc input list    | List all added inputs |
| /huemc input add     | Add a new input      |
| /huemc input remove  | Remove an input      |

## Permissions
As of now there are no permissions.
