package org.bsc.langgraph4j.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.reflect.ReflectionObjectHandler;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Generator {

    public static final String TEMPLATE_FOLDER = "template";

    private static class MapMethodReflectionHandler extends ReflectionObjectHandler {
        @Override
        protected boolean areMethodsAccessible(Map<?, ?> map) {
            return true;
        }
    }

    public record SourceFile(String path, String content) {
    }

    public record Result(SourceFile stub, SourceFile implementation, List<SourceFile> extraFiles) {
    }

    public static void main(String[] args) throws Exception {

        var definitionBuilder = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            // Read one line
            var line = "";
            while ((line = reader.readLine()) != null) {
                definitionBuilder.append(line).append('\n');
                ;
            }
        }

        if (definitionBuilder.isEmpty()) {
            throw new IllegalArgumentException("expected input!");
        }

        var gen = new Generator();

        var objectMapperYAML = new ObjectMapper((new YAMLFactory()));

        var graph = objectMapperYAML.readValue(definitionBuilder.toString(), GraphDefinition.Graph.class);

        var result = new Result(
                gen.generateBuilderFromDefinition(graph),
                gen.generateBuilderImplementationFromDefinition(graph),
                gen.generateProjectFiles());

        var objectMapper = new ObjectMapper();

        System.out.println(objectMapper.writeValueAsString(result));
    }

    final Mustache customAgentBuilderTemplate;
    final Mustache customAgentBuilderImplTemplate;

    final Path srcMainPath = Paths.get("src", "main", "java", "org", "bsc", "langgraph4j", "gen");
    final Path srcTestPath = Paths.get("src", "test", "java", "org", "bsc", "langgraph4j", "gen");

    public Generator() {
        var factory = new DefaultMustacheFactory() {

            /**
             * skip value encoding
             */
            @Override
            public void encode(String value, Writer writer) {
                try {
                    writer.write(value);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        factory.setObjectHandler(new MapMethodReflectionHandler());

        var srcPath = Paths.get(TEMPLATE_FOLDER, srcMainPath.toString(), "AgentWorkflowBuilder.java.mustache").toString();
        var testPath = Paths.get(TEMPLATE_FOLDER, srcTestPath.toString(), "AgentWorkflowTest.java.mustache").toString();

        this.customAgentBuilderTemplate = factory.compile(srcPath);
        this.customAgentBuilderImplTemplate = factory.compile(testPath);

    }


    public SourceFile generateBuilderFromDefinition(GraphDefinition.Graph graph) throws IOException {

        var out = new StringWriter();

        customAgentBuilderTemplate.execute(out, graph).flush();

        return new SourceFile(Paths.get(srcMainPath.toString(), "AgentWorkflowBuilder.java").toString(), out.toString());
    }

    public SourceFile generateBuilderImplementationFromDefinition(GraphDefinition.Graph graph) throws IOException {

        var out = new StringWriter();

        customAgentBuilderImplTemplate.execute(out, graph).flush();

        return new SourceFile(Paths.get(srcTestPath.toString(), "AgentWorkflowTest.java").toString(), out.toString());
    }

    public List<SourceFile> generateProjectFiles() throws Exception {

        var resources = List.of(
                Paths.get( TEMPLATE_FOLDER, ".gitignore"),
                Paths.get( TEMPLATE_FOLDER, "pom.xml"),
                Paths.get( TEMPLATE_FOLDER, "README.md")
        );

        var result = new ArrayList<SourceFile>();


        for( var path : resources ) {

            var resource = path.toString();

            try( var stream = getClass().getClassLoader().getResourceAsStream(resource) ) {

                if (stream == null) {
                    continue;
                }

                var content = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

                result.add(new SourceFile(Paths.get(TEMPLATE_FOLDER).relativize(path).toString(), content));
            }


        }

        return result;
    }

}