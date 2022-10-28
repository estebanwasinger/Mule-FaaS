package org.mule.faas.mulefaas;

import org.example.ExecutionStatus;
import org.example.Main;
import org.mule.faas.mulefaas.routing.RefreshableRoutesLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
public class ApplicationLifeCycleManager {

    private final RefreshableRoutesLocator locator;
    private Map<String, App> apps = new HashMap<>();
    private long ttl =  10000;

    @Autowired
    public ApplicationLifeCycleManager(RefreshableRoutesLocator locator) {
        locator.setLifeCycle(this);
        this.locator = locator;
    }

    public void register(App app) throws URISyntaxException {
        String appName = app.getName();
        apps.put(appName, app);
        this.locator.addRoute(appName, "/" + appName, URI.create("http://0.0.0.0:" + app.getPort() ));
        this.locator.buildRoutes();
        this.startApp(app);
    }

    public void execute(String name) {
        String substring = name.substring(1, name.length() - 1);
        App app = apps.get(substring);

        if(!app.isRunning()) {
            startApp(app);
        } else {
            app.setLastExecution(java.lang.System.currentTimeMillis());
        }
    }

    private void startApp(App app) {
        System.out.printf("App [%s] is not running, is going to be started%n", app.getName());
        String appCode = app.getApp().replace("${http.port}", app.getPort());
        try {
            ExecutionStatus executionStatus = Main.startDwCode(appCode);
            app.setRunning(true);
            app.setLastExecution(System.currentTimeMillis());
            app.setExecutionStatus(executionStatus);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteApp(String appName) {
        apps.remove(appName);
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        apps.values().stream()
                .filter(app -> app.isRunning)
                .filter(app -> {
                    long millisSinceLastExectuion =  System.currentTimeMillis() - app.getLastExecution();
                    boolean shouldBeTurnedOff = millisSinceLastExectuion > ttl;
//                    System.out.printf("Should [%s] be turned off? %d : %b%n", app.getName(), millisSinceLastExectuion, shouldBeTurnedOff);
                    return shouldBeTurnedOff;
                }).forEach(app -> {
                    long millisSinceLastExectuion =  System.currentTimeMillis() - app.getLastExecution();
                    System.out.println(String.format("Destroying [%s] after %d ms of inactivity", app.getName(), millisSinceLastExectuion));
                    app.getExecutionStatus().getProcess().destroy();
                    app.setRunning(false);
                });
    }
}
