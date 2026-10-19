<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Student, com.career.model.Admin" %>
<%
    String currentContext = request.getContextPath();
    HttpSession currentSession = request.getSession(false);
    String userRole = (currentSession != null) ? (String) currentSession.getAttribute("userRole") : null;
    Student studentUser = (currentSession != null) ? (Student) currentSession.getAttribute("studentUser") : null;
    Admin adminUser = (currentSession != null) ? (Admin) currentSession.getAttribute("adminUser") : null;
    String pageTitle = (String) request.getAttribute("pageTitle");
    if (pageTitle == null) pageTitle = "Career Recommendation System for B.Tech CSE";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %></title>
    <link rel="stylesheet" href="<%= currentContext %>/assets/css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;600&display=swap" rel="stylesheet">
</head>
<body>

<header class="site-header">
    <div class="container nav-wrapper">
        <a href="<%= currentContext %>/" class="brand-logo">
            <!-- Compass SVG Icon -->
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
            </svg>
            <span>CareerCompass <span class="brand-badge">CSE</span></span>
        </a>

        <!-- Desktop Navigation -->
        <nav>
            <ul class="nav-menu">
                <li><a href="<%= currentContext %>/" class="nav-link">Home</a></li>

                <% if ("STUDENT".equals(userRole)) { %>
                    <li><a href="<%= currentContext %>/student/dashboard" class="nav-link">Dashboard</a></li>
                    <li><a href="<%= currentContext %>/student/assessment" class="nav-link">Take Assessment</a></li>
                    <li><a href="<%= currentContext %>/student/results" class="nav-link">My Results</a></li>
                    <li><a href="<%= currentContext %>/student/history" class="nav-link">History</a></li>
                    <li><a href="<%= currentContext %>/student/profile" class="nav-link">Profile</a></li>
                <% } else if ("ADMIN".equals(userRole)) { %>
                    <li><a href="<%= currentContext %>/admin/dashboard" class="nav-link">Admin Dashboard</a></li>
                    <li><a href="<%= currentContext %>/admin/questions" class="nav-link">Questions</a></li>
                    <li><a href="<%= currentContext %>/admin/students" class="nav-link">Students</a></li>
                    <li><a href="<%= currentContext %>/admin/careers" class="nav-link">Careers (35)</a></li>
                <% } else { %>
                    <li><a href="<%= currentContext %>/register" class="nav-link">Student Register</a></li>
                    <li><a href="<%= currentContext %>/login" class="nav-link">Student Login</a></li>
                    <li><a href="<%= currentContext %>/admin/login" class="nav-link">Admin Portal</a></li>
                <% } %>
            </ul>
        </nav>

        <!-- Right Side Nav Actions -->
        <div class="nav-actions">
            <% if ("STUDENT".equals(userRole) && studentUser != null) { %>
                <div class="nav-user-pill">
                    <div class="nav-user-avatar">
                        <%= studentUser.getFullName().substring(0, 1).toUpperCase() %>
                    </div>
                    <span><%= studentUser.getFullName() %></span>
                </div>
                <a href="<%= currentContext %>/logout" class="btn btn-outline-white btn-sm">Logout</a>
            <% } else if ("ADMIN".equals(userRole) && adminUser != null) { %>
                <div class="nav-user-pill">
                    <div class="nav-user-avatar" style="background: var(--teal-600);">A</div>
                    <span><%= adminUser.getUsername() %> (Admin)</span>
                </div>
                <a href="<%= currentContext %>/admin/logout" class="btn btn-outline-white btn-sm">Logout</a>
            <% } else { %>
                <a href="<%= currentContext %>/login" class="btn btn-primary btn-sm">Get Started</a>
            <% } %>

            <!-- Mobile Menu Toggle Button -->
            <button class="nav-toggle" aria-label="Toggle mobile menu">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="3" y1="12" x2="21" y2="12"></line>
                    <line x1="3" y1="6" x2="21" y2="6"></line>
                    <line x1="3" y1="18" x2="21" y2="18"></line>
                </svg>
            </button>
        </div>
    </div>
</header>
<main class="main-content">
