#!/bin/sh
bash init-variables.sh
ansible-playbook -v logonebmdb3.play
ansible-playbook -v logonebm3.play
ansible-playbook -v logonebm4.play
