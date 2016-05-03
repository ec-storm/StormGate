import time
from threading import Thread

callback_list = {}
stopped = False
worker = None


class Worker(Thread):
    def __init__(self):
        Thread.__init__(self)

    def run(self):
        config = Configuration()
        global callback_list
        callback_list = config.load()

        global stopped
        while not stopped:
            time.sleep(1)


def java_on_change_callback(variable, old_value, new_value):
    global callback_list
    print callback_list
    callback_list[variable](old_value, new_value)


def java_stop_thread():
    global stopped
    stopped = True
    worker.join()


def main():
    global worker
    worker = Worker()
    worker.start()
