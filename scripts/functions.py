def read(name):
    print 'read ', name


def write(name, value):
    print 'write ', name, ' = ', value


def log(value):
    from com.minhdtb.storm.common import Utils
    Utils.writeLog(value)
