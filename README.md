# UberHelpV8

UberHelpV8 is an Android app designed to help Uber drivers maximize their earnings by automatically evaluating and accepting profitable trips. The app uses Android's Accessibility Service to read trip details from the Uber Driver app and applies AI-driven evaluation using TensorFlow Lite to determine if a trip is worth accepting.

## Features

- **Automated Trip Detail Extraction:** Uses Android Accessibility Service to read trip details from the Uber Driver app.
- **AI-Driven Evaluation:** Uses TensorFlow Lite to analyze trip data and determine profitability.
- **Automated Trip Acceptance:** Automatically accepts trips that meet profitability criteria.
- **Minimalistic UI:** Simple, dark-themed interface with an on/off toggle for easy control.
- **Local Data Storage:** Stores trip data locally using SQLite for privacy and security.
- **On-Device Model Retraining:** Periodically retrains the ML model with accumulated data for improved performance.

## Requirements

- Android device running Android 7.0 (API 24) or higher
- Uber Driver app installed
- Accessibility Service permission granted

## Setup Instructions

1. Install the UberHelpV8 app on your Android device
2. Open the app and tap on "Accessibility Setup Guide"
3. Follow the on-screen instructions to enable the Accessibility Service
4. Return to the app and toggle the service on
5. The app will now monitor the Uber Driver app and automatically accept profitable trips

## Configuration Options

The app can be configured through the Settings screen:

- **Schedule:** Set specific hours when the service should be active
- **Data Storage:** Configure how historical trip data is stored and used
- **Clear Data:** Option to clear all stored trip data from the device

## Privacy & Security

- All data is stored locally on your device using SQLite
- No data is transmitted to external servers
- The app only accesses the Uber Driver app through the Accessibility Service

## License

This project is for personal use only. 

## Credits

- TensorFlow Lite for on-device machine learning
- SQLite for local data storage
- Android Accessibility Service for interfacing with the Uber Driver app 