package net.marvk.marpletraffic;

import net.marvk.marpletraffic.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {
    private final Graph graph;

    private final List<Agent> agents;

    public Simulation(final Graph graph, final ArrayList<Agent> agents) {
        this.graph = graph;
        this.agents = List.copyOf(agents);
    }

    public void step() {
        agents.forEach(Agent::calculateNext);
        agents.forEach(Agent::step);
    }

    public Graph getGraph() {
        return graph;
    }

    public List<Agent> getAgents() {
        return agents;
    }
}
