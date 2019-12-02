package net.marvk.marpletraffic;

import net.marvk.marpletraffic.graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {
    private final Graph graph;

    private final List<Agent> agents;

    private final List<Agent> agentsUnmodifiable;

    public Simulation(final Graph graph, final ArrayList<Agent> agents) {
        this.graph = graph;
        this.agents = new ArrayList<>(agents);
        this.agentsUnmodifiable = Collections.unmodifiableList(this.agents);
    }

    public void step() {
        agents.forEach(Agent::calculateNext);
        agents.forEach(Agent::step);
    }

    public Graph getGraph() {
        return graph;
    }

    public List<Agent> getAgents() {
        return agentsUnmodifiable;
    }

    public void spawnAgents() {
        for (int i = 0; i < 40; i++) {
            agents.add(new Agent(
                    graph.getRandomLane(),
                    0.3 + ThreadLocalRandom.current().nextDouble() * 2.2,
                    agents,
                    Integer.toHexString(ThreadLocalRandom.current().nextInt())
            ));
        }
    }
}
