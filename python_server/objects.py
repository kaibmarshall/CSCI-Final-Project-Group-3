
class Item:
    def __init__(self, name :str, price_per_day :float):
        self._name = name
        self._price_per_day = price_per_day
        self._is_available = True

    @property
    def name(self):
        return self._name

    @property
    def price_per_day(self):
        return self._price_per_day

    @property
    def is_available(self):
        return self._is_available

    @property
    def set_availability(self, value :bool):
        self._is_available = value


class User:
    def __init__(self, username :str, password :str, isAdmin :bool):
        self._username = username
        self._password = password
        self._is_admin = isAdmin
        self._rented_items = []

    @property
    def username(self):
        return self._username

    @property
    def password(self):
        return self._password

    @property
    def rented_items(self):
        return self._rented_items

    @property
    def is_admin(self):
        return self._is_admin

    def rent_item(self, item: Item):
        self._rented_items.append(item)

    def return_item(self, item: Item):
        if item in self._rented_items:
            self._rented_items.remove(item)
            return True
        else:
            return False

    def __str__(self):
        return_string = self._username + " with items: "
        for item in self._rented_items:
            return_string += item.name + "\n"
        return return_string


