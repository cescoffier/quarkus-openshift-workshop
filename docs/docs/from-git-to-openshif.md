## Deploying to OpenShift cluster.

This approach will be followed for all the microservices.
With ArgoCD, deploying to the OpenShift cluster is as simple as committing and pushing your changes to the GitLab repository. 
To do this, follow these steps:

* In Dev Spaces, go to the source control view.
* Enter your commit message
* Click the Commit button.

![ds-source-control-view.png](images/ds-source-control-view.png)

* After committing, click the Sync Changes button to push your changes.

![git-push-button.png](images%2Fgit-push-button.png)

This action will trigger a pipeline, which you can monitor in the CI tab of the RHDH.
Once the pipeline completes successfully, you will see the corresponding pod in the Topology tab. 
From there, you can access your hero microservice by clicking on the pod.

## Exploring the pipeline

Let's switch to the Developer Hub console, in your hero component.

==Click the CI tab==

Once you commited your code, an OpenShift pipeline has been automaticaly triggered.
This pipeline was created automaticaly for your project when you instanciated the template.

![CI of the hero-service](images/rhdh-cicd.png)

As you can see, a lot of steps are now running so let's explore a little.

The first step in the pipeline is a simple git clone. The next step is basically a mvn package and then running of Sonarqube scan-source for static analysis. All pretty standard for CI pipelines. Get the code, compile/build the code, run some scans.

### build-sign-image
It is the build-sign-image where things get super interesting.

![build-sign-image](images/rhdh-build-sign-image.png)

The template is leveraging Tekton Chains, a Kubernetes Custom Resource Definition (CRD) controller, that nicely augments the supply chain security within OpenShift Pipelines. This tool’s capacity to automatically sign task runs, and its adoption of advanced attestation formats like in-toto and SLA provenance, bring a higher degree of trust and verification to our processes. Tekton Chains works like an independent observer within the cluster, it signs, attests and stores additional artifacts as OCI images alongside with your container image.

Also, you can see we are using this step to automatically create a SBOM (Software Bill Of Materials) which is a kind of ingredients list for your software.


### Dev Deployment
As a developer, I want to see my handy work, did my application really deploy to the dev environment ?

You can check that your newly built application has been automatically sync using OpenShift GitOps (ArgoCD) if you switch to the Overview Tab.

![rhdh-overview-dev-ready](images/rhdp-overview-dev-ready.png)

==Click on Topology==

This should look familiar to you if you regularly use the topology view from OpenShift.
![rhdh-topology-dev](images/rhdh-topology-dev.png)


Note: pre-prod and prod have NOT yet deployed

==Click the arrow==

This will open a new tab on your browser showing your deployed hero-service !

==You can add `/api/heroes/hello` , `/api/heroes/random` or `/api/heroes` to the URL in you browser to check your work==

Note that during the deployment with OpenShift GitOps, we also deployed automatically the Database containing all the superheroes using the EDB Cluster CRD that automaticaly manage our database.

==Verify that the fight-ui dev environment is now able to retrieve your hero==

