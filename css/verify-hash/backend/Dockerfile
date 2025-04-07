FROM oven/bun:alpine

WORKDIR /app

COPY package*.json .

RUN bun install

COPY . .

CMD ["bun", "run", "start"]
# can use entrypoint for static
