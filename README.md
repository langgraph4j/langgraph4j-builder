# langgrap4j-builder

Provides a powerful canvas for designing Agentic Workflow as LangGraph4j applications.

## WebUI

Based on project [langgraph-builder] we have integrate the **Langgraph4j** part (take a look to the [fork][langgraph-builder-fork]) so we can now **visually scaffolding a langchain4j graph** and **generate java code**

![builder](src/site/resources/langgraph4j-builder.mov.gif)

## Scaffolding
This module generate a langgraph4j scaffold from a [Domain Specific Language (DSL)][DSL] based on [YAML] format.

**DSL Example**
```yaml
name: CustomAgent
nodes:
  - name: model
  - name: tool
edges:
  - from: __start__
    to: model
  - from: tool
    to: model
  - from: model
    condition: route after reasoning
    paths: [tool, __end__]
```


## Setup (with Docker)

**Pull image**
```bash
docker pull bsorrentino/langgraph4j-builder:1.6-SNAPSHOT
```

**Run container**
```bash
docker run --rm \
  --name langgraph4j-builder-app \
  -p 3000:3000 \
  -v .:/app/workspace \
  -e NODE_ENV=production \
  -e LANGRAPH4J_GEN=generator-1.6-SNAPSHOT-jar-with-dependencies.jar \
  -e RUNNING_IN_DOCKER=true \
  bsorrentino/langgraph4j-builder:1.6-SNAPSHOT
```

open browser on address [http://localhost:3000](http://localhost:3000])



[DSL]: https://en.wikipedia.org/wiki/Domain-specific_language
[YAML]: https://yaml.org
[langgraph-builder]: https://github.com/langchain-ai/langgraph-builder
[langgraph-builder-fork]: https://github.com/bsorrentino/langgraph-builder