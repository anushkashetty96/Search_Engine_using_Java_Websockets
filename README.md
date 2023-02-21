# Project for the course - SOEN 6441 - Concordia university

# Requirements
1. Java Development Kit (JDK) 8 or higher
2. sbt (Scala Build Tool)
3. Play Framework 2.6 or higher
4. Git
5. GitHub API token

# Installation
1. Clone the repository to your local machine.
2. Create a new personal access token in your GitHub account. You can follow the instructions here.
3. Create a new file named .env in the root directory of the project.
4. Add the following line to the .env file: GITHUB_TOKEN=your_github_token_here.
5. Open a terminal or command prompt and navigate to the root directory of the project.
6. Run the command sbt run to start the Play Framework application.

# Usage
Once the Play Framework application is running, open a web browser and go to http://localhost:9000. You should see a search box on the page. Enter a keyword to search for repositories on GitHub, and click the "Search" button or press Enter.

As you type, the application will send a WebSocket message to the server with the current search term. The server will use the GitHub API to search for repositories matching the search term, and send the results back to the client via the WebSocket. The client will display the results in real-time as they are received.
