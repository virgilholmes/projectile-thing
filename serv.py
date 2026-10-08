from flask import Flask, redirect, render_template, url_for
import requests

app = Flask(__name__)

@app.route('/')
def index():
   return render_template("index.html")

@app.route('/shoot', methods=['POST'])
def shoot():
    response = requests.post(
        'http://localhost:8080/shoot',
        data=b'',
        timeout=5,
    )
    response.raise_for_status()

    return redirect(url_for('index'))
