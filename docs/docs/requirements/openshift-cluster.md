# OpenShift Container Platform

Your lab environment includes Red Hat’s OpenShift Container Platform (OCP).

Access to your OCP resources can be gained via both the `oc` CLI utility and the OCP web console.

## Overview

The project we are going to develop will contain 3 microservices accessing to a PostgreSQL database. 
We will make the databases to run in a OpenShift dedicated namespace.

## OpenShift Namespaces

1.  In the terminal of your Red Hat OpenShift Dev Spaces , authenticate into OpenShift as a non
    cluster admin user (USERNAME) using the `oc` utility.

You can get the command for authenticating from the OpenShift Web
Console.

    $ oc login

There are 2 namespaces (OpenShift projects) in your OpenShift cluster:
The namespace for hosting your Red Hat OpenShift Dev Spaces environment is USERNAME-devspaces
where `USERNAME` correspond to your specific username. The namespace for
hosting databases and microservices is USERNAME-heroes.

change the USERNAME with your own.

## Operators

Your lab environment comes pre-installed with an OpenShift operator.

**PostgreSQL operator**

The PostgreSQL operator allows to package, install, configure and manage
a PostgreSQL database within an OpenShift cluster.

Congratulations! Your lab environment is now ready to use.