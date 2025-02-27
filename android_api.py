import flask, json
from flask import Flask, request, jsonify

app = Flask('__main__', template_folder="", static_folder="", root_path="", static_url_path="")
manifestacije = []

@app.route('/')
def index_page():
    return "Hello"

@app.route('/json/<int:number>', methods=['GET'])
def prikaz_jednog(number=None):
    try:
        with open("manifestacija_detalj.json") as f:
            data = json.load(f)
            
            # Find manifestation by ID
            manifestacija = next((m for m in data if m['id'] == number), None)
            
            if manifestacija is not None:
                return jsonify(manifestacija)
            else:
                return "Manifestacija not found.", 404
    except Exception as e:
        return "Greška: " + str(e), 500

@app.route('/json', methods=['GET', 'POST'])
def handle_json():
    if request.method == 'GET':
        with open("manifestacija_detalj.json") as f:
            data = json.load(f)
            return jsonify(data)
    elif request.method == 'POST':
        try:
            data = request.json

            # Read current manifestations
            with open("manifestacija_detalj.json") as f:
                manifestacije = json.load(f)

            # Generate new ID
            if manifestacije:
                max_id = max([m['id'] for m in manifestacije])  # Find the largest current ID
                new_id = max_id + 1
            else:
                new_id = 1  # If no manifestations, set the first ID to 1

            # Set the new ID
            data['id'] = new_id

            # Add new manifestation to the list
            manifestacije.append(data)

            # Save to file
            with open("manifestacija_detalj.json", "w") as f:
                json.dump(manifestacije, f, indent=4)

            # Return newly created manifestation with new ID
            return jsonify(data)

        except Exception as e:
            return "Error while processing data: " + str(e), 500

@app.route('/json/<int:number>', methods=['PUT', 'DELETE'])
def handle_manifestacija(number):
    try:
        with open("manifestacija_detalj.json") as f:
            manifestacije = json.load(f)

        # Find manifestation by ID
        manifestacija = next((m for m in manifestacije if m['id'] == number), None)

        if not manifestacija:
            return "Manifestacija not found.", 404

        if request.method == 'PUT':
            data = request.json
            # Update the manifestation with new data while keeping the ID the same
            manifestacija.update(data)
            manifestacija['id'] = number  # Ensure ID remains unchanged

            # Save updated list to file
            with open("manifestacija_detalj.json", "w") as f:
                json.dump(manifestacije, f, indent=4)

            return jsonify(manifestacija), 200

        elif request.method == 'DELETE':
            # Remove manifestation with the corresponding ID
            manifestacije = [m for m in manifestacije if m['id'] != number]

            with open("manifestacija_detalj.json", "w") as f:
                json.dump(manifestacije, f, indent=4)

            return "Manifestacija deleted successfully.", 200

    except Exception as e:
        return "Error while processing request: " + str(e), 500

if __name__ == "__main__":
    app.run("0.0.0.0", 5000)