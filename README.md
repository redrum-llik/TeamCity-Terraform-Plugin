# Terraform plugin for TeamCity

This project aims to provide a simple Terraform-related build feature for TeamCity. The key features are:
* parse JSON output of `terraform show` to:
  * provide a report tab summarizing the planned changes
  * update build status
  * raise build problem if certain resource types are planned for removal or replacement
* ability to pass configuration parameters via `-var-file`

Example of the report tab:

![image](https://user-images.githubusercontent.com/63649969/132099968-5cef8b1b-9de9-4ea0-93d7-ed88ee00991e.png)

The usage scenario is to build a plan file as a first build, allow to check it manually easier and, if the changes look good, apply them in the second build. There is a planned feature to allow to request approval from a sufficiently permissioned user before starting `apply` build. 

# How to use

* Run 

```
terraform plan -out <planfile>
terraform show -json <planfile> >> <output file>
```

if you plan to use the produced plan file later (e.g. to run `terraform apply`), or 

```
terraform show -json >> <output file>
```

during the build.

* Specify the path to output file produced by `terraform show` in the build feature. 

# Configuration

## Report Parameters

**Plan changes file**: relative path to the JSON file containing `terraform show` output.

**Update build status**: update the build status of the build with the output results (reports whether there are any planned changes, there are none, or if some of the resources will be removed/replaced).

**Protected resource types**: specify Java regular expression to match against resource types. Matching resource types are treated as protected, and if plugin detects that resource is planned for removal or replacement, it will raise a build problem.

Examples of usage:

* `my_db_type`: will match exactly this type of resource
* `my_db_type|my_other_db_type`: will match both resources
* `^((?!my_throwaway_type_of_resource).)*$`: will match anything except this resource type (whitelist)

## General Parameters

**Pass system properties**: save system properties to specified path which may be used with `-var-file` argument

# Implementation details

## System properties

If a corresponding option is enabled, system properties will be exported into a temporary JSON file. This file will be supplied as the `-var-file` value. The dots (`.`) in property name are replaced with underscores (`_`) to provide a valid variable identifier. 
