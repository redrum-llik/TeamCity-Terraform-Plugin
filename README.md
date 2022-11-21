# Terraform plugin for TeamCity

This project aims to provide a simple Terraform-related build feature for TeamCity. The key features are:
* parse JSON output of `terraform show`
* provide a report tab summarizing the planned changes
* update build status
* raise build problem if certain resource types are planned for removal or replacement

Example of the report tab:
![image](https://user-images.githubusercontent.com/63649969/133670782-d2b3c061-94d2-4600-afae-f91f6cbdf24e.png)

The plugin targets a scenario where `terraform` is executed in a build chain; dependency build provides a plan file, and dependent build applies it. The build feature provided by plugin allows to control the changes introduced in plan file, review them easier and fail the plan build if necessary.
Synergises with [Manual Approval](https://www.jetbrains.com/help/teamcity/build-approval.html) feature introduced in 2022.04. 

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

**Plan changes file**: relative path to the JSON file containing `terraform show` output.

**Update build status**: update the build status of the build with the output results (reports whether there are any planned changes, there are none, or if some of the resources will be removed/replaced).

**Protected resource types**: specify Java regular expression to match against resource types. Matching resource types are treated as protected, and if plugin detects that resource is planned for removal or replacement, it will raise a build problem.

Examples of usage:

* `my_db_type`: will match exactly this type of resource
* `my_db_type|my_other_db_type`: will match both resources
* `^((?!my_throwaway_type_of_resource).)*$`: will match anything except this resource type (whitelist)
