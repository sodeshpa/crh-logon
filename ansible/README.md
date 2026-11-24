# Purpose
Ansible is used to manage the build machines of the project.

# Instructions
To update the build machnies, run the following commands on your local machnine. (Not on the build machnies.)

## 1. Activate the virtualenv
`. activate-venv.sh`
## Run the setup
`ansible-playbook -vv -i inventories/integration buildnode.yml`
