# Locator

Telegram bot shows latecomers or absent from work.

## Usage

1. You can register yourself, for this write to the bot, for example:
   ```
   I caught a cold. I'll take the day off today.
   ```
2. You can see the list of absent and latecomers. Use the following command:
   ```
   /report
   ```
   The list is updated automatically from the Google calendar.

## Build

```sh
./gradlew clean build
```

Self-executable jar will be located in `build/libs`. To start long polling execute command

```sh
TOKEN=... java -jar build/libs/*-all.jar
```

## Deploy

[![Deploy to DigitalOcean](https://www.deploytodo.com/do-btn-blue-ghost.svg)](https://cloud.digitalocean.com/apps/new?repo=https://github.com/YOUR/REPO/tree/main)