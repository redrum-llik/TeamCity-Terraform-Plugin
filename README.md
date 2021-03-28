# Terraform plugin for TeamCity

This project aims to provide a simple Terraform runner for the TeamCity. The key features are:

* wrappers for the popular commands
* automatic detection of Terraform executable on the agent side
* ability to expose plan binary output path as a build parameter
* ability to pass configuration parameters via `-var-file`
* ability to use [tfenv](https://github.com/tfutils/tfenv) to install target version on agent side, optionally to read it from .terraform-version file

The features which are planned:
* workspace logic handler

## Requirements
For auto-detection mode - installed Terraform on the agent side.
For tfenv mode - installed tfenv on the agent side. 

## Implementation details

For the `terraform plan`, the value of `-out` parameter will be preserved as `Terraform_Plan_Output` build configuration parameter which can be reused in the later steps, on the artifact rules or anywhere else.

The detection logic will look up the ansible-playbook executable in PATH as well as in any folder defined in `teamcity.terraform.detector.search.paths` agent property. All found instances are stored in the configuration variables of the agent (see `terraform.*` variables).
