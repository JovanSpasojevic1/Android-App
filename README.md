# Manifestacije Srbije - Android Application

## Overview
Manifestacije Srbije is a mobile Android application designed to manage and track cultural events and manifestations in Serbia. The app allows users to view events, mark their favorites, and manage manifestations through a REST API. Additionally, the project includes a Python-based REST API server (via `android_api.py`) built with Flask.

## Features
- **View Events:** Display all manifestations, upcoming events, and favorite events.
- **Add & Edit:** Create new manifestations and modify existing ones.
- **Favorites:** Mark manifestations as favorites and store them locally using SharedPreferences.
- **Search:** Filter manifestations by name and location.
- **Details:** Show detailed information about each manifestation.

## Technologies
- **Programming Language:** Java
- **Development Environment:** Android Studio
- **Networking:** Volley (for REST API communication)
- **Local Storage:** SharedPreferences
- **User Interface:** XML layouts, Fragments, BottomNavigationView
- **API:** Local REST API (Flask-based) for managing manifestations

## Getting Started

### Clone the Repository
Clone the repository using the following commands:
```bash
git clone https://github.com/JovanSpasojevic1/Android-App.git
```
### Setting Up the Android Application

**Open the Project:**

- Open Android Studio and select "Open an existing project".
- Navigate to the directory where the repository was cloned.

**Review Dependencies:**

- Check the build.gradle files for required libraries such as Volley, Gson, AppCompat, and ConstraintLayout.

**Update API URLs:**

- Ensure that the URL addresses in your code (e.g., in MainActivity and ManifestacijaDetailActivity) match the address where your REST API server is running.

**Run the App:**

- Choose an emulator or a physical Android device.
- Click on "Run" (or press Shift + F10) to launch the application.

### Running the REST API Server

**Install Python & Flask:**

- Ensure Python 3 is installed.
- Install the required packages using:
  ```bash
  pip install Flask
  ```
- Run the `android_api.py` script:
  
  ```bash
  python android_api.py
  ```
- The server will start (for example, at http://192.168.0.17:5000). If you run the server on a different address, update the corresponding URLs in the Android application.

## REST API Endpoints

The REST API supports the following endpoints for managing manifestations:

- **GET /json** – Retrieve all manifestations.
- **GET /json/{id}** – Retrieve details of a specific manifestation.
- **POST /json** – Create a new manifestation.
- **PUT /json/{id}** – Update an existing manifestation.
- **DELETE /json/{id}** – Delete a manifestation.
  
## Project Structure

- **SplashScreenActivity** – The initial splash screen of the application.
- **MainActivity** – The main activity featuring BottomNavigationView for navigation.
- **HomeFragment** – Displays all manifestations and provides search functionality.
- **UpcomingFragment** – Shows manifestations scheduled for the next two weeks.
- **FavoritesFragment** – Displays favorite manifestations.
- **ManifestacijaDetailActivity** – Shows detailed information about a selected manifestation.
- **EditManifestacijaActivity** – Used for adding and editing manifestations.
- **ManifestacijaFragmentHelper** – A helper class for managing manifestations.
