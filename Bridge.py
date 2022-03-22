import json
import socket
from threading import Thread

class Bridge:

	def connect(self, host, port, username):
		self.host = host
		self.port = port
		self.username = username
		self.running = True

		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.socket.connect((host, port))

		self.listenerThread = Thread(target = self.listener)
		self.listenerThread.start()

	def getSocket(self, command):
		if not command.endswith("\n"):
			command += "\n"

		self.socket.send(bytes(str(command), encoding='utf8'))
		data = self.socket.recv(1024)
		return data

	def postSocket(self, command):
		if not command.endswith("\n"):
			command += "\n"

		self.socket.send(bytes(str(command), encoding='utf8'))

	def disconnect(self):
		self.running = False
		self.socket.close()

	def listener(self):
		while self.running:
			try:
				data = self.socket.recv(1024)
				data = json.loads(data)
				typ = data['type']

				if typ == "request":
					value = data['value']
					if value == "username":
						self.postSocket(getJson("username", self.username))

				elif typ == "message":
					username = data['username']
					message = data['message']

					if username == "null":
						print(message)
					else:
						print(f"{username} > {message}")
			except Exception as e:
				break


	def sendMessage(self, message):
		self.postSocket(getJson("type", "message", "message", message))


def getJson(*strings):
	js = "{ "
	for x in range(0, len(strings), 2):
		q = ""
		if x != 0:
			q = ","
		string = strings[x]
		value = strings[x+1]

		js += f'{q} "{string}" : "{value}"'
	js += " }"
	return js
