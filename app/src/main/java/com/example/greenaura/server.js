const express = require('express');
const { gemini15Flash, googleAI } = require('@genkit-ai/googleai');
const { genkit } = require('genkit');

const app = express();
const port = 3000;

// Directly pass the API key here
const ai = genkit({
  plugins: [
    googleAI({
      apiKey: 'AIzaSyDxnldQhZcHOp4biqSVJZTrKdWPG8kSg5A',  // Directly inserted API key
    }),
  ],
  model: gemini15Flash,  // Set the Gemini model
});

const cors = require('cors');
app.use(cors());  // Allow CORS for all routes

// Middleware to parse JSON requests
app.use(express.json());

// Simple GET route for root URL
app.get('/', (req, res) => {
  res.send('Welcome to the chatbot service! Use the /chat endpoint to interact.');
});

// Endpoint to process messages
app.post('/chat', async (req, res) => {
  const userMessage = req.body.message;

  try {
    // Generate a response from the Gemini API
    const { text } = await ai.generate(userMessage);
    res.json({ reply: text });
  } catch (error) {
    console.error('Error generating response:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
