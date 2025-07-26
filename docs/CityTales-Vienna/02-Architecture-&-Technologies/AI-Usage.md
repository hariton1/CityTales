### AI Usage throughout the Project

* vectorization of scraped data for semantic search in qdrant db data using Spring-AI-Core
* detail summary generation using GPT-4
* tone sensitive story generation using GPT-4
* Fun Fact generation using SpaCy, SentenceTransformer and GPT-4
* Quiz generation using GPT-4

### Do's and Don'ts

+ do use specific system prompts for consistency and answers that match specific patterns for further processing
+ do use AI to help with system prompt generation
+ do use methods to build prompts to balance flexibility with consistency ("Answer always in English" or "use this format" might be reusable)

- don't combine queries to safe tokens (if 3 summaries should be generated, make 3 calls to prevent mixing and hallucinations in the answer)
- don't loose track of tokens spent as queries in loops might hide the actual cost
- don't forget about varying results when querying ai services during testing
