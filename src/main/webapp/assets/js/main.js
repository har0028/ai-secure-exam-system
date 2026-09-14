/* ============================================================
   Shared front-end behaviors for the AI Secure Exam System.
   Scoped defensively (feature checks) since this same file is
   loaded on the landing page, auth pages, and dashboards, each
   of which only contains a subset of these elements.
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {

    // --- Navbar background on scroll (landing page) ---
    var nav = document.querySelector('.site-nav');
    if (nav) {
        window.addEventListener('scroll', function () {
            if (window.scrollY > 30) {
                nav.classList.add('scrolled');
            } else {
                nav.classList.remove('scrolled');
            }
        });
    }

    // --- Password show/hide toggles ---
    document.querySelectorAll('.auth-toggle-visibility').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var targetId = btn.getAttribute('data-target');
            var input = document.getElementById(targetId);
            if (!input) return;
            var isPassword = input.getAttribute('type') === 'password';
            input.setAttribute('type', isPassword ? 'text' : 'password');
            btn.innerHTML = isPassword
                ? '<i class="fa-solid fa-eye-slash"></i>'
                : '<i class="fa-solid fa-eye"></i>';
        });
    });

    // --- Live password-strength hint on register page ---
    var newPasswordInputs = document.querySelectorAll('[data-password-strength]');
    newPasswordInputs.forEach(function (input) {
        var hintEl = document.querySelector(input.getAttribute('data-password-strength'));
        if (!hintEl) return;
        input.addEventListener('input', function () {
            var v = input.value;
            var checks = [
                { ok: v.length >= 8, label: '8+ characters' },
                { ok: /[A-Z]/.test(v), label: 'uppercase letter' },
                { ok: /[a-z]/.test(v), label: 'lowercase letter' },
                { ok: /\d/.test(v), label: 'number' },
                { ok: /[@#$%^&+=!]/.test(v), label: 'special character' }
            ];
            var missing = checks.filter(function (c) { return !c.ok; }).map(function (c) { return c.label; });
            hintEl.textContent = missing.length
                ? 'Still needed: ' + missing.join(', ')
                : 'Password strength looks good.';
            hintEl.style.color = missing.length ? '' : '#A7F3D0';
        });
    });

    // --- Confirm-password match check ---
    var confirmPairs = document.querySelectorAll('[data-confirm-target]');
    confirmPairs.forEach(function (confirmInput) {
        var original = document.getElementById(confirmInput.getAttribute('data-confirm-target'));
        var msgEl = document.querySelector(confirmInput.getAttribute('data-confirm-msg'));
        function check() {
            if (!msgEl) return;
            if (confirmInput.value.length === 0) {
                msgEl.textContent = '';
                return;
            }
            msgEl.textContent = confirmInput.value === original.value ? 'Passwords match.' : 'Passwords do not match.';
            msgEl.style.color = confirmInput.value === original.value ? '#A7F3D0' : '#FECDD3';
        }
        confirmInput.addEventListener('input', check);
        if (original) original.addEventListener('input', check);
    });

    // --- Risk ring renderer ---
    // Usage: <div class="risk-ring" data-score="34"></div>
    document.querySelectorAll('.risk-ring[data-score]').forEach(function (el) {
        renderRiskRing(el, parseInt(el.getAttribute('data-score'), 10));
    });

    // --- Mobile sidebar toggle (dashboards) ---
    var sidebarToggles = document.querySelectorAll('.sidebar-toggle');
    var appShell = document.querySelector('.app-shell');
    if (sidebarToggles.length && appShell) {
        sidebarToggles.forEach(function (btn) {
            btn.addEventListener('click', function (e) {
                e.stopPropagation();
                appShell.classList.toggle('sidebar-open');
            });
        });
        document.querySelectorAll('.sidebar-nav a').forEach(function (link) {
            link.addEventListener('click', function () {
                appShell.classList.remove('sidebar-open');
            });
        });
    }
});

function riskLevelFor(score) {
    if (score <= 25) return { level: 'low', label: 'Low Risk' };
    if (score <= 50) return { level: 'medium', label: 'Medium Risk' };
    if (score <= 75) return { level: 'high', label: 'High Risk' };
    return { level: 'critical', label: 'Critical Risk' };
}

function renderRiskRing(container, score) {
    score = Math.max(0, Math.min(100, score || 0));
    var info = riskLevelFor(score);
    var radius = 52;
    var circumference = 2 * Math.PI * radius;
    var offset = circumference - (score / 100) * circumference;

    container.innerHTML =
        '<svg viewBox="0 0 132 132">' +
        '  <circle class="ring-track" cx="66" cy="66" r="' + radius + '"></circle>' +
        '  <circle class="ring-progress risk-' + info.level + '" cx="66" cy="66" r="' + radius + '" ' +
        '    stroke-dasharray="' + circumference + '" stroke-dashoffset="' + circumference + '"></circle>' +
        '</svg>' +
        '<div class="ring-label">' +
        '  <span class="ring-score">' + score + '</span>' +
        '  <span class="ring-tag">' + info.label + '</span>' +
        '</div>';

    // animate after paint
    requestAnimationFrame(function () {
        var progressCircle = container.querySelector('.ring-progress');
        if (progressCircle) progressCircle.style.strokeDashoffset = offset;
    });
}
