import socket
from datetime import date
from threading import Thread
from objects import Item
from objects import User
class MultiServer:
    def __init__(self, ip: str, port: int, backlog: int):
        self.__ip = ip
        self.__port = port
        self.__backlog = backlog
        self.__server_socket = None
        self.__keep__running = True
        self.__connection_count = 0
        self.__list_cw = []

        self.current_users = {}

        self._user_list = []
        self._item_list = []


    def terminate_server(self):
        self.__keep__running = False
        self.__server_socket.close()

    def populate_users_for_testing(self):
        user = User("kai", "pass", False)
        user.rent_item(Item("Macbook", 12.99))
        user.rent_item(Item("anotha", 12.99))

        self._user_list.append(user)
        self._user_list.append(User("cp", "pass", False))

    def populate_items_for_testing(self):
        self._item_list.append(Item("Macbook", 12.99))
        self._item_list.append(Item("Windows", 10.99))



    def run(self):
        self.populate_users_for_testing()
        self.populate_items_for_testing()

        self.__server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__server_socket.bind((self.__ip, self.__port))
        self.__server_socket.listen(self.__backlog)

        while self.__keep__running:
            print(f"""[SRV] Waiting for Client""")
            try:
                client_socket, client_address = self.__server_socket.accept()
                print(f"""[SRV] Got a connection from {client_address}""")
                cw = ClientWorker(self.__connection_count, client_socket, self)
                self.__connection_count += 1
                self.__list_cw.append(cw)
                cw.start()
            except Exception as e:
                print(e)

        cw: ClientWorker
        for cw in self.__list_cw:
            cw.terminate_connection()
            cw.join()

    def send_message (self, msg:str, client_socket:socket):
        print(f"""[SRV] send>> {msg}""")
        client_socket.send(msg.encode())


    def receive_message(self, client_socket:socket, max_length:int = 1024):
        msg = client_socket.recv(max_length).decode()
        print(f"""[SRV] RCV>> {msg}""")
        return  msg

    @property
    def user_list(self):
        return self._user_list

    @property
    def item_list(self):
        return self._item_list


class ClientWorker(Thread):
    def __init__(self, client_id: int, client_socket: socket, server: MultiServer, currentUser = None):
        super().__init__()

        self.__client_socket = client_socket
        self.__keep_running_client = True
        self.__server = server
        self.__id = client_id

    @property
    def id(self):
        return self.__id

    def run(self):
        #self._send_message("Connected to Python Inventory Server")

        while self.__keep_running_client:
            self._process_client_request()

        self.__client_socket.close()

    def terminate_connection(self):
        self.__keep_running_client = False
        self.__client_socket.close()

    def _display_message(self, message: str):
        print(f"[SRV] {message}")

    def _send_message (self, msg:str):
        self._display_message(f"""SENDING>>{msg}""")
        self.__client_socket.send(msg.encode("UTF-8"))
        pass

    def _receive_message(self, max_length:int = 1024):
        msg = self.__client_socket.recv(max_length).decode("UTF-8")
        self._display_message(f"""RECEIVED>>{msg}""")
        return msg


    def _process_client_request(self):
        client_message = self._receive_message()
        self._display_message(f"CLIENT REQUEST>>{client_message}")

        arguments = client_message.split("|")
        current_user = self.__server.current_users.get(int(self.id - 1))
        # minus one for indexing reasons

        response = ""

        try:
            if arguments[0] == "L":
                is_found = False
                for user in self.__server.user_list:
                    if (arguments[1] == user.username) and (arguments[2].rstrip() == user.password):
                        response = "Login Successful!|" + str(user.is_admin) + "|success" + "\n"
                        is_found = True
                        self.__server.current_users[self.id] = self.__server.current_users.get(self.id, user)
                        print(self.__server.current_users)
                        break

                if not is_found:
                    response = "Unrecognized username/password combination|False|fail\n"

            elif arguments[0] == "I":
                if len(self.__server.item_list) == 0:
                    response += "none"
                else:
                    for item in self.__server.item_list:
                        response += item.name + "|" + str(item.price_per_day) + "|"
                response += "\n"

            elif arguments[0] == "U":
                for user in self.__server.user_list:
                    response += user.username + "|"
                response += "\n"

            elif arguments[0] == "R":
                for item in current_user.rented_items:
                    if arguments[1] == item.name:
                        response += "Item Returned!"
                        current_user.return_item(item)
                        self.__server.item_list.append(item)
                        break
                response += "\n"

            elif arguments[0] == "C":
                for item in self.__server.item_list:
                    if arguments[1] == item.name:
                        response += "Item Rented!"
                        current_user.rent_item(item)
                        self.__server.item_list.remove(item)
                        break
                response += "\n"

            elif arguments[0] == "GRI":

                is_found = False

                if len(current_user.rented_items) == 0:
                    response = "none"
                else:
                    for item in current_user.rented_items:
                        response += item.name + "|" + str(item.price_per_day) + "|"

                response += "\n"

            elif arguments[0] == "T":
                self.terminate_connection()
                response = "OK|T\n"

            elif arguments[0] == "TERMINATE":
                response = "OK\n"

            else:
                response = "ERR|Unknown Command\n"

        except ValueError as ve:
            response = "ERR|" + str(ve) + "\n"

        self._send_message(response)


































































