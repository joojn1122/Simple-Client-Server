import json
from Bridge import Bridge
import sys

username = sys.argv[1]
bridge = Bridge()
bridge.connect('localhost', 30000, username)

while bridge.running:
	message = input()

	if message == "exit":
		bridge.disconnect()
	elif message != "":
		bridge.sendMessage(message)
