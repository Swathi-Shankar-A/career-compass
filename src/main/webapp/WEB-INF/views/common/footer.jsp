<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
</main>

<footer class="site-footer">
    <div class="container">
        <div class="footer-grid">
            <div class="footer-brand">
                <h3>CareerCompass CSE</h3>
                <p style="color: var(--slate-400); font-size: 0.9rem; line-height: 1.6; margin-top: 0.5rem;">
                    A deterministic, beginner-friendly career guidance and recommendation platform tailored for B.Tech Computer Science &amp; Engineering undergraduates.
                </p>
                <div style="margin-top: 1rem; font-size: 0.8rem; color: var(--slate-500);">
                    Built with Core Java Servlets, JSP, JDBC &amp; MySQL. No frameworks required.
                </div>
            </div>

            <div class="footer-links">
                <h4>Quick Navigation</h4>
                <ul>
                    <li><a href="<%= request.getContextPath() %>/">Home</a></li>
                    <li><a href="<%= request.getContextPath() %>/register">Student Registration</a></li>
                    <li><a href="<%= request.getContextPath() %>/login">Student Portal</a></li>
                    <li><a href="<%= request.getContextPath() %>/admin/login">Administrator Login</a></li>
                </ul>
            </div>

            <div class="footer-links">
                <h4>Academic Domains</h4>
                <ul>
                    <li><span style="color: var(--slate-400);">Web &amp; Cloud Systems</span></li>
                    <li><span style="color: var(--slate-400);">AI, ML &amp; Data Science</span></li>
                    <li><span style="color: var(--slate-400);">Cybersecurity &amp; Networks</span></li>
                    <li><span style="color: var(--slate-400);">Software Quality &amp; DevOps</span></li>
                </ul>
            </div>
        </div>

        <div class="footer-bottom">
            <div>
                &copy; <%= java.time.Year.now().getValue() %> CareerCompass CSE. Academic Project for B.Tech CSE.
            </div>
            <div style="color: var(--slate-500); font-size: 0.8rem;">
                Recommendation results are algorithmic guidance to help students explore options, not guarantees of employment.
            </div>
        </div>
    </div>
</footer>

<script src="<%= request.getContextPath() %>/assets/js/app.js"></script>
</body>
</html>
