#!/bin/bash

VENV_NAME=${1:-venv}

virtualenv $VENV_NAME
source $VENV_NAME/bin/activate
pip install --upgrade pip==19.2.1
pip install -r requirements.txt
