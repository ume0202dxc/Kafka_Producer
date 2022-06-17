package com.kafka.security.azure;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;

public class ServicePrincipalAuthCallback implements AuthenticateCallbackHandler {

	final static ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

	// Tenant ID
	/*
	 * private String authority =
	 * "https://login.microsoftonline.com/49793faf-eb3f-4d99-a0cf-aef7cce79dc1";
	 * 
	 * 
	 * private String appId = System.getenv("servicePrincipalId"); private String
	 * appSecret = System.getenv("servicePrincipalSecret");
	 */
	
	private String appId = "37e4d720-9486-43d1-b262-f8f44ed3ac2e";
	private String appSecret = "dSBa_zzihQdx5F3x2T_6696~ovzP.uXWZQ";
	private String authority = "https://login.microsoftonline.com/49793faf-eb3f-4d99-a0cf-aef7cce79dc1";

	private ConfidentialClientApplication aadClient;

	private ClientCredentialParameters aadParameters;

	@Override

	public void configure(Map<String, ?> configs, String mechanism, List<AppConfigurationEntry> jaasConfigEntries) {

		String bootstrapServer = Arrays.asList(configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)).get(0).toString();
		//System.err.println("KAKFKA BOOTSTAP SERVER: " + bootstrapServer);
		//System.err.println("AppID: "+appId+", appSecret: "+appSecret);
		bootstrapServer = bootstrapServer.replaceAll("\\[|\\]", "");
		URI uri = URI.create("https://" + bootstrapServer);
		String sbUri = uri.getScheme() + "://" + uri.getHost();
		this.aadParameters =ClientCredentialParameters.builder(Collections.singleton(sbUri + "/.default")).build();
	}

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof OAuthBearerTokenCallback) {
				try {
					OAuthBearerToken token = getOAuthBearerToken();
					OAuthBearerTokenCallback oauthCallback = (OAuthBearerTokenCallback) callback;
					oauthCallback.token(token);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					e.printStackTrace();
				}

			} else {
				throw new UnsupportedCallbackException(callback);
			}
		}
	}

	OAuthBearerToken getOAuthBearerToken()
			throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {

		if (this.aadClient == null) {
			synchronized (this) {
				if (this.aadClient == null) {
					IClientCredential credential = ClientCredentialFactory.createFromSecret(this.appSecret);
					this.aadClient = ConfidentialClientApplication.builder(this.appId, credential)
							.authority(this.authority)
							.build();
				}
			}
		}

		IAuthenticationResult authResult = this.aadClient.acquireToken(this.aadParameters).get();
		System.out.println("TOKEN ACQUIRED");
		return new OAuthBearerTokenImp(authResult.accessToken(), authResult.expiresOnDate());

	}

	public void close() throws KafkaException {
	}

}