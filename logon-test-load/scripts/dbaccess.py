from java.sql import DriverManager
from oracle.jdbc import OracleDriver
from net.grinder.script.Grinder import grinder
from scripts.base_config import database, db_user, db_pass, db_schema

DriverManager.registerDriver(OracleDriver())

USE_SCHEMA = "ALTER SESSION SET CURRENT_SCHEMA=" + db_schema

def getConnection():
    return DriverManager.getConnection(database, db_user, db_pass)

def ensureClosed(object):
    try: object.close()
    except: pass

def queryColumn(query):
    connection = getConnection()
    statement = connection.createStatement()
    result = []

    statement.execute(USE_SCHEMA)
    resultSet = statement.executeQuery(query)

    while resultSet.next():
        column = resultSet.getString(1)
        result.append(column)

    ensureClosed(statement)
    ensureClosed(connection)
    return result
