# AI Player for Nine Men's Morris (Mühle)

This package contains an AI player implementation that uses LangChain4j with **free local LLM support** via Ollama.

## Features

- Implements the `Player` interface
- Uses LangChain4j for LLM integration
- **Completely free** - runs locally with Ollama
- No API keys required
- Supports multiple open-source models

## Setup

### 1. Install Ollama

**macOS:**
```bash
brew install ollama
```

**Linux:**
```bash
curl -fsSL https://ollama.ai/install.sh | sh
```

**Windows:**
Download from [ollama.ai](https://ollama.ai/)

### 2. Start Ollama Service

```bash
ollama serve
```

### 3. Pull a Model

```bash
# Recommended: Small and fast
ollama pull llama3.2

# Alternatives:
ollama pull llama2      # Older but stable
ollama pull mistral     # Good performance
ollama pull phi         # Very small and fast
```

### 4. Update Maven Dependencies

The `pom.xml` already includes:
- `langchain4j` - Core library
- `langchain4j-ollama` - Ollama integration

Run:
```bash
mvn clean install
```

## Usage

### Basic Usage

```java
// Create AI player - no API key needed!
Player aiPlayer = new AIPlayer(NodeValue.WHITE);

// Use in game
GameState gameState = new GameState();
Move move = aiPlayer.move(gameState);
```

### Using a Different Model

```java
// Specify a different Ollama model
Player aiPlayer = new AIPlayer(NodeValue.WHITE, "mistral");
```

### Run the Example

```bash
# Make sure Ollama is running
ollama serve

# In another terminal, run the example
mvn exec:java -Dexec.mainClass="prog1_muehle.llm.AIPlayerExample"
```

## How It Works

1. The AI receives the current game state
2. It converts the board to a text representation
3. It sends a prompt to the LLM with:
   - Current board state
   - Game rules
   - Available moves
4. The LLM suggests a move
5. The response is parsed back into a `Move` object

## Available Models

Check available models at [ollama.ai/library](https://ollama.ai/library)

Popular options:
- `llama3.2` - Fast and capable (recommended)
- `llama2` - Stable and well-tested
- `mistral` - Good at reasoning
- `phi` - Very small, good for quick responses
- `codellama` - Good at structured output

## Troubleshooting

**"Connection refused" error:**
- Make sure Ollama is running: `ollama serve`

**"Model not found" error:**
- Pull the model first: `ollama pull llama3.2`

**Slow responses:**
- Try a smaller model like `phi`
- First request is slower as it loads the model

**Want to use a paid API instead?**
- OpenAI, Anthropic Claude, and other providers are also supported via LangChain4j
- Check LangChain4j documentation for integration details
