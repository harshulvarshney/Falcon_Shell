# Falcon_Shell


## Problem
When you have multiple projects on GitLab, to track which pipeline is currently running you have to open GitLab UI in browser and navigate to each project; by opening multiple tabs. This is not only a time consuming task for developers but also eats up your system resources.


## Solution
A CLI (or Shell)

With a shell available:
- we can now fetch pipeline info of multiple projects in one request
- it is faster as compared to accessing all projects in browser
- easy to monitor pipelines of multiple projects


## Implementation:
This Solution is implemented by using only open source solutions:
- gitlab4j-api for accessing GitLab
- spring-shell-starter for CLI commands and shell &
- spring-boot-starter


### With this CLI we can:
- fetch latest pipelines of all configured projects
- fetch latest pipeline of a single project
- If response is single pipeline, it will be in tabular format, which gives better user experiance
