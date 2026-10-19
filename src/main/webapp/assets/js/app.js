/**
 * Career Recommendation System for B.Tech CSE Students
 * Interactive JavaScript for Mobile Navigation, Assessment Stepper,
 * Validation, and Modal Dialogs.
 */

document.addEventListener('DOMContentLoaded', function () {
    initMobileNav();
    initAssessmentStepper();
    initFormValidations();
    initModals();
});

/* --------------------------------------------------------------------------
   Mobile Navigation
   -------------------------------------------------------------------------- */
function initMobileNav() {
    var toggleBtn = document.querySelector('.nav-toggle');
    var navMenu = document.querySelector('.nav-menu');

    if (toggleBtn && navMenu) {
        toggleBtn.addEventListener('click', function () {
            navMenu.classList.toggle('active');
            var isExpanded = navMenu.classList.contains('active');
            toggleBtn.setAttribute('aria-expanded', isExpanded);
        });
    }
}

/* --------------------------------------------------------------------------
   20-Question Assessment Stepper
   -------------------------------------------------------------------------- */
function initAssessmentStepper() {
    var stepperForm = document.getElementById('assessmentForm');
    if (!stepperForm) return;

    var questionSteps = document.querySelectorAll('.assessment-step');
    var totalQuestions = questionSteps.length;
    var currentStepIndex = 0;

    var prevBtn = document.getElementById('prevQuestionBtn');
    var nextBtn = document.getElementById('nextQuestionBtn');
    var reviewBtn = document.getElementById('reviewQuestionsBtn');
    var submitBtn = document.getElementById('submitAssessmentBtn');
    var confirmModal = document.getElementById('confirmSubmitModal');

    var currentNumberSpan = document.getElementById('currentQuestionNum');
    var progressPercentSpan = document.getElementById('progressPercent');
    var progressBarFill = document.getElementById('progressBarFill');

    function updateStepView() {
        questionSteps.forEach(function (step, index) {
            if (index === currentStepIndex) {
                step.style.display = 'block';
            } else {
                step.style.display = 'none';
            }
        });

        // Update progress counters
        var qNum = currentStepIndex + 1;
        if (currentNumberSpan) currentNumberSpan.textContent = qNum;

        // Calculate answered count
        var answeredCount = countAnsweredQuestions();
        var percent = Math.round((answeredCount / totalQuestions) * 100);

        if (progressPercentSpan) progressPercentSpan.textContent = percent + '%';
        if (progressBarFill) progressBarFill.style.width = percent + '%';

        // Nav buttons visibility
        if (prevBtn) {
            prevBtn.style.display = (currentStepIndex === 0) ? 'none' : 'inline-flex';
        }

        if (nextBtn) {
            nextBtn.style.display = (currentStepIndex < totalQuestions - 1) ? 'inline-flex' : 'none';
        }

        if (reviewBtn) {
            reviewBtn.style.display = (currentStepIndex === totalQuestions - 1) ? 'inline-flex' : 'none';
        }

        // Scroll step to top smoothly
        window.scrollTo({ top: stepperForm.offsetTop - 80, behavior: 'smooth' });
    }

    function countAnsweredQuestions() {
        var count = 0;
        questionSteps.forEach(function (step) {
            var radios = step.querySelectorAll('input[type="radio"]');
            var isAnswered = Array.prototype.some.call(radios, function (radio) {
                return radio.checked;
            });
            if (isAnswered) count++;
        });
        return count;
    }

    function isCurrentStepAnswered() {
        var currentStep = questionSteps[currentStepIndex];
        var radios = currentStep.querySelectorAll('input[type="radio"]');
        return Array.prototype.some.call(radios, function (radio) {
            return radio.checked;
        });
    }

    // Radio Card click handling
    var optionCards = document.querySelectorAll('.option-card');
    optionCards.forEach(function (card) {
        card.addEventListener('click', function (e) {
            var radio = this.querySelector('input[type="radio"]');
            if (radio && e.target !== radio) {
                radio.checked = true;
                // Trigger change event
                var event = new Event('change', { bubbles: true });
                radio.dispatchEvent(event);
            }
            // Update active state visual
            var parent = this.closest('.options-list');
            if (parent) {
                parent.querySelectorAll('.option-card').forEach(function (c) {
                    c.classList.remove('selected');
                });
            }
            this.classList.add('selected');
        });
    });

    // Mark existing pre-selected radios
    document.querySelectorAll('input[type="radio"]:checked').forEach(function (radio) {
        var card = radio.closest('.option-card');
        if (card) card.classList.add('selected');
    });

    if (nextBtn) {
        nextBtn.addEventListener('click', function () {
            if (!isCurrentStepAnswered()) {
                alert('Please select an interest level before proceeding to the next question.');
                return;
            }
            if (currentStepIndex < totalQuestions - 1) {
                currentStepIndex++;
                updateStepView();
            }
        });
    }

    if (prevBtn) {
        prevBtn.addEventListener('click', function () {
            if (currentStepIndex > 0) {
                currentStepIndex--;
                updateStepView();
            }
        });
    }

    if (reviewBtn) {
        reviewBtn.addEventListener('click', function () {
            if (!isCurrentStepAnswered()) {
                alert('Please select an interest level for this question.');
                return;
            }
            var answered = countAnsweredQuestions();
            if (answered < totalQuestions) {
                alert('You have answered ' + answered + ' of ' + totalQuestions + ' questions. Please answer all 20 questions to get accurate career recommendations.');
                return;
            }
            // Open confirmation modal
            if (confirmModal) {
                confirmModal.classList.add('active');
            }
        });
    }

    // Modal Confirmation submission
    if (stepperForm) {
        stepperForm.addEventListener('submit', function (e) {
            var answered = countAnsweredQuestions();
            if (answered < totalQuestions) {
                e.preventDefault();
                alert('Incomplete assessment: Please answer all ' + totalQuestions + ' questions before submitting.');
                return false;
            }

            // Prevent double submission
            var finalSubmit = document.getElementById('finalSubmitBtn');
            if (finalSubmit) {
                finalSubmit.disabled = true;
                finalSubmit.innerHTML = 'Computing Recommendations...';
            }
            return true;
        });
    }

    // Initialize first step
    updateStepView();
}

/* --------------------------------------------------------------------------
   Client-Side Form Validations
   -------------------------------------------------------------------------- */
function initFormValidations() {
    var regForm = document.getElementById('registerForm');
    if (regForm) {
        regForm.addEventListener('submit', function (e) {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            var email = document.getElementById('email').value;

            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match. Please verify your password confirmation.');
                return false;
            }

            if (password.length < 6) {
                e.preventDefault();
                alert('Password must be at least 6 characters long.');
                return false;
            }

            var emailPattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
            if (!emailPattern.test(email)) {
                e.preventDefault();
                alert('Please enter a valid email address.');
                return false;
            }
        });
    }
}

/* --------------------------------------------------------------------------
   Modal Dialog Management
   -------------------------------------------------------------------------- */
function initModals() {
    // Close button triggers
    document.querySelectorAll('.modal-close, .modal-cancel-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var modal = this.closest('.modal-overlay');
            if (modal) {
                modal.classList.remove('active');
            }
        });
    });

    // Close when clicking outside modal box
    document.querySelectorAll('.modal-overlay').forEach(function (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target === this) {
                this.classList.remove('active');
            }
        });
    });
}

function openModal(modalId) {
    var modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
}
