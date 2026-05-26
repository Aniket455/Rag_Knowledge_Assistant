# RAG Knowledge Assistant

An intelligent customer support documentation assistant powered by Retrieval-Augmented Generation (RAG). This application allows users to upload documentation (like policies, FAQs) and ask natural language questions. The AI answers those questions strictly based on the provided documents, preventing hallucinations.

## 🚀 Features

- **Document Ingestion**: Upload `.txt` and `.md` files or index default documentation.
- **Vector Search**: Automatically chunks text, generates embeddings, and stores them in a vector database for rapid similarity search.
- **Conversational AI**: Ask questions and get answers grounded *only* in your uploaded data.
- **Source Citations**: The UI displays the exact document chunks (and their similarity scores) used to generate the answer.
- **Vector Inspection API**: Endpoints to peek inside the vector store to learn how embeddings are stored and see statistics.
- **Modern UI**: A responsive, clean vanilla HTML/JS interface for testing the RAG pipeline.

---

## 🛠️ Technology Stack & Architecture

This project uses a modern Java AI stack, carefully chosen for performance, cost-effectiveness, and ease of development.

### 1. Framework: Java 21 + Spring Boot + Spring AI
- **Why**: Spring Boot is the industry standard for enterprise Java applications. **Spring AI** provides excellent abstractions over various AI providers and vector stores, making it easy to swap out models or databases without rewriting the entire application logic.

### 2. Vector Database: PostgreSQL + pgvector (via Docker)
- **Why**: PostgreSQL is a robust, production-ready relational database. The `pgvector` extension turns it into a powerful vector database. Using PostgreSQL means you don't need to introduce a specialized, complex vector DB if you are already familiar with SQL.

### 3. Embeddings: Ollama (`mxbai-embed-large`)
- **Why**: We use Ollama to run embedding models **locally**. 
  - **Privacy**: Your sensitive documents never leave your machine during the indexing phase.
  - **Cost**: It's 100% free, avoiding API costs when indexing massive document bases.
  - **Model**: `mxbai-embed-large` is highly optimized for retrieval tasks.

### 4. Chat Generation: Groq / OpenAI API
- **Why**: For generating the final answer from the retrieved context, we use an external LLM. The project is configured to use Groq's OpenAI-compatible API, which offers blazing-fast inference speeds, ensuring a snappy user experience. It can easily be pointed to standard OpenAI models (like GPT-4o) if preferred.

### 5. Multi-Model Support: Gemini (Optional)
- **Why**: The project includes a `MultiModelConfig` demonstrating how to configure secondary AI clients (like Google Gemini) alongside the primary one, allowing for specialized tasks or fallback mechanisms.

---

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

1. **Java 21**
2. **Maven**
3. **Docker Desktop** (for running PostgreSQL + pgvector)
4. **Ollama** (for local embeddings)

---

## ⚙️ Setup & Installation

### 1. Start the Database
The project includes a `docker-compose.yml` file to spin up PostgreSQL with the `pgvector` extension pre-installed.
```bash
docker-compose up -d
```

### 2. Start Ollama and Pull the Embedding Model
Ensure Ollama is running, then pull the required embedding model:
```bash
ollama pull mxbai-embed-large
```

### 3. Configure Environment Variables
You need to set API keys for the chat generation models. You can set these in your environment or add them to your IDE's run configuration.
- `GROQ_API_KEY`: Your API key for Groq (or OpenAI if changing the base URL).
- `GEMINI_API_KEY`: (Optional) Your Google Gemini API key.

### 4. Run the Application
Run the Spring Boot application using Maven:
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8086`.

---

## 🎮 Usage

### Accessing the UI
Open your browser and navigate to the static frontend provided:
`http://localhost:8086/rag-assistant.html`

### 1. Ingest Data
Before asking questions, the AI needs knowledge.
- Click **Index Default Documentation** to load the pre-configured e-commerce support policy.
- Or, drag and drop your own `.txt` or `.md` files to upload them.

### 2. Ask Questions
Type a question in the chat box. For example, if you indexed the default e-commerce policy:
- *"What is your return policy?"*
- *"How do I track my order?"*
- *"Do you offer EMI?"*

The AI will search the vector store, find the most relevant paragraphs, and generate an answer based *only* on that context.

---

## 🔍 How It Works (The RAG Pipeline)

1. **Ingestion (`DocumentService`)**: 
   - A document is read and split into smaller chunks (e.g., 500 tokens) using `TokenTextSplitter`.
   - Each chunk is sent to **Ollama** to be converted into an embedding (a dense vector array).
   - The chunk text, metadata, and the embedding vector are saved to **PostgreSQL**.
2. **Retrieval (`RAGService`)**:
   - The user asks a question.
   - The question is converted into an embedding using Ollama.
   - We perform a **Cosine Similarity Search** in PostgreSQL to find the top 5 chunks most mathematically similar to the question.
3. **Generation (`RAGService`)**:
   - The retrieved text chunks are assembled into a "Context".
   - A prompt is built: *"Answer the question using ONLY this context: [Context] + [Question]"*.
   - The prompt is sent to the **Groq/OpenAI** LLM.
   - The LLM streams back an informed, hallucination-free answer.
