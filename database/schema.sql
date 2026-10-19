-- =====================================================================
-- Career Recommendation System for B.Tech CSE Students
-- MySQL Database Setup Script
-- Database Name: career_recommendation_system
-- =====================================================================

DROP DATABASE IF EXISTS career_recommendation_system;
CREATE DATABASE career_recommendation_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE career_recommendation_system;

-- ---------------------------------------------------------------------
-- 1. Students Table
-- ---------------------------------------------------------------------
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    college_name VARCHAR(200) NOT NULL,
    degree VARCHAR(100) NOT NULL DEFAULT 'B.Tech CSE',
    current_year INT NOT NULL CHECK (current_year BETWEEN 1 AND 4),
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. Admins Table
-- ---------------------------------------------------------------------
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_username (username)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Assessment Questions Table (Exactly 20 Active Seeded)
-- ---------------------------------------------------------------------
CREATE TABLE assessment_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_order INT NOT NULL UNIQUE,
    question_text VARCHAR(500) NOT NULL,
    category VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_active (is_active)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Question Options Table (4 Options per Question)
-- ---------------------------------------------------------------------
CREATE TABLE question_options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text VARCHAR(200) NOT NULL,
    rating_value INT NOT NULL CHECK (rating_value BETWEEN 1 AND 4),
    FOREIGN KEY (question_id) REFERENCES assessment_questions(id) ON DELETE CASCADE,
    INDEX idx_option_question (question_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Careers Table (At Least 35 Seeded Careers)
-- ---------------------------------------------------------------------
CREATE TABLE careers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    short_description VARCHAR(500) NOT NULL,
    responsibilities TEXT NOT NULL,
    skills_required TEXT NOT NULL,
    tools_technologies TEXT NOT NULL,
    learning_suggestions TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_career_name (name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. Career-Question Mappings (Weighting for Deterministic Engine)
-- ---------------------------------------------------------------------
CREATE TABLE career_question_mappings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    career_id INT NOT NULL,
    question_id INT NOT NULL,
    weight DECIMAL(3,1) NOT NULL DEFAULT 1.0,
    FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES assessment_questions(id) ON DELETE CASCADE,
    UNIQUE KEY uq_career_question (career_id, question_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. Assessment Submissions Table
-- ---------------------------------------------------------------------
CREATE TABLE assessment_submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    summary_notes TEXT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_submission_student (student_id),
    INDEX idx_submission_date (submission_date)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. Student Responses Table (Responses Snapshot to Preserve History)
-- ---------------------------------------------------------------------
CREATE TABLE student_responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    question_id INT NOT NULL,
    question_text_snapshot VARCHAR(500) NOT NULL,
    selected_option_snapshot VARCHAR(200) NOT NULL,
    rating_value INT NOT NULL CHECK (rating_value BETWEEN 1 AND 4),
    FOREIGN KEY (submission_id) REFERENCES assessment_submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES assessment_questions(id),
    INDEX idx_response_submission (submission_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. Career Recommendations Table (Top 5 Careers per Submission)
-- ---------------------------------------------------------------------
CREATE TABLE career_recommendations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    career_id INT NOT NULL,
    rank_order INT NOT NULL CHECK (rank_order BETWEEN 1 AND 5),
    compatibility_score INT NOT NULL CHECK (compatibility_score BETWEEN 0 AND 100),
    match_reason TEXT NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES assessment_submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (career_id) REFERENCES careers(id),
    INDEX idx_rec_submission (submission_id)
) ENGINE=InnoDB;

-- =====================================================================
-- SEED DATA: 20 ASSESSMENT QUESTIONS
-- Scale:
-- 4 = Very interested
-- 3 = Moderately interested
-- 2 = Somewhat interested
-- 1 = Not very interested
-- =====================================================================

INSERT INTO assessment_questions (id, question_order, question_text, category, is_active) VALUES
(1, 1, 'How interested are you in writing code and solving logic-based programming challenges?', 'Coding & Logic', TRUE),
(2, 2, 'How interested are you in building modern websites, web layouts, and interactive online applications?', 'Web Development', TRUE),
(3, 3, 'How interested are you in designing attractive, intuitive app or website screens and visual aesthetics?', 'UI/UX & Visual Design', TRUE),
(4, 4, 'How interested are you in finding bugs, diagnosing software defects, and fixing technical errors?', 'Debugging & Troubleshooting', TRUE),
(5, 5, 'How interested are you in working with data, statistical numbers, dashboards, and discovering trends?', 'Data & Analytics', TRUE),
(6, 6, 'How interested are you in artificial intelligence, neural networks, and machine learning models?', 'Artificial Intelligence', TRUE),
(7, 7, 'How interested are you in understanding computer architecture, operating systems, and network communication?', 'Systems & Networking', TRUE),
(8, 8, 'How interested are you in keeping software systems, networks, and confidential data secure from cyber threats?', 'Cybersecurity', TRUE),
(9, 9, 'How interested are you in creating mobile applications for Android or iOS smartphones and tablets?', 'Mobile App Development', TRUE),
(10, 10, 'How interested are you in researching user needs, studying human behavior, and improving software user experience?', 'User Experience & Research', TRUE),
(11, 11, 'How interested are you in organizing, querying, and managing relational and NoSQL databases?', 'Database Management', TRUE),
(12, 12, 'How interested are you in writing automation scripts to eliminate repetitive manual tasks and speed up workflows?', 'Automation & Scripting', TRUE),
(13, 13, 'How interested are you in deploying applications to cloud infrastructure and managing remote production servers?', 'Cloud & Server Operations', TRUE),
(14, 14, 'How interested are you in systematically testing applications before launch to ensure top software quality?', 'Quality Assurance & Testing', TRUE),
(15, 15, 'How interested are you in researching cutting-edge computer science innovations, whitepapers, and new paradigms?', 'Research & Innovation', TRUE),
(16, 16, 'How interested are you in analyzing organizational business needs and designing practical technology solutions?', 'Business & Requirements Analysis', TRUE),
(17, 17, 'How interested are you in creating video games, 3D interactive simulations, or virtual environments?', 'Game & Graphics Development', TRUE),
(18, 18, 'How interested are you in explaining technical concepts, writing clear documentation, or mentoring peers?', 'Technical Communication', TRUE),
(19, 19, 'How interested are you in coordinating project milestones, managing timelines, and collaborating across engineering teams?', 'Project & Team Coordination', TRUE),
(20, 20, 'How interested are you in architecting software solutions that directly serve social good and solve real-world human problems?', 'Social Impact & Solution Design', TRUE);

-- Insert 4 options for each question (1 to 20)
-- 4 = Very interested, 3 = Moderately interested, 2 = Somewhat interested, 1 = Not very interested
INSERT INTO question_options (question_id, option_text, rating_value)
SELECT q.id, 'Very interested', 4 FROM assessment_questions q
UNION ALL
SELECT q.id, 'Moderately interested', 3 FROM assessment_questions q
UNION ALL
SELECT q.id, 'Somewhat interested', 2 FROM assessment_questions q
UNION ALL
SELECT q.id, 'Not very interested', 1 FROM assessment_questions q;

-- =====================================================================
-- SEED DATA: AT LEAST 35 CAREER PATHS FOR B.TECH CSE STUDENTS
-- =====================================================================

INSERT INTO careers (id, name, short_description, responsibilities, skills_required, tools_technologies, learning_suggestions, is_active) VALUES
(1, 'Software Developer',
 'Designs, codes, tests, and maintains robust software systems, libraries, and desktop/enterprise applications.',
 'Analyze technical specifications; write clean, modular Java/C++/Python code; perform unit testing; debug application issues; collaborate on code reviews.',
 'Object-Oriented Programming, Data Structures & Algorithms, Unit Testing, Problem Solving, Clean Code Practices',
 'Java, Git, IntelliJ IDEA/Eclipse, JUnit, Maven, Linux CLI',
 'Master core DSA on LeetCode/HackerRank, build 2-3 end-to-end desktop or CLI utilities in Java, and practice Git version control.',
 TRUE),

(2, 'Frontend Developer',
 'Crafts responsive, interactive, and visually engaging web interfaces that run seamlessly across browsers and devices.',
 'Translate UI wireframes into semantic HTML/CSS; write client-side JavaScript; ensure cross-browser compatibility; optimize web page performance.',
 'HTML5 Semantic Markup, CSS3 Grid & Flexbox, Vanilla JavaScript ES6+, Responsive Design, DOM Manipulation, Accessibility (a11y)',
 'VS Code, Chrome DevTools, Git, Postman, Webpack, Figma (Inspector)',
 'Build responsive portfolio websites using only HTML/CSS/JS, master asynchronous Fetch API calls, and study browser rendering cycles.',
 TRUE),

(3, 'Backend Developer',
 'Architects server-side business logic, RESTful APIs, database transactions, and authentication workflows.',
 'Build scalable REST APIs; write optimized database queries; handle user authentication and session security; integrate third-party services.',
 'Core Java/Python/Node.js, Relational Databases, API Design, HTTP Protocol, Data Modeling, Server Architecture',
 'Java Servlets/JSP, MySQL, Postman, Git, Tomcat, Docker',
 'Build complete CRUD applications with JDBC and Servlets, design normalized database schemas, and practice writing secure API endpoints.',
 TRUE),

(4, 'Full Stack Developer',
 'Bridges both frontend user interfaces and backend server architecture to deliver comprehensive end-to-end applications.',
 'Develop intuitive client interfaces; construct backend REST endpoints; design database tables; deploy and maintain integrated web stacks.',
 'Frontend HTML/CSS/JS, Backend Server Logic (Java/Node), SQL & Database Design, API Integration, DevOps Basics',
 'Java, MySQL, Apache Tomcat, VS Code, Git, Postman, Bootstrap/Tailwind',
 'Complete a full-stack project from database design to UI deployment, understanding every layer of client-server data flow.',
 TRUE),

(5, 'UI/UX Designer',
 'Researches user behaviors, builds wireframes, designs user workflows, and crafts modern digital screen interfaces.',
 'Conduct user research interviews; map user journeys; design wireframes and high-fidelity mockups; build interactive design prototypes.',
 'User Research, Information Architecture, Wireframing, Prototyping, Usability Testing, Visual Typography, Color Theory',
 'Figma, Adobe XD, Miro, InVision, Whimsical, Sketch',
 'Create a design case study in Figma solving a college student problem, conduct usability tests with classmates, and study design systems.',
 TRUE),

(6, 'Web Designer',
 'Focuses on the visual branding, creative layout, typography, and responsive presentation of commercial websites.',
 'Design visually appealing web layouts; create branded web assets; build interactive landing pages; ensure mobile responsiveness.',
 'Visual Design, Responsive CSS, Graphic Assets Creation, Typography, CSS Animations, Cross-Device Compatibility',
 'Figma, Adobe Photoshop, HTML5/CSS3, VS Code, WordPress/Webflow',
 'Study modern CSS design techniques such as CSS Grid and SVG animations, and design 5 distinct landing pages for different industries.',
 TRUE),

(7, 'Mobile Application Developer',
 'Develops high-performing native and hybrid mobile applications for smartphones, tablets, and smart devices.',
 'Build responsive mobile interfaces; integrate mobile hardware features (camera, GPS); handle offline caching; publish to app stores.',
 'Mobile App Architecture, State Management, REST API Integration, Mobile UI/UX Principles, Memory Management',
 'Android Studio / Xcode, Java/Kotlin or Swift, Git, Postman, SQLite',
 'Build a student utility app (e.g. attendance tracker or timetable planner) with local storage and test on a physical smartphone.',
 TRUE),

(8, 'Android Developer',
 'Specializes in engineering modern Android mobile applications utilizing the official Android SDK and ecosystem.',
 'Develop Android activities and fragments; handle background services; integrate Room database storage; interact with cloud backends.',
 'Java/Kotlin, Android SDK, Android Lifecycle, Jetpack Components, SQLite/Room, Material Design',
 'Android Studio, Gradle, Logcat, Postman, Git, Firebase',
 'Build an Android app that consumes a public REST API, implements local caching with Room DB, and adheres to Material Design.',
 TRUE),

(9, 'iOS Developer',
 'Specializes in creating elegant, responsive, and secure native mobile experiences for Apple iOS devices.',
 'Develop iOS views and view controllers; implement auto-layout; manage local CoreData storage; integrate Apple frameworks.',
 'Swift / Objective-C, UIKit / SwiftUI, Apple HIG Guidelines, CoreData, RESTful Networking',
 'Xcode, Simulator, Git, CocoaPods/SPM, Postman',
 'Study Swift syntax and Apple Human Interface Guidelines, and build a sample iOS tracker or note application.',
 TRUE),

(10, 'Data Analyst',
 'Inspects, cleans, transforms, and visualizes structured data to extract actionable business insights and support decisions.',
 'Write complex SQL queries; clean messy datasets; build interactive business dashboards; present statistical reports to stakeholders.',
 'SQL Querying, Exploratory Data Analysis, Data Cleaning, Statistical Foundations, Data Storytelling, Excel Modeling',
 'MySQL/PostgreSQL, Power BI, Tableau, Advanced Excel, Python (Pandas/Matplotlib)',
 'Analyze open-source datasets (such as Kaggle Indian census or movie ratings) in SQL, write query reports, and build a Power BI dashboard.',
 TRUE),

(11, 'Data Scientist',
 'Applies advanced mathematics, machine learning algorithms, and predictive modeling to extract patterns from complex datasets.',
 'Formulate research hypotheses; preprocess tabular and unstructured data; train predictive ML models; evaluate model accuracy metrics.',
 'Machine Learning Algorithms, Probability & Statistics, Feature Engineering, Python/R, Data Wrangling',
 'Python (NumPy, Pandas, Scikit-learn, Seaborn), Jupyter Notebooks, Git, SQL',
 'Build end-to-end predictive projects (e.g. student placement prediction or house price forecasting) and document the mathematical rationale.',
 TRUE),

(12, 'Machine Learning Engineer',
 'Bridges data science and software engineering by building, deploying, optimizing, and monitoring production ML pipelines.',
 'Deploy machine learning models as scalable APIs; automate model retraining pipelines; optimize inference latency; monitor model drift.',
 'Machine Learning Pipelines, Model Deployment, Deep Learning Basics, Python, Software Engineering Principles, API Development',
 'Python, Scikit-Learn, TensorFlow/PyTorch, FastAPI, Docker, MLflow',
 'Export a trained ML classifier and serve predictions via a lightweight Java or Python REST endpoint containerized with Docker.',
 TRUE),

(13, 'Artificial Intelligence Engineer',
 'Develops intelligent agents, natural language processing pipelines, computer vision models, and cognitive computing systems.',
 'Implement deep learning neural networks; integrate generative AI and LLM APIs; train computer vision or NLP classifiers.',
 'Deep Learning, Neural Networks, Natural Language Processing, Computer Vision, Vector Embeddings, Python',
 'PyTorch, Hugging Face, OpenCV, Python, Jupyter, Google Colab',
 'Implement a text classification or image recognition model using PyTorch or Hugging Face, and build a web demo.',
 TRUE),

(14, 'Data Engineer',
 'Designs and constructs scalable data pipelines, ETL workflows, data warehouses, and robust infrastructure for big data.',
 'Build automated ETL data ingestion pipelines; design data warehouse schemas; optimize database indexing; maintain data cleanliness.',
 'ETL Pipeline Design, SQL Optimization, Distributed Computing Concepts, Data Modeling, Python/Java',
 'Apache Spark, Apache Airflow, MySQL, PostgreSQL, Docker, Kafka',
 'Build an automated pipeline in Python or Java that extracts CSV data, cleans records, and loads them into a normalized MySQL schema.',
 TRUE),

(15, 'Database Administrator',
 'Ensures relational and enterprise database systems operate reliably, securely, performantly, and without data loss.',
 'Configure database servers; implement backup and recovery strategies; optimize slow queries; manage user access permissions and security.',
 'Database Architecture, SQL Optimization, Index Tuning, Backup & Disaster Recovery, Replication, Database Security',
 'MySQL Workbench, pgAdmin, Oracle Database, Linux CLI, Bash Scripting',
 'Practice setting up MySQL automated scheduled backups, analyzing EXPLAIN query plans, and creating optimal multi-column indexes.',
 TRUE),

(16, 'Cloud Engineer',
 'Architects, configures, and oversees cloud computing resources, virtual networks, and scalable cloud-native architectures.',
 'Provision cloud virtual machines and storage; set up virtual private clouds (VPCs); optimize cloud spending; ensure high system availability.',
 'Cloud Infrastructure, Networking (DNS, VPC, Subnets), Identity & Access Management (IAM), Linux Administration, Cloud Storage',
 'AWS / Azure / GCP, Linux, Terraform, CloudWatch, Docker',
 'Deploy a Java web application on an AWS EC2 instance or Azure VM with proper security group firewall rules and domain configuration.',
 TRUE),

(17, 'DevOps Engineer',
 'Unifies software development and IT operations through Continuous Integration/Continuous Deployment (CI/CD) automation.',
 'Build automated CI/CD build pipelines; manage containerized applications; automate server configuration; monitor infrastructure health.',
 'CI/CD Automation, Containerization, Infrastructure as Code, Linux Shell Scripting, Git Branching Strategies',
 'Docker, Jenkins, GitHub Actions, Linux (Bash), Kubernetes, Ansible',
 'Set up a GitHub Actions workflow that automatically compiles Java source code on every git push and runs unit tests.',
 TRUE),

(18, 'Cybersecurity Analyst',
 'Protects enterprise networks, applications, and confidential data by monitoring, detecting, and mitigating cyber threats.',
 'Monitor security event logs; investigate security incidents; conduct vulnerability assessments; implement security best practices.',
 'Network Security, Vulnerability Scanning, Cryptography Basics, Incident Response, Security Information & Event Management (SIEM)',
 'Wireshark, Nmap, Burp Suite, Linux, Kali Linux, Snort',
 'Learn network packet analysis with Wireshark, scan local test virtual machines with Nmap, and study OWASP Top 10 web vulnerabilities.',
 TRUE),

(19, 'Penetration Tester',
 'Acts as an ethical hacker to legally discover, exploit, and report security flaws in applications and networks before attackers can.',
 'Perform black-box and white-box penetration testing; identify injection and authentication flaws; write actionable remediation reports.',
 'Ethical Hacking, Web App Security (OWASP Top 10), Network Protocols, Exploit Analysis, Scripting (Python/Bash)',
 'Kali Linux, Burp Suite Pro, Metasploit, Nmap, OWASP ZAP',
 'Practice on legal platforms like PortSwigger Web Security Academy and TryHackMe, focusing on SQL injection and XSS mitigation.',
 TRUE),

(20, 'Network Engineer',
 'Designs, installs, monitors, and maintains local and wide area computer networks (LAN/WAN) for reliable connectivity.',
 'Configure routers and switches; set up subnetting and VLANs; monitor bandwidth utilization; troubleshoot connectivity latency and outages.',
 'TCP/IP Model, Subnetting & CIDR, Routing Protocols (OSPF, BGP), DNS/DHCP, Network Troubleshooting, Firewall Configuration',
 'Cisco Packet Tracer, Wireshark, GNS3, Linux CLI, PuTTY',
 'Simulate complex multi-router office network topologies in Cisco Packet Tracer and master subnet calculations.',
 TRUE),

(21, 'Software Tester',
 'Validates software products against functional specifications through rigorous manual testing, test case design, and defect logging.',
 'Write comprehensive test plans and test scenarios; execute boundary value test cases; log detailed bug reports; verify bug fixes.',
 'Software Testing Life Cycle (STLC), Black-Box Testing, Defect Tracking, Test Case Writing, Regression Testing',
 'Jira, Bugzilla, Postman, Excel Test Matrices, Browser DevTools',
 'Write a complete 50-point test case document covering edge cases for an e-commerce checkout page or student portal.',
 TRUE),

(22, 'Automation Test Engineer',
 'Builds automated test suites and test frameworks that programmatically test web, API, and mobile applications for regressions.',
 'Develop automated test scripts; maintain modular test automation frameworks; execute parallel test suites; integrate tests with CI/CD.',
 'Test Automation Frameworks, Java/Python, Selenium WebDriver, TestNG/JUnit, API Automation',
 'Selenium WebDriver, TestNG, Postman/Newman, Git, Jenkins, Eclipse',
 'Write a Selenium Java test suite that automatically opens a browser, fills a registration form, and verifies success messages.',
 TRUE),

(23, 'Quality Assurance Engineer',
 'Oversees total software quality assurance processes across development, ensuring adherence to coding and testing standards.',
 'Define QA standards and release gates; coordinate testing phases across teams; perform sanity and exploratory testing; track defect metrics.',
 'Quality Assurance Standards, Test Management, Cross-Functional Communication, Defect Triage, Risk Analysis',
 'Jira, Postman, Selenium, Confluence, TestRail',
 'Coordinate a mock testing cycle for a university web portal, manage bug triage, and document QA release sign-off reports.',
 TRUE),

(24, 'Game Developer',
 'Programs game mechanics, physics engines, character controls, 3D graphics, and immersive interactive logic for video games.',
 'Implement gameplay mechanics and player controllers; write game physics logic; optimize frame rates; integrate sound and animations.',
 'Game Mathematics (Linear Algebra, Vectors), C# or C++, Game Engine Physics, Animation State Machines, Object Pooling',
 'Unity 3D, Unreal Engine, C#, Blender, Visual Studio, Git',
 'Build a complete 2D platformer or top-down puzzle game in Unity using C#, implementing score tracking and collision physics.',
 TRUE),

(25, 'Blockchain Developer',
 'Builds decentralized applications (DApps), smart contracts, and cryptographic ledger protocols on blockchain networks.',
 'Write secure smart contracts; deploy decentralized backend logic; interact with blockchain nodes via Web3 libraries; audit contract safety.',
 'Cryptography Basics (Hashing, Asymmetric Keys), Smart Contract Logic, Solidity, Distributed Consensus, Web3 Integration',
 'Solidity, Remix IDE, Hardhat, MetaMask, Web3.js, Ethereum Testnets',
 'Write and deploy an ERC-20 token or voting smart contract on Remix IDE and interact with it using a simple web interface.',
 TRUE),

(26, 'Business Analyst',
 'Analyzes enterprise workflows, gathers business stakeholder requirements, and translates business problems into technical specs.',
 'Conduct stakeholder discovery meetings; document Functional Requirement Documents (FRDs); create UML use-case diagrams; validate solutions.',
 'Requirements Gathering, UML Diagramming, Gap Analysis, Stakeholder Communication, Process Mapping, SQL Basics',
 'Microsoft Visio / Lucidchart, Jira, Confluence, Excel, Trello',
 'Draft a comprehensive Software Requirement Specification (SRS) for a hospital management or student grading system.',
 TRUE),

(27, 'Product Manager',
 'Defines the product vision, roadmap, feature prioritization, and strategic market success for software applications.',
 'Prioritize feature backlogs; analyze market research and user feedback; define product Key Performance Indicators (KPIs); align teams.',
 'Product Strategy, Roadmap Planning, Agile/Scrum Framework, User Empathy, Market Research, Feature Prioritization (RICE/MoSCoW)',
 'Jira, Notion, Figma, Google Analytics, Miro, Productboard',
 'Create a product roadmap and feature PRD (Product Requirement Document) prioritizing user stories for a student collaboration app.',
 TRUE),

(28, 'Technical Project Coordinator',
 'Facilitates sprint planning, tracks project milestones, eliminates team blockers, and ensures on-time software project delivery.',
 'Organize daily standup meetings; track team sprint velocity; coordinate deliverables across developers and testers; report progress.',
 'Agile & Scrum Methodologies, Sprint Management, Time Management, Team Coordination, Risk Mitigation',
 'Jira Agile Boards, Trello, Slack/Teams, Microsoft Project, Confluence',
 'Manage a mock 4-week Agile project with student teammates in Jira, holding retrospectives and tracking sprint burndown charts.',
 TRUE),

(29, 'Systems Analyst',
 'Evaluates existing computer hardware, software systems, and network infrastructure to propose architectural upgrades.',
 'Analyze legacy IT architectures; design data flow diagrams (DFDs); assess cost-benefit ratios of tech migrations; produce system specs.',
 'System Architecture, Data Flow Diagrams, Feasibility Analysis, Hardware & Software Sizing, Enterprise Integration',
 'Enterprise Architect, Lucidchart, Visio, SQL, Linux',
 'Produce a system architecture and integration report detailing how a university can migrate from paper records to cloud databases.',
 TRUE),

(30, 'Technical Support Engineer',
 'Resolves complex technical hardware, operating system, and software application issues for end-users and enterprise clients.',
 'Diagnose client software bugs and network issues; review system logs; provide root-cause solutions; document troubleshooting knowledge bases.',
 'System Troubleshooting, Operating Systems (Windows/Linux), Network Diagnostics, Customer Communication, Remote Desktop Tools',
 'ServiceNow, Zendesk, Linux Bash, Windows Event Viewer, Wireshark, Remote Desktop',
 'Practice debugging common runtime errors, analyzing Apache web server access/error logs, and writing clear diagnostic step-by-step guides.',
 TRUE),

(31, 'Embedded Systems Developer',
 'Programs low-level microcontrollers, sensors, firmware, and IoT devices running on constrained hardware.',
 'Write memory-efficient C/C++ firmware; interface with hardware sensors (I2C, SPI, UART); debug using oscilloscopes and hardware simulators.',
 'C / Embedded C, Microcontroller Architecture (ARM, AVR, ESP32), Real-Time Operating Systems (RTOS), Hardware Protocols (SPI/I2C)',
 'Arduino IDE, Keil uVision, STM32CubeIDE, PlatformIO, Proteus',
 'Build an IoT temperature and environmental monitoring device using an ESP32 microcontroller with Wi-Fi telemetry reporting.',
 TRUE),

(32, 'Research and Development Engineer',
 'Explores innovative computer science theories, experiments with emerging algorithms, and builds novel proof-of-concept prototypes.',
 'Read academic computer science papers; design novel algorithmic experiments; benchmark performance improvements; publish findings.',
 'Scientific Research Methodology, Advanced Algorithms, Mathematical Rigor, Prototyping, Technical Paper Writing',
 'Python, C++, LaTeX, Google Scholar, ArXiv, MATLAB',
 'Read 3 recent IEEE or ACM papers in machine learning or networking, reproduce an algorithm in code, and write a comparative summary.',
 TRUE),

(33, 'Technical Writer',
 'Transforms complex technical software systems, architectures, and APIs into clear, comprehensive, and user-friendly documentation.',
 'Author developer API reference guides; write end-user installation manuals; maintain software documentation wikis; create diagrams.',
 'Technical Writing, API Documentation, Markdown, Technical Editing, Git-based Documentation (Docs-as-Code)',
 'Markdown, GitBook, Swagger/OpenAPI, Docusaurus, Postman Documenter, Sphinx',
 'Write a complete developer API guide and user tutorial for an open-source project using Markdown and OpenAPI specifications.',
 TRUE),

(34, 'IT Consultant',
 'Advises business clients on leveraging modern information technology to achieve organizational goals, reduce costs, and enhance security.',
 'Analyze client technological readiness; recommend software vendor stacks; create digital transformation roadmaps; present executive findings.',
 'Technology Evaluation, IT Strategy, Business Acumen, Presentation Skills, Cloud & Security Overview',
 'PowerPoint/Keynote, Excel Financial Modeling, Mind Mapping Tools, Cloud Calculators',
 'Prepare a business technology advisory report evaluating cloud migration strategies for an educational institution.',
 TRUE),

(35, 'Site Reliability Engineer',
 'Applies software engineering disciplines to infrastructure and operations problems to build ultra-scalable and fault-tolerant systems.',
 'Define Service Level Objectives (SLOs) and SLAs; implement automated self-healing scripts; conduct post-mortem incident analyses; monitor system uptime.',
 'Reliability Engineering, Linux Internals, Automation Scripting (Go/Python), System Monitoring, High Availability Architecture',
 'Prometheus, Grafana, Docker, Kubernetes, Linux, Python/Go, ELK Stack',
 'Set up server metric monitoring using open-source tools to track CPU, memory, and HTTP response latencies with alert thresholds.',
 TRUE);

-- =====================================================================
-- SEED DATA: CAREER-TO-QUESTION MAPPINGS (Relevance Weights)
-- Weights: 1.0 (Relevant), 2.0 (High Relevance), 3.0 (Core Pillar)
-- =====================================================================

-- 1. Software Developer: Q1 (wt 3.0), Q4 (wt 2.5), Q12 (wt 2.0), Q20 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(1, 1, 3.0), (1, 4, 2.5), (1, 12, 2.0), (1, 20, 1.5);

-- 2. Frontend Developer: Q2 (wt 3.0), Q3 (wt 2.5), Q10 (wt 2.0), Q1 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(2, 2, 3.0), (2, 3, 2.5), (2, 10, 2.0), (2, 1, 1.5);

-- 3. Backend Developer: Q1 (wt 3.0), Q11 (wt 2.5), Q13 (wt 2.0), Q4 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(3, 1, 3.0), (3, 11, 2.5), (3, 13, 2.0), (3, 4, 1.5);

-- 4. Full Stack Developer: Q1 (wt 3.0), Q2 (wt 3.0), Q11 (wt 2.0), Q13 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(4, 1, 3.0), (4, 2, 3.0), (4, 11, 2.0), (4, 13, 1.5);

-- 5. UI/UX Designer: Q3 (wt 3.0), Q10 (wt 3.0), Q2 (wt 2.0), Q20 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(5, 3, 3.0), (5, 10, 3.0), (5, 2, 2.0), (5, 20, 1.5);

-- 6. Web Designer: Q3 (wt 3.0), Q2 (wt 3.0), Q10 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(6, 3, 3.0), (6, 2, 3.0), (6, 10, 2.0);

-- 7. Mobile Application Developer: Q9 (wt 3.0), Q1 (wt 2.5), Q3 (wt 2.0), Q10 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(7, 9, 3.0), (7, 1, 2.5), (7, 3, 2.0), (7, 10, 1.5);

-- 8. Android Developer: Q9 (wt 3.0), Q1 (wt 2.5), Q10 (wt 2.0), Q11 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(8, 9, 3.0), (8, 1, 2.5), (8, 10, 2.0), (8, 11, 1.5);

-- 9. iOS Developer: Q9 (wt 3.0), Q1 (wt 2.5), Q3 (wt 2.0), Q10 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(9, 9, 3.0), (9, 1, 2.5), (9, 3, 2.0), (9, 10, 1.5);

-- 10. Data Analyst: Q5 (wt 3.0), Q11 (wt 2.5), Q16 (wt 2.0), Q18 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(10, 5, 3.0), (10, 11, 2.5), (10, 16, 2.0), (10, 18, 1.5);

-- 11. Data Scientist: Q5 (wt 3.0), Q6 (wt 3.0), Q15 (wt 2.0), Q1 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(11, 5, 3.0), (11, 6, 3.0), (11, 15, 2.0), (11, 1, 1.5);

-- 12. Machine Learning Engineer: Q6 (wt 3.0), Q1 (wt 2.5), Q5 (wt 2.5), Q12 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(12, 6, 3.0), (12, 1, 2.5), (12, 5, 2.5), (12, 12, 1.5);

-- 13. Artificial Intelligence Engineer: Q6 (wt 3.0), Q15 (wt 2.5), Q1 (wt 2.0), Q5 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(13, 6, 3.0), (13, 15, 2.5), (13, 1, 2.0), (13, 5, 2.0);

-- 14. Data Engineer: Q11 (wt 3.0), Q5 (wt 2.5), Q1 (wt 2.0), Q13 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(14, 11, 3.0), (14, 5, 2.5), (14, 1, 2.0), (14, 13, 2.0);

-- 15. Database Administrator: Q11 (wt 3.0), Q8 (wt 2.0), Q13 (wt 2.0), Q4 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(15, 11, 3.0), (15, 8, 2.0), (15, 13, 2.0), (15, 4, 1.5);

-- 16. Cloud Engineer: Q13 (wt 3.0), Q7 (wt 2.5), Q8 (wt 2.0), Q12 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(16, 13, 3.0), (16, 7, 2.5), (16, 8, 2.0), (16, 12, 2.0);

-- 17. DevOps Engineer: Q12 (wt 3.0), Q13 (wt 3.0), Q7 (wt 2.0), Q4 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(17, 12, 3.0), (17, 13, 3.0), (17, 7, 2.0), (17, 4, 1.5);

-- 18. Cybersecurity Analyst: Q8 (wt 3.0), Q7 (wt 2.5), Q4 (wt 2.0), Q15 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(18, 8, 3.0), (18, 7, 2.5), (18, 4, 2.0), (18, 15, 1.5);

-- 19. Penetration Tester: Q8 (wt 3.0), Q4 (wt 2.5), Q7 (wt 2.0), Q1 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(19, 8, 3.0), (19, 4, 2.5), (19, 7, 2.0), (19, 1, 1.5);

-- 20. Network Engineer: Q7 (wt 3.0), Q8 (wt 2.0), Q13 (wt 2.0), Q4 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(20, 7, 3.0), (20, 8, 2.0), (20, 13, 2.0), (20, 4, 1.5);

-- 21. Software Tester: Q4 (wt 3.0), Q14 (wt 3.0), Q1 (wt 1.5), Q10 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(21, 4, 3.0), (21, 14, 3.0), (21, 1, 1.5), (21, 10, 1.5);

-- 22. Automation Test Engineer: Q14 (wt 3.0), Q12 (wt 2.5), Q1 (wt 2.0), Q4 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(22, 14, 3.0), (22, 12, 2.5), (22, 1, 2.0), (22, 4, 2.0);

-- 23. Quality Assurance Engineer: Q14 (wt 3.0), Q4 (wt 2.5), Q16 (wt 2.0), Q10 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(23, 14, 3.0), (23, 4, 2.5), (23, 16, 2.0), (23, 10, 1.5);

-- 24. Game Developer: Q17 (wt 3.0), Q1 (wt 2.5), Q3 (wt 2.0), Q10 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(24, 17, 3.0), (24, 1, 2.5), (24, 3, 2.0), (24, 10, 1.5);

-- 25. Blockchain Developer: Q1 (wt 2.5), Q8 (wt 2.5), Q15 (wt 2.5), Q11 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(25, 1, 2.5), (25, 8, 2.5), (25, 15, 2.5), (25, 11, 2.0);

-- 26. Business Analyst: Q16 (wt 3.0), Q5 (wt 2.0), Q18 (wt 2.0), Q20 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(26, 16, 3.0), (26, 5, 2.0), (26, 18, 2.0), (26, 20, 2.0);

-- 27. Product Manager: Q16 (wt 3.0), Q19 (wt 2.5), Q10 (wt 2.5), Q20 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(27, 16, 3.0), (27, 19, 2.5), (27, 10, 2.5), (27, 20, 2.0);

-- 28. Technical Project Coordinator: Q19 (wt 3.0), Q18 (wt 2.5), Q16 (wt 2.0), Q20 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(28, 19, 3.0), (28, 18, 2.5), (28, 16, 2.0), (28, 20, 1.5);

-- 29. Systems Analyst: Q16 (wt 2.5), Q7 (wt 2.5), Q11 (wt 2.0), Q4 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(29, 16, 2.5), (29, 7, 2.5), (29, 11, 2.0), (29, 4, 2.0);

-- 30. Technical Support Engineer: Q18 (wt 3.0), Q7 (wt 2.5), Q4 (wt 2.0), Q20 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(30, 18, 3.0), (30, 7, 2.5), (30, 4, 2.0), (30, 20, 1.5);

-- 31. Embedded Systems Developer: Q7 (wt 3.0), Q1 (wt 2.5), Q4 (wt 2.0), Q15 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(31, 7, 3.0), (31, 1, 2.5), (31, 4, 2.0), (31, 15, 1.5);

-- 32. Research and Development Engineer: Q15 (wt 3.0), Q6 (wt 2.5), Q1 (wt 2.0), Q5 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(32, 15, 3.0), (32, 6, 2.5), (32, 1, 2.0), (32, 5, 2.0);

-- 33. Technical Writer: Q18 (wt 3.0), Q10 (wt 2.0), Q1 (wt 1.5), Q14 (wt 1.5)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(33, 18, 3.0), (33, 10, 2.0), (33, 1, 1.5), (33, 14, 1.5);

-- 34. IT Consultant: Q16 (wt 3.0), Q18 (wt 2.5), Q13 (wt 2.0), Q8 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(34, 16, 3.0), (34, 18, 2.5), (34, 13, 2.0), (34, 8, 2.0);

-- 35. Site Reliability Engineer: Q13 (wt 3.0), Q4 (wt 2.5), Q12 (wt 2.5), Q7 (wt 2.0)
INSERT INTO career_question_mappings (career_id, question_id, weight) VALUES
(35, 13, 3.0), (35, 4, 2.5), (35, 12, 2.5), (35, 7, 2.0);

-- ---------------------------------------------------------------------
-- NOTE ON INITIAL ADMIN ACCOUNT:
-- Run the com.career.util.AdminSetup utility to securely generate and
-- insert the initial administrator account with a salted PBKDF2 hash.
-- ---------------------------------------------------------------------
