AlgoriSim

This is a repository for the implementation of a CPU Algorithm Scheduling Algorithm 
Simulator for a requirement in the course CMSC 125 - M.

DESCRIPTION
-----------
AlgoriSim is a Java-based scheduling algorithm simulator with a graphical user 
interface (GUI). It allows users to visualize and simulate various CPU scheduling 
algorithms, including:
- First Come, First Served (FCFS)
- Round Robin (RR)
- Shortest Job First (SJF) Preemptive and Non-Preemptive
- Priority Scheduling Preemptive and Non-Preemptive

SYSTEM REQUIREMENTS
------------------
- Java Development Kit (JDK): JDK 21 or later
- Operating System: Windows, macOS, or Linux
- IDE (Optional): Visual Studio Code or any Java-compatible IDE

INSTALLATION & SETUP
-------------------
1. Clone the Repository
   If using Git, clone the repository:
   git clone https://github.com/ishomayo/AlgoriSim.git

   Alternatively, download the source code and extract it to your preferred directory.

2. Navigate to the Project Directory
   cd AlgoriSim/AlgoriSim/src/simulator/resources

3. Open a terminal or command prompt in the project directory and run:
   javac -d bin -sourcepath src src/Main.java

   After compiling, execute the program:
   java -cp bin Main

FORMATTING THE FILE INPUT
------------------------
(For User-defined Input from a Text File)
The application expects input files to be formatted as follows:
- Each line contains three space-separated integers in the following order:
  * Arrival Time
  * Burst Time
  * Priority

Example File Format:
26 17 1  
27 3 14  
28 21 17  
19 20 19 

This will read as:
Arrival Time  Burst Time  Priority 
26            17          1  
27            3           14  
28            21          17  
19            20          19  

FEATURES
--------
- Graphical Interface: Intuitive UI using Java Swing.
- Scheduling Algorithms: Simulates FCFS, RR, SJF (Preemptive & Non-Preemptive), 
  Priority Scheduling (Preemptive & Non-Preemptive).
- Interactive Selection: Users can navigate between different screens to choose 
  and simulate algorithms.
