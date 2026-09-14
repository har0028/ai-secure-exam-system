<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>AI Secure Exam &mdash; Enterprise Proctored Online Examinations</title>
    <%@ include file="/common/head.jsp" %>
</head>
<body>

<!-- ============== NAVBAR ============== -->
<nav class="site-nav">
    <div class="container-xl nav-inner">
        <a href="<%=request.getContextPath()%>/" class="brand">
            <span class="brand-mark"><i class="fa-solid fa-shield-halved" style="color:#fff;font-size:1.05rem;"></i></span>
            AI Secure Exam
        </a>
        <div class="nav-links">
            <a href="#features">Features</a>
            <a href="#how-it-works">How it Works</a>
            <a href="#monitoring">AI Engine</a>
            <a href="#testimonials">Testimonials</a>
            <a href="#contact">Enterprise Contact</a>
        </div>
        <div style="display:flex; gap:14px; align-items:center;">
            <a href="<%=request.getContextPath()%>/login" class="btn-outline-light">Sign In</a>
            <a href="<%=request.getContextPath()%>/register" class="btn-gradient">Get Started</a>
        </div>
    </div>
</nav>

<!-- ============== HERO ============== -->
<section class="hero">
    <div class="container-xl hero-grid">
        <div>
            <span class="eyebrow"><i class="fa-solid fa-brain"></i> AI-Powered Proctoring Engine v2.0</span>
            <h1>Exams your institution can <span>actually trust.</span></h1>
            <p class="lead">Run secure, role-based online examinations with real-time face detection,
               tab-switch tracking, and live risk scoring &mdash; so integrity is monitored automatically,
               not assumed.</p>
            <div class="hero-actions">
                <a href="<%=request.getContextPath()%>/register" class="btn-gradient">
                    <i class="fa-solid fa-graduation-cap"></i> Start as Student
                </a>
                <a href="<%=request.getContextPath()%>/login" class="btn-outline-light">
                    <i class="fa-solid fa-user-shield"></i> Admin Sign In
                </a>
            </div>
        </div>

        <div class="glass-dark hero-panel">
            <div class="hero-panel-header">
                <span style="color:#fff; font-size:0.9rem; font-weight:700;">Live Candidate Monitor</span>
                <span class="live-dot">MONITORING</span>
            </div>
            <div class="hero-panel-body">
                <div class="risk-ring" data-score="18"></div>
                <div class="hero-panel-metrics">
                    <div class="metric-row"><span>Face Status</span><b style="color:var(--color-emerald);"><i class="fa-solid fa-circle-check"></i> Visible</b></div>
                    <div class="metric-row"><span>Tab Switches</span><b>0</b></div>
                    <div class="metric-row"><span>Fullscreen</span><b style="color:var(--color-cyan-light);">Active</b></div>
                    <div class="metric-row"><span>Webcam Feed</span><b style="color:var(--color-emerald);">1080p HD</b></div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- ============== FEATURES ============== -->
