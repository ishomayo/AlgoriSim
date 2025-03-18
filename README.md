<p align="center">
  <img src="icon.png" alt="Logo" width="150">
</p>

# AlgoriSim
This is a repository for the implementation of a CPU Algorithm Scheduling Algorithm Simulator for a requirement in the course CMSC 125 - M.

# Description
AlgoriSim is a Java-based scheduling algorithm simulator with a graphical user interface (GUI). It allows users to visualize and simulate various CPU scheduling algorithms, including:

* First Come, First Served (FCFS)
* Round Robin (RR)
* Shortest Job First (SJF) Preemptive and Non-Preemptive
* Priority Scheduling Preemptive and Non-Preemptive

# System Requirements
* Java Development Kit (JDK): JDK 21 or later
* Operating System: Windows, macOS, or Linux
* IDE (Optional): Visual Studio Code or any Java-compatible IDE

# Installation & Setup
## 1. Clone the Repository
If using Git, clone the repository:<br/>
```
git clone https://github.com/ishomayo/AlgoriSim.git
```
Alternatively, download the source code and extract it to your preferred directory.
## 2. Navigate to the Project Directory
```
cd AlgoriSim/AlgoriSim/src/simulator/resources
```
## 3. Open a terminal or command prompt in the project directory and run:
```
javac -d bin -sourcepath src src/Main.java
```
<br/> After compiling, execute the program: <br/>
```
java -cp bin Main
```
# Formatting the File Input 
## (For User-defined Input from a Text File)
The application expects input files to be formatted as follows:
* Each line contains three space-separated integers in the following order:
  - Arrival Time
  - Burst Time
  - Priority

### Example File Format
```
26 17 1  
27 3 14  
28 21 17  
19 20 19 
```
This will read as: <br/>
```
Arrival Time  Burst Time  Priority 
26            17          1  
27            3           14  
28            21          17  
19            20          19  
```
**Note: A sample .txt file is given as example named "sample.txt"**__
# Features
* **Graphical Interface:** Intuitive UI using Java Swing.
* **Scheduling Algorithms:** Simulates FCFS, RR, SJF (Preemptive & Non-Preemptive), Priority Scheduling (Preemptive & Non-Preemptive).
* **Interactive Selection:** Users can navigate between different screens to choose and simulate algorithms.

# Snapshots
* Lobby <br/><br/>![image](https://github.com/user-attachments/assets/8442e265-598e-456c-be96-61329451ec2b)<br/><br/>
* Data Input Method Selection Screen <br/><br/>![image](https://github.com/user-attachments/assets/4e6c9db8-b324-4ab1-af5b-4fc81fac07c2)<br/><br/>
* Random Data Generator Screen <br/><br/>![image](https://github.com/user-attachments/assets/4f6a1fc6-f65f-4bbc-99cc-2293158e61ac)<br/><br/>
* Algorithm Selection Screen <br/><br/>![image](https://github.com/user-attachments/assets/36859bd3-be93-4c8f-b929-e7ab692ae31d)<br/><br/>
* SJF Preemptive Screen <br/><br/>![image](https://github.com/user-attachments/assets/0f06f3bd-1087-4326-935d-d848a0da96de)<br/>






