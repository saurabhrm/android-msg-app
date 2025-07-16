# Local WiFi Chat App

This Android application allows users to create and join chat groups over a local WiFi network. Users on the same WiFi network can discover groups, join them, and exchange messages without requiring an internet connection.

## Features

- Create chat groups that are discoverable on the local network
- Join existing groups created by other users
- Real-time messaging within groups
- Username customization
- Group discovery over WiFi using multicast
- No internet connection required - works on local networks only

## How It Works

1. **Network Discovery**: The app uses UDP multicast to announce and discover groups on the local network.
2. **Group Creation**: Any user can create a group which will be announced on the network.
3. **Messaging**: Messages are sent using UDP packets between devices.
4. **Local Only**: All communication happens only on the local WiFi network.

## Technical Implementation

- **UDP Multicast**: Used for group discovery and announcements
- **UDP Unicast**: Used for message exchange
- **No Central Server**: The app uses a peer-to-peer architecture
- **Group Host**: The creator of a group acts as the host

## Requirements

- Android 5.0 (API level 21) or higher
- Devices must be connected to the same WiFi network
- WiFi network must allow multicast packets

## Usage

1. Enter your username
2. Create a new group or join an existing one
3. Start chatting with other users on the same network

## Limitations

- The current implementation is basic and doesn't handle all edge cases
- No message persistence (messages are lost when the app is closed)
- Limited to devices on the same WiFi network
- No encryption for messages
- No user authentication

## Future Improvements

- Add message persistence using a local database
- Implement message encryption
- Add user authentication
- Support for file sharing
- Improve UI/UX
- Add group management features (kick users, change group name, etc.)
- Handle network disconnections gracefully

## Building and Installing the App

### Prerequisites

- Android Studio (latest version recommended)
- JDK 8 or higher
- An Android device or emulator with Android 5.0 (API level 21) or higher

### Building the App

1. **Clone or download the project** to your local machine
2. **Open Android Studio** and select "Open an existing Android Studio project"
3. **Navigate to the project directory** and click "OK"
4. **Wait for Gradle sync** to complete (Android Studio will download all necessary dependencies)
5. **Build the project** by selecting "Build > Make Project" from the menu or pressing Ctrl+F9 (Cmd+F9 on Mac)

### Creating an APK

1. In Android Studio, select **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Wait for the build process to complete
3. Click on the **"locate"** link in the notification that appears
4. The APK file will be located in **app/build/outputs/apk/debug/app-debug.apk**

### Installing on Your Device

#### Method 1: Using Android Studio

1. Connect your Android device to your computer via USB
2. Enable **USB debugging** on your device (Settings > Developer options > USB debugging)
3. In Android Studio, select **Run > Run 'app'** or click the green play button
4. Select your device from the list and click "OK"

#### Method 2: Manual Installation

1. Copy the APK file to your Android device (via USB, email, cloud storage, etc.)
2. On your device, navigate to the APK file using a file manager
3. Tap the APK file to start installation
4. You may need to enable **"Install from unknown sources"** in your device settings
5. Follow the on-screen instructions to complete installation

### Troubleshooting

- If you encounter build errors, try **"File > Invalidate Caches / Restart"** in Android Studio
- Make sure all required SDK components are installed via the SDK Manager
- Check that your device has developer options and USB debugging enabled
- Ensure your device is connected properly and recognized by your computer