<section class="section" id="features">
    <div class="container-xl">
        <div class="section-head">
            <span class="eyebrow-dark">Enterprise Platform Features</span>
            <h2>Everything your examination cell needs</h2>
            <p>From question banks to instant evaluation, the entire exam lifecycle runs on a single,
               role-based platform built for administrators and students.</p>
        </div>
        <div class="feature-grid">
            <div class="feature-card">
                <div class="icon-box"><i class="fa-solid fa-file-circle-check"></i></div>
                <h3>Exam &amp; Question Management</h3>
                <p>Create, schedule, and manage exams with MCQ, true/false, and single-correct
                   questions, complete with bulk upload and randomized ordering.</p>
            </div>
            <div class="feature-card">
                <div class="icon-box"><i class="fa-solid fa-bolt"></i></div>
                <h3>Automatic Evaluation</h3>
                <p>Submissions are scored instantly, with pass/fail status, percentage, and a full
                   answer breakdown generated the moment a candidate submits.</p>
            </div>
            <div class="feature-card">
                <div class="icon-box"><i class="fa-solid fa-eye"></i></div>
                <h3>Live Proctoring Dashboard</h3>
                <p>Watch every active candidate's webcam status, risk score, and violation feed
                   update in real time from a single monitoring screen.</p>
            </div>
            <div class="feature-card">
                <div class="icon-box"><i class="fa-solid fa-shield-halved"></i></div>
                <h3>Role-Based Security</h3>
                <p>Session-protected, filter-enforced access separates admin and student
                   capabilities completely, backed by BCrypt hashed credentials.</p>
            </div>
            <div class="feature-card">
                <div class="icon-box"><i class="fa-solid fa-chart-pie"></i></div>
                <h3>Analytics &amp; Risk Badges</h3>
                <p>Pass/fail charts, violation distribution graphs, and risk levels
                   (Low, Medium, High, Critical) highlight suspicious behavior at a glance.</p>
            </div>
            <div class="feature-card">
                <div class="icon-box"><i class="fa-solid fa-mobile-screen"></i></div>
                <h3>Clean Responsive UI</h3>
                <p>Custom glassmorphic interface, dark theme, and fluid responsive design designed
                   for a seamless experience on desktop and tablet devices.</p>
            </div>
        </div>
    </div>
</section>

<!-- ============== HOW IT WORKS ============== -->
<section class="section" id="how-it-works" style="background:#F1F5F9;">
    <div class="container-xl">
        <div class="section-head">
            <span class="eyebrow-dark">Simplified Workflow</span>
            <h2>Four simple steps to secure assessment</h2>
        </div>
        <div style="display:grid; grid-template-columns:repeat(4, 1fr); gap:24px;">
            <div class="glass-light" style="padding:26px;">
                <div style="font-family:var(--font-mono); color:var(--color-indigo); font-weight:800; font-size:1.1rem; margin-bottom:12px;">01. SETUP</div>
                <h4 style="font-size:1.05rem; margin-bottom:8px; color:var(--color-text-dark);">Create &amp; Schedule</h4>
                <p style="color:var(--color-text-muted); font-size:0.9rem; line-height:1.55; margin:0;">Admin adds questions, sets passing marks, configures timer &amp; proctoring toggles.</p>
            </div>
            <div class="glass-light" style="padding:26px;">
                <div style="font-family:var(--font-mono); color:var(--color-indigo); font-weight:800; font-size:1.1rem; margin-bottom:12px;">02. AUTH</div>
                <h4 style="font-size:1.05rem; margin-bottom:8px; color:var(--color-text-dark);">Student Verification</h4>
                <p style="color:var(--color-text-muted); font-size:0.9rem; line-height:1.55; margin:0;">Student logs in, reviews instructions, checks webcam &amp; grants camera permission.</p>
            </div>
            <div class="glass-light" style="padding:26px;">
                <div style="font-family:var(--font-mono); color:var(--color-indigo); font-weight:800; font-size:1.1rem; margin-bottom:12px;">03. MONITOR</div>
                <h4 style="font-size:1.05rem; margin-bottom:8px; color:var(--color-text-dark);">AI Proctoring</h4>
                <p style="color:var(--color-text-muted); font-size:0.9rem; line-height:1.55; margin:0;">AI tracks face presence, tab switching, and fullscreen compliance in real time.</p>
            </div>
            <div class="glass-light" style="padding:26px;">
                <div style="font-family:var(--font-mono); color:var(--color-indigo); font-weight:800; font-size:1.1rem; margin-bottom:12px;">04. REPORT</div>
                <h4 style="font-size:1.05rem; margin-bottom:8px; color:var(--color-text-dark);">Instant Results</h4>
                <p style="color:var(--color-text-muted); font-size:0.9rem; line-height:1.55; margin:0;">Automated grading generates score breakdown immediately while admin audits risk logs.</p>
            </div>
        </div>
    </div>
