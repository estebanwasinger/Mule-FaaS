package org.mule.faas.mulefaas;

import org.example.ExecutionStatus;
import org.example.Main;
import org.example.xmlparser.MuleAppParser;
import org.mule.faas.mulefaas.routing.GatewayRoutesRefresher;
import org.mule.faas.mulefaas.routing.RefreshableRoutesLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApplicationManager {

    @Autowired
    private RefreshableRoutesLocator locator;

    @Autowired
    protected RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    protected RouteLocator routeLocator;

    @Autowired
    private GatewayRoutesRefresher gatewayRoutesRefresher;

    @Autowired
    private RouteDefinitionRepository repository;

    public Map<String, App> apps = new HashMap<>();
    public Map<String, Process> runningApps = new HashMap<>();

    @PostMapping("/apps")
    public AppCreationResponse createApp(@RequestBody AppCreationRequest request) throws ParserConfigurationException, IOException, SAXException {
        MuleAppParser parser = new MuleAppParser();
        String muleApp = parser.generateMuleApp(new ByteArrayInputStream(request.getApp().getBytes()), "0.0.0.0", "${http.port}");
        apps.put(request.getName(), new App(request.getName(), muleApp));
        return new AppCreationResponse(true);
    }

    @GetMapping("/apps")
    public List<App> appsList() {
//        CachingRouteLocator bean = (CachingRouteLocator) applicationContext.getBean(RouteLocator.class);
//        applicationContext.bean
        return new ArrayList<>(apps.values());
    }

    @GetMapping("/apps/{appName}/start")
    public AppStartResponse startApp(@PathVariable String appName) throws InterruptedException, IOException, URISyntaxException {
        App app = apps.get(appName);
        String appCode;

        int localPort = 0;

        if (app == null) {
            return new AppStartResponse(false, String.format("App [%s] doesn't exist", appName));
        } else {

            if (!runningApps.containsKey(appName)) {
                ServerSocket s = new ServerSocket(0);
                s.close();
                localPort = s.getLocalPort();

                String replacement = String.valueOf(localPort);
                appCode = app.getApp().replace("${http.port}", replacement);

                ExecutionStatus executionStatus = Main.startDwCode(appCode);
                this.locator.addRoute(appName, "/" + appName, URI.create("http://0.0.0.0:" + replacement ));
                this.locator.buildRoutes();
                if (executionStatus.getException() == null) {
                    runningApps.put(appName, executionStatus.getProcess());
                } else {
                    executionStatus.getException().printStackTrace();
                    return new AppStartResponse(false, String.format("App [%s] failed to start", appName));
                }
            } else {
                return new AppStartResponse(false, String.format("App [%s] is already running", appName));
            }
        }

        String url = String.format("http://0.0.0.0:8080/%s", appName);
        app.setUrl(url);
        app.setRunning(true);
        return new AppStartResponse(true, new AppStatus(appName, url), "ok");
    }

    @GetMapping("/apps/{appName}/stop")
    public boolean stopApp(@PathVariable String appName) {
        Process process = runningApps.get(appName);
        process.destroy();
        runningApps.remove(appName);
        App app = apps.get(appName);
        app.setRunning(false);
        app.setUrl(null);
        removeRoute(appName);
        return true;
    }

    private void removeRoute(String appName) {
        locator.deleteRoute(appName);
    }

    public ServerSocket create(int[] ports) throws IOException {
        for (int port : ports) {
            try {
                return new ServerSocket(port);
            } catch (IOException ex) {
                continue; // try next port
            }
        }

        // if the program gets here, no port in the range was found
        throw new IOException("no free port found");
    }
}
