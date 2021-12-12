import socket
from datetime import date
from threading import Thread
from objects import Item, User
import pickle

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

    def add_default_admins(self):
        admin = User("kai.marshall", "password", True)
        admin2 = User("kyung.youm", "password", True)
        admin3 = User("ahmed.aldallee", "password", True)

        self._user_list.append(admin)
        self._user_list.append(admin2)
        self._user_list.append(admin3)

    def load_users_and_items(self):
        with open('user_data', 'rb') as file:
            self._user_list = pickle.load(file)
            file.close()

        with open('item_data', 'rb') as file:
            self._item_list = pickle.load(file)
            file.close()

    def update_users_and_items(self):
        with open('user_data', 'wb') as file:
            pickle.dump(self._user_list, file)

        with open('item_data', 'wb') as file:
            pickle.dump(self._item_list, file)

    def run(self):

        try:
            self.load_users_and_items()
        except EOFError:
            self.add_default_admins()
            self.update_users_and_items()


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

    def get_all_items(self):
        returnString = ""
        for item in self.__server.item_list:
            returnString += item.name + "|" + "N/A" + "|" + str(item.price_per_day) + "|"
        for user in self.__server.user_list:
            if len(user.rented_items) != 0:
                for item in user.rented_items:
                    returnString += item.name + "|" + user.username + "|" + str(item.price_per_day) + "|"
        return returnString

    def _process_client_request(self):
        client_message = self._receive_message()
        self._display_message(f"CLIENT REQUEST>>{client_message}")

        arguments = client_message.split("|")
        current_user = self.__server.current_users.get(int(self.id - 1))
        # minus one for indexing reasons

        response = ""

        try:
            # client attempting to login
            if arguments[0] == "L":
                is_found = False
                for user in self.__server.user_list:
                    if (arguments[1] == user.username) and (arguments[2].rstrip() == user.password):
                        response = "Login Successful!|" + str(user.is_admin) + "|success" + "\n"
                        is_found = True
                        self.__server.current_users[self.id] = self.__server.current_users.get(self.id, user)
                        break

                if not is_found:
                    response = "Unrecognized username/password combination|False|fail\n"

            # user-client requesting available items in inventory
            elif arguments[0] == "I":
                if len(self.__server.item_list) == 0:
                    response += "none"
                else:
                    for item in self.__server.item_list:
                        response += item.name + "|" + str(item.price_per_day) + "|"
                response += "\n"

            # admin-client requesting all items
            elif arguments[0] == "adminI":
                if len(self.__server.item_list) == 0:
                    response += "none"
                else:
                    response += self.get_all_items()
                response += "\n"

            # admin-client attempting removal of an Item
            elif arguments[0] == "adminR":
                if len(self.__server.item_list) == 0:
                    response += "none"
                else:
                    is_found = False
                    for item in self.__server.item_list:
                        if item.name == arguments[1]:
                            self.__server.item_list.remove(item)
                            self.__server.update_users_and_items()
                            is_found = True
                            response += "Item Successfully Removed!"
                            break
                    if not is_found:
                        response += "Item either not in inventory or currently rented by a user."
                response += "\n"

            # admin-client attempting removal of an User
            elif arguments[0] == "adminRU":
                if len(self.__server.user_list) == 0:
                    response += "none"
                elif arguments[1] == current_user.username:
                    response += "Cannot remove self."
                else:
                    is_found = False
                    for user in self.__server.user_list:
                        if user.username == arguments[1]:
                            for item in user.rented_items:
                                self.__server.item_list.append(item)

                            self.__server.user_list.remove(user)
                            self.__server.update_users_and_items()
                            is_found = True
                            response += "User Successfully Removed!"
                            break
                    if not is_found:
                        response += "User not found."

                response += "\n"

            # admin-client requesting all users
            elif arguments[0] == "adminUL":
                if len(self.__server.user_list) == 0:
                    response += "none"
                else:
                    for user in self.__server.user_list:
                        response += user.username + "|" + user.password + "|" + str(user.is_admin) + "|"
                response += "\n"

            # admin-client attempting addition of Item
            elif arguments[0] == "adminA":
                is_valid = True
                for item in self.__server.item_list:
                    if item.name == arguments[1]:
                        response += "Items can't have the same name."
                        is_valid = False
                        break

                if is_valid:
                    self.__server.item_list.append(Item(arguments[1], float(arguments[2].strip('$'))))
                    self.__server.update_users_and_items()
                    response += "Item successfully added!"
                response += "\n"

            # admin-client attempting addition of User
            elif arguments[0] == "adminAU":
                is_valid = True
                for user in self.__server.user_list:
                    if user.username == arguments[1]:
                        response += "Users can't have the same username."
                        is_valid = False
                        break

                if is_valid:
                    self.__server.user_list.append(User(arguments[1], arguments[2], False))
                    self.__server.update_users_and_items()
                    response += "User successfully added!"
                response += "\n"

            # user-client attempting to return a rented item
            elif arguments[0] == "R":
                for item in current_user.rented_items:
                    if arguments[1] == item.name:
                        response += "Item Returned!"
                        current_user.return_item(item)
                        self.__server.item_list.append(item)
                        self.__server.update_users_and_items()
                        break
                response += "\n"

            # user-client attempting to [C]heck out (rent) and item
            elif arguments[0] == "C":
                for item in self.__server.item_list:
                    if arguments[1] == item.name:
                        response += "Item Rented!"
                        current_user.rent_item(item)
                        self.__server.item_list.remove(item)
                        self.__server.update_users_and_items()

                        break
                response += "\n"

            # user-client requesting their rented items
            elif arguments[0] == "GRI":

                is_found = False

                if len(current_user.rented_items) == 0:
                    response = "none"
                else:
                    for item in current_user.rented_items:
                        response += item.name + "|" + str(item.price_per_day) + "|"

                response += "\n"

            # client terminate
            elif arguments[0] == "T":
                response = "OK|T\n"
                self._send_message(response)
                self.terminate_connection()
                return

            # server terminate
            elif arguments[0] == "TERMINATE":
                response = "Server Shutdown\n"
                self._send_message(response)
                self.__server.terminate_server()

            else:
                response = "ERR|Unknown Command\n"

        except ValueError as ve:
            response = "ERR|" + str(ve) + "\n"

        self._send_message(response)


































































