from flask import Flask, redirect, render_template, url_for
from flask import jsonify
import requests

app = Flask(__name__)

@app.route('/')
def index():
   return render_template("index.html")
# shoot requests code
@app.route('/shoot', methods=['POST'])
def shoot():
    response = requests.post(
        'http://localhost:8080/shoot',
        data=b'',
        timeout=5,
    )
    response.raise_for_status()
    # this lets us stay on the page while also sending a page request
    return redirect(url_for('index'))
    return jsonify({"status": "success"}), 200
@app.route('/move', methods=['POST'])
def move():
    response = requests.post(
        'http://localhost:8080/move',
        data=b'',
        timeout=5,
    )
    response.raise_for_status()
    # this lets us stay on the page while also sending a page request
    return redirect(url_for('index'))
    return jsonify({"status": "success"}), 200