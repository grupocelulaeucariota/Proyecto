# Game Project

## Overview
This project is a simple game that involves a ball interacting with a surface. The game is designed to demonstrate basic object-oriented programming concepts in Java.

## Project Structure
```
GameProject
├── src
│   ├── main
│   │   ├── java
│   │   │   └── game
│   │   │       ├── Main.java
│   │   │       ├── Ball.java
│   │   │       └── Surface.java
│   │   └── resources
│   └── test
│       ├── java
│       └── resources
├── pom.xml
└── README.md
```

## Setup Instructions
1. **Clone the repository**: 
   ```
   git clone <repository-url>
   ```
2. **Navigate to the project directory**:
   ```
   cd GameProject
   ```
3. **Build the project**:
   ```
   mvn clean install
   ```
4. **Run the game**:
   ```
   mvn exec:java -Dexec.mainClass="game.Main"
   ```

## Classes
- **Main**: The entry point of the game that initializes game components.
- **Ball**: Represents the ball with properties like position and velocity, and includes methods for movement and collision detection.
- **Surface**: Represents the game area where the ball interacts, with methods for rendering and handling interactions.

## Dependencies
This project uses Maven for dependency management. Ensure you have Maven installed to build and run the project.

## License
This project is licensed under the MIT License - see the LICENSE file for details.