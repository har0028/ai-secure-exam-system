/**
 * proctoring.js  –  AI-Powered Proctoring Engine
 *
 * Loads face-api.js models, starts the webcam, then continuously:
 *   • Detects face presence / absence / multiple faces (Face-api.js TinyFaceDetector)
 *   • Tracks head-pose/gaze direction to detect "looking away"
 *   • Monitors tab/window visibility (visibilitychange + blur/focus)
 *   • Monitors fullscreen exits
 *   • Requests mic access and samples audio level for unusual noise
 *
 * Every violation is POSTed to /proctoring/event. The server returns the
 * updated risk score, which is reflected in the on-screen risk ring.
 */
(function () {
  'use strict';

  var CONTEXT_PATH = window.CONTEXT_PATH || '';
  var ATTEMPT_ID = window.EXAM_ATTEMPT_ID;

  /* ── Risk ring DOM element ── */
  var riskContainer = document.getElementById('risk-ring-container');

  /* ── Violation counters (per session, not persisted - the server tracks true totals) ── */
  var faceMissingCount = 0;
  var tabSwitchCount = 0;
  var fullscreenExitCount = 0;

  /* ── Warning overlay ── */
  var warningOverlay = document.getElementById('proctoring-warning');
  var warningText = document.getElementById('proctoring-warning-text');

  function showWarning(msg) {
    if (warningText) warningText.textContent = msg;
    if (warningOverlay) warningOverlay.style.display = 'flex';
    setTimeout(function () {
      if (warningOverlay) warningOverlay.style.display = 'none';
    }, 4000);
  }

  /* ── POST violation to server ── */
  function reportEvent(eventType, details) {
    if (!ATTEMPT_ID) return;
    fetch(CONTEXT_PATH + '/proctoring/event', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'attemptId=' + encodeURIComponent(ATTEMPT_ID) +
            '&eventType=' + encodeURIComponent(eventType) +
            '&details=' + encodeURIComponent(details || '')
    })
    .then(function (r) { return r.json(); })
    .then(function (data) {
      updateRiskRing(data.riskScore, data.riskLevel);
      if (data.autoSubmitted) {
        window.location.href = CONTEXT_PATH + '/student/exam/result?attemptId=' + ATTEMPT_ID;
      }
    })
    .catch(function () { /* silent – never block the student's exam on a network hiccup */ });
  }

  function updateRiskRing(score, level) {
    if (!riskContainer) return;
    riskContainer.dataset.score = score;
    if (window.renderRiskRing) {
      window.renderRiskRing(riskContainer, parseInt(score, 10));
    }
  }

  /* ── Tab / Window visibility monitoring ── */
  document.addEventListener('visibilitychange', function () {
    if (document.hidden) {
      tabSwitchCount++;
      var severity = tabSwitchCount === 1 ? 'First tab switch warning.' :
                     tabSwitchCount === 2 ? 'Second tab switch - serious warning.' :
                                            'Repeated tab switching detected.';
      showWarning('Tab switch detected! ' + severity);
      reportEvent('TAB_SWITCH', 'Tab hidden. Count: ' + tabSwitchCount);
    }
  });

  window.addEventListener('blur', function () {
    showWarning('Window focus lost! Please keep the exam window in focus.');
    reportEvent('WINDOW_BLUR', 'Window blur event.');
  });

  /* ── Fullscreen exit monitoring ── */
  document.addEventListener('fullscreenchange', function () {
    if (!document.fullscreenElement) {
      fullscreenExitCount++;
      showWarning('Fullscreen exited! Please return to fullscreen to continue.');
      reportEvent('FULLSCREEN_EXIT', 'Fullscreen exited. Count: ' + fullscreenExitCount);
    }
  });

  /* ── Webcam + Face detection (face-api.js) ── */
  var videoEl = document.getElementById('proctoring-video');
  var canvasEl = document.getElementById('proctoring-canvas');
  var faceDetectionInterval = null;
  var modelsLoaded = false;
  var FACE_API_MODEL_URL = CONTEXT_PATH + '/assets/face-api-models';

  function initWebcam() {
    if (!videoEl) return;
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      reportEvent('CAMERA_DISABLED', 'getUserMedia not supported.');
      return;
    }
    navigator.mediaDevices.getUserMedia({ video: true, audio: false })
      .then(function (stream) {
        videoEl.srcObject = stream;
        videoEl.play();
        loadFaceApiModels();
      })
      .catch(function (err) {
        reportEvent('CAMERA_DISABLED', 'Webcam access denied: ' + err.message);
        showWarning('Webcam is required for this proctored exam. Please allow camera access.');
      });
  }

  function loadFaceApiModels() {
    if (!window.faceapi) {
      console.warn('face-api.js not loaded. Face detection disabled.');
      return;
    }
    Promise.all([
      faceapi.nets.tinyFaceDetector.loadFromUri(FACE_API_MODEL_URL),
      faceapi.nets.faceLandmark68TinyNet.loadFromUri(FACE_API_MODEL_URL)
    ]).then(function () {
      modelsLoaded = true;
      startFaceDetection();
    }).catch(function (err) {
      console.warn('face-api model load failed:', err);
    });
  }

  function startFaceDetection() {
    faceDetectionInterval = setInterval(function () {
      if (!modelsLoaded || videoEl.paused || videoEl.ended) return;
      faceapi.detectAllFaces(videoEl, new faceapi.TinyFaceDetectorOptions())
        .then(function (detections) {
          var count = detections.length;
          if (count === 0) {
            faceMissingCount++;
            var msg = faceMissingCount === 1 ? 'Face not visible - warning.'
                    : faceMissingCount === 2 ? 'Face still missing - risk score increased.'
                    : 'Face repeatedly missing - candidate flagged.';
            showWarning('Face not detected! ' + msg);
            reportEvent('FACE_MISSING', 'No face detected. Consecutive count: ' + faceMissingCount);
          } else if (count > 1) {
            showWarning('Multiple faces detected! Only the exam candidate should be visible.');
            reportEvent('MULTIPLE_FACES', count + ' faces detected in frame.');
          } else {
            faceMissingCount = 0;   // reset consecutive counter on successful detection
            checkGaze(detections[0]);
          }
        });
    }, 3000);   // sample every 3 seconds
  }

  function checkGaze(detection) {
    // Simplified gaze: if the face bounding box is significantly off-center, flag it.
    if (!videoEl || !detection) return;
    var box = detection.box;
    var frameW = videoEl.videoWidth;
    if (!frameW) return;
    var centerX = box.x + box.width / 2;
    var ratio = centerX / frameW;
    if (ratio < 0.20 || ratio > 0.80) {
      showWarning('Looking away detected! Please keep your eyes on the screen.');
      reportEvent('LOOKING_AWAY', 'Face center ratio: ' + ratio.toFixed(2));
    }
  }

  /* ── Microphone monitoring (audio level) ── */
  var micStream = null;
  var audioContext = null;
  var analyser = null;

  function initMicrophone() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) return;
    navigator.mediaDevices.getUserMedia({ audio: true, video: false })
      .then(function (stream) {
        micStream = stream;
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
        var source = audioContext.createMediaStreamSource(stream);
        analyser = audioContext.createAnalyser();
        analyser.fftSize = 256;
        source.connect(analyser);
        monitorAudio();
      })
      .catch(function () {
        reportEvent('MIC_DISABLED', 'Microphone access denied.');
      });
  }

  function monitorAudio() {
    var dataArray = new Uint8Array(analyser.frequencyBinCount);
    var NOISE_THRESHOLD = 80;   // 0-255 scale
    var quietStreak = 0;

    setInterval(function () {
      if (!analyser) return;
      analyser.getByteFrequencyData(dataArray);
      var avg = dataArray.reduce(function (a, b) { return a + b; }, 0) / dataArray.length;
      if (avg > NOISE_THRESHOLD) {
        quietStreak = 0;
        reportEvent('UNUSUAL_NOISE', 'Average audio level: ' + avg.toFixed(0));
      } else {
        quietStreak++;
      }
    }, 5000);
  }

  /* ── Boot ── */
  document.addEventListener('DOMContentLoaded', function () {
    initWebcam();
    initMicrophone();
  });

  /* Expose to exam-engine for auto-submit sync */
  window.PROCTORING_STOP = function () {
    clearInterval(faceDetectionInterval);
    if (micStream) micStream.getTracks().forEach(function (t) { t.stop(); });
    if (audioContext) audioContext.close();
  };

})();
