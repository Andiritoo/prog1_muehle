# AI Player for Mühle

This package contains an AI player implementation that uses LangChain4j with local LLM support via Ollama.

## Features

- Implements the `Player` interface
- Uses LangChain4j for LLM integration

## Setup

### 1. Install Ollama

**macOS:**
```bash
brew install ollama
```

**Windows:**
Download from [ollama.ai](https://ollama.ai/)

### 2. Start Ollama Service

```bash
ollama serve
```

### 3. Pull a Model

```bash
ollama pull llama3.2
```

Run:
```bash
mvn clean install
```