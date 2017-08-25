package com.commsen.wedeploy.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INSTALL, requiresOnline = true, requiresProject = true, requiresDirectInvocation = true)
// @Execute (phase=LifecyclePhase.PACKAGE)
public class DeployMojo extends AbstractMojo {

	private Logger logger = LoggerFactory.getLogger(InfoMojo.class);

	/**
	 * Location of the directory containing files to deploy.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/wedeploy", property = "wedeploy.source.directory", required = true)
	private File sourceDirectory;

	/**
	 * Location of the directory containing files to deploy.
	 */
	@Parameter(defaultValue = "${project.build.directory}/wedeploy", property = "wedeploy.output.directory", required = true)
	private File targetDirectory;

	/**
	 * Name of the WeDeploy project.
	 */
	@Parameter(property = "wedeploy.project", required = true)
	private String project;

	/**
	 * WeDeploy user.
	 */
	@Parameter(property = "wedeploy.user", required = false)
	private String user;

	/**
	 * WeDeploy password.
	 */
	@Parameter(property = "wedeploy.password", required = false)
	private String password;

	/**
	 * WeDeploy password.
	 */
	@Parameter(property = "wedeploy.serverId", required = false)
	private String serverId;

	/**
	 * Additional (likely generated) files to be deployed.
	 */
	@Parameter(property = "wedeploy.add.files")
	private List<File> addFiles;

	/**
	 * Name of the WeDeploy project.
	 */
	@Parameter(defaultValue = "false", property = "wedeploy.dryrun", required = false)
	private boolean dryrun;

	@Parameter(defaultValue = "${settings}", readonly = true)
	private Settings settings;

    @Component( hint = "mng-4384" )
    private SecDispatcher securityDispatcher;
	
	
	public void execute() throws MojoExecutionException {

		logger.info("Checking WeDeploy credentials");
		
		String serverId = this.serverId;
		if (serverId == null || serverId.isEmpty()) {
			serverId = "wedeploy";
		}
		
		String user = this.user;
		Server server = settings.getServer(serverId);
		if (user == null && server != null) {
			user = server.getUsername();
		}

		String password;
		try {
			password = securityDispatcher.decrypt(this.password);
			if (password == null && server != null) {
				password = securityDispatcher.decrypt(server.getPassword());
			}
		} catch (SecDispatcherException e) {
			throw new MojoExecutionException("Failed to decrypt password!", e);
		}

		if (user == null || password == null) {
			throw new MojoExecutionException(
					"Missing WeDeploy credentials! You can provide them in one of the following ways: \n"
							+ " 1) use <user> and <password> in plugin's configuration \n"
							+ " 2) use <wedeploy.user> and <wedeploy.password> project properties \n"
							+ " 3) use <serverId> (default: wedeploy) in plugin's configuration and add server configuration in `settings.xml`");
		}

		logger.info("Preparing files to deploy");
		
		try {
			FileUtils.deleteDirectory(targetDirectory);
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);

			if (!addFiles.isEmpty()) {
				for (File file : addFiles) {
					FileUtils.copyFileToDirectory(file, targetDirectory);
				}
			}

			InitCommand init = Git.init().setDirectory(targetDirectory);
			Git git = init.call();
			git.add().addFilepattern(".").call();
			git.commit().setMessage("Auto commit by wedeploy-maven-plugin").call();

			RemoteAddCommand remoteAdd = git.remoteAdd();
			remoteAdd.setName("wedeploy");
			remoteAdd.setUri(new URIish("https://git.wedeploy.com/" + project + ".git"));
			remoteAdd.call();

			if (!dryrun) {
				logger.info("Deploying files to project {}", project);
				git.push()
					.setForce(true) //
					.setRemote("wedeploy") //
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password)) //
					.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))) //
					.call(); //
			} else {
				logger.warn("Dry run mode! No files are deployed! Temporary git repo generated in {} directory!", targetDirectory);
			}
		} catch (IOException | GitAPIException | URISyntaxException e) {
			throw new MojoExecutionException("Failed to deploy", e);
		}
	}
}
