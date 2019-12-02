package net.marvk.marpletraffic.app;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.internal.MvvmfxApplication;
import eu.lestard.easydi.EasyDI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.marvk.marpletraffic.Agent;
import net.marvk.marpletraffic.Simulation;
import net.marvk.marpletraffic.app.view.main.MainView;
import net.marvk.marpletraffic.graph.Graph;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public final class MarpleUi extends Application implements MvvmfxApplication {
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        startMvvmfx(primaryStage);
    }

    @Override
    public void startMvvmfx(final Stage primaryStage) {
        final EasyDI easyDI = new EasyDI();

        easyDI.bindInstance(Simulation.class, s3());

        MvvmFX.setCustomDependencyInjector(easyDI::getInstance);

        final var viewTuple = FluentViewLoader.fxmlView(MainView.class).load();

        final Scene scene = new Scene(viewTuple.getView());

        primaryStage.setTitle("Marple Traffic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Simulation s1() {
        final ArrayList<Agent> agents = new ArrayList<>();
        final Graph graph = Graph.testGraph();
        for (int i = 0; i < 39; i++) {
            agents.add(new Agent(graph.getRandomLane(), 0.3 + ThreadLocalRandom.current().nextDouble() * 2.2, agents));
        }

        agents.add(new Agent(graph.getRandomLane(), 2.5, agents, "Bob"));

        return new Simulation(graph, agents);
    }

    private Simulation s2() {
        final ArrayList<Agent> agents = new ArrayList<>();
        final Graph graph = Graph.simpleTestGraph();
        agents.add(new Agent(graph.getLanes().stream().findFirst().get(), 10, agents, "fasto"));
        agents.add(new Agent(graph.getLanes().stream().findFirst().get(), 0.1, agents));
        return new Simulation(graph, agents);
    }

    private Simulation s3() {
        final Graph graph = Graph.simpleTestGraph();
        return new Simulation(graph, new ArrayList<>());
    }
}
