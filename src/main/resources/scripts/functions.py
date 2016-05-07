from com.minhdtb.storm.common import Utils
from com.minhdtb.storm.core.engine import StormEngine


def read(name):
    return StormEngine.readVariable(name)


def write(name, value):
    StormEngine.writeVariable(name, value)


def log(value):
    Utils.log(str(value))
