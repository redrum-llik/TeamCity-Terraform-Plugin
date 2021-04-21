# Terraform plugin for TeamCity

This project aims to provide a simple Terraform runner for TeamCity. The key features are:
* wrappers for the popular commands and initialization stage (init/workspaces logic)
* automatic detection of Terraform executable on the agent side
* ability to pass configuration parameters via `-var-file`
* ability to use [tfenv](https://github.com/tfutils/tfenv) to install/switch to the target version on the agent side

Example of the build log:
![image](https://user-images.githubusercontent.com/63649969/113509602-27608200-955f-11eb-8438-feb62088e10a.png)

## Requirements

For auto-detection mode: installed Terraform on the agent side.  
For tfenv mode: installed tfenv on the agent side.

# Configuration

## Initialization Parameters

**Version**: select the Terraform version choice logic:

* Auto-detect: use the automatically detected version on the agent side.
* Fetch with tfenv: specify an exact version to be fetched on the agent side, or leave blank for the `tfenv` auto-detection logic.

**Init**: run `terraform init` before execution of the specified command.

**Use workspace**: try switching to the specified workspace, fail if the workspace has not been found.

**Create if not found**: if the specified workspace has not been found, try creating it.

## Command Parameters

**Command**: select the Terraform command:

* plan: invoke `terraform plan`, allows specifying a custom path for the plan output.
* apply: invoke `terraform apply`, allows specifying a custom path to the state backup file (produced by the plan).
* Custom: specify your own command for the execution.

**Additional arguments**: any extra arguments to be passed to the command.

**Pass system properties**: add `--extra-vars` argument pointing to the temporary file with system variables (see below).

## Docker Settings

See the relevant information on the [Docker Wrapper](https://www.jetbrains.com/help/teamcity/docker-wrapper.html) documentation page.

# Implementation details

## Plan output

For `terraform plan`, this runner will always append the `-out` argument unless it is already available. The output file path is defined as follows:
* if a custom output path is specified, use this path (either in the dedicated field or in the _Additional arguments_ field)
* else, if the workspace name is defined, use `<working directory>/terraform_plan_<workspace name>.out`
* else, use `<working directory>/terraform_plan.out`

The runner will [automatically publish the output file](https://www.jetbrains.com/help/teamcity/service-messages.html#Publishing+Artifacts+while+the+Build+is+Still+in+Progress) as an artifact.

## Terraform detection

The detection logic will look for the `terraform` executable in `PATH` as well as in any directory defined in the `teamcity.terraform.detector.search.path` agent property. All found instances are stored in the configuration variables of the agent (see `terraform.*` variables).

The runner will impose the following [agent requirements](https://www.jetbrains.com/help/teamcity/agent-requirements.html) (depending on the version choise logic described above):

* `terraform.version` exists
* `tfenv.version` exists

## System properties

If a corresponding option is enabled, system properties will be exported into a temporary JSON file. This file will be supplied as the `-var-file` value. The dots (`.`) in property name are replaced with underscores (`_`) to provide a valid variable identifier. 
If the additional arguments field contains the `-var-file` argument too, the logic will be skipped.
