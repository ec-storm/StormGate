from functions import read, write, log


def function1(old_value, new_value):
    pass


def function2(old_value, new_value):
    pass


class Configuration:
    def __init__(self):
        self.callback_list = {}

    def change_event(self, name, callback):
        self.callback_list[name] = callback

    def load(self):
        self.change_event("channel1.variable1", function1)
        self.change_event("channel1.variable2", function2)

        return self.callback_list
