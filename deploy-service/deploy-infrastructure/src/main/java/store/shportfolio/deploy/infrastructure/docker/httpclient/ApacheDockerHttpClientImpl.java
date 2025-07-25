package store.shportfolio.deploy.infrastructure.docker.httpclient;

import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.NamedPipeSocket;
import com.github.dockerjava.transport.SSLConfig;
import com.github.dockerjava.transport.UnixSocket;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.ContentLengthStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.DefaultContentLengthStrategy;
import org.apache.hc.core5.http.impl.io.EmptyInputStream;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ApacheDockerHttpClientImpl implements DockerHttpClient {

    private final CloseableHttpClient httpClient;
    private final HttpHost host;
    private final String pathPrefix;

    protected ApacheDockerHttpClientImpl(
            URI dockerHost,
            SSLConfig sslConfig,
            int maxConnections,
            Duration connectionTimeout,
            Duration responseTimeout
    ) {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = createConnectionSocketFactoryRegistry(sslConfig, dockerHost);

        switch (dockerHost.getScheme()) {
            case "unix":
            case "npipe":
                pathPrefix = "";
                host = new HttpHost(dockerHost.getScheme(), "localhost", 2375);
                break;
            case "tcp":
                String rawPath = dockerHost.getRawPath();
                pathPrefix = rawPath.endsWith("/")
                        ? rawPath.substring(0, rawPath.length() - 1)
                        : rawPath;
                host = new HttpHost(
                        socketFactoryRegistry.lookup("https") != null ? "https" : "http",
                        dockerHost.getHost(),
                        dockerHost.getPort()
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported protocol scheme: " + dockerHost);
        }

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry,
                new ManagedHttpClientConnectionFactory(
                        null,
                        null,
                        null,
                        null,
                        message -> {
                            Header transferEncodingHeader = message.getFirstHeader(HttpHeaders.TRANSFER_ENCODING);
                            if (transferEncodingHeader != null) {
                                if ("identity".equalsIgnoreCase(transferEncodingHeader.getValue())) {
                                    return ContentLengthStrategy.UNDEFINED;
                                }
                            }
                            return DefaultContentLengthStrategy.INSTANCE.determineLength(message);
                        },
                        null
                )
        );
        // See https://github.com/docker-java/docker-java/pull/1590#issuecomment-870581289
        connectionManager.setDefaultSocketConfig(
                SocketConfig.copy(SocketConfig.DEFAULT)
                        .setSoTimeout(Timeout.ZERO_MILLISECONDS)
                        .build()
        );
        connectionManager.setValidateAfterInactivity(TimeValue.NEG_ONE_SECOND);
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnections);
        RequestConfig.Builder defaultRequest = RequestConfig.custom();
        if (connectionTimeout != null) {
            defaultRequest.setConnectTimeout(connectionTimeout.toNanos(), TimeUnit.NANOSECONDS);
        }
        if (responseTimeout != null) {
            defaultRequest.setResponseTimeout(responseTimeout.toNanos(), TimeUnit.NANOSECONDS);
        }

        httpClient = HttpClients.custom()
                .setRequestExecutor(new HijackingHttpRequestExecutor(null))
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequest.build())
                .disableConnectionState()
                .build();
    }

    private Registry<ConnectionSocketFactory> createConnectionSocketFactoryRegistry(
            SSLConfig sslConfig,
            URI dockerHost
    ) {
        RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistryBuilder = RegistryBuilder.create();

        if (sslConfig != null) {
            try {
                SSLContext sslContext = sslConfig.getSSLContext();
                if (sslContext != null) {
                    socketFactoryRegistryBuilder.register("https", new SSLConnectionSocketFactory(sslContext));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return socketFactoryRegistryBuilder
                .register("tcp", PlainConnectionSocketFactory.INSTANCE)
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("unix", new ConnectionSocketFactory() {
                    @Override
                    public Socket createSocket(HttpContext context) throws IOException {
                        return UnixSocket.get(dockerHost.getPath());
                    }

                    @Override
                    public Socket connectSocket(TimeValue timeValue, Socket socket, HttpHost httpHost, InetSocketAddress inetSocketAddress,
                                                InetSocketAddress inetSocketAddress1, HttpContext httpContext) throws IOException {
                        return PlainConnectionSocketFactory.INSTANCE.connectSocket(timeValue, socket, httpHost, inetSocketAddress,
                                inetSocketAddress1, httpContext);
                    }
                })
                .register("npipe", new ConnectionSocketFactory() {
                    @Override
                    public Socket createSocket(HttpContext context) {
                        return new NamedPipeSocket(dockerHost.getPath());
                    }

                    @Override
                    public Socket connectSocket(TimeValue timeValue, Socket socket, HttpHost httpHost, InetSocketAddress inetSocketAddress,
                                                InetSocketAddress inetSocketAddress1, HttpContext httpContext) throws IOException {
                        return PlainConnectionSocketFactory.INSTANCE.connectSocket(timeValue, socket, httpHost, inetSocketAddress,
                                inetSocketAddress1, httpContext);
                    }
                })
                .build();
    }

    @Override
    public Response execute(Request request) {
        HttpContext context = new BasicHttpContext();
        HttpUriRequestBase httpUriRequest = new HttpUriRequestBase(request.method(), URI.create(pathPrefix + request.path()));
        httpUriRequest.setScheme(host.getSchemeName());
        httpUriRequest.setAuthority(new URIAuthority(host.getHostName(), host.getPort()));

        request.headers().forEach(httpUriRequest::addHeader);

        byte[] bodyBytes = request.bodyBytes();
        if (bodyBytes != null) {
            httpUriRequest.setEntity(new ByteArrayEntity(bodyBytes, null));
        } else {
            InputStream body = request.body();
            if (body != null) {
                httpUriRequest.setEntity(new InputStreamEntity(body, null));
            }
        }

        if (request.hijackedInput() != null) {
            context.setAttribute(HijackingHttpRequestExecutor.HIJACKED_INPUT_ATTRIBUTE, request.hijackedInput());
            httpUriRequest.setHeader("Upgrade", "tcp");
            httpUriRequest.setHeader("Connection", "Upgrade");
        }

        try {
            CloseableHttpResponse response = httpClient.execute(host, httpUriRequest, context);

            return new ApacheResponse(httpUriRequest, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    static class ApacheResponse implements Response {

        private static final Logger LOGGER = LoggerFactory.getLogger(ApacheResponse.class);

        private final HttpUriRequestBase request;

        private final CloseableHttpResponse response;

        ApacheResponse(HttpUriRequestBase httpUriRequest, CloseableHttpResponse response) {
            this.request = httpUriRequest;
            this.response = response;
        }

        @Override
        public int getStatusCode() {
            return response.getCode();
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            return Stream.of(response.getHeaders()).collect(Collectors.groupingBy(
                    NameValuePair::getName,
                    Collectors.mapping(NameValuePair::getValue, Collectors.toList())
            ));
        }

        @Override
        public String getHeader(String name) {
            Header firstHeader = response.getFirstHeader(name);
            return firstHeader != null ? firstHeader.getValue() : null;
        }

        @Override
        public InputStream getBody() {
            try {
                return response.getEntity() != null
                        ? response.getEntity().getContent()
                        : EmptyInputStream.INSTANCE;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                request.abort();
            } catch (Exception e) {
                LOGGER.debug("Failed to abort the request", e);
            }

            try {
                response.close();
            } catch (ConnectionClosedException e) {
                LOGGER.trace("Failed to close the response", e);
            } catch (Exception e) {
                LOGGER.debug("Failed to close the response", e);
            }
        }
    }
}