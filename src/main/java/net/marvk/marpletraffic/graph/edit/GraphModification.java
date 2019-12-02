package net.marvk.marpletraffic.graph.edit;

import net.marvk.marpletraffic.graph.Graph;

@FunctionalInterface
public interface GraphModification {
    GraphModification NO_OP = g -> {
    };

    void apply(final Graph graph);
}
