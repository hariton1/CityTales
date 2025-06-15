from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer, util
import spacy
import random
import openai
import os
from hashlib import sha256
from datetime import datetime
import csv

app = Flask(__name__)

# API-Key laden
openai.api_key = os.getenv("OPENAI_API_KEY")

# Modelle laden
model = SentenceTransformer('paraphrase-MiniLM-L6-v2')
nlp = spacy.load("de_core_news_sm")

# In-Memory-Cache
fact_cache = {}

# Intro-Sätze nach Kategorie
THEMATIC_INTROS = {
    'history': [
        "Geschichtsstunde mal anders: 📜",
        "Aus der Mottenkiste der Geschichte: 🕰️",
        "Früher war nicht alles besser – aber manchmal seltsamer: 🏺"
    ],
    'science': [
        "Wissenschaft kann auch witzig sein: 🔬",
        "Wusstest du das aus der Forschung? 🧪",
        "Klugscheißer-Alarm! 🤓"
    ],
    'funny': [
        "Das bringt dich sicher zum Schmunzeln: 😁",
        "Klingt wie ein Witz, ist aber wahr: 😂",
        "Total absurd – aber wissenschaftlich belegbar: 🤯"
    ],
    'animal': [
        "Tierisch gute Info: 🐒",
        "Tiere überraschen uns immer wieder: 🐾",
        "Natur kann echt verrückt sein: 🌿"
    ],
    'random': [
        "Wusstest du schon? 🤔",
        "Hier kommt ein Fun Fact für dich! 🎉",
        "Zeit für unnützes Wissen: 📚"
    ]
}

FALLBACK_JOKES = [
    "Tintenfische haben drei Herzen – und trotzdem Herzschmerz, wenn sie allein sind. ❤️🐙",
    "Honig wird nie schlecht. Man hat essbaren Honig in altägyptischen Gräbern gefunden! 🍯",
    "In Frankreich ist es verboten, ein Schwein Napoleon zu nennen. 🐷"
]

FUNFACT_QUERIES = [
    "Lustige unnütze Tatsache",
    "Skurrile Info",
    "Klingt wie ein Witz, ist aber wahr",
    "Verrückte historische Anekdote",
    "Total absurde Wahrheit",
    "Überraschendes Wissen",
    "Unerwartete Erkenntnis"
]


def classify_topic(text):
    s = text.lower()
    if any(w in s for w in ['geschichte', 'jahrhundert', 'mittelalter', 'römer', 'antike', 'könig']):
        return 'history'
    elif any(w in s for w in ['wissenschaft', 'experiment', 'forschung', 'physik', 'chemie']):
        return 'science'
    elif any(w in s for w in ['hund', 'katze', 'tier', 'kuh', 'elefant', 'affe']):
        return 'animal'
    elif any(w in s for w in ['witzig', 'lustig', 'verrückt', 'absurd']):
        return 'funny'
    else:
        return 'random'


def is_boring(text):
    boring_keywords = [
        "Abschnitt", "Kapitel", "Abbildung", "siehe", "Prozent", "Studie",
        "Daten", "Tabelle", "Quelle", "zitiert", "Artikel",
        "Verkehrsfläche", "benannt", "gewidmet", "Straße", "Platz",
        "Verwaltung", "Bezeichnung", "Maßnahme", "Richtlinie", "Verordnung"
    ]
    if len(text.split()) < 5 or len(text) < 50:
        return True
    return any(word.lower() in text.lower() for word in boring_keywords)


def is_incomplete(text):
    text = text.strip()
    if text.endswith(":") or text.endswith("…"):
        return True
    if len(text.split()) < 4:
        return True
    if text[0].islower():
        return True
    if not any(w in text.lower() for w in
               [" ist ", " war ", " wurde ", " hat ", " sind ", " waren ", " haben ", "konnte", "kann", "wird", "sei"]):
        return True
    if text.lower().startswith(("einige ", "diese ", "manche ", "andere ", "dabei ", "diese wurden", "dabei wurden")):
        return True
    return False


def create_chunks(sentences, max_len=3):
    chunks = []
    for i in range(len(sentences)):
        for size in range(1, max_len + 1):
            if i + size <= len(sentences):
                first = sentences[i]
                chunk = " ".join(sentences[i:i + size])
                if not is_boring(chunk) and not is_incomplete(chunk) and not is_incomplete(first):
                    chunks.append(chunk)
    return chunks


def get_cached_or_generate_gpt_fact(text):
    key = sha256(text.encode()).hexdigest()
    if key in fact_cache:
        return fact_cache[key]

    prompt = (
        "Lies den folgenden Text und formuliere daraus einen unterhaltsamen Fun Fact. "
        "Er soll spannend, lustig oder skurril klingen – so, dass man ihn Freunden erzählen will. "
        "Maximal 2 Sätze, keine Fachsprache, kein 'Einige davon wurden...'. Text:\n\n"
        f"{text}\n\nFun Fact:"
    )
    try:
        response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.8,
            max_tokens=100
        )
        result = response.choices[0].message["content"].strip()
        fact_cache[key] = result
        return result
    except Exception as e:
        return f"[Fehler bei GPT: {str(e)}]"


def log_funfact(input_text, funfact, score, note):
    with open("funfact_log.csv", "a", encoding="utf-8", newline="") as f:
        writer = csv.writer(f)
        writer.writerow([
            datetime.now().isoformat(timespec="seconds"),
            input_text[:200].replace("\n", " "),
            funfact.replace("\n", " "),
            round(score, 4),
            note
        ])


@app.route('/funfact', methods=['POST'])
def extract_funfact():
    data = request.json
    text = data.get('text')
    if not text:
        return jsonify({'error': 'No text provided'}), 400

    doc = nlp(text)
    sentences = [sent.text.strip() for sent in doc.sents if len(sent.text.strip().split()) >= 5]

    if not sentences:
        fallback = random.choice(FALLBACK_JOKES)
        log_funfact(text, fallback, 0.0, "Kein Text – Fallback-Witz")
        return jsonify({'funfact': fallback, 'note': "Fallback 😄"})

    chunks = create_chunks(sentences)

    if not chunks:
        fallback = random.choice(FALLBACK_JOKES)
        log_funfact(text, fallback, 0.0, "Keine brauchbaren Chunks – Fallback")
        return jsonify({'funfact': fallback, 'note': "Fallback 😅"})

    chunk_embeddings = model.encode(chunks, convert_to_tensor=True)
    query_embeddings = model.encode(FUNFACT_QUERIES, convert_to_tensor=True)
    scores = util.cos_sim(query_embeddings, chunk_embeddings).max(dim=0).values

    best_idx = scores.argmax().item()
    best_chunk = chunks[best_idx]
    best_score = float(scores[best_idx].item())

    if best_score < 0.45:
        fallback = random.choice(FALLBACK_JOKES)
        log_funfact(text, fallback, best_score, "Score zu niedrig – Fallback")
        return jsonify({'funfact': fallback, 'score': best_score, 'note': "Score zu niedrig 😅"})

    if is_incomplete(best_chunk) or is_boring(best_chunk):
        gpt_fact = get_cached_or_generate_gpt_fact(best_chunk)
        log_funfact(text, gpt_fact, best_score, "GPT-Fallback")
        return jsonify({'funfact': gpt_fact, 'score': best_score, 'note': "GPT-Optimierung"})

    topic = classify_topic(best_chunk)
    intro = random.choice(THEMATIC_INTROS[topic])
    final = f"{intro} {best_chunk}"

    log_funfact(text, final, best_score, "Klassisch – OK")
    return jsonify({'funfact': final, 'score': best_score})


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5005, debug=True)
