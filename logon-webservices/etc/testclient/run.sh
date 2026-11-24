#!/bin/sh

java -cp logon-client.jar:etc -Dfile.encoding=UTF8 TestClient $@
