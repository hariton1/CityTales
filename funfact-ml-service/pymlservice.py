from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer, util

app = Flask(__name__)

# Load pretrained Modell
model = SentenceTransformer('paraphrase-MiniLM-L6-v2')

import spacy

nlp = spacy.load("de_core_news_sm")

@app.route('/funfact', methods=['POST'])
def extract_funfact():
    data = request.json
    text = data.get('text')
    if not text:
        return jsonify({'error': 'No text provided'}), 400

    # Saubere Satztrennung
    doc = nlp(text)
    sentences = [sent.text.strip() for sent in doc.sents if len(sent.text.strip().split()) >= 5]

    queries = [
        "Witzige Information",
        "Spannende Tatsache",
        "Etwas, das überrascht",
        "Historische Kuriosität",
        "Das interessanteste Detail"
    ]
    sentence_embeddings = model.encode(sentences, convert_to_tensor=True)
    query_embeddings = model.encode(queries, convert_to_tensor=True)

    scores = util.cos_sim(query_embeddings, sentence_embeddings)
    max_score_idx = scores.max(dim=1).indices[0].item()  # Beste Query, dann bester Satz
    best_sentence = sentences[max_score_idx]

    return jsonify({
        'funfact': best_sentence,
        'score': float(scores.max().item())
    })

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5005, debug=True)