</section>

<!-- ============== AI MONITORING ============== -->
<section class="section dark-section" id="monitoring">
    <div class="container-xl">
        <div class="section-head">
            <span class="eyebrow-dark">AI Proctoring Engine</span>
            <h2>Multi-layered integrity protection</h2>
            <p>Every attempt is scanned continuously using client-side AI and browser event hooks.</p>
        </div>
        <div class="monitor-grid">
            <div class="monitor-item glass-dark">
                <div class="icon-box"><i class="fa-solid fa-user-xmark"></i></div>
                <div><h4>Face Absence &amp; Multi-Face Detection</h4><p>Flags when the candidate leaves the webcam frame or when multiple faces appear in view.</p></div>
            </div>
            <div class="monitor-item glass-dark">
                <div class="icon-box"><i class="fa-solid fa-arrow-up-right-from-square"></i></div>
                <div><h4>Tab &amp; Window Tracking</h4><p>Logs every tab switch, window blur, or browser focus loss event with timestamps.</p></div>
            </div>
            <div class="monitor-item glass-dark">
                <div class="icon-box"><i class="fa-solid fa-expand"></i></div>
                <div><h4>Fullscreen Enforcement</h4><p>Enforces full-screen mode throughout the exam and prompts candidate to return upon exit.</p></div>
            </div>
            <div class="monitor-item glass-dark">
                <div class="icon-box"><i class="fa-solid fa-gauge-high"></i></div>
                <div><h4>Dynamic Risk Scoring</h4><p>Aggregates every violation signal into one cumulative score, auto-flagging critical attempts.</p></div>
            </div>
        </div>
    </div>
</section>

<!-- ============== TESTIMONIALS ============== -->
<section class="section" id="testimonials">
    <div class="container-xl">
        <div class="section-head">
            <span class="eyebrow-dark">Institutional Trust</span>
            <h2>Trusted by examination controllers</h2>
        </div>
        <div class="testimonial-grid">
            <div class="glass-light testimonial-card">
                <p class="quote">"Setting up an exam with randomized question pools took minutes, and the
                   violation feed gave us real visibility we never had with manual proctoring."</p>
                <div class="testimonial-person">
                    <div class="testimonial-avatar">RK</div>
                    <div><div class="name">Dr. Ramesh Kulkarni</div><div class="role">Chief Examination Controller</div></div>
                </div>
            </div>
            <div class="glass-light testimonial-card">
                <p class="quote">"The fullscreen prompts and webcam check felt strict but completely fair &mdash;
                   candidates always knew exactly why a warning appeared."</p>
                <div class="testimonial-person">
                    <div class="testimonial-avatar">SN</div>
                    <div><div class="name">Sneha Nambiar</div><div class="role">Head of Assessment</div></div>
                </div>
            </div>
            <div class="glass-light testimonial-card">
                <p class="quote">"Results and pass/fail status generated the second a student submitted.
                   No waiting around for manual grading or verification."</p>
                <div class="testimonial-person">
                    <div class="testimonial-avatar">AV</div>
                    <div><div class="name">Arjun Verma</div><div class="role">Academic Director</div></div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- ============== ENTERPRISE CONTACT SECTION ============== -->
