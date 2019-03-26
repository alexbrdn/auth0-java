package com.auth0.client.mgmt;

import com.auth0.client.MockServer;
import com.auth0.net.TelemetryInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;

import static com.auth0.client.UrlMatcher.isUrl;
import static okhttp3.logging.HttpLoggingInterceptor.Level;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ManagementAPITest {

    private static final String DOMAIN = "domain.auth0.com";
    private static final String API_TOKEN = "apiToken";

    private MockServer server;
    private ManagementAPI api;
    private PasswordAuthentication passwordAuthentication;
    private InetSocketAddress socketAddress;
    private Proxy proxy;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        server = new MockServer();
        api = new ManagementAPI(server.getBaseUrl(), API_TOKEN);
        passwordAuthentication = new PasswordAuthentication("test", "pass".toCharArray());
        socketAddress = new InetSocketAddress(0);
        proxy = new Proxy(Proxy.Type.HTTP, socketAddress);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    // Configuration

    @Test
    public void shouldAcceptDomainWithNoScheme() throws Exception {
        ManagementAPI api = new ManagementAPI("me.something.com", API_TOKEN);

        assertThat(api.getBaseUrl(), is(notNullValue()));
        assertThat(api.getBaseUrl().toString(), isUrl("https", "me.something.com"));
    }

    @Test
    public void shouldAcceptDomainWithHttpScheme() throws Exception {
        ManagementAPI api = new ManagementAPI("http://me.something.com", API_TOKEN);

        assertThat(api.getBaseUrl(), is(notNullValue()));
        assertThat(api.getBaseUrl().toString(), isUrl("http", "me.something.com"));
    }

    @Test
    public void shouldThrowWhenDomainIsInvalid() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The domain had an invalid format and couldn't be parsed as an URL.");
        new ManagementAPI("", API_TOKEN);
    }

    @Test
    public void shouldThrowWhenDomainIsNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'domain' cannot be null!");
        new ManagementAPI(null, API_TOKEN);
    }

    @Test
    public void shouldThrowWhenApiTokenIsNull() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'api token' cannot be null!");
        new ManagementAPI(DOMAIN, null);
    }

    @Test
    public void shouldThrowOnUpdateWhenApiTokenIsNull() throws Exception {
        ManagementAPI api = new ManagementAPI(DOMAIN, API_TOKEN);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'api token' cannot be null!");
        api.setApiToken(null);
    }

    @Test
    public void shouldUpdateApiToken() throws Exception {
        //Initialize with a token
        ManagementAPI api = new ManagementAPI(DOMAIN, "first token");

        assertThat(api.blacklists().apiToken, is("first token"));
        assertThat(api.clientGrants().apiToken, is("first token"));
        assertThat(api.clients().apiToken, is("first token"));
        assertThat(api.connections().apiToken, is("first token"));
        assertThat(api.deviceCredentials().apiToken, is("first token"));
        assertThat(api.emailProvider().apiToken, is("first token"));
        assertThat(api.emailTemplates().apiToken, is("first token"));
        assertThat(api.grants().apiToken, is("first token"));
        assertThat(api.guardian().apiToken, is("first token"));
        assertThat(api.jobs().apiToken, is("first token"));
        assertThat(api.logEvents().apiToken, is("first token"));
        assertThat(api.resourceServers().apiToken, is("first token"));
        assertThat(api.rules().apiToken, is("first token"));
        assertThat(api.stats().apiToken, is("first token"));
        assertThat(api.tenants().apiToken, is("first token"));
        assertThat(api.tickets().apiToken, is("first token"));
        assertThat(api.userBlocks().apiToken, is("first token"));
        assertThat(api.users().apiToken, is("first token"));

        //Update the token
        api.setApiToken("new token");

        assertThat(api.blacklists().apiToken, is("new token"));
        assertThat(api.clientGrants().apiToken, is("new token"));
        assertThat(api.clients().apiToken, is("new token"));
        assertThat(api.connections().apiToken, is("new token"));
        assertThat(api.deviceCredentials().apiToken, is("new token"));
        assertThat(api.emailProvider().apiToken, is("new token"));
        assertThat(api.emailTemplates().apiToken, is("new token"));
        assertThat(api.grants().apiToken, is("new token"));
        assertThat(api.guardian().apiToken, is("new token"));
        assertThat(api.jobs().apiToken, is("new token"));
        assertThat(api.logEvents().apiToken, is("new token"));
        assertThat(api.resourceServers().apiToken, is("new token"));
        assertThat(api.rules().apiToken, is("new token"));
        assertThat(api.stats().apiToken, is("new token"));
        assertThat(api.tenants().apiToken, is("new token"));
        assertThat(api.tickets().apiToken, is("new token"));
        assertThat(api.userBlocks().apiToken, is("new token"));
        assertThat(api.users().apiToken, is("new token"));
    }

    @Test
    public void shouldAddAndEnableTelemetryInterceptor() throws Exception {
        ManagementAPI api = new ManagementAPI(DOMAIN, API_TOKEN);
        assertThat(api.getClient().interceptors(), hasItem(isA(TelemetryInterceptor.class)));

        for (Interceptor i : api.getClient().interceptors()) {
            if (i instanceof TelemetryInterceptor) {
                TelemetryInterceptor telemetry = (TelemetryInterceptor) i;
                assertThat(telemetry.isEnabled(), is(true));
            }
        }
    }

    @Test
    public void shouldDisableTelemetryInterceptor() throws Exception {
        ManagementAPI api = new ManagementAPI(DOMAIN, API_TOKEN);
        assertThat(api.getClient().interceptors(), hasItem(isA(TelemetryInterceptor.class)));
        api.doNotSendTelemetry();

        for (Interceptor i : api.getClient().interceptors()) {
            if (i instanceof TelemetryInterceptor) {
                TelemetryInterceptor telemetry = (TelemetryInterceptor) i;
                assertThat(telemetry.isEnabled(), is(false));
            }
        }
    }

    @Test
    public void shouldAddAndDisableLoggingInterceptor() throws Exception {
        ManagementAPI api = new ManagementAPI(DOMAIN, API_TOKEN);
        assertThat(api.getClient().interceptors(), hasItem(isA(HttpLoggingInterceptor.class)));

        for (Interceptor i : api.getClient().interceptors()) {
            if (i instanceof HttpLoggingInterceptor) {
                HttpLoggingInterceptor logging = (HttpLoggingInterceptor) i;
                assertThat(logging.getLevel(), is(Level.NONE));
            }
        }
    }

    @Test
    public void shouldEnableLoggingInterceptor() throws Exception {
        ManagementAPI api = new ManagementAPI(DOMAIN, API_TOKEN);
        assertThat(api.getClient().interceptors(), hasItem(isA(HttpLoggingInterceptor.class)));
        api.setLoggingEnabled(true);

        for (Interceptor i : api.getClient().interceptors()) {
            if (i instanceof HttpLoggingInterceptor) {
                HttpLoggingInterceptor logging = (HttpLoggingInterceptor) i;
                assertThat(logging.getLevel(), is(Level.BODY));
            }
        }
    }

    @Test
    public void shouldDisableLoggingInterceptor() throws Exception {
        ManagementAPI api = new ManagementAPI(DOMAIN, API_TOKEN);
        assertThat(api.getClient().interceptors(), hasItem(isA(HttpLoggingInterceptor.class)));
        api.setLoggingEnabled(false);

        for (Interceptor i : api.getClient().interceptors()) {
            if (i instanceof HttpLoggingInterceptor) {
                HttpLoggingInterceptor logging = (HttpLoggingInterceptor) i;
                assertThat(logging.getLevel(), is(Level.NONE));
            }
        }
    }

    //Entities

    @Test
    public void shouldGetBlacklists() throws Exception {
        assertThat(api.blacklists(), notNullValue());
    }

    @Test
    public void shouldGetClientGrants() throws Exception {
        assertThat(api.clientGrants(), notNullValue());
    }

    @Test
    public void shouldGetClients() throws Exception {
        assertThat(api.clients(), notNullValue());
    }

    @Test
    public void shouldGetConnections() throws Exception {
        assertThat(api.connections(), notNullValue());
    }

    @Test
    public void shouldGetDeviceCredentials() throws Exception {
        assertThat(api.deviceCredentials(), notNullValue());
    }

    @Test
    public void shouldGetEmailProvider() throws Exception {
        assertThat(api.emailProvider(), notNullValue());
    }

    @Test
    public void shouldGetEmailTemplates() throws Exception {
        assertThat(api.emailTemplates(), notNullValue());
    }

    @Test
    public void shouldGetGrants() throws Exception {
        assertThat(api.grants(), notNullValue());
    }

    @Test
    public void shouldGetGuardian() throws Exception {
        assertThat(api.guardian(), notNullValue());
    }

    @Test
    public void shouldGetJobs() throws Exception {
        assertThat(api.jobs(), notNullValue());
    }

    @Test
    public void shouldGetLogEvents() throws Exception {
        assertThat(api.logEvents(), notNullValue());
    }

    @Test
    public void shouldGetResourceServers() throws Exception {
        assertThat(api.resourceServers(), notNullValue());
    }

    @Test
    public void shouldGetRules() throws Exception {
        assertThat(api.rules(), notNullValue());
    }

    @Test
    public void shouldGetStats() throws Exception {
        assertThat(api.stats(), notNullValue());
    }

    @Test
    public void shouldGetTenants() throws Exception {
        assertThat(api.tenants(), notNullValue());
    }

    @Test
    public void shouldGetTickets() throws Exception {
        assertThat(api.tickets(), notNullValue());
    }

    @Test
    public void shouldGetUserBlocks() throws Exception {
        assertThat(api.userBlocks(), notNullValue());
    }

    @Test
    public void shouldGetUsers() throws Exception {
        assertThat(api.users(), notNullValue());
    }

    @Test
    public void testConstructOkHttpClientWithProxyNoAuth() {
        OkHttpClient okHttpClient = api.constructOkHttpClient(proxy, null);
        assertThat(okHttpClient.authenticator(), notNullValue());
        assertThat(okHttpClient.proxy(), notNullValue());
        assertThat(okHttpClient.proxy().address(), equalTo(socketAddress));
        assertThat(okHttpClient, notNullValue());
    }

    @Test
    public void testConstructOkHttpClientWithProxyWithAuth() {
        OkHttpClient okHttpClient = api.constructOkHttpClient(proxy, passwordAuthentication);
        assertThat(okHttpClient, notNullValue());
        assertThat(okHttpClient.authenticator(), notNullValue());
        assertThat(okHttpClient.proxy(), notNullValue());
        assertThat(okHttpClient.proxy().address(), equalTo(socketAddress));
    }

    @Test
    public void testProxyAuthenticator() {
        okhttp3.Authenticator authenticator = api.constructAuthenticator(passwordAuthentication);
        assertThat(authenticator, notNullValue());
    }

}