## Usage

In order to use this plugin to [deploy your application](#deploy), you need to make sure

 - [relevant WeDeploy files are in proper place](#prepare)
 - [your WeDeply credentials are accessible](#credentials)
 - [WeDeply Maven Plugin is configured in your POM](#plugin)


### <a name="prepare" /> Prepare your WeDeploy project files

Following Maven's standard directory structure, this plugin by default uses  `${project.basedir}/src/wedeploy` but you are free to configure this via [sourceDirectory](/deploy-mojo.html#sourceDirectory) property! The whole tree under this directory will be deployed. 

The directory should contain at least 2 files:

#### `wedeploy.json`

This is what tells WeDeploy the service name (and parameters) that you want to create. It may look like this for example:

	{
	  "id": "MyGreatJavaApp",
	  "memory": 2048,
	  "port": 8080
	}
	
See [WeDeploy's documentation](https://wedeploy.com/docs/deploy/getting-started/) for more details!	

#### `Dockerfile`

This is what tells WeDeploy how to build the container for you. It may look like this for example:

	FROM frolvlad/alpine-oraclejdk8
	COPY MyGreatJavaApp.jar /app/MyGreatJavaApp.jar
	WORKDIR /app
	ENTRYPOINT ["java","-jar","/app/MyGreatJavaApp.jar"]

See [Dockerfile format](https://docs.docker.com/engine/reference/builder/#format) for more details!	


### <a name="credentials" /> Provide your WeDeploy credentials

Open `<MAVEN_HOME>/settings.xml` file and create a server entry for your WeDeploy account. It should look like this:

	<servers>
		...
		<server>
			<id>wedeploy</id>
			<username>user@domain.com</username>
			<password>....</password>
		</server>
	</servers>
 
You can provide your password in plain text (not recommended) or [encrypt it first](https://maven.apache.org/guides/mini/guide-encryption.html)!

By default the plugin will try to find credentials in server with id `wedeploy`. If you choose to use another id, you'll also need to set [serverId](/deploy-mojo.html#serverId) property!

### <a name="plugin" /> Configure WeDeploy Maven Plugin

Here is an example configuration that will try to deploy `${project.build.directory}/${project.artifactId}.jar` to `my_wedeploy_project` :

	<build>
		<plugins>
			...
			<plugin>
				<groupId>com.commsen.wedeploy</groupId>
				<artifactId>wedeploy-maven-plugin</artifactId>
				<version>0.1.0</version>
				<configuration>
					<project>my_wedeploy_project</project>
					<includes>
						<include>${project.build.directory}/${project.artifactId}.jar</include>
					</includes>
				</configuration>
			</plugin>
		</plugins>
	</build>

__Please note__, future versions of this plugin may allow to automatically create projects but for now __`MyWeDeployProject` must exists__ in the user's account. You can create it using [online console](https://console.wedeploy.com). 

### <a name="deploy" /> Deploy your project

Deploying to production is something you better be 100% sure about! Therefore by default this plugin will not do it automatically for you. If that's what you want, configure the plugin to execute at the maven stage that makes sense for you deployment process!  

To manually deploy your project run `mvn we:deploy`

