import { useNavigate } from 'react-router-dom';
import './LandingPage.css';

function LandingPage() {
  const navigate = useNavigate();

  return (
    <div className="landing-page">
      <nav className="navbar">
        <div className="container">
          <h2 className="logo">🩺 BloodReport.ai</h2>
          <button className="cta-button-nav" onClick={() => navigate('/analyzer')}>
            Get Started →
          </button>
        </div>
      </nav>

      <main className="hero-section">
        <div className="container">
          <div className="hero-content">
            <div className="hero-badge">
              <span className="badge-icon">✨</span>
              <span>Powered by AI</span>
            </div>
            
            <h1 className="hero-title">
              Understand Your Blood Report
              <span className="title-highlight"> Like You're 10</span>
            </h1>
            
            <p className="hero-description">
              Stop being confused by medical jargon. Get instant, AI-powered insights 
              about your blood test results in simple, easy-to-understand language.
            </p>

            <div className="hero-cta">
              <button className="primary-button" onClick={() => navigate('/analyzer')}>
                <span>Analyze Your Report</span>
                <span className="button-arrow">→</span>
              </button>
              <div className="hero-stats">
                <div className="stat">
                  <span className="stat-icon">⚡</span>
                  <span>Instant Results</span>
                </div>
                <div className="stat">
                  <span className="stat-icon">🔒</span>
                  <span>100% Private</span>
                </div>
              </div>
            </div>
          </div>

          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🎯</div>
              <h3 className="feature-title">Simple Analysis</h3>
              <p className="feature-description">
                Complex medical terms explained in plain English that anyone can understand
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">⚠️</div>
              <h3 className="feature-title">Risk Detection</h3>
              <p className="feature-description">
                Identify concerning values and potential health risks from your results
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">💪</div>
              <h3 className="feature-title">Action Plan</h3>
              <p className="feature-description">
                Get personalized lifestyle advice to improve your health markers
              </p>
            </div>
          </div>

          <div className="social-proof">
            <div className="proof-item">
              <div className="proof-number">AI-Powered</div>
              <div className="proof-label">Latest Technology</div>
            </div>
            <div className="proof-divider"></div>
            <div className="proof-item">
              <div className="proof-number">Instant</div>
              <div className="proof-label">Get Results Fast</div>
            </div>
            <div className="proof-divider"></div>
            <div className="proof-item">
              <div className="proof-number">Secure</div>
              <div className="proof-label">Your Privacy Matters</div>
            </div>
          </div>
        </div>
      </main>

      <footer className="footer">
        <div className="container">
          <p className="footer-text">
            Made with ❤️ for better health understanding
          </p>
        </div>
      </footer>
    </div>
  );
}

export default LandingPage;
