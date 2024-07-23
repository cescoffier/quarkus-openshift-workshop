# Software Requirements

First of all, make sure you have Web browser installed on your laptop
and internet connectivity.

Your environment is remote and can be accessed via DevSpaces through your local browser, you just need to sign up and configure
some elements.
Your environment includes also Red Hat’s OpenShift Container Platform (OCP).

The next section focuses on how to install and setup the needed
software.

# Red Hat OpenShift Dev Spaces

Red Hat OpenShift Dev Spaces  Workspaces is a collaborative Kubernetes-native development
solution that delivers OpenShift workspaces and in-browser IDE for rapid
cloud application development. 

## Dev Spaces creation

[//]: # (-   Go to the Etherpad site and choose an user. This user will be used)

[//]: # (    to access the CRW and the OpenShift Web Console and for naming some)

[//]: # (    components that you are going to create during the workshop.)

[//]: # ()
[//]: # (-   Launch the CRW creation by clicking the link mentioned in the)

[//]: # (    Etherpad site.)

[//]: # ()
[//]: # (-   Once the CRW creation done, access to your CRW and sign up with your)

[//]: # (    own user &#40;selected previously in Etherpad&#41; and full fill the form:)

[//]: # ()
[//]: # (        user: USERNAME)

[//]: # (        pwd: openshift)

[//]: # (        email: USERNAME@ocp.com)

[//]: # (        first name: Yago)

[//]: # (        last name: Sanchez)

If everything goes well, you should have a Red Hat OpenShift Dev Spaces  Workspace with a
`quarkus-workshop` folder ready to start to code:

<figure>
<img src="devspaces.png" alt="devspaces.png" />
</figure>

-   Finally, open a terminal from the Terminal menu → Open Terminal in
    specific container → maven.

## Command Line Utilities

Just make sure the following commands work on your CRW terminal

    $ java -version
    $ mvn -version
    $ curl --version


