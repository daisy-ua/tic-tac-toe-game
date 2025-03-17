# Tic Tac Toe - Game Server

## Overview

This is the backend server for the Tic Tac Toe game, built with Kotlin and Ktor, that supports multiplayer gameplay.

It uses a combination of HTTP endpoints for creating rooms, and WebSockets for real-time gameplay.

## Endpoints

```POST /room/create``` - Creates a new game room.

```WS /room/{roomId}/game``` - WebSocket connection for real-time gameplay.

## Features

* Manage game rooms.

* Support real-time multiplayer game state synchronization.

* Handle reconnections and player management.
