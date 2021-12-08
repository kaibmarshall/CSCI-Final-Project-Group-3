from server import MultiServer

if __name__ == "__main__":
    server = MultiServer("localhost", 10000, 5)
    server.run()
