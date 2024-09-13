Red Hat Trusted Application Pipelines is a set of Red Hat products able to build secured pipelines by signing and verify your build artifacts.

### Advanced Cluster Security (ACS) 

Back to the pipeline itself and let’s look at the 3 Advanced Cluster Security tasks leveraging roxctl

The bottom-most task is performing CVE scanning via roxctl image scan.

The middle task is performing a policy check via roxctl image check. These policies might be things like 'No log4j allowed' or perhaps no curl, wget nor a package manager in a production image.

The top-most task is perform yaml scanning via roxctl deployment check and it might identify that your Deployment.yaml has not properly configured its resource request and limit.

A series of dashboards visualize the reporting from these 3 roxctl tasks, click on the View Output icon under ACTIONS

![acs-image-scan](images/rhdh-acs-1.png)
![acs-image-check](images/rhdh-acs-2.png)
![acs-deployment-check](images/rhdh-acs-3.png)

These 3 aspects of roxctl are a great example of the "shift-left" idea by bringing these critical checks into the pipeline itself to be executed on each `git push`.


### scan-export-sbom
Pushes the SBOM to a CycloneDX repository. Currently, the SBOM is stored in Quay and is not in an easily accessible or readable format. Pushing the SBOM to an additional repository allows us to access the raw JSON/XML file for further use outside of the CI process.

The SBOM is an artifact that deserves special mention. Think of the Software Bill of Materials as the ingredient list. I am sure you have gone to the grocery store and picked up an item and reviewed its packaging. On the back of the product there will be a list of ingredients. You can find out that something you might wish to eat includes gluten, perhaps too much sugar, or shellfish (my sister is allergic to shellfish) or perhaps nuts. Well, if you are allergic to nuts you might also be allergic to struts :-) You might remember that infamous Struts 2 vulnerability that allowed 143 million American’s private data to end up on the dark web Struts 2 hack.

The SBOM is either a JSON or XML file and is stored in the container registry as the ingredient list associated with the container image.

