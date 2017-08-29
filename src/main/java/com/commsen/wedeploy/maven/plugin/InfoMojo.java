package com.commsen.wedeploy.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commsen.wedeploy.client.WeDeployClient;
import com.commsen.wedeploy.client.WeDeployClientException;
import com.commsen.wedeploy.client.cloud.WeDeployStatusDTO;
import com.commsen.wedeploy.client.cloud.WeDeployStatusService;

/**
 * Displays the status of WeDeploy's online services. 
 * 
 * @author milen
 *
 */
@Mojo(name = "info", defaultPhase = LifecyclePhase.NONE, requiresProject=false)
public class InfoMojo extends AbstractMojo {

	private Logger logger = LoggerFactory.getLogger(InfoMojo.class);
	
	private WeDeployStatusService weDeployStatus;
	
	public InfoMojo() throws WeDeployClientException {
		weDeployStatus = new WeDeployClient().status();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			logger.info("Checking WeDeploy status:");
			WeDeployStatusDTO status = weDeployStatus.get(true);
			logger.info("Auth service: " + status.auth);
			logger.info("Data service: " + status.data);
			logger.info("Email service: " + status.email);
		} catch (WeDeployClientException e) {
			throw new MojoExecutionException("Failed to check WeDeploy status", e);
		}
	}
}
