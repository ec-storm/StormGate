def read(name):
    print 'read ', name


def write(name, value):
    from com.minhdtb.storm.core.engine import StormEngine
    StormEngine.writeVariable(name, value)


def log(value):
    from com.minhdtb.storm.common import Utils
    Utils.writeLog(value)