<section class="section" id="contact" style="background:linear-gradient(180deg, #F8FAFC 0%, #EEF2F6 100%); border-top:1px solid #E2E8F0;">
    <div class="container-xl contact-grid">
        <div>
            <span class="eyebrow-dark">Enterprise Support</span>
            <h2 style="margin-top:12px; font-size:2.2rem; font-family:var(--font-heading);">Questions about deployment?</h2>
            <p style="color:var(--color-text-muted); margin-top:16px; line-height:1.65; font-size:1.02rem;">
                Reach out to our dedicated examination support engineering team for custom institution onboarding, SSO integration, or dedicated server deployment.
            </p>
            
            <div class="contact-card-info">
                <div class="contact-info-item">
                    <div class="info-icon"><i class="fa-solid fa-envelope"></i></div>
                    <div>
                        <div class="info-title">Support Email</div>
                        <div class="info-value">support@aiexam.com</div>
                    </div>
                </div>
                <div class="contact-info-item">
                    <div class="info-icon"><i class="fa-solid fa-clock"></i></div>
                    <div>
                        <div class="info-title">Response SLA</div>
                        <div class="info-value">&lt; 15 Minutes Average Turnaround</div>
                    </div>
                </div>
                <div class="contact-info-item">
                    <div class="info-icon"><i class="fa-solid fa-shield-check"></i></div>
                    <div>
                        <div class="info-title">Security Standard</div>
                        <div class="info-value">ISO 27001 &amp; GDPR Compliant Engine</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="glass-light" style="padding:36px; border-radius:var(--radius-xl); box-shadow:0 20px 45px rgba(15,23,42,0.08);">
            <h3 style="font-size:1.3rem; margin-bottom:8px; color:var(--color-text-dark);">Send us a Message</h3>
            <p style="font-size:0.88rem; color:var(--color-text-muted); margin-bottom:24px;">Fill out your details and our team will get back to you immediately.</p>
            
            <form id="contactForm" onsubmit="event.preventDefault(); alert('Thank you! Your message has been routed to the examination support team.');">
                <div class="form-floating-group">
                    <label for="contactName"><i class="fa-solid fa-user" style="color:var(--color-indigo);"></i> Full Name</label>
                    <input type="text" id="contactName" class="form-control-custom" placeholder="e.g. Dr. Alex Morgan" required>
                </div>
                <div class="form-floating-group">
                    <label for="contactEmail"><i class="fa-solid fa-envelope" style="color:var(--color-indigo);"></i> Work Email</label>
                    <input type="email" id="contactEmail" class="form-control-custom" placeholder="alex.morgan@university.edu" required>
                </div>
                <div class="form-floating-group">
                    <label for="contactMsg"><i class="fa-solid fa-message" style="color:var(--color-indigo);"></i> Message</label>
                    <textarea id="contactMsg" class="form-control-custom" placeholder="Tell us about your institution's examination requirements..." required></textarea>
                </div>
                <button type="submit" class="btn-gradient btn-block" style="padding:15px; font-size:1rem;">
                    <i class="fa-solid fa-paper-plane"></i> Send Enterprise Inquiry
                </button>
            </form>
        </div>
    </div>
</section>

<!-- ============== FOOTER ============== -->
<footer class="site-footer">
    <div class="container-xl">
        <div class="footer-grid">
            <div>
                <div class="brand" style="margin-bottom:14px;">
                    <span class="brand-mark"><i class="fa-solid fa-shield-halved"></i></span>
                    AI Secure Exam
                </div>
                <p style="font-size:0.88rem; line-height:1.6; max-width:320px;">An enterprise role-based online examination platform with real-time AI proctoring, automated evaluation, and audit logs.</p>
            </div>
            <div>
                <h5>Platform</h5>
                <a href="#features">Features</a><br>
                <a href="#how-it-works">Workflow</a><br>
                <a href="#monitoring">AI Engine</a><br>
                <a href="<%=request.getContextPath()%>/login">Sign In</a>
            </div>
            <div>
                <h5>Account</h5>
                <a href="<%=request.getContextPath()%>/register">Student Registration</a><br>
                <a href="<%=request.getContextPath()%>/forgot-password">Forgot Password</a>
            </div>
            <div>
                <h5>Support</h5>
                <a href="#contact">Contact Support</a><br>
                <a href="#">Security Overview</a><br>
                <a href="#">System SLA</a>
            </div>
        </div>
        <div class="footer-bottom">
            <span>&copy; 2026 AI Secure Exam System. All rights reserved.</span>
            <span>Built with Java 17 &middot; Servlets &middot; H2 / MySQL 8.x</span>
        </div>
    </div>
</footer>

<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
