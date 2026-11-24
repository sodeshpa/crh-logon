#!/bin/sh
bash init-variables.sh
ansible-playbook -v logonebmdb2.play
ansible-playbook -v logonebm1.play
ansible-playbook -v logonebm2.play
