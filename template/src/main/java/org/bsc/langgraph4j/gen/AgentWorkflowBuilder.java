package org.bsc.langgraph4j.gen;

import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.serializer.StateSerializer;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.AgentStateFactory;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.utils.EdgeMappings;

import java.util.Map;
import java.util.Objects;

import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.StateGraph.END;

/**
 * This is an automatically generated file. Do not modify it.
 *
 * This file was generated using `langgraph4j-gen`.
 */
public class AgentWorkflowBuilder<State extends AgentState> {

    private AsyncNodeAction<State> Supervisor;
    private AsyncNodeAction<State> Agent_2;
    private AsyncNodeAction<State> Agent_1;
    private AsyncEdgeAction<State> route;

    public AgentWorkflowBuilder<State> Supervisor(AsyncNodeAction<State> Supervisor) {
        this.Supervisor = Supervisor;
        return this;
    }
    public AgentWorkflowBuilder<State> Agent_2(AsyncNodeAction<State> Agent_2) {
        this.Agent_2 = Agent_2;
        return this;
    }
    public AgentWorkflowBuilder<State> Agent_1(AsyncNodeAction<State> Agent_1) {
        this.Agent_1 = Agent_1;
        return this;
    }
    public AgentWorkflowBuilder<State> route(AsyncEdgeAction<State> route) {
        this.route = route;
        return this;
    }


    private StateGraph<State> of(AgentStateFactory<State> factory) {
        return new StateGraph<>( factory );
    }

    private StateGraph<State> of( StateSerializer<State> serializer) {
        return new StateGraph<>( serializer );
    }

    private StateGraph<State> of(Map<String, Channel<?>> channels, AgentStateFactory<State> factory) {
        return new StateGraph<>(factory);
    }

    private StateGraph<State> of(Map<String, Channel<?>> channels, StateSerializer<State> serializer) {
        return new StateGraph<>(serializer);
    }

    private StateGraph<State> build( StateGraph<State> graph ) throws GraphStateException {
        Objects.requireNonNull( Supervisor, "Supervisor cannot be null");
        Objects.requireNonNull( Agent_2, "Agent_2 cannot be null");
        Objects.requireNonNull( Agent_1, "Agent_1 cannot be null");
        Objects.requireNonNull( route, "route cannot be null");

        return graph
            .addNode( "Supervisor", Supervisor )
            .addNode( "Agent 2", Agent_2 )
            .addNode( "Agent 1", Agent_1 )
            .addEdge( START, "Supervisor" )
            .addEdge( "Agent 2", "Supervisor" )
            .addEdge( "Agent 1", "Supervisor" )
            .addConditionalEdges( "Supervisor",
                route,
                EdgeMappings.builder()
                    .to( "Agent 1" )
                    .to( "Agent 2" )
                    .to( END )
                .build())
        ;

    }

    public StateGraph<State> build( AgentStateFactory<State> factory ) throws GraphStateException {
        return build( of( factory ) );
    }

    public StateGraph<State> build( StateSerializer<State> serializer ) throws GraphStateException {
        return build( of( serializer ) );
    }

    public StateGraph<State> build(AgentStateFactory<State> factory, Map<String, Channel<?>> channels ) throws GraphStateException {
        return build(of( channels, factory ));
    }

    public StateGraph<State> build(Map<String, Channel<?>> channels, StateSerializer<State> serializer) throws GraphStateException {
        return build(of( channels, serializer ));
    }

}