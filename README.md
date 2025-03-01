# Secure Notepad

This project was developed during my Erasmus exchange at Adam Mickiewicz University in Poznań for the **Mobile System Security** course. The main goal was to create a **highly secure notepad application**, ensuring that all text remains encrypted.

## Features
- **Encrypted Notes**: All notes are securely encrypted.
- **Password-based Encryption**: The encryption key is derived from the password set during the first launch.
- **Password Change Support**: When changing the password, the text is decrypted and re-encrypted with the new password.
- **Secure Storage**: The encryption key is never stored in plaintext.

## Technologies Used
- **Android Studio**: Development environment.
- **Java/Kotlin**: Programming language.
- **AES Encryption**: Secure encryption algorithm to protect note content.

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/secure-notepad.git
   ```
2. Open the project in **Android Studio**.
3. Build and run the application on an emulator or Android device.

## Usage
1. Launch the application.
2. Set a **secure password** during the first launch.
3. Create and save encrypted notes.
4. If you change the password, all notes will be decrypted and re-encrypted automatically.

## License
This project is open-source and available under the [MIT License](LICENSE).

## Contact
For any questions or suggestions, feel free to contact me at [your email or GitHub profile].

