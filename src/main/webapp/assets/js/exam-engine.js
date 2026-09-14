/**
 * exam-engine.js  –  Exam taking engine
 * Handles: timer countdown, question navigation, answer saving,
 * mark-for-review, autosave (every 30s), manual submit confirmation,
 * and fullscreen enforcement.
 */
(function () {
  'use strict';

  /* ── Bootstrap data injected by JSP ── */
  var ATTEMPT_ID = window.EXAM_ATTEMPT_ID;
  var EXAM_DURATION_MINS = window.EXAM_DURATION_MINUTES;
  var START_TIME_MS = window.EXAM_START_TIME_MS;
  var CONTEXT_PATH = window.CONTEXT_PATH || '';
  var SAVED_ANSWERS = window.EXISTING_ANSWERS || {};   // { questionId: { selectedOption, markedForReview } }

  var currentQuestionIndex = 0;
  var questions = [];         // NodeList → Array of .question-card elements
  var autoSaveTimer = null;
  var countdownTimer = null;
  var lastSaveIndicator = null;

  /* ── Init ── */
  document.addEventListener('DOMContentLoaded', function () {
    questions = Array.from(document.querySelectorAll('.question-card'));
    lastSaveIndicator = document.getElementById('save-indicator');

    restoreAnswers();
    buildPalette();
    showQuestion(0);
    startCountdown();
    startAutoSave();
    enforceFullscreen();

    document.getElementById('btn-prev') && document.getElementById('btn-prev').addEventListener('click', function () { navigate(-1); });
    document.getElementById('btn-next') && document.getElementById('btn-next').addEventListener('click', function () { navigate(1); });
    document.getElementById('btn-mark') && document.getElementById('btn-mark').addEventListener('click', toggleMarkForReview);
    document.getElementById('btn-submit') && document.getElementById('btn-submit').addEventListener('click', confirmSubmit);
  });

  /* ── Restore saved answers on page load/refresh ── */
  function restoreAnswers() {
    Object.keys(SAVED_ANSWERS).forEach(function (qId) {
      var saved = SAVED_ANSWERS[qId];
      var card = document.querySelector('[data-question-id="' + qId + '"]');
      if (!card) return;
      if (saved.selectedOption) {
        var radio = card.querySelector('input[value="' + saved.selectedOption + '"]');
        if (radio) radio.checked = true;
      }
      if (saved.markedForReview) {
        card.dataset.marked = 'true';
      }
    });
  }

  /* ── Question display ── */
  function showQuestion(idx) {
    questions.forEach(function (q, i) { q.style.display = i === idx ? 'block' : 'none'; });
    currentQuestionIndex = idx;
    updatePaletteHighlight();
    updateNavButtons();
    var card = questions[idx];
    if (card) {
      var markBtn = document.getElementById('btn-mark');
      if (markBtn) {
        markBtn.textContent = card.dataset.marked === 'true' ? '★ Marked' : '☆ Mark for Review';
      }
    }
  }

  function navigate(delta) {
    var newIdx = currentQuestionIndex + delta;
    if (newIdx >= 0 && newIdx < questions.length) {
      saveCurrentAnswer(function () { showQuestion(newIdx); });
    }
  }

  function updateNavButtons() {
    var prev = document.getElementById('btn-prev');
    var next = document.getElementById('btn-next');
    if (prev) prev.disabled = currentQuestionIndex === 0;
    if (next) next.disabled = currentQuestionIndex === questions.length - 1;
  }

  /* ── Palette ── */
  function buildPalette() {
    var palette = document.getElementById('question-palette');
    if (!palette) return;
    palette.innerHTML = '';
    questions.forEach(function (q, i) {
      var btn = document.createElement('button');
      btn.className = 'palette-btn';
      btn.textContent = i + 1;
      btn.dataset.qIndex = i;
      btn.addEventListener('click', function () {
        saveCurrentAnswer(function () { showQuestion(i); });
      });
      palette.appendChild(btn);
    });
  }

  function updatePaletteHighlight() {
    document.querySelectorAll('.palette-btn').forEach(function (btn, i) {
      btn.classList.toggle('active', i === currentQuestionIndex);
      var card = questions[i];
      if (!card) return;
      var answered = card.querySelector('input[type="radio"]:checked');
      btn.classList.toggle('answered', !!answered);
      btn.classList.toggle('marked', card.dataset.marked === 'true');
    });
  }

  /* ── Mark for review ── */
  function toggleMarkForReview() {
    var card = questions[currentQuestionIndex];
    if (!card) return;
    card.dataset.marked = card.dataset.marked === 'true' ? 'false' : 'true';
    var markBtn = document.getElementById('btn-mark');
    if (markBtn) markBtn.textContent = card.dataset.marked === 'true' ? '★ Marked' : '☆ Mark for Review';
    updatePaletteHighlight();
    saveCurrentAnswer(null);
  }

  /* ── Answer save ── */
  function saveCurrentAnswer(callback) {
    var card = questions[currentQuestionIndex];
    if (!card) { if (callback) callback(); return; }
    var qId = card.dataset.questionId;
    var selectedRadio = card.querySelector('input[type="radio"]:checked');
    var selected = selectedRadio ? selectedRadio.value : '';
    var marked = card.dataset.marked === 'true';
    sendSave(qId, selected, marked, callback);
    updatePaletteHighlight();
  }

  function sendSave(qId, selected, marked, callback) {
    var params = 'attemptId=' + encodeURIComponent(ATTEMPT_ID) +
                 '&questionId=' + encodeURIComponent(qId) +
                 '&selectedOption=' + encodeURIComponent(selected) +
                 '&markedForReview=' + encodeURIComponent(marked);
    fetch(CONTEXT_PATH + '/student/exam/save-answer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    }).then(function (r) { return r.json(); }).then(function (data) {
      showSaveIndicator(data.success ? 'Saved' : 'Save failed');
      if (data.autoSubmitted) { window.location.href = CONTEXT_PATH + '/student/exam/result?attemptId=' + ATTEMPT_ID; return; }
      if (callback) callback();
    }).catch(function () { if (callback) callback(); });
  }

  function saveAllAnswers(callback) {
    var saves = [];
    questions.forEach(function (card) {
      var qId = card.dataset.questionId;
      var sel = card.querySelector('input[type="radio"]:checked');
      saves.push({ qId: qId, selected: sel ? sel.value : '', marked: card.dataset.marked === 'true' });
    });
    var done = 0;
    if (saves.length === 0) { if (callback) callback(); return; }
    saves.forEach(function (s) {
      sendSave(s.qId, s.selected, s.marked, function () {
        done++;
        if (done === saves.length && callback) callback();
      });
    });
  }

  function showSaveIndicator(msg) {
    if (!lastSaveIndicator) return;
    lastSaveIndicator.textContent = msg + ' at ' + new Date().toLocaleTimeString();
  }

  /* ── Autosave (every 30s) ── */
  function startAutoSave() {
    autoSaveTimer = setInterval(function () {
      saveCurrentAnswer(null);
    }, 30000);
  }

  /* ── Countdown timer ── */
  function startCountdown() {
    var timerEl = document.getElementById('exam-timer');
    if (!timerEl) return;
    var endMs = START_TIME_MS + EXAM_DURATION_MINS * 60 * 1000;

    function tick() {
      var remaining = Math.max(0, endMs - Date.now());
      var mins = Math.floor(remaining / 60000);
      var secs = Math.floor((remaining % 60000) / 1000);
      timerEl.textContent = String(mins).padStart(2, '0') + ':' + String(secs).padStart(2, '0');
      if (remaining <= 5 * 60 * 1000) timerEl.style.color = 'var(--color-risk-critical)';
      if (remaining <= 0) {
        clearInterval(countdownTimer);
        autoSubmit();
      }
    }
    tick();
    countdownTimer = setInterval(tick, 1000);
  }

  /* ── Submit ── */
  function confirmSubmit() {
    if (!confirm('Submit this exam now? You will not be able to change your answers after submission.')) return;
    saveAllAnswers(function () { submitExam(); });
  }

  function autoSubmit() {
    saveAllAnswers(function () { submitExam(); });
  }

  function submitExam() {
    clearInterval(autoSaveTimer);
    clearInterval(countdownTimer);
    var form = document.getElementById('submit-form');
    if (form) { form.submit(); return; }
    fetch(CONTEXT_PATH + '/student/exam/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'attemptId=' + encodeURIComponent(ATTEMPT_ID)
    }).then(function () {
      window.location.href = CONTEXT_PATH + '/student/exam/result?attemptId=' + ATTEMPT_ID;
    });
  }

  /* ── Fullscreen enforcement ── */
  function enforceFullscreen() {
    requestFullscreen();
    document.addEventListener('fullscreenchange', function () {
      if (!document.fullscreenElement) {
        showFullscreenWarning();
      }
    });
  }

  function requestFullscreen() {
    var el = document.documentElement;
    if (el.requestFullscreen) el.requestFullscreen();
    else if (el.webkitRequestFullscreen) el.webkitRequestFullscreen();
  }

  function showFullscreenWarning() {
    var warn = document.getElementById('fullscreen-warning');
    if (warn) warn.style.display = 'flex';
  }

  window.returnToFullscreen = function () {
    var warn = document.getElementById('fullscreen-warning');
    if (warn) warn.style.display = 'none';
    requestFullscreen();
  };

})();
