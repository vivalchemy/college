FROM oven/bun:alpine AS builder
WORKDIR /app
COPY package*.json .
RUN bun install
COPY . .
RUN bun run build

FROM nginx:latest
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
