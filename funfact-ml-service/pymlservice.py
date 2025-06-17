from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer, util
import spacy
import openai
import os
import random
from hashlib import sha256
from datetime import datetime
import csv
import logging

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("funfact_app.log", encoding="utf-8")
    ]
)
logger = logging.getLogger(__name__)

app = Flask(__name__)

model = SentenceTransformer('paraphrase-MiniLM-L6-v2')
nlp = spacy.load("de_core_news_sm")

fact_cache = {}

FUNFACT_QUERIES = [
    "Lustige unnütze Tatsache",
    "Skurrile Info",
    "Klingt wie ein Witz, ist aber wahr",
    "Verrückte historische Anekdote",
    "Total absurde Wahrheit",
    "Überraschendes Wissen",
    "Unerwartete Erkenntnis"
]

FALLBACKS = [
    "Tintenfische haben drei Herzen – und trotzdem Herzschmerz, wenn sie allein sind. ❤️🐙",
    "Honig wird nie schlecht. Man hat essbaren Honig in altägyptischen Gräbern gefunden! 🍯",
    "In Frankreich ist es verboten, ein Schwein Napoleon zu nennen. 🐷"
]

def log_funfact(input_text, funfact, score, note):
    logger.info(f"Log Fun Fact - Note: {note}, Score: {score:.4f}, Text: {funfact}")
    with open("funfact_log.csv", "a", encoding="utf-8", newline="") as f:
        csv.writer(f).writerow([
            datetime.now().isoformat(timespec="seconds"),
            input_text[:200].replace("\n", " "),
            funfact.replace("\n", " "),
            round(score, 4),
            note
        ])

def is_boring(text):
    boring = ["Abschnitt", "Kapitel", "gewidmet", "benannt", "Platz", "Verwaltung", "Verkehrsfläche"]
    return any(b in text for b in boring) or len(text.split()) < 4 or len(text) < 40

def is_incomplete(text):
    return text.strip().endswith(":") or text[0].islower() or "…" in text or text.lower().startswith(("einige", "dabei", "diese"))

def create_chunks(sentences, max_len=3):
    chunks = []
    for i in range(len(sentences)):
        for size in range(1, max_len + 1):
            if i + size <= len(sentences):
                chunk = " ".join(sentences[i:i + size])
                if not is_boring(chunk) and not is_incomplete(chunk):
                    chunks.append(chunk)
    return chunks

def gpt_funfact_with_context(full_text, focus):
    key = sha256((focus + full_text).encode()).hexdigest()
    if key in fact_cache:
        logger.info("Using cached GPT result")
        return fact_cache[key]
    try:
        client = openai.OpenAI()  # Dynamisch innerhalb der Funktion
        prompt = (
            f"Lies den folgenden Text und finde einen überraschenden, unterhaltsamen Fun Fact. "
            f"Konzentriere dich besonders auf diesen Ausschnitt:\n\"{focus}\"\n\n"
            f"Hier ist der gesamte Zusammenhang:\n{full_text}\n\n"
            "Formuliere daraus einen Fun Fact in maximal zwei Sätzen. Er soll spannend, witzig oder kurios sein – so, dass man ihn Freunden erzählen will."
        )
        response = client.chat.completions.create(
            model="gpt-4",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.8,
            max_tokens=120
        )
        fact = response.choices[0].message.content.strip()
        fact_cache[key] = fact
        logger.info(f"GPT response: {fact[:100]}...")
        return fact
    except Exception as e:
        logger.error(f"GPT Fun Fact Error: {e}")
        return f"[Fehler bei GPT: {str(e)}]"

@app.route('/funfact', methods=['POST'])
def extract_funfact():
    data = request.json
    text = data.get('text')

    if not text:
        logger.warning("No text provided in request")
        return jsonify({'error': 'No text provided'}), 400

    logger.info("Received text for fun fact extraction")
    doc = nlp(text)
    sentences = [s.text.strip() for s in doc.sents]
    chunks = create_chunks(sentences)

    if not chunks:
        fallback = random.choice(FALLBACKS)
        logger.info("No valid chunks – using fallback fun fact")
        log_funfact(text, fallback, 0.0, "Keine sinnvollen Chunks – Fallback")
        return jsonify({'funfact': fallback, 'score': 0.0, 'note': 'Fallback'})

    chunk_embeddings = model.encode(chunks, convert_to_tensor=True, show_progress_bar=False)
    query_embeddings = model.encode(FUNFACT_QUERIES, convert_to_tensor=True, show_progress_bar=False)
    scores = util.cos_sim(query_embeddings, chunk_embeddings).max(dim=0).values

    best_idx = scores.argmax().item()
    best_chunk = chunks[best_idx]
    best_score = float(scores[best_idx].item())
    logger.info(f"Best Chunk: {best_chunk[:80]}... (Score: {best_score:.4f})")

    gpt_fact = gpt_funfact_with_context(text, best_chunk)
    candidates = [best_chunk, gpt_fact]
    candidate_embeddings = model.encode(candidates, convert_to_tensor=True, show_progress_bar=False)
    final_scores = util.cos_sim(query_embeddings, candidate_embeddings).max(dim=0).values

    final_idx = final_scores.argmax().item()
    final_fact = candidates[final_idx]
    final_score = float(final_scores[final_idx].item())
    note = "GPT" if final_idx == 1 else "Eigener Chunk"

    logger.info(f"Selected Final Fun Fact – Note: {note}, Score: {final_score:.4f}")
    log_funfact(text, final_fact, final_score, note)
    return jsonify({'funfact': final_fact, 'score': final_score, 'note': note})

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5005, debug=True)
