## Conclusion and References

This is the end of the Super Hero workshop. We hope you liked it, learnt a few things, and more importantly, will be able to take this knowledge back to your projects.

This workshop started making sure your development environment was ready to develop the entire application. 
If you find it was too short and need more details on Quarkus, Microservices, MicroProfile, Cloud Native, or GraalVM, check the [Quarkus website](https://quarkus.io) for more references.

You first installed an already coded React application in an instance of Quarkus.
At this stage, the React application couldn’t access the microservices because they were not deployed.

Then, we focused on developing several isolated microservices. 
Some written in pure JAX-RS (such as the Hero) others with Quarkus Spring compatibility layer (such as the Hero). 
These microservices return data in JSON, validate data thanks to Bean Validation, store and retrieve data from a relational database with the help of JPA, Panache and JTA.

Then, we made the microservices communicate with each other in HTTP thanks to REST Client.

We’ve also added some Artificial Intelligence. Thanks to Semantic Kernel, with a few lines of code, we allowed our Narration microservice to narrate the fight between a Super Hero and a Super Villain.

Finally, we added the Micrometer metrics library to collect runtime, extension and application metrics and expose them as a Prometheus (OpenMetrics) endpoint.

Then, came deployment time. With Argo CD it’s very easy to deploy by just pushing the code to the GitLab repository. 
From that, we used Red Hat Trusted Pipelines to detect a CVE and fix it.

Remember that you can find all the code for this fascicle at https://github.com/cescoffier/quarkus-openshift-workshop. 
