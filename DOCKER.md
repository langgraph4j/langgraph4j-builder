# build a multi arch docker image

To build multi-architecture Docker images from Docker Compose on macOS with Docker Desktop, you'll need to use Docker Buildx, which comes pre-installed with Docker Desktop.

## Prerequisites Setup

First, ensure you have a builder instance that supports multi-platform builds:

```bash
# Create and use a new builder instance
docker buildx create --name multiarch --driver docker-container --use

# Bootstrap the builder (this pulls the buildx container)
docker buildx inspect --bootstrap
```

## Docker Compose Configuration

Modify your `docker-compose.yml` to specify the platforms you want to build for:

```yaml
version: '3.8'

services:
  your-app:
    build:
      context: .
      dockerfile: Dockerfile
      platforms:
        - linux/amd64
        - linux/arm64
        - linux/arm/v7  # optional, for ARM v7
    image: your-registry/your-app:latest
    # ... other service configuration
```

## Dockerfile Considerations

Make sure your Dockerfile supports multi-arch builds. Use base images that support multiple architectures:

```dockerfile
# Good - official images typically support multiple architectures
FROM node:18-alpine

# If you need architecture-specific logic, you can use build args
ARG TARGETPLATFORM
ARG BUILDPLATFORM

# Example of conditional commands based on architecture
RUN if [ "$TARGETPLATFORM" = "linux/arm64" ]; then \
      echo "Building for ARM64"; \
    else \
      echo "Building for AMD64"; \
    fi

COPY . .
RUN npm install
CMD ["npm", "start"]
```

## Building Multi-Arch Images

### Option 1: Using docker-compose with buildx

```bash
# Build and push multi-arch images
docker buildx bake -f docker-compose.yml --push

# Or build without pushing (stores in local cache)
docker buildx bake -f docker-compose.yml
```

### Option 2: Using docker-compose build with explicit platform

```bash
# Build for specific platforms
docker-compose build --build-arg BUILDKIT_MULTI_PLATFORM=1

# Or use buildx directly with compose file
docker buildx bake -f docker-compose.yml --set "*.platform=linux/amd64,linux/arm64"
```

### Option 3: Manual buildx approach

If you prefer more control:

```bash
# Build multi-arch image directly
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t your-registry/your-app:latest \
  --push .
```

**Build commands:**
```bash
# Setup builder (one-time)
docker buildx create --name multiarch --driver docker-container --use
docker buildx inspect --bootstrap

# Build multi-arch images
docker buildx bake --push

# Or build without pushing
docker buildx bake
```

## Important Notes

1. **Registry Push Required**: Multi-arch manifests need to be pushed to a registry. You can't store them locally with `docker images`.

2. **Platform Testing**: You can test ARM images on your Mac using emulation:
   ```bash
   docker run --platform linux/arm64 your-image
   ```

3. **Build Cache**: Use build cache mounts for faster builds:
   ```dockerfile
   RUN --mount=type=cache,target=/root/.npm npm install
   ```

4. **Verification**: Check your multi-arch manifest:
   ```bash
   docker buildx imagetools inspect your-registry/your-app:latest
   ```

This approach will create Docker images that work on both Intel/AMD64 and ARM64 architectures (like Apple Silicon Macs and ARM-based servers).