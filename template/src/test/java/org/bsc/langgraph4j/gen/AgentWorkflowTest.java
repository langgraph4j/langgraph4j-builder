package org.bsc.langgraph4j.gen;

import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
* This is an automatically generated file.
*
* This file was generated using `langgraph4j-gen`.
*
* This file provides a placeholder implementation for the corresponding stub.
*
* Replace the placeholder implementation with your own logic.
*/
public class AgentWorkflowTest {

    @Test
    public void workflowTest() throws Exception {

        var graph = new AgentWorkflowBuilder<MessagesState<String>>()
            .Supervisor( node_async(state -> {
                System.out.println( "Supervisor" );
                return Map.of();
            }))
            .Agent_2( node_async(state -> {
                System.out.println( "Agent 2" );
                return Map.of();
            }))
            .Agent_1( node_async(state -> {
                System.out.println( "Agent 1" );
                return Map.of();
            }))
            .route( edge_async( state -> {
                System.out.println( "route" );
                // Implement me. Returns one of the paths.
                return END;
            }))
            .build( MessagesState::new, MessagesState.SCHEMA );


        var compileConfig = CompileConfig.builder()
                            // Add persistence
                            // .checkpointSaver(new MemorySaver())
                            // add interruptions
                            // .interruptAfter( ... )  // Before nodes
                            // .interruptBefore( ) // After nodes
                            .build();

        var workflow = graph.compile( compileConfig );

        var runnableConfig = RunnableConfig.builder()
                            // add thread Id (i.e. execution session id )
                            // valid only if checkpointSaver is enabled (see above)
                            // .threadId( "my_thread" )
                            .build();

        var iterator = workflow.stream( Map.of(), runnableConfig );

        for( var out : iterator ) {
            System.out.println( out );
        }
    }
}