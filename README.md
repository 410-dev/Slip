# Slip
Inspired by XPopup



## Prerequisite

1. A connection to router with internal IP address (192.168.x.x)
2. Java Runtime Environment 17



## Usage

1. Open
2. Change your name, then press enter or return key to save
3. Another person should have the program ready
4. Double click on the person you'd like to send a message to, then write a message.
5. Press send key to send.



## Shortcuts

1. Received Message:
   1. Shift + Enter: Open a reply message
2. Send messasge:
   1. Shift + Enter: Send the message



## Option documentation

options_localslip.txt file is a option file that contains the application configuration.

- ``name=Text``
  - User's name that will be displayed to others
- ``recvPort=PortInteger``
  - Message will be received on this port. Default highly recommended, but you may change to chat in a different channel. Changing port will make you invisible from default port.
  - Default: 30551
- ``messageReceiveWindowSize=WidthInteger,HeightInteger``
  - Default window size for message received window and messege send window.
  - Default: 500, 400
- ``mainWindowSize=WidthInteger,HeightInteger``
  - Default window size for main window that contains user list
  - Default: 400, 600
- ``updateInterval=SecondsInteger``
  - Time that will scan the network automatically
  - Default: 30

