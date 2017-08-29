
[<img style="float:right; margin:20px; width:200px;" src="images/wedeploy-logo.png" />](https://wedeploy.com)

This WeDeploy Maven Plugin is used to deploy Java applications built with Maven to [WeDeploy](https://wedeploy.com)! 

### What is WeDeploy?

[WeDeploy](https://wedeploy.com) provides a set of ready-to-use services that enables you to store data in the cloud, search and stream content in real time, authenticate users, send e-mails to your users and so much more!

It allows you to deploy many types of applications, services and containers via [web interface](https://wedeploy.com/docs/intro/using-the-console/), [command line](https://wedeploy.com/docs/intro/using-the-command-line/) or [straight from GitHub](https://wedeploy.com/docs/deploy/continuous-deployment/)!

### Why use WeDeploy Maven Plugin?

[WeDeploy](https://wedeploy.com) provides great deployment options OOTB for applications written in interpreted languages where you deploy the actual source code. It also allows you to use the same approach for compiled languages (like Java). You can deploy your source code as [Java service](https://wedeploy.com/docs/deploy/deploying-java/) and WeDeploy will detect your build system (Maven/Gradle) and then build and run the code for you.

This does not work in the following cases:

 - you don't want to (or are not allowed to) share your source code
 - your build depends on artifacts  that are not publicly available (private/company repositories)
 - your application needs to run inside specific runtime (servlet / EJB / portal / OSGi / ... )   
 - your application depends on other applications running next to it (cache, service registry client, scheduler, ...)

In the above cases you are better of creating and deploying [Docker](https://www.docker.com/) container with everything you application needs.

__WeDeploy Maven Plugin allows you to do this with asingle command:__ `mvn we:deploy`! 
 

### License

The project is released under [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
