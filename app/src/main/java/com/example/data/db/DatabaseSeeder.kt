package com.example.data.db

import com.example.data.model.AssignmentEntity
import com.example.data.model.CampusEventEntity
import com.example.data.model.CareerEntity
import com.example.data.model.CommunityGroupEntity
import com.example.data.model.CommunityPostEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.ExpenseGroupEntity
import com.example.data.model.LostFoundEntity
import com.example.data.model.MarketplaceEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.SettlementEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectNoteEntity
import com.example.data.model.UserEntity

object DatabaseSeeder {
    suspend fun seedInitialData(database: AppDatabase) {
        val userDao = database.userDao()
        if (userDao.getUser("user_default") != null) {
            return
        }
        val expenseDao = database.expenseDao()
        val studyDao = database.studyDao()
        val communityDao = database.communityDao()
        val servicesDao = database.servicesDao()


        userDao.insertUser(
            UserEntity(
                id = "user_default",
                name = "Alex Rivera",
                email = "alex.rivera@campus.edu",
                college = "National Institute of Technology",
                course = "B.Tech Computer Science & AI",
                year = "3rd Year (Junior)",
                semester = "Semester 6",
                gpa = 3.85,
                attendancePercent = 92,
                skills = "Kotlin, Jetpack Compose, Python, Machine Learning, UI/UX, System Design",
                avatarRes = "avatar_1",
                role = "Student",
                monthlyBudget = 8500.0
            )
        )


        val group1 = ExpenseGroupEntity(
            id = "grp_roommates",
            name = "Room 304 Flatmates",
            category = "Flat & Utilities",
            membersJson = "You,Rahul,Priya,Amit",
            totalExpense = 4800.0,
            createdDate = "Aug 2026"
        )
        val group2 = ExpenseGroupEntity(
            id = "grp_goa_trip",
            name = "Weekend Roadtrip",
            category = "Travel",
            membersJson = "You,Rahul,Sneha,Rohan",
            totalExpense = 9600.0,
            createdDate = "Jul 2026"
        )
        val group3 = ExpenseGroupEntity(
            id = "grp_project_team",
            name = "Hackathon Squad",
            category = "Food & Snacks",
            membersJson = "You,Ananya,Karan",
            totalExpense = 1450.0,
            createdDate = "Aug 2026"
        )
        expenseDao.insertGroup(group1)
        expenseDao.insertGroup(group2)
        expenseDao.insertGroup(group3)


        val expenses = listOf(
            ExpenseEntity(
                title = "Weekend Cafe & Pizza treat",
                amount = 1200.0,
                category = "Food",
                date = "2026-08-25",
                description = "4 large pizzas with garlic bread at Domino's",
                paidBy = "Rahul",
                groupId = "grp_roommates",
                groupName = "Room 304 Flatmates",
                splitType = "EQUAL",
                participantsJson = "You:300.0,Rahul:300.0,Priya:300.0,Amit:300.0",
                isSettled = false,
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            ExpenseEntity(
                title = "High-Speed WiFi Bill",
                amount = 1600.0,
                category = "Bills",
                date = "2026-08-22",
                description = "Airtel Xstream Fiber 200Mbps Monthly Plan",
                paidBy = "You",
                groupId = "grp_roommates",
                groupName = "Room 304 Flatmates",
                splitType = "EQUAL",
                participantsJson = "You:400.0,Rahul:400.0,Priya:400.0,Amit:400.0",
                isSettled = false,
                timestamp = System.currentTimeMillis() - 86400000L * 4
            ),
            ExpenseEntity(
                title = "Uber to Tech Expo",
                amount = 480.0,
                category = "Travel",
                date = "2026-08-24",
                description = "Cab to City Convention Center",
                paidBy = "You",
                groupId = "grp_project_team",
                groupName = "Hackathon Squad",
                splitType = "EQUAL",
                participantsJson = "You:160.0,Ananya:160.0,Karan:160.0",
                isSettled = false,
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            ExpenseEntity(
                title = "Operating Systems Reference Book",
                amount = 850.0,
                category = "Education",
                date = "2026-08-20",
                description = "Silberschatz OS Concepts 10th Edition",
                paidBy = "You",
                groupId = null,
                groupName = "Personal",
                splitType = "EQUAL",
                participantsJson = "You:850.0",
                isSettled = true,
                timestamp = System.currentTimeMillis() - 86400000L * 6
            ),
            ExpenseEntity(
                title = "Movie Night: Interstellar Re-release",
                amount = 900.0,
                category = "Entertainment",
                date = "2026-08-18",
                description = "IMAX Tickets for 3",
                paidBy = "Sneha",
                groupId = "grp_goa_trip",
                groupName = "Weekend Roadtrip",
                splitType = "EQUAL",
                participantsJson = "You:300.0,Sneha:300.0,Rohan:300.0",
                isSettled = false,
                timestamp = System.currentTimeMillis() - 86400000L * 8
            )
        )
        expenses.forEach { expenseDao.insertExpense(it) }


        val settlements = listOf(
            SettlementEntity(
                payerName = "Rahul",
                receiverName = "You",
                amount = 400.0,
                groupName = "Room 304 Flatmates",
                note = "WiFi Share",
                isCompleted = false,
                timestamp = System.currentTimeMillis() - 86400000L * 3
            ),
            SettlementEntity(
                payerName = "You",
                receiverName = "Rahul",
                amount = 300.0,
                groupName = "Room 304 Flatmates",
                note = "Domino's Pizza",
                isCompleted = false,
                timestamp = System.currentTimeMillis() - 86400000L * 1
            ),
            SettlementEntity(
                payerName = "Ananya",
                receiverName = "You",
                amount = 160.0,
                groupName = "Hackathon Squad",
                note = "Uber ride reimbursement",
                isCompleted = true,
                timestamp = System.currentTimeMillis() - 86400000L * 5
            )
        )
        settlements.forEach { expenseDao.insertSettlement(it) }


        val studyTasks = listOf(
            StudyTaskEntity(
                title = "Revise DBMS B+ Trees & Indexing",
                subject = "Database Management Systems",
                dueDate = "Today, 6:00 PM",
                priority = "High",
                isCompleted = false,
                estimatedMinutes = 45
            ),
            StudyTaskEntity(
                title = "Implement Dijkstra Algorithm in Kotlin",
                subject = "Design & Analysis of Algorithms",
                dueDate = "Tomorrow, 11:59 PM",
                priority = "High",
                isCompleted = false,
                estimatedMinutes = 60
            ),
            StudyTaskEntity(
                title = "Read Chapter 4: Neural Network Backprop",
                subject = "Deep Learning Fundamentals",
                dueDate = "28 Aug 2026",
                priority = "Medium",
                isCompleted = true,
                estimatedMinutes = 30
            ),
            StudyTaskEntity(
                title = "Prepare 5 slides for Cloud Architecture Seminar",
                subject = "Cloud Computing & DevOps",
                dueDate = "30 Aug 2026",
                priority = "Low",
                isCompleted = false,
                estimatedMinutes = 40
            )
        )
        studyTasks.forEach { studyDao.insertTask(it) }


        val exams = listOf(

            ExamEntity(
                subjectCode = "MATH101",
                subjectName = "Engineering Mathematics I",
                academicYear = "1st Year",
                semester = "Semester 1",
                date = "2026-09-01",
                time = "09:30 AM - 12:30 PM",
                venue = "Auditorium Hall 1",
                syllabus = "Differential Calculus, Matrices & Eigenvalues, Taylor Series, Multiple Integrals, Vector Calculus",
                daysRemaining = 6
            ),
            ExamEntity(
                subjectCode = "PHY102",
                subjectName = "Engineering Physics & Optics",
                academicYear = "1st Year",
                semester = "Semester 1",
                date = "2026-09-04",
                time = "09:30 AM - 12:30 PM",
                venue = "Block A Exam Hall 102",
                syllabus = "Interference & Diffraction, Laser & Fiber Optics, Quantum Mechanics, Schrodinger Equation, Superconductivity",
                daysRemaining = 9
            ),

            ExamEntity(
                subjectCode = "CS401",
                subjectName = "Data Structures & Algorithms",
                academicYear = "2nd Year",
                semester = "Semester 3",
                date = "2026-09-02",
                time = "09:30 AM - 12:30 PM",
                venue = "Exam Hall B1",
                syllabus = "AVL Trees, Red-Black Trees, Graph Traversals (BFS/DFS), Dijkstra, Dynamic Programming, Hashing",
                daysRemaining = 7
            ),
            ExamEntity(
                subjectCode = "CS402",
                subjectName = "Computer Organization & Architecture",
                academicYear = "2nd Year",
                semester = "Semester 3",
                date = "2026-09-06",
                time = "02:00 PM - 05:00 PM",
                venue = "Lecture Hall 2",
                syllabus = "Instruction Pipelining, Hazard Resolution, Cache Associativity, Virtual Memory, Booth's Algorithm",
                daysRemaining = 11
            ),

            ExamEntity(
                subjectCode = "CS601",
                subjectName = "Database Management Systems",
                academicYear = "3rd Year",
                semester = "Semester 5",
                date = "2026-09-02",
                time = "09:30 AM - 12:30 PM",
                venue = "Lecture Hall 4, Main Tech Block",
                syllabus = "Unit 1 to 5: ER Modeling, Relational Algebra, SQL, Normalization (1NF-BCNF), Transactions & Concurrency",
                daysRemaining = 7
            ),
            ExamEntity(
                subjectCode = "CS602",
                subjectName = "Design & Analysis of Algorithms",
                academicYear = "3rd Year",
                semester = "Semester 5",
                date = "2026-09-05",
                time = "09:30 AM - 12:30 PM",
                venue = "Exam Hall B2",
                syllabus = "Divide & Conquer, Dynamic Programming, Greedy, Graph Algorithms, NP-Completeness",
                daysRemaining = 10
            ),
            ExamEntity(
                subjectCode = "CS603",
                subjectName = "Machine Learning & Neural Networks",
                academicYear = "3rd Year",
                semester = "Semester 5",
                date = "2026-09-09",
                time = "02:00 PM - 05:00 PM",
                venue = "Lecture Hall 1",
                syllabus = "Linear Models, Decision Trees, SVM, CNN, RNN, Transformers architecture basics",
                daysRemaining = 14
            ),

            ExamEntity(
                subjectCode = "CS801",
                subjectName = "Cloud Computing & DevOps",
                academicYear = "4th Year",
                semester = "Semester 7",
                date = "2026-09-03",
                time = "09:30 AM - 12:30 PM",
                venue = "Tech Block C, Seminar Hall 3",
                syllabus = "Cloud Architecture (IaaS/PaaS/SaaS), Kubernetes Cluster Orchestration, Docker Containers, CI/CD, Serverless",
                daysRemaining = 8
            ),
            ExamEntity(
                subjectCode = "CS802",
                subjectName = "Information & Cyber Security",
                academicYear = "4th Year",
                semester = "Semester 7",
                date = "2026-09-07",
                time = "02:00 PM - 05:00 PM",
                venue = "Block D Hall 4",
                syllabus = "Cryptography (AES/RSA/ECC), Key Exchange, Digital Signatures, Buffer Overflow, Penetration Testing & OWASP Top 10",
                daysRemaining = 12
            )
        )
        exams.forEach { studyDao.insertExam(it) }


        val notes = listOf(

            SubjectNoteEntity(
                subject = "Engg Mathematics",
                title = "Calculus, Matrices & Linear Algebra PYQs + Formula Sheet",
                chapter = "Unit 1 & 2 - Matrices & Eigenvalues",
                academicYear = "1st Year",
                semester = "Semester 1",
                fileSize = "3.2 MB",
                fileType = "PDF",
                summary = "Full step-by-step solved previous 5-year university exam questions with quick eigenvalue tricks and Cayley-Hamilton proofs.",
                tags = "PYQ, Formula Sheet, 1st Year Core",
                downloadsCount = 740
            ),
            SubjectNoteEntity(
                subject = "Engineering Physics",
                title = "Wave Optics, Quantum Mechanics & Lasers Handwritten Guide",
                chapter = "Unit 3 - Lasers & Fiber Optics",
                academicYear = "1st Year",
                semester = "Semester 1",
                fileSize = "2.8 MB",
                fileType = "PDF",
                summary = "Crisp diagrammatic explanations for Michelson Interferometer, Ruby Laser, Population Inversion and Numerical Aperture derivations.",
                tags = "Handwritten, Ray Diagrams, High Yield",
                downloadsCount = 520
            ),
            SubjectNoteEntity(
                subject = "Basic Electrical (BEE)",
                title = "DC Circuit Theorems & AC Fundamentals Master Notes",
                chapter = "Unit 1 - Circuit Analysis",
                academicYear = "1st Year",
                semester = "Semester 1",
                fileSize = "2.5 MB",
                fileType = "PDF",
                summary = "Comprehensive guide covering Thevenin's, Norton's, Superposition & Maximum Power Transfer theorem with 30+ solved numericals.",
                tags = "Solved Numericals, BEE, 1st Year",
                downloadsCount = 610
            ),
            SubjectNoteEntity(
                subject = "Programming in C",
                title = "Pointers, Dynamic Memory & Data Structures in C",
                chapter = "Unit 4 - Pointers & File I/O",
                academicYear = "1st Year",
                semester = "Semester 2",
                fileSize = "1.9 MB",
                fileType = "PDF",
                summary = "Complete pointer arithmetic, malloc/calloc/free lifecycle, structure padding, and linked list implementation with dry-run diagrams.",
                tags = "C Programming, Code Snippets, Labs",
                downloadsCount = 890
            ),


            SubjectNoteEntity(
                subject = "Data Structures (DSA)",
                title = "Trees, Graphs & Dynamic Programming Visual Handbook",
                chapter = "Unit 3 - Non-Linear Data Structures",
                academicYear = "2nd Year",
                semester = "Semester 3",
                fileSize = "4.5 MB",
                fileType = "PDF",
                summary = "Binary Search Trees, AVL balance rotations, Segment Trees, Topological Sort, and Dijkstra algorithm with complete C++/Java implementations.",
                tags = "DSA, Placement Prep, Visual Diagrams",
                downloadsCount = 980
            ),
            SubjectNoteEntity(
                subject = "OOPs & Java",
                title = "Object-Oriented Design Principles & Java Collections",
                chapter = "Unit 2 - Polymorphism & Collections",
                academicYear = "2nd Year",
                semester = "Semester 3",
                fileSize = "2.6 MB",
                fileType = "PDF",
                summary = "Deep dive into OOP concepts (Encapsulation, Inheritance, Abstraction, Polymorphism), JVM internal architecture, and multithreading.",
                tags = "Java, OOPs, Interview Qs",
                downloadsCount = 670
            ),
            SubjectNoteEntity(
                subject = "Computer Organization",
                title = "Pipelining, Cache Mapping & MIPS Architecture Notes",
                chapter = "Unit 4 - Memory Hierarchy",
                academicYear = "2nd Year",
                semester = "Semester 4",
                fileSize = "3.1 MB",
                fileType = "PDF",
                summary = "Pipeline hazard management (Data, Structural, Control hazards), Direct vs Set-Associative Cache calculations, and Virtual Memory page tables.",
                tags = "COA, Solved Problems, 2nd Year",
                downloadsCount = 430
            ),
            SubjectNoteEntity(
                subject = "Discrete Mathematics",
                title = "Combinatorics, Graph Theory & Recurrence Relations",
                chapter = "Unit 2 - Generating Functions & Relations",
                academicYear = "2nd Year",
                semester = "Semester 4",
                fileSize = "2.2 MB",
                fileType = "PDF",
                summary = "Master theorem cheat sheet, Euler & Hamiltonian graph properties, Pigeonhole principle with 40+ solved university exam questions.",
                tags = "Maths, CheatSheet, 2nd Year",
                downloadsCount = 380
            ),


            SubjectNoteEntity(
                subject = "DBMS",
                title = "Complete Normalization Master Guide (1NF to 5NF)",
                chapter = "Chapter 3 - Functional Dependencies",
                academicYear = "3rd Year",
                semester = "Semester 5",
                fileSize = "3.8 MB",
                fileType = "PDF",
                summary = "Concise handwritten summaries with step-by-step canonical decomposition examples, BCNF lossless join proofs, and PYQ solutions.",
                tags = "PYQ, Formula Sheet, High Yield",
                downloadsCount = 850
            ),
            SubjectNoteEntity(
                subject = "Algorithms (DAA)",
                title = "Dynamic Programming Patterns & Cheat Sheet",
                chapter = "Unit 4 - Advanced DP",
                academicYear = "3rd Year",
                semester = "Semester 5",
                fileSize = "4.2 MB",
                fileType = "PDF",
                summary = "14 core DP patterns: 0/1 Knapsack, LCS, LIS, Matrix Chain Multiplication, Interval DP with visual state-transition matrices.",
                tags = "CheatSheet, Placement Prep",
                downloadsCount = 1120
            ),
            SubjectNoteEntity(
                subject = "Computer Networks",
                title = "OSI vs TCP/IP Protocol Stack & Subnetting Guide",
                chapter = "Unit 2 - Network Layer",
                academicYear = "3rd Year",
                semester = "Semester 6",
                fileSize = "2.1 MB",
                fileType = "PDF",
                summary = "CIDR calculations, Routing protocols (OSPF, BGP, RIP), TCP 3-way handshake diagrams, and congestion control (Tahoe/Reno).",
                tags = "Diagrams, Quick Revision",
                downloadsCount = 610
            ),
            SubjectNoteEntity(
                subject = "Machine Learning",
                title = "Supervised & Deep Neural Network Cheat Sheet",
                chapter = "Unit 3 - Neural Networks & Backprop",
                academicYear = "3rd Year",
                semester = "Semester 6",
                fileSize = "3.4 MB",
                fileType = "PDF",
                summary = "Mathematical intuition of Gradient Descent, Cross-Entropy Loss, Convolutional Layers, Attention Mechanism and Transformer Architecture.",
                tags = "AI/ML, Math Derivations, 3rd Year",
                downloadsCount = 780
            ),


            SubjectNoteEntity(
                subject = "Cloud & DevOps",
                title = "Kubernetes, Docker & Cloud Native Architecture Manual",
                chapter = "Unit 2 - Container Orchestration",
                academicYear = "4th Year",
                semester = "Semester 7",
                fileSize = "3.9 MB",
                fileType = "PDF",
                summary = "Production Kubernetes architecture: Pods, Services, Ingress, Persistent Volumes, Helm charts, and automated GitHub Actions CI/CD pipelines.",
                tags = "Cloud Native, DevOps, Industry Ready",
                downloadsCount = 690
            ),
            SubjectNoteEntity(
                subject = "System Design",
                title = "Scalable High-Level (HLD) & Low-Level Design (LLD)",
                chapter = "Unit 1 - Scalability Fundamentals",
                academicYear = "4th Year",
                semester = "Semester 7",
                fileSize = "5.1 MB",
                fileType = "PDF",
                summary = "Distributed caching (Redis), Database Sharding, Consistent Hashing, CAP Theorem, Rate Limiters, and designing Uber/WhatsApp systems.",
                tags = "System Design, Tech Interviews, 4th Year",
                downloadsCount = 1240
            ),
            SubjectNoteEntity(
                subject = "Cyber Security",
                title = "Applied Cryptography, Penetration Testing & OWASP Top 10",
                chapter = "Unit 3 - Public Key Cryptography",
                academicYear = "4th Year",
                semester = "Semester 8",
                fileSize = "2.7 MB",
                fileType = "PDF",
                summary = "RSA mathematical proof, Diffie-Hellman Key Exchange, SQL Injection / XSS mitigations, and network packet sniffing with Wireshark.",
                tags = "Security, Cryptography, 4th Year",
                downloadsCount = 490
            )
        )
        notes.forEach { studyDao.insertNote(it) }


        val assignments = listOf(
            AssignmentEntity(
                title = "Lab 4: Transaction Serializability Checker",
                subject = "Database Management Systems",
                deadline = "Tomorrow, 05:00 PM",
                status = "PENDING",
                maxMarks = 25,
                instructions = "Write a program in Python/Kotlin to test conflict serializability using precedence graph cycle detection.",
                daysLeft = 1
            ),
            AssignmentEntity(
                title = "Mini Project: Bellman-Ford Routing Simulator",
                subject = "Computer Networks",
                deadline = "03 Sep 2026",
                status = "PENDING",
                maxMarks = 50,
                instructions = "Implement dynamic distance vector algorithm with split-horizon count-to-infinity mitigation.",
                daysLeft = 8
            ),
            AssignmentEntity(
                title = "Assignment 2: Kernel Memory Management Report",
                subject = "Operating Systems",
                deadline = "20 Aug 2026",
                status = "GRADED",
                maxMarks = 20,
                score = 19,
                instructions = "Analyze buddy allocator vs slab allocation mechanisms in Linux kernel 6.x.",
                daysLeft = 0
            )
        )
        assignments.forEach { studyDao.insertAssignment(it) }


        val quizQuestions = listOf(
            QuizQuestionEntity(
                subject = "DBMS",
                topic = "Normalization",
                difficulty = "Easy",
                question = "Which normal form eliminates multi-valued dependency?",
                optionA = "1NF",
                optionB = "2NF",
                optionC = "3NF",
                optionD = "4NF",
                correctIndex = 3,
                explanation = "4NF (Fourth Normal Form) specifically resolves non-trivial multi-valued dependencies that are not functional dependencies."
            ),
            QuizQuestionEntity(
                subject = "Algorithms",
                topic = "Time Complexity",
                difficulty = "Medium",
                question = "What is the worst-case time complexity of QuickSelect to find the k-th smallest element?",
                optionA = "O(log N)",
                optionB = "O(N)",
                optionC = "O(N log N)",
                optionD = "O(N^2)",
                correctIndex = 3,
                explanation = "While QuickSelect has an average time complexity of O(N), its worst-case is O(N^2) with poor pivot selections."
            ),
            QuizQuestionEntity(
                subject = "Operating Systems",
                topic = "Deadlocks",
                difficulty = "Hard",
                question = "In Banker's algorithm, what does a 'safe state' guarantee?",
                optionA = "Zero resource utilization",
                optionB = "No process will ever be blocked",
                optionC = "There exists an execution sequence preventing deadlock",
                optionD = "Preemption is never required",
                correctIndex = 2,
                explanation = "A safe state guarantees that there exists at least one safe sequence of resource allocation such that every process can finish without deadlock."
            ),
            QuizQuestionEntity(
                subject = "Computer Networks",
                topic = "Transport Layer",
                difficulty = "Easy",
                question = "Which header field in TCP provides sequence synchronization during handshake?",
                optionA = "ACK flag",
                optionB = "SYN flag & ISN",
                optionC = "Checksum",
                optionD = "Window Size",
                correctIndex = 1,
                explanation = "The SYN flag along with the Initial Sequence Number (ISN) establishes synchronized sequence numbers between sender and receiver."
            )
        )
        studyDao.insertQuizQuestions(quizQuestions)


        val groups = listOf(
            CommunityGroupEntity("grp_coding", "Coding & Open Source Club", "Technical", 1240, "DSA, Hackathons, Web3, ML & Android devs collaboration.", true),
            CommunityGroupEntity("grp_photography", "Campus Shutterbugs", "Arts & Culture", 460, "Weekly photo walks, camera gear discussions and exhibitions.", false),
            CommunityGroupEntity("grp_placement", "Placement & Internship Prep 2027", "Career", 2150, "Resume reviews, mock interview pairings, company test experiences.", true),
            CommunityGroupEntity("grp_gaming", "Esports & Gaming Guild", "Recreation", 890, "Valorant, BGMI, Chess, FIFA tournaments and LAN parties.", false)
        )
        groups.forEach { communityDao.insertGroup(it) }

        val posts = listOf(
            CommunityPostEntity(
                authorName = "Devansh Sharma",
                authorRole = "Lead Organizer, Coding Club",
                title = "🚀 HackSprint 2026 Registrations Open! ($5,000 Prize Pool)",
                content = "Calling all developers, designers and creators! 36-hour offline campus hackathon on Sept 14-15. Tracks: AI/ML, FinTech, Green Tech & Open Innovation. Mentors from top tech firms will be onsite.",
                groupTag = "Coding & Open Source Club",
                likesCount = 54,
                commentsCount = 18,
                timeAgo = "1 hour ago"
            ),
            CommunityPostEntity(
                authorName = "Pooja Hegde",
                authorRole = "Final Year CSE",
                title = "💡 Google SWE Summer Internship Interview Experience + Tips",
                content = "Just finished my Google onsite rounds! Focused heavily on Tree/Graph DP and System Design trade-offs. Dropping a full breakdown of the questions and preparation roadmap in the thread.",
                groupTag = "Placement & Internship Prep 2027",
                likesCount = 128,
                commentsCount = 42,
                timeAgo = "3 hours ago"
            ),
            CommunityPostEntity(
                authorName = "Prof. Arvind Kumar",
                authorRole = "HOD Computer Science",
                title = "📢 Notice: Mid-Semester Exam Schedule & Hall Ticket Issuance",
                content = "Mid-sem examinations commence from Sept 2nd. Students can download digital hall tickets from the student portal starting Friday. Attendance minimum is strictly 75%.",
                groupTag = "General",
                likesCount = 89,
                commentsCount = 12,
                timeAgo = "Yesterday"
            )
        )
        posts.forEach { communityDao.insertPost(it) }


        val events = listOf(
            CampusEventEntity(
                title = "Tech Summit: Generative AI on the Edge",
                clubName = "Google Developer Student Club",
                date = "Tomorrow, Aug 27",
                time = "04:30 PM - 06:30 PM",
                location = "Audi 1, Tech Tower",
                category = "Technical",
                attendeesCount = 180,
                isRegistered = true,
                description = "Hands-on session building on-device Gemini Nano apps on Android with Jetpack Compose."
            ),
            CampusEventEntity(
                title = "Inter-College Robotics Expo & BattleBots",
                clubName = "Robotics & Automation Society",
                date = "Friday, Aug 29",
                time = "10:00 AM - 04:00 PM",
                location = "Sports Complex Indoor Arena",
                category = "Competition",
                attendeesCount = 340,
                isRegistered = false,
                description = "Watch high-torque custom combat bots fight in bulletproof arenas with obstacle courses."
            )
        )
        events.forEach { communityDao.insertEvent(it) }


        val lostItems = listOf(
            LostFoundEntity(
                type = "LOST",
                itemName = "Navy Blue Sony WH-1000XM4 Headphones",
                category = "Electronics",
                description = "Left on 2nd floor library study desk near window cubicle 14 around 4 PM.",
                location = "Central Library 2nd Floor",
                date = "25 Aug 2026",
                contactInfo = "Campus ID: 22BCS044 (Verified via SafeChat)"
            ),
            LostFoundEntity(
                type = "FOUND",
                itemName = "Black Leather Wallet with Student ID",
                category = "Personal Item",
                description = "Found near the Canteen juice counter. Contains ID card of Rohan Verma and Metro Pass.",
                location = "Student Canteen Block A",
                date = "26 Aug 2026",
                contactInfo = "Submitted at Security Desk #2 (or Ping Poster)"
            ),
            LostFoundEntity(
                type = "LOST",
                itemName = "TI-84 Plus CE Graphing Calculator",
                category = "Study Gear",
                description = "White casing with small NASA sticker on the back cover.",
                location = "Maths Lab Room 208",
                date = "24 Aug 2026",
                contactInfo = "Verified Student (SafeConnect)"
            )
        )
        lostItems.forEach { servicesDao.insertLostFound(it) }


        val marketplaceItems = listOf(
            MarketplaceEntity(
                title = "Hero Sprint Pro 21-Speed Gear Cycle",
                price = 3400.0,
                category = "Cycles",
                condition = "Good",
                location = "Hostel Block 4 Bicycle Stand",
                description = "Dual disc brakes, front suspension, barely 1 year old. Free helmet and number lock included.",
                sellerName = "Kunal Verma",
                sellerContact = "Scholario Safe Connect"
            ),
            MarketplaceEntity(
                title = "Casio FX-991EX Classwiz Scientific Calculator",
                price = 750.0,
                category = "Calculators",
                condition = "Like New",
                location = "Campus North Gate",
                description = "High-resolution display, 552 functions. Original box and warranty slip available.",
                sellerName = "Sneha Patel",
                sellerContact = "Scholario Safe Connect"
            ),
            MarketplaceEntity(
                title = "Introduction to Algorithms (CLRS) 4th Edition",
                price = 900.0,
                category = "Books",
                condition = "Like New",
                location = "Hostel Block 2",
                description = "No pen marks or highlights. Pristine condition hardcover.",
                sellerName = "Arjun Mehta",
                sellerContact = "Scholario Safe Connect"
            ),
            MarketplaceEntity(
                title = "Ergonomic Mesh Study Chair with Lumbar Support",
                price = 1800.0,
                category = "Furniture",
                condition = "Good",
                location = "Off-Campus Flat 102",
                description = "Adjustable armrests, smooth swivel castors, perfect for long study sessions.",
                sellerName = "Tanmay Roy",
                sellerContact = "Scholario Safe Connect"
            )
        )
        marketplaceItems.forEach { servicesDao.insertMarketplaceItem(it) }


        val careers = listOf(
            CareerEntity(
                type = "INTERNSHIP",
                title = "Android & Kotlin Developer Intern",
                companyOrRole = "Swiggy / FinTech Labs",
                stipendOrSalary = "$1,200 / month ($45,000 INR)",
                location = "Bangalore / Hybrid",
                deadline = "10 Sep 2026",
                tags = "Kotlin, Jetpack Compose, Coroutines, MVVM",
                description = "Work alongside senior mobile architects to build high-performance user interfaces with Compose and offline-first Room architectures."
            ),
            CareerEntity(
                type = "INTERNSHIP",
                title = "AI / ML Research Intern (LLM Agents)",
                companyOrRole = "Google DeepMind Academic Partner",
                stipendOrSalary = "$1,800 / month ($75,000 INR)",
                location = "Remote",
                deadline = "15 Sep 2026",
                tags = "Python, PyTorch, Gemini API, RAG, LangChain",
                description = "Develop multimodal intelligent agent workflows, tool-use evaluation benchmarks, and fine-tuning pipelines."
            ),
            CareerEntity(
                type = "JOB",
                title = "Associate Software Engineer (Batch 2027)",
                companyOrRole = "Microsoft Campus Hiring",
                stipendOrSalary = "$24,000 / year (22 LPA CTC)",
                location = "Hyderabad / Noida",
                deadline = "30 Sep 2026",
                tags = "Data Structures, System Design, Algorithms, C++/Java",
                description = "Full-time campus placement role for pre-final/final year students across Azure, Office 365, and Core Search."
            ),
            CareerEntity(
                type = "ROADMAP",
                title = "Full-Stack AI Engineer Roadmap 2026-2027",
                companyOrRole = "Scholario Career Academy",
                stipendOrSalary = "Curated Learning Path",
                location = "Self-Paced (12 Weeks)",
                tags = "Python -> PyTorch -> Gemini -> FastApi -> Docker -> Next.js / Compose",
                description = "A comprehensive structured semester guide to mastering foundation models, vector embeddings, fine-tuning, and production deployment."
            )
        )
        careers.forEach { servicesDao.insertCareerItem(it) }


        val notifications = listOf(
            NotificationItemEntity(
                title = "Splitzy Expense Settlement Alert",
                message = "Rahul marked 'Weekend Cafe & Pizza' expense. Your share: $300.00.",
                category = "SPLITZY",
                timeAgo = "10 mins ago"
            ),
            NotificationItemEntity(
                title = "Upcoming Exam in 7 Days",
                message = "Database Management Systems (CS601) exam on Sept 2nd, 09:30 AM in Hall 4.",
                category = "EXAM",
                timeAgo = "2 hours ago"
            ),
            NotificationItemEntity(
                title = "Assignment Deadline Approaching",
                message = "Lab 4: Transaction Serializability Checker due tomorrow at 5:00 PM.",
                category = "ASSIGNMENT",
                timeAgo = "4 hours ago"
            ),
            NotificationItemEntity(
                title = "New Event Registration Confirmed",
                message = "You're registered for 'Generative AI on the Edge' tomorrow at 4:30 PM.",
                category = "COMMUNITY",
                timeAgo = "Yesterday"
            )
        )
        notifications.forEach { servicesDao.insertNotification(it) }


        val activityDao = database.activityDao()
        val activities = listOf(
            com.example.data.model.ActivityEntity(
                iconName = "quiz",
                title = "Quiz Mastered: DBMS Normalization",
                description = "Scored 90% (9/10) on Intermediate DBMS Quiz.",
                category = "Study",
                xpEarned = 50,
                timeAgo = "15m ago"
            ),
            com.example.data.model.ActivityEntity(
                iconName = "account_balance_wallet",
                title = "Splitzy Expense Added",
                description = "Added ₹1,200 for 'Weekend Cafe & Pizza treat' split with 4 people.",
                category = "Money",
                xpEarned = 20,
                timeAgo = "2h ago"
            ),
            com.example.data.model.ActivityEntity(
                iconName = "check_circle",
                title = "Assignment Submitted",
                description = "Turned in Operating Systems Virtual Memory Simulator assignment.",
                category = "Study",
                xpEarned = 40,
                timeAgo = "5h ago"
            ),
            com.example.data.model.ActivityEntity(
                iconName = "forum",
                title = "Community Discussion",
                description = "Posted a query in DSA Study Group with 14 student upvotes.",
                category = "Community",
                xpEarned = 15,
                timeAgo = "Yesterday"
            ),
            com.example.data.model.ActivityEntity(
                iconName = "emoji_events",
                title = "Unlocked Badge: 7-Day Study Streak",
                description = "Achieved consecutive 7-day Pomodoro and study task check-ins!",
                category = "Achievements",
                xpEarned = 100,
                timeAgo = "Yesterday"
            ),
            com.example.data.model.ActivityEntity(
                iconName = "shopping_bag",
                title = "Marketplace Item Listed",
                description = "Listed 'Casio fx-991EX Scientific Calculator' for ₹750.",
                category = "Campus",
                xpEarned = 25,
                timeAgo = "2 days ago"
            ),
            com.example.data.model.ActivityEntity(
                iconName = "work",
                title = "Career Resume Updated",
                description = "Exported ATS-optimized tech resume for Google STEP internship.",
                category = "Career",
                xpEarned = 30,
                timeAgo = "3 days ago"
            )
        )
        activities.forEach { activityDao.insertActivity(it) }


        val gamificationDao = database.gamificationDao()
        val badges = listOf(
            com.example.data.model.BadgeEntity(
                id = "badge_first_quiz",
                title = "First Quiz",
                description = "Completed your very first interactive subject quiz",
                icon = "🏆",
                category = "Quiz",
                isUnlocked = true,
                unlockedDate = "Aug 20",
                xpValue = 50
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_streak_7",
                title = "7-Day Study Streak",
                description = "Completed study tasks 7 days in a row without breaking",
                icon = "🔥",
                category = "Study",
                isUnlocked = true,
                unlockedDate = "Aug 25",
                xpValue = 100
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_smart_saver",
                title = "Smart Saver",
                description = "Maintained monthly expenses under 85% of total budget",
                icon = "💰",
                category = "Money",
                isUnlocked = true,
                unlockedDate = "Aug 22",
                xpValue = 75
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_study_master",
                title = "Study Master",
                description = "Accumulated over 25 hours of focused Pomodoro study time",
                icon = "📚",
                category = "Study",
                isUnlocked = false,
                xpValue = 120
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_community_helper",
                title = "Community Helper",
                description = "Uploaded 5+ verified study notes and answered peer questions",
                icon = "👥",
                category = "Community",
                isUnlocked = true,
                unlockedDate = "Aug 18",
                xpValue = 80
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_goal_crusher",
                title = "Goal Crusher",
                description = "Completed all weekly academic targets before deadlines",
                icon = "🎯",
                category = "General",
                isUnlocked = false,
                xpValue = 90
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_tech_prodigy",
                title = "Tech Prodigy",
                description = "Built an ATS resume and practiced 5 AI technical interviews",
                icon = "⚡",
                category = "Career",
                isUnlocked = true,
                unlockedDate = "Aug 24",
                xpValue = 110
            ),
            com.example.data.model.BadgeEntity(
                id = "badge_campus_guardian",
                title = "Campus Guardian",
                description = "Returned a lost campus item or reported spam listings safely",
                icon = "🛡️",
                category = "Campus",
                isUnlocked = false,
                xpValue = 60
            )
        )
        gamificationDao.insertBadges(badges)


        val quizHistory = listOf(
            com.example.data.model.QuizHistoryEntity(
                subject = "Database Systems",
                topic = "BCNF & 3NF Normalization",
                score = 9,
                totalQuestions = 10,
                percentage = 90,
                difficulty = "Medium",
                xpEarned = 45,
                date = "26 Aug 2026"
            ),
            com.example.data.model.QuizHistoryEntity(
                subject = "Operating Systems",
                topic = "Deadlocks & Semaphores",
                score = 8,
                totalQuestions = 10,
                percentage = 80,
                difficulty = "Hard",
                xpEarned = 50,
                date = "24 Aug 2026"
            ),
            com.example.data.model.QuizHistoryEntity(
                subject = "Data Structures",
                topic = "Red-Black Trees & Graphs",
                score = 10,
                totalQuestions = 10,
                percentage = 100,
                difficulty = "Medium",
                xpEarned = 60,
                date = "22 Aug 2026"
            )
        )
        quizHistory.forEach { gamificationDao.insertQuizHistory(it) }


        val resumeDao = database.resumeDao()
        resumeDao.saveResume(com.example.data.model.ResumeEntity())


        val grievanceDao = database.grievanceDao()
        val grievances = listOf(
            com.example.data.model.CampusGrievanceEntity(
                title = "Water Cooler Dirty & Algae at 2nd Floor Block-B",
                category = "Water & Sanitation",
                location = "Block B - 2nd Floor, Near CS Lab 4",
                description = "The water cooler dispensing area is contaminated with visible algae and mud residue. Water smells foul. Urgent filter replacement & tank cleaning required.",
                photoEmoji = "🚰",
                photoTag = "Contaminated Water Cooler & Leaking Tap",
                reportedByName = "Alex Rivera",
                reportedByRoll = "STU-2024-8842",
                reportedByPhone = "+91 9876543210",
                department = "Computer Science & Engineering",
                priority = "URGENT",
                status = "PENDING",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3
            ),
            com.example.data.model.CampusGrievanceEntity(
                title = "Ceiling Projector HDMI Broken in Seminar Hall 1",
                category = "Classroom & Furniture",
                location = "Main Academic Wing - Seminar Hall 1",
                description = "Projector flickering with severe green tint during guest lectures. Port cable socket is damaged.",
                photoEmoji = "📽️",
                photoTag = "Damaged HDMI Display Port",
                reportedByName = "Rohan Sharma",
                reportedByRoll = "STU-2024-3319",
                reportedByPhone = "+91 9811223344",
                department = "Electronics & Communication",
                priority = "HIGH",
                status = "IN_PROGRESS",
                hodRemarks = "Technician dispatched from IT AV Cell. Replacement HDMI cord ordered.",
                resolvedByHodName = "Dr. K. Verma (HOD ECE)",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24
            ),
            com.example.data.model.CampusGrievanceEntity(
                title = "Lab 2 AC Compressor Not Cooling / Overheating",
                category = "Electricity & AC",
                location = "Turing Tech Building - AI & Robotics Lab 2",
                description = "AC units 3 and 4 blowing warm air. High temperature causing server racks in lab to overheat.",
                photoEmoji = "❄️",
                photoTag = "HVAC Air Flow Error & Heat Alarm",
                reportedByName = "Priya Nair",
                reportedByRoll = "STU-2024-5512",
                reportedByPhone = "+91 9988776655",
                department = "Computer Science & Engineering",
                priority = "HIGH",
                status = "RESOLVED",
                hodRemarks = "AC service engineer cleaned condenser coils and refilled gas. Temperature normal at 21°C.",
                resolvedByHodName = "Dr. R. Sharma (HOD CSE)",
                resolvedDate = "Yesterday, 4:30 PM",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48
            )
        )
        grievances.forEach { grievanceDao.insertGrievance(it) }
    }
}

