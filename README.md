# Konter a Matt

## Important Dates

1.  **22nd Oct**: Name, Project, and GitHub link (**5 Points**)

2.  **1st Dec**: First Deliverable with substantial content (**15 Points**)

3.  **20th Jan**: Final GitHub submission with all tasks completed

---

## Project Overview

### A) Project Description

The goal of this project is to develop a multiplayer, web-based adaptation of the Luxembourgish card game "Konter a Matt" in Java. Since I greatly enjoy playing it in real life and no online version currently exists, this project aims to bring the game to a broader audience by providing an accessible digital experience. Rules: https://en.wikipedia.org/wiki/Konter_a_Matt

---

## Topics to Cover:

### 1. **GIT**

I use and understand git for updating the code

### 2. **UML** ✅

The project includes three UML diagrams. I used https://online.visual-paradigm.com for the creation

- **Class Diagram**

![Class Diagram](https://i.imgur.com/0qZ1tZy.png)

- **Use Case Diagram**

![Use Case Diagram](https://i.imgur.com/aFHpX6t.png)

- **Activity Diagram**

![Activity Diagram](https://imgur.com/QITpP4p.png)

---

### 3. **REQUIREMENTS** ✅

- Trello [here](https://trello.com/b/Kjyi0f5f/konter-a-matt)
- Jira [here](https://claudereppert98.atlassian.net/jira/software/projects/MBA/boards/1)

### 4. **ANALYSIS** ✅
Initial Brainstorming without the use of AI can be found [here](https://github.com/ClaudeReppert/Konteramatt/blob/main/Analysis%20(no%20AI).pdf)

Final version refined with AI can be found [here](https://github.com/ClaudeReppert/Konteramatt/blob/main/Analysis%20(AI).pdf)
### 5. **DDD** ✅

- **A) Event Storming (Arranged)**

![Event Storming](https://i.imgur.com/kvTTo5U.png)

- **B) Core Domain**

![Core Domain](https://i.imgur.com/SG9c8nC.png)

- **C) Relations/Mappings**

![Event Storming](https://i.imgur.com/S3Qb498.png)

### 6. **METRICS**
- 
### 7. **CLEAN CODE DEVELOPMENT**

A)

B) CCD cheat sheet can be found [here](https://github.com/ClaudeReppert/Konteramatt/blob/main/CCD%20cheat%20sheet.pdf)

### 8. **REFACTORING** ✅

Example 1)

Inside the constructor of the Card class I initially used a hardcoded string for the Imagepath. However this approach has the downside of having to manually update this everywhere where its need if I would ever need to change the path to the cards images. By creating a constant for the image base path I can avoid all of this in the future.

![refactor1](https://i.imgur.com/Ya21YNd.png) ![refactor2](https://i.imgur.com/NxMbiVy.png)

Example 2) 

To prevent runtime errors if an image path is missing or incorrect, I added a simple IllegalArgumentException to handle the error gracefully.

![refactor1](https://i.imgur.com/T4iMCJ6.png) ![refactor2](https://i.imgur.com/9i5UWJL.png)

### 9. **BUILD** ✅
My project is built with Maven. The generated .war files are utilized by Apache Tomcat connected to the MySQL database

![Proof](https://i.imgur.com/PlDncku.png)
  
### 10. **CONTINUOUS DELIVERY** ✅

I used Jenkins to automate packaging my files from github with maven, deploying it to Apache Tomcat and restarting the Tomcat server for fast reloading. This worked like a charm and I didn't have to package, copy the .war file and restart the server manually.

```groovy
pipeline {
    agent any

    environment {
        WAR_FILE = "target/Kontermatt.war"
        TOMCAT_WEBAPPS_DIR = "/opt/tomcat/webapps"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ClaudeReppert/Konteramatt.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sh "sudo cp ${WAR_FILE} ${TOMCAT_WEBAPPS_DIR}"
            }
        }

        stage('Restart Tomcat') {
            steps {
                sh 'sudo systemctl restart tomcat'
            }
        }
    }

    post {
        success {
            echo 'Application deployed successfully to Tomcat!'
        }
        failure {
            echo 'Deployment failed. Please check the logs.'
        }
    }
}

```

### 11. **UNIT TESTS**
- 
### 12. **IDE** ✅
**IntelliJ Idea Community Edition**
#### Favorite Shortcuts:

- **`Double Shift`**: Search everywhere

- **`Ctrl + Space`**: Code Completion

- **`Ctrl + Alt + Shift + T`**: Refactor

### 13. **AI CODING**
**Github Copilot Plugin**

The installation was pretty straightforward. I went into the Intellij Plugin section, installed Copilot and logged in with my Github Account. After, I registered for the free trial. The ability to use AI directly inside my IDE was a gamechanger. Before, I always used ChatGPT inside the browser but now I use Copilot exclusively after adding it to my stack.

### 14. **FUNCTIONAL PROGRAMMING**
- 
