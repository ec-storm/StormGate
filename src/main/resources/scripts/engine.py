import time
from threading import Thread

stopped = False
worker = None


class Worker(Thread):
    def __init__(self):
        Thread.__init__(self)

    def run(self):
        global stopped
        while not stopped:
            time.sleep(1)


def java_on_change_callback(variable, old_value, new_value):
    try:
        method = globals()['change_' + variable.replace('.', '_')]
        if method is not None:
            method(old_value, new_value)
    except:
        pass


def java_stop_thread():
    global stopped
    stopped = True
    worker.join()


def main():
    global worker
    worker = Worker()
    worker.start()
